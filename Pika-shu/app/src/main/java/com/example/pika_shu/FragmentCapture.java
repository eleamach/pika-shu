package com.example.pika_shu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FragmentCapture extends Fragment {
    private DatabaseHandler db;

    public FragmentCapture() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_capture, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        db = new DatabaseHandler(getContext());
        List<possede> possedeList = getPossedeFromDatabase();
        ItemAdapter adapter = new ItemAdapter(getContext(), possedeList);
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    private List<possede> getPossedeFromDatabase() {
        return db.getAllPossede();
    }
}
