package com.shrewd.healthcard.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shrewd.healthcard.Activity.HistoryActivity;
import com.shrewd.healthcard.Activity.VerifyActivity;
import com.shrewd.healthcard.ModelClass.User;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.Utilities.CS;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VerifyAdapter extends RecyclerView.Adapter<VerifyAdapter.ViewHolder> {

    private static final String TAG = "HistoryAdapter";
    private final Context mContext;
    private final ArrayList<User> alUser;
    private final LayoutInflater inflater;
    private final ArrayList<String> alUserid;

    public VerifyAdapter(Context mContext, ArrayList<User> alUser, ArrayList<String> alUserid) {
        this.mContext = mContext;
        this.alUser = alUser;
        this.alUserid = alUserid;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_verification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final User user = alUser.get(position);
        holder.tvName.setText(user.getName());
        holder.tvEmail.setText(user.getEmail());
        holder.llVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VerifyActivity.class);
                intent.putExtra(CS.userid,alUserid.get(position));
                intent.putExtra(CS.User, user);
                mContext.startActivity(intent);
            }
        });
        Log.e(TAG, "onBindViewHolder: " );
    }

    @Override
    public int getItemCount() {
        return alUser.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvEmail, tvName;
        private final LinearLayout llVerify;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llVerify = (LinearLayout) itemView.findViewById(R.id.llVerify);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);

        }
    }
}
