package com.example.pika_shu;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.example.pika_shu.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding binding;
    private PokedexFragment pokedex_fragment = new PokedexFragment();
    private StarterPackFragment fragment_starterpack = new StarterPackFragment();
    private FragmentCarte carte_Fragment = new FragmentCarte();
    private FragmentCapture capture_Fragment = new FragmentCapture();
    private FragmentInventaire inventaire_fragment = new FragmentInventaire();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, pokedex_fragment)
                .commit();
        requestLocationPermission();
        requestExternalStoragePermission();
        DatabaseHandler db = new DatabaseHandler(MainActivity.this);
        insertSampleInventory(db);
        List<possede> possedeList = getPossedeFromDatabase();
        if (possedeList.isEmpty()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment_starterpack)
                    .commit();
        }
        else {
            insertPokemonsFromJSON();

        }


    }
    private void insertSampleInventory(DatabaseHandler db) {
        db.addInventory(new Inventory(1, "soin", "5"));
        db.addInventory(new Inventory(2, "pokeball", "10"));
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }}

    private void requestExternalStoragePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        }}

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        switch (item.getItemId()) {
            case R.id.poke:
                selectedFragment = pokedex_fragment;
                break;
            case R.id.car:
                selectedFragment = carte_Fragment;
                break;
            case R.id.capt:
                selectedFragment = capture_Fragment;
                break;
            case R.id.invent:
                selectedFragment = inventaire_fragment;
                break;
        }
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        }
        return false;
    }

    private List<possede> getPossedeFromDatabase() {
        DatabaseHandler db = new DatabaseHandler(MainActivity.this);
        return db.getAllPossede();
    }

    private void insertPokemonsFromJSON() {
        try {
            InputStreamReader isr = new InputStreamReader(getResources().openRawResource(R.raw.poke));
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder builder = new StringBuilder();
            String data;
            while ((data = reader.readLine()) != null) {
                builder.append(data);
            }
            JSONArray jsonArray = new JSONArray(builder.toString());
            DatabaseHandler db = new DatabaseHandler(MainActivity.this);
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject object = jsonArray.getJSONObject(i);
                    String name = object.getString("name");
                    String imageName = object.getString("image");
                    int imageResourceId = getResources().getIdentifier(imageName, "drawable", MainActivity.this.getPackageName());
                    String type1 = object.getString("type1");
                    String type2 = object.optString("type2", null);
                    int weight = object.getInt("weight");
                    int height = object.getInt("height");
                    int id = object.getInt("id");
                    Log.d("POKEMON_DETAILS", "Name: " + name + ", Image: " + imageName + ", Type1: " + type1 + ", Type2: " + type2 + ", Weight: " + weight + ", Height: " + height );
                    Pokemon pokemon = new Pokemon(id, name, imageResourceId, Enum.valueOf(Pokemon.POKEMON_TYPE.class, type1), type2 == null ? null : Enum.valueOf(Pokemon.POKEMON_TYPE.class, type2), weight, height);
                    db.addPokemon(pokemon);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON_ERROR", "Error parsing JSON object at index " + i + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IO_ERROR", "Error reading JSON file: " + e.getMessage());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}