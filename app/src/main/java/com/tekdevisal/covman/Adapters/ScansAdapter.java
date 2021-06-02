package com.tekdevisal.covman.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tekdevisal.covman.Helpers.Accessories;
import com.tekdevisal.covman.Models.AttendanceModel;
import com.tekdevisal.covman.Models.ScansModel;
import com.tekdevisal.covman.R;

import java.util.ArrayList;

public class ScansAdapter extends RecyclerView.Adapter<ScansAdapter.ViewHolder>{
    ArrayList<ScansModel> itemList;
    Context context;
    Accessories adapater;

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public ScansAdapter(ArrayList<ScansModel> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
    }

    @Override
    public ScansAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_scans_attachment,parent,false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }


    @Override
    public void onBindViewHolder(ScansAdapter.ViewHolder holder, final int position) {
        CardView scan_card = holder.view.findViewById(R.id.scan_card);
        TextView date = holder.view.findViewById(R.id.date);
        TextView time = holder.view.findViewById(R.id.time);
        TextView temperature = holder.view.findViewById(R.id.temperature);
        TextView status = holder.view.findViewById(R.id.status);
        adapater = new Accessories(context);

        try{
            date.setText(itemList.get(position).getDate());
            time.setText(itemList.get(position).getTime());

            if(itemList.get(position).getStatus().equals("0")){
                temperature.setTextColor(context.getResources().getColor(R.color.navy_green));
                status.setTextColor(context.getResources().getColor(R.color.navy_green));
                status.setText("Negative");
            }
            else{
                temperature.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                status.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                status.setText("Positive");
            }
            temperature.setText(itemList.get(position).getTemperature() + "\u00B0" + "C");
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


}
