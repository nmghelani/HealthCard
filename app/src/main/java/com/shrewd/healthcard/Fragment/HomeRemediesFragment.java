package com.shrewd.healthcard.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shrewd.healthcard.R;
import com.shrewd.healthcard.databinding.FragmentHomeRemediesBinding;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeRemediesFragment extends Fragment {


    private FragmentHomeRemediesBinding binding;

    public HomeRemediesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeRemediesBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

}
