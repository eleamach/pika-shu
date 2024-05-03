package com.example.pika_shu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.pika_shu.databinding.PokedexFragmentBinding;
import java.util.List;

public class PokedexFragment extends Fragment implements PokemonListAdapter.OnPokemonClickListener {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        PokedexFragmentBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.pokedex_fragment, container, false);
        binding.pokemonList.setLayoutManager(new LinearLayoutManager(
                binding.getRoot().getContext()));
        DatabaseHandler db = new DatabaseHandler(getActivity());
        List<Pokemon> pokemonList = db.getAllPokemons();
        PokemonListAdapter adapter = new PokemonListAdapter(pokemonList, this);
        binding.pokemonList.setAdapter(adapter);
        return binding.getRoot();
    }

    public void onPokemonClick(Pokemon pokemon) {
        fragmentDescription fragment = new fragmentDescription();
        Bundle bundle = new Bundle();
        bundle.putParcelable("pokemon", pokemon);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}