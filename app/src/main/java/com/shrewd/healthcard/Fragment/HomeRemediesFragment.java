package com.shrewd.healthcard.Fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shrewd.healthcard.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeRemediesFragment extends Fragment {


    public HomeRemediesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_remedies, container, false);
    }

}
