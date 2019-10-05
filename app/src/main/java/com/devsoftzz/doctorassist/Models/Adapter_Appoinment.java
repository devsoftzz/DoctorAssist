package com.devsoftzz.doctorassist.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devsoftzz.doctorassist.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Appoinment extends RecyclerView.Adapter<Adapter_Appoinment.progViewHolder> {

    private List<AppointmentPojo> data;
    Context context;


    public Adapter_Appoinment(List<AppointmentPojo> data, Context context){
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public progViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        LayoutInflater l1 = LayoutInflater.from(viewGroup.getContext());
        View view = l1.inflate(R.layout.appoinment_layout,viewGroup,false);

        return new progViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull progViewHolder progViewHolder, int i) {

                progViewHolder.t1.setText(data.get(i).getHospital());
                progViewHolder.t2.setText(data.get(i).getDate()+" | "+data.get(i).getTime());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public  void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public class progViewHolder extends RecyclerView.ViewHolder{
        private TextView t1,t2;

        public progViewHolder(View itemView){
            super(itemView);
            t1 = itemView.findViewById(R.id.textView);
            t2 = itemView.findViewById(R.id.textView2);
        }
    }
}