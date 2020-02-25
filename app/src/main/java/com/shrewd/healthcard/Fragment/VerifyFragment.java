package com.shrewd.healthcard.Fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.shrewd.healthcard.Activity.MainActivity;
import com.shrewd.healthcard.Adapter.VerifyAdapter;
import com.shrewd.healthcard.ModelClass.User;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.Utilities.CS;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyFragment extends Fragment {


    private final Context mContext;
    private FirebaseUser firebaseUser;
    private static final String TAG = "VerifyFragment";
    private RecyclerView rvVerify;
    private LinearLayout llNoData;
    private ArrayList<User> alUser = new ArrayList<>();
    private ArrayList<String> alUserid = new ArrayList<>();

    public VerifyFragment(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verify, container, false);
        rvVerify = view.findViewById(R.id.rvVerify);
        llNoData = view.findViewById(R.id.llNoData);

        Log.e(TAG, "onCreateView: ");

        return view;
    }

    @Override
    public void onStart() {
        Log.e(TAG, "onStart: ");
        Log.e(TAG, "onAttach: ");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ((MainActivity) mContext).setInProgress();
        db.collection(CS.User)
                .orderBy(CS.type)
                .whereGreaterThan(CS.type, -1)
                .orderBy(CS.regdate, Query.Direction.DESCENDING)
                .whereEqualTo(CS.verified, false)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        alUser.clear();
                        alUserid.clear();
                        Log.e(TAG, "onSuccess: " + queryDocumentSnapshots.size());
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            try {
                                if (documentSnapshot.getLong(CS.type) == 4) {
                                    continue;
                                }
                                User user = documentSnapshot.toObject(User.class);
//                                user.setContactno(documentSnapshot.getLong(CS.contactno));
                                Log.e(TAG, "onSuccess: " + user.getType());
                                Log.e(TAG, "onSuccess: " + user.getAddress());
                                Log.e(TAG, "onSuccess: " + user.getEmail());
                                Log.e(TAG, "onSuccess: " + user.getName());
                                Log.e(TAG, "onSuccess: " + user.getProof());
                                Log.e(TAG, "onSuccess: " + user.getContactno());
                                Log.e(TAG, "onSuccess: " + user.getDob());
                                Log.e(TAG, "onSuccess: " + user.getGender());
                                Log.e(TAG, "onSuccess: " + user.getRegdate());
                                Log.e(TAG, "onSuccess: " + user.isVerified());
                                alUser.add(user);
                                alUserid.add(documentSnapshot.getId());
                            } catch (Exception ex) {
                                Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                            }
                        }
                        ((MainActivity) mContext).setProgressCompleted();
                        setAdapter(alUser,alUserid);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ((MainActivity) mContext).setProgressCompleted();
                        setAdapter(alUser,alUserid);
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });
        super.onStart();
    }

    private void setAdapter(ArrayList<User> alUser, ArrayList<String> alUserid) {
        if (alUser.size() > 0) {
            rvVerify.setVisibility(View.VISIBLE);
            llNoData.setVisibility(View.GONE);
            Log.e(TAG, "onSuccess: " + alUser.size());
            VerifyAdapter verifyAdapter = new VerifyAdapter(mContext, alUser, alUserid);
            rvVerify.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
            rvVerify.setAdapter(verifyAdapter);
        } else {
            rvVerify.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }

    }
}
