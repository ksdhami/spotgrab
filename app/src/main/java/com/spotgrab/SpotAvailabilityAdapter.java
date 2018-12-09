package com.spotgrab;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SpotAvailabilityAdapter extends RecyclerView.Adapter <SpotAvailabilityAdapter.CandidateSelectorHolder>{
    private ArrayList<SpotCardItem> mCardItemArrayList;
    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener =listener;
    }
    public static class CandidateSelectorHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView jobTitleText;
        public TextView dateText;
        public TextView timeText;
        public TextView endTimeText;
        public TextView jobDescriptionText;


        public CandidateSelectorHolder(View itemView, final OnItemClickListener listener){
            super(itemView);
            //mImageView = itemView.findViewById(R.id.imageView);
            jobTitleText = itemView.findViewById(R.id.textView);
            dateText = itemView.findViewById(R.id.textView3);
            timeText = itemView.findViewById(R.id.textView2);
            //endTimeText = itemView.findViewById(R.id.textView4);
            //jobDescriptionText = itemView.findViewById(R.id.textView5);


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

    public SpotAvailabilityAdapter(ArrayList<SpotCardItem> cardItemArrayList){
        mCardItemArrayList = cardItemArrayList;
    }
    @Override
    public CandidateSelectorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.availability_card_item, parent, false);
        CandidateSelectorHolder csh = new CandidateSelectorHolder(v, mListener);
        return csh;
    }

    @Override
    public void onBindViewHolder(CandidateSelectorHolder holder, int position) {
        SpotCardItem currentItem = mCardItemArrayList.get(position);

        //holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.jobTitleText.setText("Available:");
        holder.dateText.setText(currentItem.getShift_Date());
        holder.timeText.setText(currentItem.getShift_StartTime() + " - " + currentItem.getShift_EndTime());
        //holder.endTimeText.setText("End Time: " + currentItem.getShift_EndTime());
        //holder.jobDescriptionText.setText(currentItem.getShift_Description());

    }

    @Override
    public int getItemCount() {
        return mCardItemArrayList.size();
    }

}
