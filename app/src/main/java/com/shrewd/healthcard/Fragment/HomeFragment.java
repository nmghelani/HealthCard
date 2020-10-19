package com.shrewd.healthcard.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.shrewd.healthcard.Activity.LoginActivity;
import com.shrewd.healthcard.Utilities.CS;
import com.shrewd.healthcard.Utilities.CU;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private Context mContext;
    private ArrayList pieEntries;
    private PieDataSet pieDataSet;
    private PieData pieData;
    private ArrayList pcLabEntries;
    private PieDataSet pcLabDataSet;
    private PieData pcLabData;
    private long type;
    private ArrayList lcPatientlineEntries;
    private LineDataSet lcPatientlineDataSet;
    private LineData lcPatientlineData;
    private ArrayList pcAdminpieEntries;
    private PieData pcAdminpieData;
    private PieDataSet pcAdminpieDataSet;
    private FragmentHomeBinding binding;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);
        mContext = getContext();
        type = mContext.getSharedPreferences("GC", MODE_PRIVATE).getLong(CS.type, -1);
        binding.pcDoctor.setCenterText("Disease");
        binding.pcDoctor.setCenterTextSize(getResources().getDimension(R.dimen._7sdp));
        binding.pcDoctor.getDescription().setEnabled(false);
        CU.setLayout(type, binding.flDoctor, binding.flGovernment, binding.flLab, binding.flPatient, binding.flAdmin);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(mContext, "Some error occurred! Please login again", Toast.LENGTH_SHORT).show();
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
            ((Activity) mContext).finish();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (type == CS.DOCTOR) {
            db.collection(CS.Doctor)
                    .whereEqualTo(CS.userid, firebaseUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot dsDoctor : queryDocumentSnapshots.getDocuments()) {
                                db.collection(CS.History)
                                        .whereEqualTo(CS.doctorid, dsDoctor.getString(CS.doctorid))
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                HashMap<String, Float> map = new HashMap<>();
                                                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                                    if (map.containsKey(doc.getString(CS.disease))) {
                                                        try {
                                                            map.put(doc.getString(CS.disease), map.get(doc.getString(CS.disease)) + 1);
                                                        } catch (Exception ex) {
                                                            Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                                        }
                                                    } else {
                                                        map.put(doc.getString(CS.disease), 1f);
                                                    }
                                                }

                                                Set<String> set = map.keySet();

                                                Log.e(TAG, "onSuccess: ***");
                                                pieEntries = new ArrayList<>();
                                                for (Iterator<String> it = set.iterator(); it.hasNext(); ) {
                                                    String s = it.next();
                                                    Log.e(TAG, "onSuccess: " + s);
                                                    Log.e(TAG, "onSuccess: " + map.get(s));
                                                    pieEntries.add(new PieEntry(map.get(s), s));
                                                }

                                                if (pieEntries.size() > 0) {
                                                    pieDataSet = new PieDataSet(pieEntries, "");
                                                    pieData = new PieData(pieDataSet);
                                                    binding.pcDoctor.setData(pieData);
                                                    pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                                                    pieDataSet.setSliceSpace(2f);
                                                    pieDataSet.setValueTextColor(Color.WHITE);
                                                    pieDataSet.setValueTextSize(10f);
                                                    pieDataSet.setSliceSpace(5f);
                                                    binding.pcDoctor.animateXY(1500, 500);
                                                    Log.e(TAG, "onSuccess: ***");
                                                }
                                            }
                                        });
                                break;
                            }
                        }
                    });
        } else if (type == CS.LAB) {
            binding.pcLab.setCenterText("Reports");

            binding.pcLab.setCenterTextSize(getResources().getDimension(R.dimen._7sdp));
            binding.pcLab.getDescription().setEnabled(false);
//        getEntries();

            Log.e(TAG, "onSuccess: " + firebaseUser.getUid());
            db.collection(CS.LabAssistant)
                    .whereEqualTo(CS.userid, firebaseUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot dsLabAssistant : queryDocumentSnapshots.getDocuments()) {
                                Log.e(TAG, "onSuccess: " + dsLabAssistant.getString(CS.labid));
                                db.collection(CS.Report)
                                        .whereEqualTo(CS.labid, dsLabAssistant.getString(CS.labid))
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                HashMap<String, Float> map = new HashMap<>();
                                                for (DocumentSnapshot dsReport : queryDocumentSnapshots.getDocuments()) {
                                                    Log.e(TAG, "onSuccess: " + dsReport.getString(CS.type));
                                                    if (map.containsKey(dsReport.getString(CS.type))) {
                                                        try {
                                                            map.put(dsReport.getString(CS.type), map.get(dsReport.getString(CS.type)) + 1);
                                                        } catch (Exception ex) {
                                                            Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                                        }
                                                    } else {
                                                        map.put(dsReport.getString(CS.type), 1f);
                                                    }
                                                }

                                                Set<String> set = map.keySet();

                                                Log.e(TAG, "onSuccess: ***");
                                                pcLabEntries = new ArrayList<>();
                                                for (Iterator<String> it = set.iterator(); it.hasNext(); ) {
                                                    String s = it.next();
                                                    Log.e(TAG, "onSuccess: " + s);
                                                    Log.e(TAG, "onSuccess: " + map.get(s));
                                                    pcLabEntries.add(new PieEntry(map.get(s), s));
                                                }

                                                if (pcLabEntries.size() > 0) {
                                                    pcLabDataSet = new PieDataSet(pcLabEntries, "");
                                                    pcLabData = new PieData(pcLabDataSet);
                                                    binding.pcLab.setData(pcLabData);
                                                    pcLabDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                                                    pcLabDataSet.setSliceSpace(2f);
                                                    pcLabDataSet.setValueTextColor(Color.WHITE);
                                                    pcLabDataSet.setValueTextSize(10f);
                                                    pcLabDataSet.setSliceSpace(5f);
                                                    binding.pcLab.animateXY(1500, 500);
                                                    Log.e(TAG, "onSuccess: ***");
                                                }
                                            }
                                        });
                                break;
                            }
                        }
                    });
        } else if (type == CS.PATIENT) {
            lcPatientlineEntries = new ArrayList<>();
            binding.lcPatient.getDescription().setEnabled(false);
            db.collection(CS.Patient)
                    .whereEqualTo(CS.userid, firebaseUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot dsPatient : queryDocumentSnapshots.getDocuments()) {
                                Log.e(TAG, "onSuccess: " + dsPatient.getString(CS.labid));
                                db.collection(CS.History)
                                        .whereEqualTo(CS.patientid, dsPatient.getString(CS.patientid))
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                int cnt = 0;
                                                for (DocumentSnapshot dsHistory : queryDocumentSnapshots.getDocuments()) {
                                                    Date date = dsHistory.getDate(CS.date);
                                                    float time = date.getTime() / (1000 * 60 * 60 * 24);
                                                    Log.e(TAG, "onSuccess: " + time);
                                                    lcPatientlineEntries.add(new Entry(time, cnt++));
                                                }
                                                if (lcPatientlineEntries.size() > 0) {
                                                    lcPatientlineDataSet = new LineDataSet(lcPatientlineEntries, "");
                                                    lcPatientlineData = new LineData(lcPatientlineDataSet);
                                                    binding.lcPatient.setData(lcPatientlineData);
                                                    lcPatientlineDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                                                    lcPatientlineDataSet.setValueTextColor(Color.BLACK);
                                                    lcPatientlineDataSet.setValueTextSize(18f);
                                                    binding.lcPatient.animateY(1500);
                                                }
                                            }
                                        });
                                break;
                            }
                        }
                    });
        } else if (type == CS.ADMIN) {
            binding.pcAdminDisease.getDescription().setEnabled(false);
            binding.pcAdminDisease.setCenterText("Disease");
            binding.pcAdminDisease.setCenterTextSize(getResources().getDimension(R.dimen._7sdp));
            binding.pcAdminDisease.getDescription().setEnabled(false);
            db.collection(CS.History)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            HashMap<String, Float> map = new HashMap<>();
                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                if (map.containsKey(doc.getString(CS.disease))) {
                                    try {
                                        map.put(doc.getString(CS.disease), map.get(doc.getString(CS.disease)) + 1);
                                    } catch (Exception ex) {
                                        Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                    }
                                } else {
                                    map.put(doc.getString(CS.disease), 1f);
                                }
                            }

                            Set<String> set = map.keySet();

                            Log.e(TAG, "onSuccess: ***");
                            pcAdminpieEntries = new ArrayList<>();
                            for (Iterator<String> it = set.iterator(); it.hasNext(); ) {
                                String s = it.next();
                                Log.e(TAG, "onSuccess: " + s);
                                Log.e(TAG, "onSuccess: " + map.get(s));
                                pcAdminpieEntries.add(new PieEntry(map.get(s), s));
                            }

                            if (pcAdminpieEntries.size() > 0) {
                                pcAdminpieDataSet = new PieDataSet(pcAdminpieEntries, "");
                                pcAdminpieData = new PieData(pcAdminpieDataSet);
                                binding.pcAdminDisease.setData(pcAdminpieData);
                                pcAdminpieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                                pcAdminpieDataSet.setSliceSpace(2f);
                                pcAdminpieDataSet.setValueTextColor(Color.WHITE);
                                pcAdminpieDataSet.setValueTextSize(10f);
                                pcAdminpieDataSet.setSliceSpace(5f);
                                binding.pcAdminDisease.animateXY(1500, 500);
                                Log.e(TAG, "onSuccess: ***");
                            }
                        }
                    });
        }

        return binding.getRoot();
    }

}
