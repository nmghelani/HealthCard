package com.shrewd.healthcard.Fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.shrewd.healthcard.Adapter.ReportAdapter;
import com.shrewd.healthcard.ModelClass.Report;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.Utilities.CS;
import com.shrewd.healthcard.Utilities.CU;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportsFragment extends Fragment {


    private static final String TAG = "ReportsFragment";
    private final Context mContext;
    private LinearLayout llNoData;
    private RecyclerView rvReport;
    private ArrayList<Report> alReport = new ArrayList<>();
    private LinearLayout llNoDataAdmin;
    private RecyclerView rvReportAdmin;
    private FrameLayout flReport, flReportAdmin;
    private ProgressDialog pd;

    public ReportsFragment(Context mContext) {
        // Required empty public constructor
        this.mContext = mContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        rvReport = view.findViewById(R.id.rvReport);
        llNoData = view.findViewById(R.id.llNoData);
        flReport = view.findViewById(R.id.flReport);
        flReportAdmin = view.findViewById(R.id.flReportAdmin);
        rvReportAdmin = view.findViewById(R.id.rvReportAdmin);
        llNoDataAdmin = view.findViewById(R.id.llNoDataAdmin);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(mContext, "Some error occurred! Please login again", Toast.LENGTH_SHORT).show();
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
            ((Activity) mContext).finish();
        }
//        loadData();


        return view;
    }

    private void loadData() {
        SharedPreferences sp = mContext.getSharedPreferences("GC", Context.MODE_PRIVATE);
        long type = sp.getLong(CS.type, -1);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (type == CS.ADMIN) {
            MainActivity.NFCReportEnabled = true;
            flReportAdmin.setVisibility(View.VISIBLE);
            flReport.setVisibility(View.GONE);

            ((MainActivity) mContext).setInProgress();
            db.collection(CS.Report)
                    .orderBy(CS.date, Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            alReport.clear();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                try {
                                    Report report = documentSnapshot.toObject(Report.class);
                                    /*History history = new History(documentSnapshot.getString(CS.doctorid), documentSnapshot.getString(CS.patientid),
                                            documentSnapshot.getString(CS.reportid), documentSnapshot.getString(CS.area), documentSnapshot.getString(CS.disease),
                                            (ArrayList<String>) documentSnapshot.get(CS.medicine), (ArrayList<String>) documentSnapshot.get(CS.symptoms), (ArrayList<String>) documentSnapshot.get(CS.vigilance),
                                            documentSnapshot.getTimestamp(CS.date).toDate());*/
                                    Log.e(TAG, "onSuccess: report: " + report.getLabid());
                                    Log.e(TAG, "onSuccess: report: " + report.getReportid());
                                    Log.e(TAG, "onSuccess: report: " + report.getType());
                                    Log.e(TAG, "onSuccess: report: " + report.getDate());
                                    Log.e(TAG, "onSuccess: report: " + (report.getImage().size() > 0 ? report.getImage().get(0) : "no image"));
                                    alReport.add(report);
                                } catch (Exception ex) {
                                    Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                }
                            }
                            ((MainActivity) mContext).setProgressCompleted();
                            setAdapter(alReport, (int) type);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ((MainActivity) mContext).setProgressCompleted();
                            setAdapter(alReport, (int) type);
                            Log.e(TAG, "onFailure: " + e.getMessage());
                        }
                    });
        } else if (type == CS.LAB) {
            flReportAdmin.setVisibility(View.GONE);
            flReport.setVisibility(View.VISIBLE);
            MainActivity.NFCReportEnabled = true;

            ((MainActivity) mContext).setInProgress();

            Log.e(TAG, "onCreateView: firebaseUser: " + firebaseUser.getUid());
            db.collection(CS.LabAssistant)
                    .whereEqualTo(CS.userid, firebaseUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                Log.e(TAG, "onSuccess: " + doc.getString(CS.labid));
                                db.collection(CS.Report)
                                        .orderBy(CS.date, Query.Direction.DESCENDING)
                                        .whereEqualTo(CS.labid, doc.getString(CS.labid))
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                alReport.clear();
                                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                                    try {
                                                        Report report = documentSnapshot.toObject(Report.class);
                                                        Log.e(TAG, "onSuccess: " + report.getPatientid());
                                                        alReport.add(report);
                                                    } catch (Exception ex) {
                                                        Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                                    }
                                                }
                                                ((MainActivity) mContext).setProgressCompleted();
                                                setAdapter(alReport, (int) type);

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                ((MainActivity) mContext).setProgressCompleted();
                                                setAdapter(alReport, (int) type);
                                                Log.e(TAG, "onFailure: " + e.getMessage());
                                            }
                                        });
                                break;
                            }

                        }
                    });
        } else if (type == CS.PATIENT) {
            flReportAdmin.setVisibility(View.GONE);
            flReport.setVisibility(View.VISIBLE);
            MainActivity.NFCReportEnabled = true;

            ((MainActivity) mContext).setInProgress();
            db.collection(CS.Patient)
                    .whereEqualTo(CS.userid, firebaseUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot dsPatient : queryDocumentSnapshots.getDocuments()) {
                                ((MainActivity) mContext).setInProgress();
                                db.collection(CS.Report)
                                        .orderBy(CS.date, Query.Direction.DESCENDING)
                                        .whereEqualTo(CS.patientid, dsPatient.getString(CS.patientid))
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                alReport.clear();
                                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                                    try {
                                                        Report report = documentSnapshot.toObject(Report.class);
                                    /*History history = new History(documentSnapshot.getString(CS.doctorid), documentSnapshot.getString(CS.patientid),
                                            documentSnapshot.getString(CS.reportid), documentSnapshot.getString(CS.area), documentSnapshot.getString(CS.disease),
                                            (ArrayList<String>) documentSnapshot.get(CS.medicine), (ArrayList<String>) documentSnapshot.get(CS.symptoms), (ArrayList<String>) documentSnapshot.get(CS.vigilance),
                                            documentSnapshot.getTimestamp(CS.date).toDate());*/
                                                        Log.e(TAG, "onSuccess: report: " + report.getLabid());
                                                        Log.e(TAG, "onSuccess: report: " + report.getReportid());
                                                        Log.e(TAG, "onSuccess: report: " + report.getType());
                                                        Log.e(TAG, "onSuccess: report: " + report.getDate());
                                                        Log.e(TAG, "onSuccess: report: " + (report.getImage().size() > 0 ? report.getImage().get(0) : "no image"));
                                                        alReport.add(report);
                                                    } catch (Exception ex) {
                                                        Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                                    }
                                                }
                                                ((MainActivity) mContext).setProgressCompleted();
                                                setAdapter(alReport, (int) type);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                ((MainActivity) mContext).setProgressCompleted();
                                                setAdapter(alReport, (int) type);
                                                Log.e(TAG, "onFailure: " + e.getMessage());
                                            }
                                        });
                                break;
                            }
                        }
                    });
        } else if (type == CS.DOCTOR) {
            pd = new ProgressDialog(mContext);
            pd.setMessage("Waiting for NFC...");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
            MainActivity.NFCReportEnabled = true;
            pd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ((MainActivity)mContext).nav_view.getMenu().performIdentifierAction(R.id.navPatient, 0);
                    ((MainActivity)mContext).nav_view.setCheckedItem(R.id.navPatient);
                }
            });
        }
    }

    private void setAdapter(ArrayList<Report> alReport, int type) {
        rvReport.setVisibility(View.GONE);
        llNoData.setVisibility(View.GONE);
        switch (type) {
            case CS.PATIENT:
            case CS.LAB:
                if (alReport.size() > 0) {
                    rvReport.setVisibility(View.VISIBLE);
                    llNoData.setVisibility(View.GONE);
                    Log.e(TAG, "onSuccess: " + alReport.size());
                    ReportAdapter reportAdapter = new ReportAdapter(mContext, alReport, type);
                    rvReport.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                    rvReport.setAdapter(reportAdapter);
                } else {
                    rvReport.setVisibility(View.GONE);
                    llNoData.setVisibility(View.VISIBLE);
                }
                break;
            case CS.ADMIN:
                if (alReport.size() > 0) {
                    rvReportAdmin.setVisibility(View.VISIBLE);
                    llNoDataAdmin.setVisibility(View.GONE);
                    Log.e(TAG, "onSuccess: " + alReport.size());
                    ReportAdapter reportAdapter = new ReportAdapter(mContext, alReport, type);
                    rvReportAdmin.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                    rvReportAdmin.setAdapter(reportAdapter);
                } else {
                    rvReportAdmin.setVisibility(View.GONE);
                    llNoDataAdmin.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onResume() {
        loadData();
        super.onResume();
    }

    @Override
    public void onDetach() {
        MainActivity.NFCReportEnabled = false;
        super.onDetach();
    }

    @Override
    public void onPause() {
        if (pd != null) {
            pd.dismiss();
        }
        super.onPause();
    }
}
