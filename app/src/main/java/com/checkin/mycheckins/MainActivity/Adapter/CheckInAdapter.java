package com.checkin.mycheckins.MainActivity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.mycheckins.AppData.IntentKeys;
import com.checkin.mycheckins.Models.CheckinModel;
import com.checkin.mycheckins.NewCheckin.AddCheckInActivity;
import com.checkin.mycheckins.R;
import com.checkin.mycheckins.Utils;

import java.util.List;

public class CheckInAdapter extends RecyclerView.Adapter<CheckInAdapter.ViewHolder> {

    private List<CheckinModel> list;
    private Context context;

    public CheckInAdapter(List<CheckinModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.checkin_list_single_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {

        CheckinModel checkin = list.get(i);

        holder.tvTitle.setText(checkin.getTitle());
        holder.tvPlace.setText(checkin.getPlace());
        holder.tvDetail.setText(checkin.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddCheckInActivity.class);
                intent.putExtra(IntentKeys.ACTIVITY_TITLE, "Update CheckIn");
                intent.putExtra(IntentKeys.IS_NEW_CHECKIN, false);
                intent.putExtra(IntentKeys.CHECKIN_MODEL, list.get(i));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list == null || list.isEmpty())
            return 0;
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvPlace, tvDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDetail = itemView.findViewById(R.id.tvDetails);
            tvPlace = itemView.findViewById(R.id.tvPlace);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }
}
