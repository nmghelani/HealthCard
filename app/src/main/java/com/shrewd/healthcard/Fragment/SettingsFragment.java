package com.shrewd.healthcard.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.shrewd.healthcard.Activity.MainActivity;
import com.shrewd.healthcard.Utilities.CS;
import com.shrewd.healthcard.Utilities.CU;
import com.shrewd.healthcard.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private final Context mContext;
    private FrameLayout flDoctor, flPatient, flLab, flGovernment;
    private FrameLayout flAdmin;
    private String TAG = "SettingsFragment";

    public SettingsFragment(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        flDoctor = view.findViewById(R.id.flDoctor);
        flPatient = view.findViewById(R.id.flPatient);
        flLab = view.findViewById(R.id.flLab);
        flGovernment = view.findViewById(R.id.flGovernment);
        flAdmin = view.findViewById(R.id.flAdmin);

        SharedPreferences sp = mContext.getSharedPreferences("GC", MODE_PRIVATE);
        long type = sp.getLong(CS.type, 1);
        Log.e(TAG, "onCreateView: " + type);
        CU.setLayout(type, flDoctor, flGovernment, flLab, flPatient, flAdmin);
        return view;
    }

}
