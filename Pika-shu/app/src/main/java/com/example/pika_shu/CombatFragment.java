package com.example.pika_shu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import java.util.HashMap;

public class CombatFragment extends Fragment {
    private static final String KEY_NAME_POKEMON = "namepoke";
    private static final String KEY_PV = "pvpoke";
    private static final String KEY_IMAGE_POKEMON = "imgpoke";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_combat, container, false);
        TextView pokemontoattackname = rootView.findViewById(R.id.NamePokemonCombat);
        TextView pokemontoattackpv = rootView.findViewById(R.id.PVPokemonCombat);
        ImageView pokemontoattackimg = rootView.findViewById(R.id.imageView);
        Button soinbutton = rootView.findViewById(R.id.buttonInventorySoin);
        Button capturebutton = rootView.findViewById(R.id.buttonCapture);
        Button attaquebutton = rootView.findViewById(R.id.buttonAttaque);
        TextView mespv = rootView.findViewById(R.id.pv);
        DatabaseHandler db = new DatabaseHandler(getContext());
        int pokemonId = getArguments().getInt("pokemonId", -1);
        int pokemonPV2 = db.getPokemonPV(pokemonId);
        db.updateWildPokemonPV(pokemonId, pokemonPV2);
        soinbutton.setOnClickListener(v -> {
            int currentPv = getMyPokemonPv();
            if (currentPv >= 150) {
                showExplanationDialog("Maximum de PV atteint", "Tes points de vie sont déjà au maximum, tu es un peu trop gourmand ! ", 0);
            }
            else {
                int numberOfItems = db.getNumberOfItems("soin");
                if (numberOfItems > 0) {
                    db.removeItemFromInventory("soin");
                    int updatedmyPV = db.getMyPokemonPv();
                    mespv.setText(updatedmyPV + " PV");
                }
                else {
                    showExplanationDialog("Rupture de stock", "Tu n'as pas assez de soin, la mort est inévitable...", 0);
                }
            }
        });

        capturebutton.setOnClickListener(v -> {
            int pokemonCurrentPV = db.getPokemonCurrentPV(pokemonId);
            if (pokemonCurrentPV < 50) {
                int numberOfItems = db.getNumberOfItems("pokeball");
                Log.d("test", String.valueOf(numberOfItems));
                if (numberOfItems > 0) {
                    db.removeItemFromInventory("pokeball");
                    db.addInventory(new Inventory(1, "soin", "5"));
                    db.addInventory(new Inventory(2, "pokeball", "2"));
                    showExplanationDialog("Cadeau ! ","Bravo vous avez gagné 5 soins et 2 pokeball", 0);
                    db.addPossede(new possede(pokemonId, pokemonCurrentPV));
                    showExplanationDialog("Capturé !", "Et un de plus que la team Rocket n'aura pas.", 0);
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    Fragment fragmentCarte = new FragmentCarte(true, pokemonId);
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragmentCarte)
                            .addToBackStack(null)
                            .commit();
                }
                else {
                    showExplanationDialog("Rupture de stock", "Tu n'as pas assez de pokeball, tu ne les attraperas pas tous...", 0);
                }
            }
            else {
                showExplanationDialog("Zut", "Ce Pokémon a trop de points de vie pour être capturé. Diminue ses points de vie avant de capturer.", 0);
            }
        });

        HashMap<String, Object> pokemonInfo = db.getPokemonInfoByID(pokemonId);
        if (!pokemonInfo.isEmpty()) {
            String pokemonName = (String) pokemonInfo.get(KEY_NAME_POKEMON);
            int pokemonPV = (int) pokemonInfo.get(KEY_PV);
            String imgString = (String) pokemonInfo.get(KEY_IMAGE_POKEMON);
            int img = Integer.parseInt(imgString);
            Context context = getContext();
            @SuppressLint("ResourceType") Drawable drawable2 = ContextCompat.getDrawable(context, img);
            if (drawable2 != null) {
                pokemontoattackimg.setImageDrawable(drawable2);
            }
            pokemontoattackname.setText(pokemonName);
            pokemontoattackpv.setText(String.valueOf(pokemonPV + " PV"));
        }
        else {
            Log.e("Fragment", "Aucune information trouvée pour le Pokémon avec l'ID " + pokemonId);
        }
        db.close();
        attaquebutton.setOnClickListener(v -> {
            showExplanationDialog("Il vous attaque !", "Ohoh, bon courage...",0);
            db.removePV(pokemonId);
            int updatedPV = db.getPokemonCurrentPV(pokemonId);
            pokemontoattackpv.setText(updatedPV + " PV");
            showExplanationDialog("Vous l'attaquez  !", "Pouahlala beau gosse",0);
            db.removemyPV();
            int updatedmyPV = db.getMyPokemonPv();
            mespv.setText(updatedmyPV + " PV");
        });
        int currentPv = getMyPokemonPv();
        mespv.setText(currentPv + " PV");
        return rootView;
    }

    private int getMyPokemonPv() {
        DatabaseHandler db = new DatabaseHandler(getContext());
        return db.getMyPokemonPv();
    }

    private void showExplanationDialog(String title, String message, final int requestCode) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();
    }
}
