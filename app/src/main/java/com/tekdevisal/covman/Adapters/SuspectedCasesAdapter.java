package com.tekdevisal.covman.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tekdevisal.covman.Helpers.Accessories;
import com.tekdevisal.covman.Models.AttendanceModel;
import com.tekdevisal.covman.Models.SuspectedCasesModel;
import com.tekdevisal.covman.R;
import com.tekdevisal.covman.SuspectedCases;

import java.util.ArrayList;

public class SuspectedCasesAdapter extends RecyclerView.Adapter<SuspectedCasesAdapter.ViewHolder>{
    ArrayList<SuspectedCasesModel> itemList;
    Context context;
    Accessories adapater;

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public SuspectedCasesAdapter(ArrayList<SuspectedCasesModel> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
    }

    @Override
    public SuspectedCasesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.suspected_cases_attachment,parent,false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }


    @Override
    public void onBindViewHolder(SuspectedCasesAdapter.ViewHolder holder, final int position) {
        TextView name = holder.view.findViewById(R.id.patient_name);
        TextView date = holder.view.findViewById(R.id.date);
        TextView time = holder.view.findViewById(R.id.time);
        TextView org_name = holder.view.findViewById(R.id.org);
        ImageView call = holder.view.findViewById(R.id.call_button);
        adapater = new Accessories(context);

        name.setText(itemList.get(position).getFirst_name() + " " + itemList.get(position).getLast_name());
        date.setText(itemList.get(position).getDate());
        time.setText(itemList.get(position).getOrgName());
        org_name.setText("@" + itemList.get(position).getTime());
        call.setOnClickListener(view -> {
            adapater.openDialer(view, itemList.get(position).getPhone_number());
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


}
