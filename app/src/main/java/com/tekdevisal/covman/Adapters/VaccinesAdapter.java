package com.tekdevisal.covman.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tekdevisal.covman.Helpers.Accessories;
import com.tekdevisal.covman.Models.AttendanceModel;
import com.tekdevisal.covman.Models.VaccinesModel;
import com.tekdevisal.covman.R;
import com.tekdevisal.covman.RequestedVaccineDetails;

import java.util.ArrayList;

public class VaccinesAdapter extends RecyclerView.Adapter<VaccinesAdapter.ViewHolder>{
    ArrayList<VaccinesModel> itemList;
    Context context;
    Accessories adapater;

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public VaccinesAdapter(ArrayList<VaccinesModel> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
        adapater      = new Accessories(context);
    }

    @Override
    public VaccinesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.vaccines_requests_attachment,parent,false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }


    @Override
    public void onBindViewHolder(VaccinesAdapter.ViewHolder holder, final int position) {
        CardView _card          = holder.view.findViewById(R.id._card);
        TextView fullname       = holder.view.findViewById(R.id.full_name);
        TextView phone_number   = holder.view.findViewById(R.id.phone_number);
        TextView time           = holder.view.findViewById(R.id.time);

        fullname.setText(itemList.get(position).getFirst_name() + " " + itemList.get(position).getLast_name());
        phone_number.setText(itemList.get(position).getPhone_number());
        time.setText(new Accessories(context).OneMinuteAgoTimeFormat(itemList.get(position).getDate()));

        _card.setOnClickListener(view -> {
            Intent details = new Intent(context, RequestedVaccineDetails.class);
            adapater.put("user_id", itemList.get(position).getUser_id());
            adapater.put("request_id", itemList.get(position).getId());
            adapater.put("v_fname", itemList.get(position).getFirst_name());
            adapater.put("v_lname", itemList.get(position).getLast_name());
            adapater.put("v_latitude", itemList.get(position).getLatitude());
            adapater.put("v_longitude", itemList.get(position).getLongitude());
            adapater.put("v_phone_number",itemList.get(position).getPhone_number());
            adapater.put("v_date", itemList.get(position).getDate());
            details.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(details);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


}
