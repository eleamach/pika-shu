package com.example.pika_shu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.pika_shu.databinding.FragmentCarteBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FragmentCarte extends Fragment {
    private boolean captureReussie;
    private int pokemonId;
    private FragmentCarteBinding binding;
    private DatabaseHandler db;

    private MyLocationNewOverlay mLocationOverlay;
    private List<GeoPoint> wildPokemonLocations = new ArrayList<>();
    private List<GeoPoint> healthCenterLocations = new ArrayList<>();
    private Handler mHandler = new Handler();
    private static final int DELAY = 5000;

    private Runnable mUpdateLocationRunnable = new Runnable() {
        @Override
        public void run() {
            checkWildPokemonDistance(mLocationOverlay.getMyLocation());
            checkHealthCenterDistance(mLocationOverlay.getMyLocation());
            mHandler.postDelayed(this, DELAY);
        }
    };

    public FragmentCarte(boolean captureReussie, int pokemonId) {
        this.captureReussie = captureReussie;
        this.pokemonId = pokemonId;
    }

    public FragmentCarte() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCarteBinding.inflate(inflater, container, false);
        Context context = getContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK);
        binding.mapView.getController().setZoom(20);
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), binding.mapView);
        mLocationOverlay.enableMyLocation();
        binding.mapView.getOverlays().add(this.mLocationOverlay);
        db = new DatabaseHandler(getContext());
        mLocationOverlay.runOnFirstFix(() -> {
            final GeoPoint playerLocation = mLocationOverlay.getMyLocation();
            if (playerLocation != null) {
                getActivity().runOnUiThread(() -> binding.mapView.getController().setCenter(playerLocation));
            }
        });
        View view = binding.getRoot();
        GetPharmacyLocationsTask task = new GetPharmacyLocationsTask();
        GetBarLocationsTask task2 = new GetBarLocationsTask();
        task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        MapEventsReceiver receiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }
            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        MapEventsOverlay eventsOverlay = new MapEventsOverlay(receiver);
        binding.mapView.getOverlays().add(eventsOverlay);
        binding.mapView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                checkWildPokemonDistance(mLocationOverlay.getMyLocation());
                return false;
            }
        });
        mHandler.postDelayed(mUpdateLocationRunnable, DELAY);
        return view;
    }

    private static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
        mLocationOverlay.runOnFirstFix(() -> {
            final GeoPoint playerLocation = mLocationOverlay.getMyLocation();
            if (playerLocation != null) {
                getActivity().runOnUiThread(() -> {
                    binding.mapView.getController().setCenter(playerLocation);
                    checkWildPokemonDistance(playerLocation);
                    checkHealthCenterDistance(playerLocation);
                });
            }
        });
        if (captureReussie) {
            db.removeWild(pokemonId);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    private void addPharmacySoinMarkers(List<GeoPoint> pharmacyLocations) {
        if (binding.mapView == null) {
            return;
        }
        for (GeoPoint geoPoint : pharmacyLocations) {
            Marker marker = new Marker(binding.mapView);
            marker.setPosition(geoPoint);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            marker.setIcon(getResources().getDrawable(R.drawable.hopital));
            binding.mapView.getOverlays().add(marker);
            healthCenterLocations.add(geoPoint);
        }
    }

    private void generateWildPokemonsOnBars(List<GeoPoint> barLocations) {
        for (GeoPoint location : barLocations) {
            int id = getRandomNumberInRange(1, 150);
            db.addWild(new wild(id, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude())));
        }
    }

    private void addBarWildPokemonMarkers(List<GeoPoint> barLocations) {
        if (binding.mapView == null) {
            return;
        }
        for (GeoPoint geoPoint : barLocations) {
            Marker marker = new Marker(binding.mapView);
            marker.setPosition(geoPoint);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            marker.setIcon(getResources().getDrawable(R.drawable.epingle));
            binding.mapView.getOverlays().add(marker);
            wildPokemonLocations.add(geoPoint);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetBarLocationsTask extends AsyncTask<Void, Void, List<GeoPoint>> {
        @Override
        protected List<GeoPoint> doInBackground(Void... voids) {
            List<GeoPoint> barLocations = new ArrayList<>();
            String overpassQuery = "[out:json];(node['amenity'='bar'](45.4375, 4.5306,45.8345,5.2084););out;";
            try {
                String overpassUrl = "https://overpass-api.de/api/interpreter?data=" + java.net.URLEncoder.encode(overpassQuery, "UTF-8");
                URL url = new URL(overpassUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray elements = jsonResponse.getJSONArray("elements");
                for (int i = 0; i < elements.length(); i++) {
                    JSONObject element = elements.getJSONObject(i);
                    double lat = element.getDouble("lat");
                    double lon = element.getDouble("lon");
                    barLocations.add(new GeoPoint(lat, lon));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return barLocations;
        }

        @Override
        protected void onPostExecute(List<GeoPoint> barLocations) {
            super.onPostExecute(barLocations);
            if (binding.mapView != null) {
                generateWildPokemonsOnBars(barLocations);
                addBarWildPokemonMarkers(barLocations);
            } else {
                Log.e("FragmentCarte", "MapView is null. Cannot generate wild Pokemons on bars.");
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetPharmacyLocationsTask extends AsyncTask<Void, Void, List<GeoPoint>> {
        @Override
        protected List<GeoPoint> doInBackground(Void... voids) {
            List<GeoPoint> pharmacyLocations = new ArrayList<>();
            String overpassQuery = "[out:json];(node['amenity'='pharmacy'](45.4375, 4.5306,45.8345,5.2084););out;";
            try {
                String overpassUrl = "https://overpass-api.de/api/interpreter?data=" + java.net.URLEncoder.encode(overpassQuery, "UTF-8");
                URL url = new URL(overpassUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray elements = jsonResponse.getJSONArray("elements");
                for (int i = 0; i < elements.length(); i++) {
                    JSONObject element = elements.getJSONObject(i);
                    double lat = element.getDouble("lat");
                    double lon = element.getDouble("lon");
                    pharmacyLocations.add(new GeoPoint(lat, lon));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return pharmacyLocations;
        }

        @Override
        protected void onPostExecute(List<GeoPoint> pharmacyLocations) {
            super.onPostExecute(pharmacyLocations);
            if (binding.mapView != null) {
                addHealthCentersToDatabase(pharmacyLocations);
                addPharmacySoinMarkers(pharmacyLocations);
            } else {
                Log.e("FragmentCarte", "MapView is null. Cannot add pharmacy markers.");
            }
        }
    }

    private void addHealthCentersToDatabase(List<GeoPoint> pharmacyLocations) {
        DatabaseHandler db = new DatabaseHandler(getContext());
        for (GeoPoint location : pharmacyLocations) {
            db.addHealthCenter(location.getLatitude(), location.getLongitude());
        }
    }

    private void checkWildPokemonDistance(GeoPoint userLocation) {
        if (userLocation == null) {
            return;
        }
        for (GeoPoint wildPokemonLocation : wildPokemonLocations) {
            Location user = new Location("");
            user.setLatitude(userLocation.getLatitude());
            user.setLongitude(userLocation.getLongitude());
            Location wildPokemon = new Location("");
            wildPokemon.setLatitude(wildPokemonLocation.getLatitude());
            wildPokemon.setLongitude(wildPokemonLocation.getLongitude());
            float distance = user.distanceTo(wildPokemon);
            int pokemonId = db.getPokemonIdByCoordinates(wildPokemon.getLatitude(), wildPokemon.getLongitude());
            if (pokemonId != 1 ) {
                Log.d("1FragmentCarte", "Distance entre l'utilisateur et le wildpokemon : " + distance + "id du pokemon : " + pokemonId);
                if (distance < 10) {
                        openCombatFragment(pokemonId);
                    break;
                }}}
    }

    private void openCombatFragment(int pokemonId) {
        if (pokemonId != -1) {
            Log.d("2FragmentCarte", "ID du Pokémon envoyé au CombatFragment : " + pokemonId);
            Fragment fragment = new CombatFragment();
            Bundle args = new Bundle();
            args.putInt("pokemonId", pokemonId);
            fragment.setArguments(args);
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            Log.d("2FragmentCarte", "pokemonId est -1, ne lance pas le CombatFragment.");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void checkHealthCenterDistance(GeoPoint userLocation) {
        if (userLocation == null) {
            return;
        }
        for (GeoPoint healthCenterLocation : healthCenterLocations) {
            Location user = new Location("");
            user.setLatitude(userLocation.getLatitude());
            user.setLongitude(userLocation.getLongitude());
            Location healthCenter = new Location("");
            healthCenter.setLatitude(healthCenterLocation.getLatitude());
            healthCenter.setLongitude(healthCenterLocation.getLongitude());
            float distance = user.distanceTo(healthCenter);
            Log.d("FragmentCarte2", "Distance entre l'utilisateur et le centre de soins : " + distance);
            if (distance < 10) {
                showExplanationMessage();
                db.setmyPV();
                break;
            }
        }
    }

    private void showExplanationMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Proximité d'un centre de soins");
        builder.setMessage("Vous vous approchez d'un centre de soins. Vous avez soigné vos pokemon !.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}
