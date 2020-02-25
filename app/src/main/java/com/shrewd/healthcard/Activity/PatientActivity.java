package com.shrewd.healthcard.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.shrewd.healthcard.Adapter.HistoryAdapter;
import com.shrewd.healthcard.ModelClass.History;
import com.shrewd.healthcard.ModelClass.Patient;
import com.shrewd.healthcard.ModelClass.User;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.Utilities.CS;
import com.shrewd.healthcard.Utilities.CU;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PatientActivity extends AppCompatActivity {

    private static final String TAG = "PatientActivity";
    private RecyclerView rvHistory;
    private LinearLayout llNoData;
    private Context mContext;
    private FrameLayout flProgressbar;
    private CoordinatorLayout crdPatient;
    private ArrayList<History> alHistory = new ArrayList<>();
    private FloatingActionButton fabAdd;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        mContext = PatientActivity.this;
        flProgressbar = (FrameLayout) findViewById(R.id.flProgressbar);
        crdPatient = (CoordinatorLayout) findViewById(R.id.crdPatient);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);

        Intent intent = getIntent();
        String id = intent.getStringExtra(CS.userid);

        if (id == null)
            return;

        //region set Actionbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Patient");
        }

        rvHistory = (RecyclerView) findViewById(R.id.rvHistory);
        llNoData = (LinearLayout) findViewById(R.id.llNoData);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.e(TAG, "onCreate: " + id);
        db.collection(CS.User)
                .document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            user = documentSnapshot.toObject(User.class);
                            Log.e(TAG, "onSuccess: " + documentSnapshot.getString(CS.name));
                            ActionBar actionBar = getSupportActionBar();
                            if (actionBar != null) {
                                actionBar.setDisplayHomeAsUpEnabled(true);
                                actionBar.setTitle(user.getName());
                            }
                        }
                    }
                });

        db.collection(CS.Patient)
                .whereEqualTo(CS.userid, id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot dsPatient : queryDocumentSnapshots.getDocuments()) {
                            db.collection(CS.History)
                                    .orderBy(CS.date, Query.Direction.DESCENDING)
                                    .whereEqualTo(CS.patientid, dsPatient.getString(CS.patientid))
//                                    .whereEqualTo(CS.doctorid, firebaseUser.getUid())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            Log.e(TAG, "onSuccess: " + queryDocumentSnapshots.size());
                                            alHistory.clear();
                                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                                try {
                                                    Log.e(TAG, "onSuccess: doctorid: " + documentSnapshot.getString(CS.doctorid));
                                                    Log.e(TAG, "onSuccess: patientid: " + documentSnapshot.getString(CS.patientid));
                                                    Log.e(TAG, "onSuccess: getUid: " + firebaseUser.getUid());
                                                    Log.e(TAG, "onSuccess: id: " + documentSnapshot.getId());
                                                    /*History history = new History(documentSnapshot.getString(CS.doctorid), documentSnapshot.getString(CS.patientid),
                                                            documentSnapshot.getString(CS.reportid), documentSnapshot.getString(CS.area), documentSnapshot.getString(CS.disease),
                                                            (ArrayList<String>) documentSnapshot.get(CS.medicine), (ArrayList<String>) documentSnapshot.get(CS.symptoms), (ArrayList<String>) documentSnapshot.get(CS.vigilance),
                                                            documentSnapshot.getTimestamp(CS.date).toDate(), documentSnapshot.getString(CS.reportsuggestion));*/
                                                    History history = documentSnapshot.toObject(History.class);
                                                    alHistory.add(history);
                                                } catch (Exception ex) {
                                                    Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                                }
                                            }
                                            setProgressCompleted();
                                            setAdapter(alHistory);

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            setProgressCompleted();
                                            Log.e(TAG, "onFailure: " + e.getMessage());
                                        }
                                    });
                        }
                    }
                });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dg = new Dialog(mContext);
                dg.setContentView(R.layout.dg_new_patient);
                TextInputEditText etDisease = dg.findViewById(R.id.etDisease);
                TextInputEditText etMedicines = dg.findViewById(R.id.etMedicines);
                TextInputEditText etSymptoms = dg.findViewById(R.id.etSymptoms);
                TextInputEditText etVigilance = dg.findViewById(R.id.etVigilance);
                TextInputEditText etAllergies = dg.findViewById(R.id.etAllergies);
                TextInputEditText etReportSuggestion = dg.findViewById(R.id.etReportSuggestion);

                MaterialButton btnSubmit = dg.findViewById(R.id.btnSubmit);
                SpinKitView progressBar = dg.findViewById(R.id.progressBar);

                Window window = dg.getWindow();
                if (window != null) {
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(getDrawable(R.drawable.bg_dg_newuser));
                    window.setGravity(Gravity.BOTTOM);
                }
                dg.setCanceledOnTouchOutside(false);

                progressBar.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.VISIBLE);

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (CU.isNullOrEmpty(etDisease)) {
                            etDisease.setError("Field required");
                            etDisease.setText("");
                            etDisease.requestFocus();
                            return;
                        }
                        if (CU.isNullOrEmpty(etMedicines)) {
                            etMedicines.setError("Field required");
                            etMedicines.setText("");
                            etMedicines.requestFocus();
                            return;
                        }
                        if (CU.isNullOrEmpty(etSymptoms)) {
                            etSymptoms.setError("Field required");
                            etSymptoms.setText("");
                            etSymptoms.requestFocus();
                            return;
                        }
                        if (CU.isNullOrEmpty(etVigilance)) {
                            etVigilance.setError("Field required");
                            etVigilance.setText("");
                            etVigilance.requestFocus();
                            return;
                        }

                        progressBar.setVisibility(View.VISIBLE);
                        btnSubmit.setVisibility(View.GONE);

                        ArrayList<String> alMedicine = new ArrayList<>(Arrays.asList(etMedicines.getText().toString().trim().split("\n")));
                        ArrayList<String> alSymptoms = new ArrayList<>(Arrays.asList(etSymptoms.getText().toString().trim().split("\n")));
                        ArrayList<String> alVigilance = new ArrayList<>(Arrays.asList(etVigilance.getText().toString().trim().split("\n")));

                        db.collection(CS.Doctor)
                                .whereEqualTo(CS.userid, firebaseUser.getUid())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (DocumentSnapshot dsDoctor : queryDocumentSnapshots.getDocuments()) {

                                            db.collection(CS.Patient)
                                                    .whereEqualTo(CS.userid, id)
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                                            for (DocumentSnapshot dsPatient : queryDocumentSnapshots.getDocuments()) {

                                                                String city = null, state = null, country = null;
                                                                try {
                                                                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                                                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                                                        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                                                                        double longitude = location.getLongitude();
                                                                        double latitude = location.getLatitude();
                                                                        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                                                                        List<Address> addresses = null;
                                                                        try {
                                                                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                                                        } catch (IOException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                        city = addresses.get(0).getLocality();
                                                                        state = addresses.get(0).getAdminArea();
                                                                        country = addresses.get(0).getCountryName();
                                                                        String s = (city != null ? city + ", " + state + ", " + country : "");
                                                                    }
                                                                } catch (Exception ex) {
                                                                    Log.e(TAG, "onSuccess: " + ex.getMessage());
                                                                }

                                                                History history = new History(dsDoctor.getString(CS.doctorid), dsPatient.getString(CS.patientid), "",
                                                                        (city != null ? city + ", " + state + ", " + country : user.getAddress()), etDisease.getText().toString().trim(),
                                                                        alMedicine, alSymptoms, alVigilance, new Date(System.currentTimeMillis()), etReportSuggestion.getText().toString().trim());

                                                                Patient patient = dsPatient.toObject(Patient.class);
                                                                if (patient != null) {
                                                                    patient.setAllergy(new ArrayList<>(Arrays.asList(etAllergies.getText().toString().trim().split("\n"))));
                                                                    dsPatient.getReference().set(patient);
                                                                }

                                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                                db.collection(CS.History)
                                                                        .add(history)
                                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentReference documentReference) {
                                                                                Toast.makeText(mContext, "Data added successfully", Toast.LENGTH_SHORT).show();
                                                                                dg.dismiss();
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(mContext, "Failed to add data", Toast.LENGTH_SHORT).show();
                                                                                dg.dismiss();
                                                                            }
                                                                        });

                                                                break;
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });


                        dg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {

                                db.collection(CS.Patient)
                                        .whereEqualTo(CS.userid, id)
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
                                                                    Log.e(TAG, "onSuccess: " + queryDocumentSnapshots.size());
                                                                    alHistory.clear();
                                                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                                                        try {
                                                                            Log.e(TAG, "onSuccess: doctorid: " + documentSnapshot.getString(CS.doctorid));
                                                                            Log.e(TAG, "onSuccess: patientid: " + documentSnapshot.getString(CS.patientid));
                                                                            Log.e(TAG, "onSuccess: getUid: " + firebaseUser.getUid());

                                                                            Log.e(TAG, "onSuccess: id: " + documentSnapshot.getId());
                                                                            History history = new History(documentSnapshot.getString(CS.doctorid), documentSnapshot.getString(CS.patientid),
                                                                                    documentSnapshot.getString(CS.reportid), documentSnapshot.getString(CS.area), documentSnapshot.getString(CS.disease),
                                                                                    (ArrayList<String>) documentSnapshot.get(CS.medicine), (ArrayList<String>) documentSnapshot.get(CS.symptoms), (ArrayList<String>) documentSnapshot.get(CS.vigilance),
                                                                                    documentSnapshot.getTimestamp(CS.date).toDate(), etReportSuggestion.getText().toString().trim());
                                                                            alHistory.add(history);
                                                                        } catch (Exception ex) {
                                                                            Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                                                        }
                                                                    }
                                                                    setProgressCompleted();
                                                                    setAdapter(alHistory);

                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    setAdapter(alHistory);
                                                                    setProgressCompleted();
                                                                    Log.e(TAG, "onFailure: " + e.getMessage());
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }
                        });

                    }
                });

                dg.show();
            }
        });
        setInProgress();


    }

    public void setInProgress() {
        crdPatient.setActivated(false);
        CU.showProgressBar(flProgressbar);
    }

    public void setProgressCompleted() {
        crdPatient.setActivated(true);
        CU.hideProgressBar(flProgressbar);
    }

    private void setAdapter(ArrayList<History> alHistory) {
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
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}