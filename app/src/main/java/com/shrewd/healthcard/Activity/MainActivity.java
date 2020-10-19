package com.shrewd.healthcard.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shrewd.healthcard.ModelClass.Doctor;
import com.shrewd.healthcard.ModelClass.Government;
import com.shrewd.healthcard.ModelClass.Hospital;
import com.shrewd.healthcard.ModelClass.LabAssistant;
import com.shrewd.healthcard.ModelClass.Laboratory;
import com.shrewd.healthcard.ModelClass.Patient;
import com.shrewd.healthcard.ModelClass.User;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.Utilities.CS;
import com.shrewd.healthcard.Utilities.CU;
import com.shrewd.healthcard.databinding.ActivityMainBinding;
import com.shrewd.healthcard.databinding.DgNewUserBinding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101;
    private static final int PICK_VERIFICATION_PROOF = 102;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 103;
    private static final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 104;
    private static final int MY_PERMISSION_PERMISSION_ID = 105;
    public static boolean fromReport = false;
    public ActionBarDrawerToggle toggle;
    private Context mContext;
    private Uri filePathUri;
    private boolean isDialogDisplayed = false;
    private boolean isWrite = false;
    public NfcAdapter mNfcAdapter;
    public static boolean NFCPatientEnabled = false;
    public static boolean NFCReportEnabled = false;
    private Dialog dgNewUser;
    private LocationManager locationManager;
    private FusedLocationProviderClient mFusedLocationClient;
    public ActivityMainBinding binding;
    private SpinKitView progressBarHeader;
    private TextView tvUsername;
    private TextView tvFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = MainActivity.this;

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.navView, navController);

        /*if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, MY_PERMISSIONS_REQUEST_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, MY_PERMISSIONS_REQUEST_COARSE_LOCATION)) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "onRequestPermissionsResult: " + "cannot get location");
                return;
            } else {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = null;
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    onLocationChanged(location);
                }
            }
        }*/

        /*LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!statusOfGPS) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }*/

        binding.ivNavigation.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                if (binding.drawerLayout.isDrawerOpen(Gravity.START)) {
                    binding.drawerLayout.closeDrawer(Gravity.START);
                } else {
                    binding.drawerLayout.openDrawer(Gravity.START);
                }
            }
        });

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        HashMap<String, String> map = new HashMap<>();
                        map.put("token", token);
                        db.collection(CS.Token)
                                .document(token)
                                .set(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.e(TAG, "onSuccess: " + token);
                                    }
                                });
                        // Log and toast
                        String msg = "getString(R.string.msg_token_fmt, token)";
                        Log.e(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        FirebaseMessaging.getInstance().subscribeToTopic(CS.pushNotification);
        FirebaseMessaging.getInstance().subscribeToTopic(CS.messageNotification);

        try {
            initNFC();
        } catch (Exception ex) {
            Log.e(TAG, "onCreate: error: " + ex.getMessage());
        }

        progressBarHeader = binding.navView.getHeaderView(0).findViewById(R.id.progressBarHeader);
        tvUsername = binding.navView.getHeaderView(0).findViewById(R.id.tvUsername);

        /*for (int i = 0; i < nav_view.getMenu().size(); i++) {
            MenuItem menuItem = nav_view.getMenu().getItem(i);
            menuItem.setVisible(true);
        }*/

        binding.navView.getMenu().setGroupVisible(R.id.grpCategory, false);
        setInProgress();

        if (!CU.isNetworkEnabled(mContext)) {
            Toast.makeText(mContext, "No Internet!", Toast.LENGTH_SHORT).show();
            Dialog dgNoInternet = new Dialog(mContext);
            dgNoInternet.setContentView(R.layout.dg_no_internet);
            MaterialButton btnMobileData = dgNoInternet.findViewById(R.id.btnMobileData);
            MaterialButton btnWifi = dgNoInternet.findViewById(R.id.btnWifi);
            btnWifi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
            btnMobileData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            });
            Window window = dgNoInternet.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER);
                window.setBackgroundDrawable(getDrawable(R.drawable.bg_dg_rounded));
            }
            dgNoInternet.setCancelable(false);
            dgNoInternet.show();
            return;
        }

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        db.collection(CS.User)
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        setProgressCompleted();
                        if (documentSnapshot.exists() && documentSnapshot.getLong(CS.type) != -1) {
                            Log.e(TAG, "onSuccess: " + documentSnapshot.getId() + " " + documentSnapshot.getBoolean(CS.verified));
                            try {
                                long type = documentSnapshot.getLong(CS.type);
                                if (type != CS.ADMIN && !documentSnapshot.getBoolean(CS.verified)) {
                                    final Dialog dgVerificationPending = new Dialog(mContext);
                                    dgVerificationPending.setContentView(R.layout.dg_pending_verification);
                                    MaterialButton btnOk = dgVerificationPending.findViewById(R.id.btnOk);
                                    MaterialButton btnLogout = dgVerificationPending.findViewById(R.id.btnLogout);

                                    ImageView ivVerification = dgVerificationPending.findViewById(R.id.ivVerification);
                                    Glide.with(mContext).load(CS.verificationGIF).placeholder(R.mipmap.pending).into(ivVerification);
                                    btnOk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dgVerificationPending.dismiss();
                                            finish();
                                        }
                                    });

                                    btnLogout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            FirebaseAuth.getInstance().signOut();
                                            startActivity(new Intent(mContext, LoginActivity.class));
                                            finish();
                                        }
                                    });

                                    Window window = dgVerificationPending.getWindow();
                                    if (window != null) {
                                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        window.setBackgroundDrawable(getDrawable(R.drawable.bg_dg_rounded));
                                        window.setGravity(Gravity.CENTER);
                                    }
                                    dgVerificationPending.setCancelable(false);
                                    dgVerificationPending.show();
                                } else {
                                    SharedPreferences.Editor editor = getSharedPreferences("GC", MODE_PRIVATE).edit();
                                    editor.putLong(CS.type, type);
                                    editor.apply();
                                    tvUsername.setText(documentSnapshot.getString(CS.name));
                                    setIcon(type);
                                    setMenu(type);
                                    CU.navigateTo(mContext, R.id.homeFragment);
                                }
                            } catch (Exception ex) {
                                Log.e(TAG, "onSuccess: " + ex.getMessage());
                            }

                        } else {
                            if (documentSnapshot.exists() && documentSnapshot.getLong(CS.type) == -1) {
                                Toast.makeText(mContext, "Your profile is rejected due to invalid verification proof\nPlease submit valid proof", Toast.LENGTH_LONG).show();
                            }
                            dgNewUser = new Dialog(mContext);
                            filePathUri = null;
                            DgNewUserBinding bndNewUser = DgNewUserBinding.inflate(getLayoutInflater());
                            dgNewUser.setContentView(bndNewUser.getRoot());
                            Window window = dgNewUser.getWindow();
                            if (window != null) {
                                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                window.setGravity(Gravity.BOTTOM);
                                window.setBackgroundDrawable(getDrawable(R.drawable.bg_dg_newuser));
                            }

                            dgNewUser.show();

                            dgNewUser.setCancelable(false);
                            tvFile = dgNewUser.findViewById(R.id.tvFile);
                            MaterialButton btnUpload = dgNewUser.findViewById(R.id.btnUpload);
                            MaterialButton btnSubmit = dgNewUser.findViewById(R.id.btnSubmit);
                            MaterialButton btnLogout = dgNewUser.findViewById(R.id.btnLogout);
                            btnLogout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(mContext, LoginActivity.class));
                                    finish();
                                }
                            });

                            bndNewUser.llNewLab.setVisibility(View.GONE);
                            bndNewUser.rlLab.setVisibility(View.VISIBLE);

                            bndNewUser.fabAddLab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    bndNewUser.llNewLab.setVisibility(View.VISIBLE);
                                    bndNewUser.rlLab.setVisibility(View.GONE);
                                }
                            });

                            bndNewUser.fabLab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    bndNewUser.llNewLab.setVisibility(View.GONE);
                                    bndNewUser.rlLab.setVisibility(View.VISIBLE);
                                }
                            });

                            bndNewUser.llNewHospital.setVisibility(View.GONE);
                            bndNewUser.rlHospital.setVisibility(View.VISIBLE);

                            bndNewUser.fabAddHospital.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    bndNewUser.llNewHospital.setVisibility(View.VISIBLE);
                                    bndNewUser.rlHospital.setVisibility(View.GONE);
                                }
                            });

                            bndNewUser.fabHospital.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    bndNewUser.llNewHospital.setVisibility(View.GONE);
                                    bndNewUser.rlHospital.setVisibility(View.VISIBLE);
                                }
                            });

                            ArrayList<Hospital> alHospital = new ArrayList<Hospital>();
                            db.collection(CS.Hospital)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            alHospital.clear();
                                            alHospital.add(new Hospital("-- Select Hospital --", -1, null, null));
                                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                                Hospital hospital = doc.toObject(Hospital.class);
                                                alHospital.add(hospital);

                                                ArrayAdapter adapter =
                                                        new ArrayAdapter(
                                                                mContext,
                                                                R.layout.dropdown_menu_popup_item,
                                                                R.id.tvItem,
                                                                alHospital);

                                                bndNewUser.spnHospital.setAdapter(adapter);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(mContext, "Error fetching hospitals!", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            ArrayList<Laboratory> alLab = new ArrayList<Laboratory>();
                            db.collection(CS.Laboratory)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            alLab.clear();
                                            alLab.add(new Laboratory("-- Select Laboratory --", -1, "", ""));
                                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                                Laboratory lab = doc.toObject(Laboratory.class);
                                                alLab.add(lab);

                                                ArrayAdapter adapterLab =
                                                        new ArrayAdapter(
                                                                mContext,
                                                                R.layout.dropdown_menu_popup_item,
                                                                R.id.tvItem,
                                                                alLab);

                                                bndNewUser.spnLab.setAdapter(adapterLab);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(mContext, "Error fetching hospitals!", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                            btnUpload.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getFileChooserIntent();
                                }
                            });

                            btnSubmit.setVisibility(View.VISIBLE);
                            bndNewUser.progressBar.setVisibility(View.GONE);

                            btnSubmit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    bndNewUser.etAddress.clearFocus();
                                    if (CU.isNullOrEmpty(bndNewUser.etFullName)) {
                                        bndNewUser.etFullName.setError("Field Required");
                                        bndNewUser.etFullName.requestFocus();
                                        return;
                                    }
                                    if (CU.isNullOrEmpty(bndNewUser.etDOB)) {
                                        bndNewUser.etDOB.setError("Field Required");
                                        bndNewUser.etDOB.requestFocus();
                                        return;
                                    } else if (bndNewUser.etDOB.getText() != null && !CU.isDate(bndNewUser.etDOB.getText().toString())) {
                                        bndNewUser.etDOB.setError("Badly formatted date");
                                        return;
                                    }
                                    if (CU.isNullOrEmpty(bndNewUser.etMobile)) {
                                        bndNewUser.etMobile.setError("Field Required");
                                        bndNewUser.etMobile.requestFocus();
                                        return;
                                    } else if (bndNewUser.etMobile.getText() != null && !CU.isValidMobile(bndNewUser.etMobile.getText().toString())) {
                                        bndNewUser.etMobile.setError("Invalid mobile no.");
                                    }
                                    if (CU.isNullOrEmpty(bndNewUser.etAddress)) {
                                        bndNewUser.etAddress.setError("Field Required");
                                        bndNewUser.etAddress.requestFocus();
                                        return;
                                    }
                                    if (bndNewUser.rgGender.getCheckedRadioButtonId() == RadioGroup.NO_ID && bndNewUser.rgGender.getChildCount() > 0) {
                                        ((MaterialRadioButton) bndNewUser.rgGender.getChildAt(0)).setError("Gender selection required");
                                        bndNewUser.rgGender.requestFocus();
                                        return;
                                    }
                                    if (bndNewUser.rgDesignation.getCheckedRadioButtonId() == RadioGroup.NO_ID && bndNewUser.rgDesignation.getChildCount() > 0) {
                                        ((MaterialRadioButton) bndNewUser.rgDesignation.getChildAt(0)).setError("Designation selection required");
                                        bndNewUser.rgDesignation.requestFocus();
                                        return;
                                    }

                                    switch (bndNewUser.rgDesignation.getCheckedRadioButtonId()) {
                                        case R.id.rbtnDoctor:
                                            if (CU.isNullOrEmpty(bndNewUser.etDoctorType)) {
                                                bndNewUser.etDoctorType.setError("Field required!");
                                                bndNewUser.etDoctorType.requestFocus();
                                                return;
                                            }

                                            if (CU.isNullOrEmpty(bndNewUser.etLicenseNo)) {
                                                bndNewUser.etLicenseNo.setError("Field required!");
                                                bndNewUser.etLicenseNo.requestFocus();
                                                return;
                                            }

                                            if (bndNewUser.rlHospital.getVisibility() == View.VISIBLE) {
                                                if (bndNewUser.spnHospital.getSelectedItemPosition() == 0) {
                                                    Toast.makeText(mContext, "Select Hospital first!", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            } else {
                                                if (CU.isNullOrEmpty(bndNewUser.etHospitalName)) {
                                                    bndNewUser.etHospitalName.setError("Field required!");
                                                    bndNewUser.etHospitalName.requestFocus();
                                                    return;
                                                }
                                                if (CU.isNullOrEmpty(bndNewUser.etHospitalContactNo)) {
                                                    bndNewUser.etHospitalContactNo.setError("Field required!");
                                                    bndNewUser.etHospitalContactNo.requestFocus();
                                                    return;
                                                }
                                                if (CU.isNullOrEmpty(bndNewUser.etHospitalArea)) {
                                                    bndNewUser.etHospitalArea.setError("Field required!");
                                                    bndNewUser.etHospitalArea.requestFocus();
                                                    return;
                                                }
                                            }

                                            break;
                                        case R.id.rbtnLabAssistant:
                                            if (bndNewUser.rlLab.getVisibility() == View.VISIBLE) {
                                                if (bndNewUser.spnLab.getSelectedItemPosition() == 0) {
                                                    Toast.makeText(mContext, "Select lab first!", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            } else {
                                                if (CU.isNullOrEmpty(bndNewUser.etLabName)) {
                                                    bndNewUser.etLabName.setError("Field required!");
                                                    bndNewUser.etLabName.requestFocus();
                                                    return;
                                                }
                                                if (CU.isNullOrEmpty(bndNewUser.etLabContactNo)) {
                                                    bndNewUser.etLabContactNo.setError("Field required!");
                                                    bndNewUser.etLabContactNo.requestFocus();
                                                    return;
                                                }
                                                if (CU.isNullOrEmpty(bndNewUser.etLabArea)) {
                                                    bndNewUser.etLabArea.setError("Field required!");
                                                    bndNewUser.etLabArea.requestFocus();
                                                    return;
                                                }
                                            }
                                            break;
                                        case R.id.rbtnGovernment:
                                            if (CU.isNullOrEmpty(bndNewUser.etGovernmentArea)) {
                                                bndNewUser.etGovernmentArea.setError("Field required!");
                                                bndNewUser.etGovernmentArea.requestFocus();
                                                return;
                                            }
                                            break;
                                    }

                                    if ((CU.isNullOrEmpty(tvFile) || tvFile.getText().toString().equals("Choose File") || filePathUri == null) && bndNewUser.rgDesignation.getCheckedRadioButtonId() != R.id.rbtnPatient) {
                                        tvFile.setError("Verification Proof required");
                                        tvFile.requestFocus();
                                        Toast.makeText(mContext, "Please upload a proof!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    db.collection(CS.Doctor)
                                            .whereEqualTo(CS.licenseno, bndNewUser.etLicenseNo.getText().toString().trim())
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    if (queryDocumentSnapshots.size() > 0) {
                                                        bndNewUser.etLicenseNo.setError("License No");
                                                        bndNewUser.etLicenseNo.requestFocus();
                                                    } else {
                                                        btnSubmit.setVisibility(View.GONE);
                                                        bndNewUser.progressBar.setVisibility(View.VISIBLE);

                                                        long type = CS.PATIENT, Gender = CS.NA;
                                                        if (bndNewUser.rgGender.getCheckedRadioButtonId() == R.id.rbtnMale) {
                                                            Gender = CS.Male;
                                                        } else if (bndNewUser.rgGender.getCheckedRadioButtonId() == R.id.rbtnFemale) {
                                                            Gender = CS.Female;
                                                        }

                                                        if (bndNewUser.rgDesignation.getCheckedRadioButtonId() == R.id.rbtnDoctor) {
                                                            type = CS.DOCTOR;
                                                        } else if (bndNewUser.rgDesignation.getCheckedRadioButtonId() == R.id.rbtnLabAssistant) {
                                                            type = CS.LAB;
                                                        } else if (bndNewUser.rgDesignation.getCheckedRadioButtonId() == R.id.rbtnGovernment) {
                                                            type = CS.GOVERNMENT;
                                                        }

                                                        try {
                                                            final long finalGender = Gender;
                                                            final long finalType = type;
                                                            if (bndNewUser.rgDesignation.getCheckedRadioButtonId() == R.id.rbtnPatient) {
                                                                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                                                String city = null, state = null, country = null;
                                                                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                                                }
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
                                                                Log.e(TAG, "onClick: " + s);

                                                                User user = new User(firebaseUser.getUid(), bndNewUser.etFullName.getText().toString(), s,
                                                                        firebaseUser.getEmail(), "", finalType, finalGender, Long.parseLong(bndNewUser.etMobile.getText().toString()),
                                                                        false, CU.getDate(bndNewUser.etDOB.getText().toString()), new Date(System.currentTimeMillis()));

                                                                Patient patient = new Patient(null, null, user.getUserid(), String.valueOf(System.currentTimeMillis()));
                                                                db.collection(CS.User)
                                                                        .document(firebaseUser.getUid())
                                                                        .set(user);

                                                                db.collection(CS.Patient)
                                                                        .document(patient.getPatientid())
                                                                        .set(patient)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                dgNewUser.dismiss();
                                                                                Toast.makeText(mContext, "New user registered", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                dgNewUser.dismiss();
                                                                                Toast.makeText(mContext, "Failed to upload data", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            } else {
                                                                Log.e(TAG, "onClick: " + MimeTypeMap.getFileExtensionFromUrl(filePathUri.toString()));
                                                                final StorageReference sRef = FirebaseStorage.getInstance().getReference("Users/" + System.currentTimeMillis() + "." + MimeTypeMap.getFileExtensionFromUrl(filePathUri.toString()));
                                                                sRef.putFile(filePathUri)
                                                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                    @Override
                                                                                    public void onSuccess(Uri uri) {
                                                                                        Log.e("successfully get uri", "onSuccess: " + uri);

                                                                                        User user = new User(firebaseUser.getUid(), bndNewUser.etFullName.getText().toString(), bndNewUser.etAddress.getText().toString(),
                                                                                                firebaseUser.getEmail(), uri.toString(), finalType, finalGender, Long.parseLong(bndNewUser.etMobile.getText().toString()),
                                                                                                false, CU.getDate(bndNewUser.etDOB.getText().toString()), new Date(System.currentTimeMillis()));

                                                                                        db.collection(CS.User).document(firebaseUser.getUid())
                                                                                                .set(user)
                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                        Log.e(TAG, "onSuccess: " + firebaseUser.getUid());
                                                                                                    }
                                                                                                });

                                                                                        switch (bndNewUser.rgDesignation.getCheckedRadioButtonId()) {
                                                                                            case R.id.rbtnDoctor:
                                                                                                Hospital hospital = new Hospital("", -1, "", "");
                                                                                                if (bndNewUser.rlHospital.getVisibility() == View.VISIBLE) {
                                                                                                    try {
                                                                                                        hospital = (Hospital) bndNewUser.spnHospital.getSelectedItem();
                                                                                                    } catch (Exception ex) {
                                                                                                        Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                                                                                    }
                                                                                                } else {
                                                                                                    hospital = new Hospital(bndNewUser.etAddress.getText().toString().trim(),
                                                                                                            Long.valueOf(bndNewUser.etHospitalContactNo.getText().toString().trim()),
                                                                                                            String.valueOf(System.currentTimeMillis()),
                                                                                                            bndNewUser.etHospitalName.getText().toString().trim());
                                                                                                }

                                                                                                Doctor doctor = new Doctor(String.valueOf(System.currentTimeMillis()), bndNewUser.etDoctorType.getText().toString().trim(),
                                                                                                        hospital.getHospitalid(), user.getUserid(), bndNewUser.etLicenseNo.getText().toString().trim());

                                                                                                db.collection(CS.Hospital)
                                                                                                        .document(hospital.getHospitalid())
                                                                                                        .set(hospital);

                                                                                                db.collection(CS.Doctor)
                                                                                                        .document(doctor.getDoctorid())
                                                                                                        .set(doctor)
                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                dgNewUser.dismiss();
                                                                                                                Toast.makeText(mContext, "New user registered", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        })
                                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                                            @Override
                                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                                dgNewUser.dismiss();
                                                                                                                Toast.makeText(mContext, "Failed to upload data", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        });
                                                                                                break;
                                                                                            case R.id.rbtnLabAssistant:
                                                                                                Laboratory laboratory = new Laboratory("", -1, "", "");
                                                                                                if (bndNewUser.rlLab.getVisibility() == View.VISIBLE) {
                                                                                                    laboratory = (Laboratory) bndNewUser.spnLab.getSelectedItem();
                                                                                                } else {
                                                                                                    laboratory = new Laboratory(bndNewUser.etAddress.getText().toString().trim(),
                                                                                                            Long.valueOf(bndNewUser.etLabContactNo.getText().toString().trim()),
                                                                                                            String.valueOf(System.currentTimeMillis()),
                                                                                                            bndNewUser.etLabName.getText().toString().trim());
                                                                                                }
                                                                                                LabAssistant labAssistant = new LabAssistant(String.valueOf(System.currentTimeMillis()),
                                                                                                        laboratory.getLabid(), user.getUserid());

                                                                                                db.collection(CS.Laboratory)
                                                                                                        .document(laboratory.getLabid())
                                                                                                        .set(laboratory);

                                                                                                db.collection(CS.LabAssistant)
                                                                                                        .document(labAssistant.getLabassistantid())
                                                                                                        .set(labAssistant)
                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                dgNewUser.dismiss();
                                                                                                                Toast.makeText(mContext, "New user registered", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        })
                                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                                            @Override
                                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                                dgNewUser.dismiss();
                                                                                                                Toast.makeText(mContext, "Failed to upload data", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        });
                                                                                                break;
                                                                                            case R.id.rbtnGovernment:
                                                                                                Government government = new Government(String.valueOf(System.currentTimeMillis()),
                                                                                                        user.getUserid(), bndNewUser.etGovernmentArea.getText().toString().trim());
                                                                                                db.collection(CS.Government)
                                                                                                        .document(government.getGovernmentid())
                                                                                                        .set(government)
                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                dgNewUser.dismiss();
                                                                                                                Toast.makeText(mContext, "New user registered", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        })
                                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                                            @Override
                                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                                dgNewUser.dismiss();
                                                                                                                Toast.makeText(mContext, "Failed to upload data", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        });
                                                                                                break;
                                                                                        }

                                                                                    }
                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Toast.makeText(mContext, "Failed to get url: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                        dgNewUser.dismiss();
                                                                                        Log.e("Failed to get url", "onFailure: ");
                                                                                    }
                                                                                });
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                dgNewUser.dismiss();
                                                                                Toast.makeText(mContext, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                Log.e("upload failure", "onFailure: " + e.getMessage());
                                                                            }
                                                                        });
                                                            }

                                                        } catch (Exception ex) {
                                                            Log.e(TAG, "onClick: error: " + ex.getMessage());
                                                        }
                                                    }
                                                }
                                            });
                                }
                            });

                            bndNewUser.rgDesignation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                    ((MaterialRadioButton) bndNewUser.rgDesignation.getChildAt(0)).setError(null);
                                    bndNewUser.llDoctor.setVisibility(View.GONE);
                                    bndNewUser.llLabAssistant.setVisibility(View.GONE);
                                    bndNewUser.llGovernment.setVisibility(View.GONE);
                                    bndNewUser.rlVerification.setVisibility(View.VISIBLE);
                                    if (checkedId == R.id.rbtnDoctor) {
                                        bndNewUser.llDoctor.setVisibility(View.VISIBLE);
                                    } else if (checkedId == R.id.rbtnLabAssistant) {
                                        bndNewUser.llLabAssistant.setVisibility(View.VISIBLE);
                                    } else if (checkedId == R.id.rbtnGovernment) {
                                        bndNewUser.llGovernment.setVisibility(View.VISIBLE);
                                    } else {
                                        bndNewUser.rlVerification.setVisibility(View.GONE);
                                    }
                                }
                            });

                            bndNewUser.rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                    ((MaterialRadioButton) bndNewUser.rgGender.getChildAt(0)).setError(null);
                                }
                            });

                            bndNewUser.etDOB.setShowSoftInputOnFocus(false);
                            final String dpTitle = "Select your birthdate";
                            bndNewUser.etDOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (hasFocus)
                                        showDatePicker(bndNewUser.etDOB, dpTitle);
                                }
                            });
                            bndNewUser.etDOB.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDatePicker(bndNewUser.etDOB, dpTitle);
                                }
                            });
                            bndNewUser.etDOB.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    showDatePicker(bndNewUser.etDOB, dpTitle);
                                    return false;
                                }
                            });

                            dgNewUser.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    btnSubmit.setVisibility(View.VISIBLE);
                                    bndNewUser.progressBar.setVisibility(View.GONE);
                                    MainActivity.this.recreate();
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setProgressCompleted();
                        Log.e(TAG, "onFailure: error: " + e.getMessage());
                    }
                });

        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                binding.drawerLayout.closeDrawer(GravityCompat.START);
                switch (item.getItemId()) {
                    case R.id.navLogout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(mContext, LoginActivity.class));
                        finish();
                        return true;
                }
                CU.navigateTo(mContext, item.getItemId());
                return true;
            }
        });

        enableCustomActionBar();
    }

    private void showDatePicker(final TextInputEditText etDate, String title) {
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText(title.equals("") ? "Select Date" : title);
        builder.setCalendarConstraints(new CalendarConstraints.Builder().setEnd(System.currentTimeMillis()).build());
        MaterialDatePicker datePicker = builder.build();

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                try {
                    etDate.setText(CU.getDate((long) selection, CS.ddMMyyyy));
                    Log.e(TAG, "onPositiveButtonClick: " + selection.getClass());
                    etDate.setError(null);
                } catch (Exception ex) {
                    Toast.makeText(mContext, "Cannot select date! Please try again", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onPositiveButtonClick: " + ex.getMessage());
                }
            }
        });
        datePicker.show(getSupportFragmentManager(), "Date of birth");
    }

    public void setInProgress() {
        CU.showProgressBar(binding.flProgressbar);
        CU.showProgressBar(progressBarHeader);
    }

    public void setProgressCompleted() {
        CU.hideProgressBar(binding.flProgressbar);
        CU.hideProgressBar(progressBarHeader);
    }

    private void setIcon(long op) {
        CircleImageView civUser = binding.navView.getHeaderView(0).findViewById(R.id.civHeaderDP);
        TextView tvDesignation = binding.navView.getHeaderView(0).findViewById(R.id.tvDesignation);
        switch ((int) op) {
            case CS.DOCTOR:
                tvDesignation.setText("Doctor");
                civUser.setImageDrawable(getDrawable(R.mipmap.doctor));
                break;
            case CS.PATIENT:
                tvDesignation.setText("Patient");
                civUser.setImageDrawable(getDrawable(R.mipmap.patient3));
                break;
            case CS.LAB:
                tvDesignation.setText("Lab Assistant");
                civUser.setImageDrawable(getDrawable(R.mipmap.lab_assistant));
                break;
            case CS.GOVERNMENT:
                tvDesignation.setText("Government");
                civUser.setImageDrawable(getDrawable(R.mipmap.government));
                break;
            case CS.ADMIN:
                tvDesignation.setText("Admin");
                civUser.setImageDrawable(getDrawable(R.drawable.verified_user));
                break;
        }
    }

    public void enableCustomActionBar() {
        toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    public void setMenu(long op) {
        Menu menu = binding.navView.getMenu();
        MenuItem navHistory = menu.findItem(R.id.historyFragment);
        MenuItem navAnalysis = menu.findItem(R.id.analysisFragment);
        MenuItem navReport = menu.findItem(R.id.reportsFragment);
        MenuItem navPatient = menu.findItem(R.id.patientFragment);
        MenuItem navHome = menu.findItem(R.id.homeFragment);
        MenuItem navSetting = menu.findItem(R.id.settingsFragment);
        MenuItem navAbout = menu.findItem(R.id.aboutFragment);
        MenuItem navLogout = menu.findItem(R.id.navLogout);
        MenuItem navVerify = menu.findItem(R.id.verifyFragment);
        navHome.setVisible(true);
        navAbout.setVisible(true);
        navSetting.setVisible(false);
        navLogout.setVisible(true);
        navVerify.setVisible(false);
        switch ((int) op) {
            case CS.DOCTOR:
                navHistory.setVisible(false);
                navAnalysis.setVisible(false);
                navReport.setVisible(true);
                navPatient.setVisible(true);
                break;
            case CS.LAB:
                navHistory.setVisible(false);
                navAnalysis.setVisible(false);
                navReport.setVisible(true);
                navPatient.setVisible(false);
                break;
            case CS.GOVERNMENT:
                navHome.setVisible(false);
                navHistory.setVisible(false);
                navAnalysis.setVisible(true);
                navReport.setVisible(false);
                navPatient.setVisible(false);
                break;
            case CS.ADMIN:
                navHistory.setVisible(true);
                navAnalysis.setVisible(true);
                navReport.setVisible(true);
                navPatient.setVisible(true);
                navVerify.setVisible(true);
                break;
            default:
                navHome.setVisible(true);
                navHistory.setVisible(true);
                navAnalysis.setVisible(false);
                navReport.setVisible(true);
                navPatient.setVisible(false);
                break;
        }
    }

    private void getFileChooserIntent() {
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)) {
            String[] mimeTypes = {"image/*"};
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            startActivityForResult(Intent.createChooser(intent, "Choose valid proof..."), PICK_VERIFICATION_PROOF);
        }
    }

    private boolean checkPermission(final String permission, final int CODE) {
        if (ContextCompat.checkSelfPermission(mContext, permission)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale((MainActivity) mContext, permission)) {

                AlertDialog.Builder builder = new AlertDialog
                        .Builder(mContext);
                builder.setMessage("• The permission is needed to upload verification proof\n"
                        + "Do you want to give permissions?");
                builder.setTitle("Permission needed");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((MainActivity) mContext,
                                new String[]{permission},
                                CODE);
                    }
                });
                builder.show();
            } else {
                ActivityCompat.requestPermissions((MainActivity) mContext,
                        new String[]{permission},
                        CODE);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_VERIFICATION_PROOF:
                    if (tvFile != null && data != null && data.getData() != null) {
                        Uri uri = data.getData();
                        filePathUri = Uri.fromFile(new File(CU.getPath(mContext, uri)));
                        tvFile.setText(uri.getPath());
                        tvFile.setError(null);
                        Log.e(TAG, "onActivityResult: " + filePathUri);
                        Log.e(TAG, "onActivityResult: extension: " + MimeTypeMap.getFileExtensionFromUrl(filePathUri.toString()));
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0) {
//                getFileChooserIntent();
                String[] mimeTypes = {"image/*"};
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(Intent.createChooser(intent, "Choose valid proof..."), PICK_VERIFICATION_PROOF);
            } else {
                Toast.makeText(mContext, "Cannot upload file, due to denied permission", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "onRequestPermissionsResult: " + "cannot get location");
                    return;
                } else {
                    Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                    onLocationChanged(location);
                }
            }
        } else if (requestCode == MY_PERMISSION_PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStart() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(mContext, LoginActivity.class));
            finish();
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (fromReport) {
            binding.navView.getMenu().performIdentifierAction(R.id.patientFragment, 0);
            binding.navView.setCheckedItem(R.id.patientFragment);
            fromReport = false;
        }

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected, tagDetected, ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    public void initNFC() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            Toast.makeText(mContext, "This device doesn't support NFC", Toast.LENGTH_SHORT).show();
//            ((Activity) mContext).finish();
            return;
        }

        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Please activate NFC and press Back to continue!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
            return;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NFCPatientEnabled) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Log.e(TAG, "onNewIntent: " + intent.getAction());
            if (tag != null) {
//            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
                Ndef ndef = Ndef.get(tag);
                try {
                    NdefMessage ndefMessage = ndef.getCachedNdefMessage();
                    if (ndefMessage != null) {
                        NdefRecord[] records = ndefMessage.getRecords();
                        if (records.length > 0) {
                            for (NdefRecord ndefRecord : records) {
                                if (ndefRecord.getPayload().length > 0) {
                                    byte[] payload = ndefRecord.getPayload();
                                    String id = new String(payload);
                                    Log.e(TAG, "onNewIntent: ");
//                                    Toast.makeText(mContext, "id: " + id, Toast.LENGTH_SHORT).show();

                                    if (!id.equals("")) {
                                        Intent intent1 = new Intent(mContext, PatientActivity.class);
                                        intent1.putExtra(CS.userid, id);
                                        startActivity(intent1);
                                    }

                                } else {
                                    Toast.makeText(mContext, "NFC Tag is empty!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(mContext, "NFC Tag is empty!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "onNewIntent: " + ex.getMessage());
                    Toast.makeText(mContext, "Read error", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "onNewIntent: tag is null");
            }
        } else if (NFCReportEnabled) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Log.e(TAG, "onNewIntent: " + intent.getAction());
            if (tag != null) {
                Ndef ndef = Ndef.get(tag);
                try {
                    NdefMessage ndefMessage = ndef.getCachedNdefMessage();
                    if (ndefMessage != null) {
                        NdefRecord[] records = ndefMessage.getRecords();
                        if (records.length > 0) {
                            for (NdefRecord ndefRecord : records) {
                                if (ndefRecord.getPayload().length > 0) {
                                    byte[] payload = ndefRecord.getPayload();
                                    String id = new String(payload);
                                    Log.e(TAG, "onNewIntent: ");

                                    if (!id.equals("")) {
                                        Intent intent1 = new Intent(mContext, ReportActivity.class);
                                        intent1.putExtra(CS.userid, id);
                                        intent1.putExtra(CS.type, getSharedPreferences("GC", MODE_PRIVATE).getLong(CS.type, -1));
                                        startActivity(intent1);
                                    }

                                } else {
                                    Toast.makeText(mContext, "NFC Tag is empty!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(mContext, "NFC Tag is empty!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "onNewIntent: " + ex.getMessage());
                    Toast.makeText(mContext, "Read error", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "onNewIntent: tag is null");
            }
        } else {
            long type = getSharedPreferences("GC", MODE_PRIVATE).getLong(CS.type, -1);
            if (type != CS.PATIENT && type != CS.GOVERNMENT) {
                Toast.makeText(mContext, "Cannot add user here\nPlease select user tag from navigation menu", Toast.LENGTH_LONG).show();
            }
        }
    }


    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
//                                    latTextView.setText(location.getLatitude() + "");
//                                    lonTextView.setText(location.getLongitude() + "");
                                    double latitude = location.getLatitude();
                                    double longitude = location.getLongitude();
                                    Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                                    List<Address> addresses = null;
                                    try {
                                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                        String cityName = addresses.get(0).getLocality();
                                        String stateName = addresses.get(0).getAddressLine(1);
                                        String countryName = addresses.get(0).getAddressLine(2);
                                        Log.e(TAG, "onComplete: location: " + cityName);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Log.e(TAG, "onComplete: location: " + e.getMessage());
                                    }


//                                    t1.setText("Longitude :" + longitude + "latitude :" + latitude + "city:" + cityName + "state" + stateName + "country:" + countryName);
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
//            latTextView.setText(mLastLocation.getLatitude() + "");
//            lonTextView.setText(mLastLocation.getLongitude() + "");

        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSION_PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dgNewUser != null) {
            dgNewUser.dismiss();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            //        Toast.makeText(mContext, location.toString(), Toast.LENGTH_SHORT).show();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String cityName = addresses.get(0).getLocality();
            String stateName = addresses.get(0).getAdminArea();
            String countryName = addresses.get(0).getCountryName();
            Log.e(TAG, "onLocationChanged: " + "Longitude :" + longitude + "latitude :" + latitude + "city:" + cityName + "state" + stateName + "country:" + countryName);
//        t1.setText("Longitude :"+longitude+"latitude :"+latitude+"city:"+cityName+"state"+stateName+"country:"+countryName);
        } catch (Exception ex) {
            Log.e(TAG, "onLocationChanged: " + ex.getMessage());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
