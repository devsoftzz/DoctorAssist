package com.devsoftzz.doctorassist.Models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.devsoftzz.doctorassist.R;
import com.google.gson.Gson;

import java.util.ArrayList;

public class notesAdapter extends RecyclerView.Adapter<notesAdapter.ViewHolder> {

    private ArrayList<Place.Result> mNotes;
    private OnNoteListner onNoteListner;
    private Place.Result note;
    private ArrayList<Integer> mColours;
    private Integer mColourSize;

    public notesAdapter(ArrayList<Place.Result> mNotes, OnNoteListner onNoteListner) {
        this.mNotes = mNotes;
        this.onNoteListner = onNoteListner;

        Colours colours = new Colours();
        mColours = colours.getColours();
        mColourSize = colours.coloursSize();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hospital_list_pojo,parent,false);
        return new ViewHolder(view , onNoteListner);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        note = mNotes.get(position);

        holder.mName.setText(note.getName());
        holder.mRating.setText(String.valueOf(note.getRating())+"/5.0");
        holder.mIcon.setText(note.getName().toUpperCase());
        holder.mIconBack.setCardBackgroundColor(mColours.get(position%mColourSize));

    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mName,mRating,mBtn,mIcon;
        CardView mIconBack;
        LinearLayout mLayout;
        OnNoteListner onNoteListner;

        public ViewHolder(@NonNull View itemView, OnNoteListner onNoteListner) {
            super(itemView);
            this.onNoteListner=onNoteListner;
            mName = itemView.findViewById(R.id.name);
            mRating = itemView.findViewById(R.id.ratingText);
            mIcon = itemView.findViewById(R.id.iconText);
            mIconBack = itemView.findViewById(R.id.iconView);
            mLayout = itemView.findViewById(R.id.layout);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListner.onNoteClick(getAdapterPosition(),mNotes.get(getAdapterPosition()));
        }
    }

    public interface OnNoteListner{
        void onNoteClick(int position, Place.Result note);
    }
}
