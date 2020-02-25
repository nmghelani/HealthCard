package com.shrewd.healthcard.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.shrewd.healthcard.ModelClass.Laboratory;
import com.shrewd.healthcard.ModelClass.Report;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.Utilities.CS;
import com.shrewd.healthcard.Utilities.CU;

import java.text.SimpleDateFormat;

public class ReportHistoryActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_history);

        mContext = ReportHistoryActivity.this;

        SharedPreferences sp = getSharedPreferences("GC", MODE_PRIVATE);
        long userType = sp.getLong(CS.type, -1);

        //region set Actionbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("History");
        }

        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvReport = findViewById(R.id.tvReport);
        TextView tvReportType = findViewById(R.id.tvReportType);
        TextView tvPatient = findViewById(R.id.tvPatient);
        final ImageView ivReport = findViewById(R.id.ivReport);

        Intent intent = getIntent();
        Report report = intent.getParcelableExtra(CS.report);
        if (report == null) {
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("E dd, MMM yyyy hh:mm aa");
        tvDate.setText(sdf.format(report.getDate()));
        tvReportType.setText(report.getType());

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();



        db.collection(CS.Patient)
                .whereEqualTo(CS.patientid, report.getPatientid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot dsPatient : queryDocumentSnapshots.getDocuments()) {
                            db.collection(CS.User)
                                    .document(dsPatient.getString(CS.userid))
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                tvPatient.setText(documentSnapshot.getString(CS.name));
                                            }
                                        }
                                    });
                            break;
                        }
                    }
                });

        if (userType == CS.ADMIN) {
            tvReport.setVisibility(View.VISIBLE);
            db.collection(CS.Laboratory)
                    .whereEqualTo(CS.labid, report.getLabid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot dsLab : queryDocumentSnapshots.getDocuments()) {
                                Laboratory laboratory = dsLab.toObject(Laboratory.class);
                                String report = null;
                                if (laboratory != null) {
                                    report = laboratory.getName() + ",\n" + laboratory.getAddress() + "\n" + laboratory.getContactno();
                                    tvReport.setText(report);
                                }
                            }
                        }
                    });
        } else {
            tvReport.setVisibility(View.GONE);
        }

        if (report.getImage().size() > 0) {
            Glide.with(mContext)
                    .load(report.getImage().get(0))
                    .placeholder(R.drawable.broken_report)
                    .into(ivReport);
        }

        ivReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dg = new Dialog(mContext);
                dg.setContentView(R.layout.dg_image_preview);
                ImageView iv = dg.findViewById(R.id.ivImg);
                TextView tvName = dg.findViewById(R.id.tvName);
                tvName.setText(!CU.isNullOrEmpty(tvPatient) ? tvPatient.getText().toString() + " (" + report.getType() + ")" : report.getType());
                iv.setImageDrawable(ivReport.getDrawable());
                Window window = dg.getWindow();
                if (window != null) {
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen._500sdp));
                    window.setBackgroundDrawable(getDrawable(R.drawable.bg_dg_newuser));
                    window.setGravity(Gravity.BOTTOM);
                }
                dg.show();
            }
        });

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
}
