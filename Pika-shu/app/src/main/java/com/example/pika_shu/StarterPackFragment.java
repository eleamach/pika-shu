package com.example.pika_shu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.pika_shu.databinding.StarterPackFragmentBinding;
public class StarterPackFragment extends Fragment {
    private DatabaseHandler db;
    private StarterPackFragmentBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = StarterPackFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Button button1 = view.findViewById(R.id.button1);
        Button button2 = view.findViewById(R.id.button2);
        Button button3 = view.findViewById(R.id.button3);
        db = new DatabaseHandler(getContext());
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.addPossede(new possede(1, 150));
                if (getActivity() != null) {
                    getActivity().recreate();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.addPossede(new possede( 4, 150));
                if (getActivity() != null) {
                    getActivity().recreate();
                }
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.addPossede(new possede(7, 150));
                if (getActivity() != null) {
                    getActivity().recreate();
                }
            }
        });

        return view;
    }
}
