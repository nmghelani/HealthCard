package com.shrewd.healthcard.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shrewd.healthcard.R;
import com.shrewd.healthcard.databinding.FragmentAboutBinding;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {


    private Context mContext;
    private FragmentAboutBinding binding;

    public AboutFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAboutBinding.inflate(inflater, container, false);
        mContext = getContext();
        return binding.getRoot();
    }

}
