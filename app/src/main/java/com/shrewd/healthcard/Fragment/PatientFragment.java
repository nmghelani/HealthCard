package com.shrewd.healthcard.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.shrewd.healthcard.Activity.LoginActivity;
import com.shrewd.healthcard.Activity.MainActivity;
import com.shrewd.healthcard.Adapter.HistoryAdapter;
import com.shrewd.healthcard.ModelClass.History;
import com.shrewd.healthcard.Utilities.CS;
import com.shrewd.healthcard.Utilities.CU;
import com.shrewd.healthcard.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatientFragment extends Fragment {

    private final Context mContext;
    private FrameLayout flDoctor, flPatient, flLab, flGovernment;
    private FrameLayout flAdmin;
    private String TAG = "PatientFragment";
    private LinearLayout llNoData;
    private RecyclerView rvHistory;
    private FirebaseUser firebaseUser;
    private ArrayList<History> alHistory = new ArrayList<>();
    private RecyclerView rvHistoryAdmin;
    private LinearLayout llNoDataAdmin;

    public PatientFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient, container, false);
        flDoctor = view.findViewById(R.id.flDoctor);
        flPatient = view.findViewById(R.id.flPatient);
        flLab = view.findViewById(R.id.flLab);
        flGovernment = view.findViewById(R.id.flGovernment);
        flAdmin = view.findViewById(R.id.flAdmin);
        rvHistory = view.findViewById(R.id.rvHistory);
        llNoData = view.findViewById(R.id.llNoData);
        rvHistoryAdmin = view.findViewById(R.id.rvHistoryAdmin);
        llNoDataAdmin = view.findViewById(R.id.llNoDataAdmin);

        MainActivity.NFCPatientEnabled = true;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(mContext, LoginActivity.class));
            ((Activity) mContext).finish();
        }

        SharedPreferences sp = mContext.getSharedPreferences("GC", MODE_PRIVATE);
        long type = sp.getLong(CS.type, 1);
        Log.e(TAG, "onCreateView: " + type);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ((MainActivity) mContext).setInProgress();
        if (type == CS.ADMIN) {
            db.collection(CS.History)
                    .orderBy(CS.date, Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            alHistory.clear();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                try {
                                    History history = documentSnapshot.toObject(History.class);
                                    Log.e(TAG, "onSuccess: " + history.getDisease());
                                    Log.e(TAG, "onSuccess: " + history.getArea());
                                    Log.e(TAG, "onSuccess: " + history.getPatientid());
                                    Log.e(TAG, "onSuccess: " + history.getDoctorid());
                                    alHistory.add(history);
                                } catch (Exception ex) {
                                    Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                }
                            }
                            ((MainActivity) mContext).setProgressCompleted();
                            setAdapter(alHistory, CS.ADMIN);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ((MainActivity) mContext).setProgressCompleted();
                            setAdapter(alHistory, CS.ADMIN);
                            Log.e(TAG, "onFailure: " + e.getMessage());
                        }
                    });
        } else if (type == CS.DOCTOR){
            db.collection(CS.Doctor)
                    .whereEqualTo(CS.userid, firebaseUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot dsDoctor : queryDocumentSnapshots.getDocuments()) {
                                db.collection(CS.History)
                                        .orderBy(CS.date, Query.Direction.DESCENDING)
                                        .whereEqualTo(CS.doctorid, dsDoctor.getString(CS.doctorid))
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                alHistory.clear();
                                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                                    try {
                                                        History history = documentSnapshot.toObject(History.class);
                                                        alHistory.add(history);
                                                    } catch (Exception ex) {
                                                        Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                                    }
                                                }
                                                ((MainActivity) mContext).setProgressCompleted();
                                                setAdapter(alHistory, CS.DOCTOR);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                ((MainActivity) mContext).setProgressCompleted();
                                                setAdapter(alHistory, CS.DOCTOR);
                                                Log.e(TAG, "onFailure: " + e.getMessage());
                                            }
                                        });
                                break;
                            }
                        }
                    });
        }

        CU.setLayout(type, flDoctor, null, flLab, null, flAdmin);
        return view;
    }

    private void setAdapter(ArrayList<History> alHistory, int type) {
        switch (type) {
            case CS.DOCTOR:
                if (alHistory.size() > 0) {
                    rvHistory.setVisibility(View.VISIBLE);
                    llNoData.setVisibility(View.GONE);
                    Log.e(TAG, "onSuccess: " + alHistory.size());
                    HistoryAdapter historyAdapter = new HistoryAdapter(mContext, alHistory, CS.DOCTOR);
                    rvHistory.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                    rvHistory.setAdapter(historyAdapter);
                } else {
                    rvHistory.setVisibility(View.GONE);
                    llNoData.setVisibility(View.VISIBLE);
                }
                break;
            case CS.ADMIN:
                if (alHistory.size() > 0) {
                    rvHistoryAdmin.setVisibility(View.VISIBLE);
                    llNoDataAdmin.setVisibility(View.GONE);
                    Log.e(TAG, "onSuccess: " + alHistory.size());
                    HistoryAdapter historyAdapter = new HistoryAdapter(mContext, alHistory, CS.ADMIN);
                    rvHistoryAdmin.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                    rvHistoryAdmin.setAdapter(historyAdapter);
                } else {
                    rvHistoryAdmin.setVisibility(View.GONE);
                    llNoDataAdmin.setVisibility(View.VISIBLE);
                }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MainActivity.NFCPatientEnabled = false;
    }
}