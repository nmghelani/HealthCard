package com.shrewd.healthcard.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.shrewd.healthcard.Activity.HistoryActivity;
import com.shrewd.healthcard.ModelClass.History;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.Utilities.CS;
import com.shrewd.healthcard.Utilities.CU;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private static final String TAG = "HistoryAdapter";
    private final Context mContext;
    private final ArrayList<History> alHistory;
    private final LayoutInflater inflater;
    private final int fromWhom;

    public HistoryAdapter(Context mContext, ArrayList<History> alHistory, int fromWhom) {
        this.mContext = mContext;
        this.alHistory = alHistory;
        inflater = LayoutInflater.from(mContext);
        this.fromWhom = fromWhom;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final History history = alHistory.get(position);

        setTools(holder, fromWhom);
        if (fromWhom == CS.DOCTOR) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(CS.Patient)
                    .whereEqualTo(CS.patientid, history.getPatientid())
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
                                                        String patientName = documentSnapshot.getString(CS.name);
                                                        holder.tvPatient.setText(patientName != null ? patientName : "N/A");
                                                    }
                                                }
                                            });
                                    break;
                                }
                            }
                        }
                    });
        } else if (fromWhom == CS.PATIENT) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        } else if (fromWhom == CS.ADMIN) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection(CS.Patient)
                    .whereEqualTo(CS.patientid, history.getPatientid())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (queryDocumentSnapshots != null) {
                                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                    String userid = doc.getString(CS.userid);
                                    if (userid != null) {
                                        db.collection(CS.User)
                                                .document(userid)
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        String patientName = documentSnapshot.getString(CS.name);
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

            db.collection(CS.Doctor)
                    .whereEqualTo(CS.doctorid, history.getDoctorid())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (queryDocumentSnapshots != null) {
                                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                    String userid = doc.getString(CS.userid);
                                    if (userid != null) {
                                        db.collection(CS.User)
                                                .document(userid)
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        String doctorName = documentSnapshot.getString(CS.name);
                                                        holder.tvDoctor.setText(CS.Dr + doctorName);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        holder.tvDoctor.setText("N/A");
                                                    }
                                                });
                                    }
                                }
                            }
                        }
                    });
        }

        holder.tvDiseaseHistory.setText(history.getDisease());
        holder.tvDiseasePatient.setText(history.getDisease());

        Date cDate = history.getDate();
        String fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);
        holder.tvDateHistory.setText(fDate);
        holder.tvDatePatient.setText(fDate);
        holder.llHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HistoryActivity.class);
                intent.putExtra(CS.History, history);
                if (history.getReportsuggesion() != null && history.getReportsuggesion().equals("")) {
                    intent.putExtra(CS.type, CS.REPORT);
                    mContext.startActivity(intent);
                } else {
                    Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.dg_report_suggesstion);
                    MaterialButton btnAddReport = dialog.findViewById(R.id.btnAddReport);
                    TextView tvSuggestedReport = dialog.findViewById(R.id.tvSuggestedReport);
                    tvSuggestedReport.setText(history.getReportsuggesion());
                    Window window = dialog.getWindow();
                    if (window != null) {
                        window.setBackgroundDrawable(mContext.getDrawable(R.drawable.bg_dg_rounded));
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                    btnAddReport.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            }
        });
        Log.e(TAG, "onBindViewHolder: ");
    }

    private void setTools(ViewHolder holder, int fromWhom) {
        holder.llHistoryPatient.setVisibility(View.GONE);
        holder.llPatient.setVisibility(View.GONE);
        if (fromWhom == CS.DOCTOR) {
            holder.llPatient.setVisibility(View.VISIBLE);
        } else if (fromWhom == CS.ADMIN) {
            holder.llHistoryPatient.setVisibility(View.VISIBLE);
            holder.llPatient.setVisibility(View.VISIBLE);
        } else {
            holder.llHistoryPatient.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return alHistory.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvDateHistory, tvDatePatient, tvDiseaseHistory, tvDiseasePatient, tvDoctor, tvPatient;
        private final LinearLayout llHistoryPatient, llPatient, llHistory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llHistoryPatient = (LinearLayout) itemView.findViewById(R.id.llHistoryPatient);
            llHistory = (LinearLayout) itemView.findViewById(R.id.llHistory);

            llPatient = (LinearLayout) itemView.findViewById(R.id.llPatient);
            tvDiseaseHistory = (TextView) itemView.findViewById(R.id.tvDiseaseHistory);
            tvDateHistory = (TextView) itemView.findViewById(R.id.tvDateHistory);
            tvDiseasePatient = (TextView) itemView.findViewById(R.id.tvDiseasePatient);
            tvDoctor = (TextView) itemView.findViewById(R.id.tvDoctor);
            tvPatient = (TextView) itemView.findViewById(R.id.tvPatient);
            tvDatePatient = (TextView) itemView.findViewById(R.id.tvDatePatient);
        }
    }
}
