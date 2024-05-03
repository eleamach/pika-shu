package com.example.pika_shu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.util.List;

public class FragmentInventaire extends Fragment {

    public FragmentInventaire() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inventaire, container, false);
        TextView itemName1 = rootView.findViewById(R.id.item_name_1);
        TextView itemQuantity1 = rootView.findViewById(R.id.item_quantity_1);
        TextView itemName2 = rootView.findViewById(R.id.item_name_2);
        TextView itemQuantity2 = rootView.findViewById(R.id.item_quantity_2);
        List<Inventory> inventoryList = getInventoryFromDatabase();
        if (inventoryList.size() >= 1) {
            itemName1.setText(inventoryList.get(0).getName());
            itemQuantity1.setText(inventoryList.get(0).getQuantity());
        }
        if (inventoryList.size() >= 2) {
            itemName2.setText(inventoryList.get(1).getName());
            itemQuantity2.setText(inventoryList.get(1).getQuantity());
        }
        return rootView;
    }

    private List<Inventory> getInventoryFromDatabase() {
        DatabaseHandler db = new DatabaseHandler(getContext());
        return db.getAllInventory();
    }

    private void onDropTablesButtonClick() {
        DatabaseHandler dbHandler = new DatabaseHandler(getContext());
        dbHandler.onUpgrade(dbHandler.getWritableDatabase(), dbHandler.DATABASE_VERSION, dbHandler.DATABASE_VERSION + 1);
        dbHandler.close();
        Toast.makeText(getContext(), "Toutes les tables ont été supprimées !", Toast.LENGTH_SHORT).show();
        if (getActivity() != null) {
            getActivity().recreate();
        }
    }
}