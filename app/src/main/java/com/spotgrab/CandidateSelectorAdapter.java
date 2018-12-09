package com.spotgrab;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class CandidateSelectorAdapter extends RecyclerView.Adapter <CandidateSelectorAdapter.CandidateSelectorHolder>{
    private ArrayList<CardItem> mCardItemArrayList;
    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener =listener;
    }
    public static class CandidateSelectorHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView name, id;
        public RatingBar rating;

        public CandidateSelectorHolder(View itemView, final OnItemClickListener listener){
            super(itemView);
            mImageView = itemView.findViewById(R.id.profilePic);
            name = itemView.findViewById(R.id.fullName);
            id = itemView.findViewById(R.id.spotterUid);
            rating = itemView.findViewById(R.id.candidateRating);


            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public CandidateSelectorAdapter(ArrayList<CardItem> cardItemArrayList){
        mCardItemArrayList = cardItemArrayList;
    }
    @Override
    public CandidateSelectorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        CandidateSelectorHolder csh = new CandidateSelectorHolder(v, mListener);
        return csh;
    }

    @Override
    public void onBindViewHolder(CandidateSelectorHolder holder, int position) {
        CardItem currentItem = mCardItemArrayList.get(position);

        //holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.name.setText(currentItem.getName());
        holder.id.setText(currentItem.getmUid());
        holder.rating.setRating(currentItem.getmRating());

    }

    @Override
    public int getItemCount() {
        return mCardItemArrayList.size();
    }

}
