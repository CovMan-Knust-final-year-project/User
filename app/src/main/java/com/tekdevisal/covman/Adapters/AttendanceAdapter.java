package com.tekdevisal.covman.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tekdevisal.covman.ChatActivity;
import com.tekdevisal.covman.Helpers.Accessories;
import com.tekdevisal.covman.Models.AttendanceModel;
import com.tekdevisal.covman.Models.Doctors;
import com.tekdevisal.covman.R;
import com.tekdevisal.covman.VideoChat_Activity;

import java.util.ArrayList;
import java.util.HashMap;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder>{
    ArrayList<AttendanceModel> itemList;
    Context context;
    Accessories adapater;

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public AttendanceAdapter(ArrayList<AttendanceModel> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
    }

    @Override
    public AttendanceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_attachment,parent,false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }


    @Override
    public void onBindViewHolder(AttendanceAdapter.ViewHolder holder, final int position) {
        CardView attendance_card = holder.view.findViewById(R.id.attendance_card);
        TextView venue = holder.view.findViewById(R.id.venue);
        TextView date = holder.view.findViewById(R.id.date);
        TextView time = holder.view.findViewById(R.id.time);
        adapater = new Accessories(context);

        venue.setText(itemList.get(position).getVenue());
        date.setText(itemList.get(position).getDate());
        time.setText(itemList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


}
