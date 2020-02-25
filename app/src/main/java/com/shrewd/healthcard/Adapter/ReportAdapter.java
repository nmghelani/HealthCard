package com.shrewd.healthcard.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.shrewd.healthcard.Activity.HistoryActivity;
import com.shrewd.healthcard.Activity.ReportActivity;
import com.shrewd.healthcard.Activity.ReportHistoryActivity;
import com.shrewd.healthcard.ModelClass.History;
import com.shrewd.healthcard.ModelClass.Report;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.Utilities.CS;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private static final String TAG = "ReportAdapter";
    private final Context mContext;
    private final ArrayList<Report> alReport;
    private final LayoutInflater inflater;
    private final int fromWhom;

    public ReportAdapter(Context mContext, ArrayList<Report> alReport, int fromWhom) {
        this.mContext = mContext;
        this.alReport = alReport;
        inflater = LayoutInflater.from(mContext);
        this.fromWhom = fromWhom;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Report report = alReport.get(position);

        holder.tvDoctor.setVisibility(fromWhom == CS.ADMIN ? View.VISIBLE : View.GONE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(CS.Patient)
                .whereEqualTo(CS.patientid, report.getPatientid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot dsPatient : queryDocumentSnapshots.getDocuments()) {
                            db.collection(CS.User)
                                    .whereEqualTo(CS.userid, dsPatient.getString(CS.userid))
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (DocumentSnapshot dsUser : queryDocumentSnapshots.getDocuments()) {
                                                String patientName = dsUser.getString(CS.name);
                                                Log.e(TAG, "onSuccess: " + patientName);
                                                holder.tvPatient.setText(patientName);
                                                break;
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            holder.tvPatient.setText("N/A");
                                        }
                                    });
                        }
                    }
                });
        holder.tvReportType.setText(report.getType());

        holder.llReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ReportHistoryActivity.class);
                intent.putExtra(CS.report, report);
                mContext.startActivity(intent);
            }
        });

        db.collection(CS.History)
                .whereEqualTo(CS.reportid, report.getReportid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot dsHistory : queryDocumentSnapshots.getDocuments()) {
                            History history = dsHistory.toObject(History.class);
                            Log.e(TAG, "onSuccess: history: " + history.getDisease());
                            holder.tvReportType.setText(history.getDisease());
                            holder.llReport.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, HistoryActivity.class);
                                    intent.putExtra(CS.History, history);
                                    intent.putExtra(CS.type, CS.REPORT);
                                    mContext.startActivity(intent);
                                }
                            });

                            db.collection(CS.Doctor)
                                    .whereEqualTo(CS.doctorid, history.getDoctorid())
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            if (queryDocumentSnapshots != null) {
                                                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {

                                                    db.collection(CS.User)
                                                            .document(doc.getString(CS.userid))
                                                            .get()
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                    if (documentSnapshot.exists()) {
                                                                        String doctorName = documentSnapshot.getString(CS.name);
                                                                        holder.tvDoctor.setText(doctorName != null ? CS.Dr + doctorName : "N/A");
                                                                    }
                                                                }
                                                            });
                                                    break;
                                                }
                                            }
                                        }
                                    });

                            Log.e(TAG, "onSuccess: patientid: " + report.getPatientid());
                            db.collection(CS.Patient)
                                    .whereEqualTo(CS.patientid, report.getPatientid())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (DocumentSnapshot dsPatient : queryDocumentSnapshots.getDocuments()) {
                                                db.collection(CS.User)
                                                        .whereEqualTo(CS.userid, dsPatient.getString(CS.userid))
                                                        .get()
                                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                for (DocumentSnapshot dsUser : queryDocumentSnapshots.getDocuments()) {
                                                                    String patientName = dsUser.getString(CS.name);
                                                                    Log.e(TAG, "onSuccess: " + patientName);
                                                                    holder.tvPatient.setText(patientName);
                                                                    break;
                                                                }
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                holder.tvPatient.setText("N/A");
                                                            }
                                                        });
                                            }
                                        }
                                    });

                            db.collection(CS.Patient)
                                    .whereEqualTo(CS.patientid, report.getPatientid())
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            if (queryDocumentSnapshots != null) {
                                                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                                    String userid = doc.getString(CS.userid);
                                                    Log.e(TAG, "onEvent: userid: ");
                                                    if (userid != null) {
                                                        db.collection(CS.User)
                                                                .document(userid)
                                                                .get()
                                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                        String patientName = documentSnapshot.getString(CS.name);
                                                                        Log.e(TAG, "onSuccess: " + patientName);
                                                                        holder.tvPatient.setText(patientName);
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        holder.tvPatient.setText("N/A");
                                                                    }
                                                                });
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    });
                            break;
                        }
                    }
                });

        Date cDate = report.getDate();
        String fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);
        holder.tvDate.setText(fDate);
        Log.e(TAG, "onBindViewHolder: ");
    }

    @Override
    public int getItemCount() {
        return alReport.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvDate, tvDoctor, tvPatient;
        private final LinearLayout llReport;
        private final TextView tvReportType;
        private final ImageView ivReport;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llReport = (LinearLayout) itemView.findViewById(R.id.llReport);
            tvPatient = (TextView) itemView.findViewById(R.id.tvPatient);
            tvReportType = (TextView) itemView.findViewById(R.id.tvReportType);
            tvDoctor = (TextView) itemView.findViewById(R.id.tvDoctor);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            ivReport = (ImageView) itemView.findViewById(R.id.ivReport);

        }
    }
}
