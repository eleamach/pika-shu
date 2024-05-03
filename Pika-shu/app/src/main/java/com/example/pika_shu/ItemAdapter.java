package com.example.pika_shu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    private final Context context;
    private final List<possede> possedeList;
    private DatabaseHandler db;

    public ItemAdapter(Context context, List<possede> possedeList) {
        this.context = context;
        this.possedeList = possedeList;
        this.db = new DatabaseHandler(context);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        possede possed = possedeList.get(position);
        String itemNameInfo = db.getPokemonNameById(possed.getIdPokemon());
        String pvInfo = "PV : " + possed.getCurrentPv();
        holder.itemNameInfo.setText(itemNameInfo);
        holder.itemPokemonInfo.setText(pvInfo);
    }
    
    @Override
    public int getItemCount() {
        return possedeList.size();
    }

}

