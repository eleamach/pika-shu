package com.example.pika_shu;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class fragmentDescription extends Fragment{
    public fragmentDescription() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_description, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Pokemon pokemon = bundle.getParcelable("pokemon");
            if (pokemon != null) {
                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textViewName = rootView.findViewById(R.id.pokename);
                TextView textViewType1 = rootView.findViewById(R.id.poketype1);
                TextView textViewType2 = rootView.findViewById(R.id.poketype2);
                ImageView poke = rootView.findViewById(R.id.pokeimage);
                textViewName.setText(pokemon.getName());
                poke.setImageResource(pokemon.getFrontResource());
                textViewType1.setText(pokemon.getType1().toString());
                if (pokemon.getType2() != null) {
                    textViewType2.setVisibility(View.VISIBLE);
                    textViewType2.setText(pokemon.getType2().toString());
                } else {
                    textViewType2.setVisibility(View.GONE);
                }}}
        return rootView;
    }}
