package com.example.pika_shu;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    public TextView itemNameInfo;
    public TextView itemPokemonInfo;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        itemNameInfo = itemView.findViewById(R.id.itemNameInfo);
        itemPokemonInfo = itemView.findViewById(R.id.itemPokemonInfo);
    }
}
