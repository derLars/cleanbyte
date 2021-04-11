package com.derlars.password_server.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.derlars.password_server.MainActivity;
import com.derlars.password_server.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public abstract class BaseFragment extends Fragment {
    public static final String TAG = "DEBUGPRINT";

    protected FloatingActionButton floatingActionButton = null;

    protected NavController navController = null;

    public MainActivity parent;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        parent = (MainActivity)getActivity();

        navController = Navigation.findNavController(view);

        parent.addFragment(this);

        try {
            floatingActionButton = view.findViewById(R.id.floatingActionButton);
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onFloatingButtonClick();
                }
            });
        }catch(Exception ex) {
            Log.e(TAG,"FloatingButton not found.");
        }
        
        initializeView(view);
        onViewCreated(view);
    }

    public void initializeView(View view) {

    }

    public abstract void onViewCreated(@NonNull View view);

    public abstract void onBackPress();

    public abstract void onFloatingButtonClick();
}
