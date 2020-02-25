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
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.Utilities.CS;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private final Context mContext;
    private ArrayList<History> alHistory = new ArrayList<>();
    private FirebaseUser firebaseUser;
    private static final String TAG = "HistoryFragment";
    private RecyclerView rvHistory;
    private LinearLayout llNoData;
    private FrameLayout flPatient, flAdmin;
    private RecyclerView rvHistoryAdmin;
    private LinearLayout llNoDataAdmin;

    public HistoryFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_history, container, false);
        flPatient = view.findViewById(R.id.flPatient);
        flAdmin = view.findViewById(R.id.flAdmin);

        rvHistory = view.findViewById(R.id.rvHistory);
        llNoData = view.findViewById(R.id.llNoData);

        rvHistoryAdmin = view.findViewById(R.id.rvHistoryAdmin);
        llNoDataAdmin = view.findViewById(R.id.llNoDataAdmin);

        SharedPreferences sp = mContext.getSharedPreferences("GC", MODE_PRIVATE);
        long type = sp.getLong(CS.type, 1);
        Log.e(TAG, "onCreateView: " + type);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ((MainActivity) mContext).setInProgress();

        if (type == CS.ADMIN) {
            flAdmin.setVisibility(View.VISIBLE);
            flPatient.setVisibility(View.GONE);
            db.collection(CS.History)
                    .orderBy(CS.date, Query.Direction.DESCENDING)
//                    .whereEqualTo(CS.patientid, firebaseUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            alHistory.clear();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                try {
                                    /*History history = new History(documentSnapshot.getString(CS.doctorid), documentSnapshot.getString(CS.patientid),
                                            documentSnapshot.getString(CS.reportid), documentSnapshot.getString(CS.area), documentSnapshot.getString(CS.disease),
                                            (ArrayList<String>) documentSnapshot.get(CS.medicine), (ArrayList<String>) documentSnapshot.get(CS.symptoms), (ArrayList<String>) documentSnapshot.get(CS.vigilance),
                                            documentSnapshot.getTimestamp(CS.date).toDate());*/
                                    History history = documentSnapshot.toObject(History.class);
                                    alHistory.add(history);
                                } catch (Exception ex) {
                                    Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                }
                            }
                            ((MainActivity) mContext).setProgressCompleted();
                            setAdapter(alHistory, type);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ((MainActivity) mContext).setProgressCompleted();
                            setAdapter(alHistory, type);
                            Log.e(TAG, "onFailure: " + e.getMessage());
                        }
                    });
        } else {
            flAdmin.setVisibility(View.GONE);
            flPatient.setVisibility(View.VISIBLE);
            db.collection(CS.Patient)
                    .whereEqualTo(CS.userid, firebaseUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot dsPatient : queryDocumentSnapshots.getDocuments()) {
                                db.collection(CS.History)
                                        .orderBy(CS.date, Query.Direction.DESCENDING)
                                        .whereEqualTo(CS.patientid, dsPatient.getString(CS.patientid))
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
                                                setAdapter(alHistory, type);

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                ((MainActivity) mContext).setProgressCompleted();
                                                setAdapter(alHistory, type);
                                                Log.e(TAG, "onFailure: " + e.getMessage());
                                            }
                                        });
                                break;
                            }
                        }
                    });

        }

        return view;
    }

    private void setAdapter(ArrayList<History> alHistory, long type) {
        switch ((int) type) {
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
                break;
            case CS.PATIENT:
                if (alHistory.size() > 0) {
                    rvHistory.setVisibility(View.VISIBLE);
                    llNoData.setVisibility(View.GONE);
                    Log.e(TAG, "onSuccess: " + alHistory.size());
                    HistoryAdapter historyAdapter = new HistoryAdapter(mContext, alHistory, CS.PATIENT);
                    rvHistory.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                    rvHistory.setAdapter(historyAdapter);
                } else {
                    rvHistory.setVisibility(View.GONE);
                    llNoData.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(mContext, LoginActivity.class));
            ((Activity) mContext).finish();
        }
        super.onAttach(context);
    }
}
