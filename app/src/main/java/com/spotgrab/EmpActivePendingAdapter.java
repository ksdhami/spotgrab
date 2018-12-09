package com.spotgrab;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class EmpActivePendingAdapter extends RecyclerView.Adapter <EmpActivePendingAdapter.EmpActivePendingHolder>{
    private ArrayList<EmpCardItem> mCardItemArrayList;
    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    public static class EmpActivePendingHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView jobTitleText;
        public TextView dateText;
        public TextView timeText;
        public TextView endTimeText;
        public TextView jobDescriptionText;
        public TextView offerSent;


        public EmpActivePendingHolder(View itemView, final OnItemClickListener listener){
            super(itemView);
            //mImageView = itemView.findViewById(R.id.imageView);
            jobTitleText = itemView.findViewById(R.id.textView);
            dateText = itemView.findViewById(R.id.textView2);
            timeText = itemView.findViewById(R.id.textView3);
            endTimeText = itemView.findViewById(R.id.startTimeText);
            jobDescriptionText = itemView.findViewById(R.id.textView5);
            offerSent = itemView.findViewById(R.id.textView6);


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

    public EmpActivePendingAdapter(ArrayList<EmpCardItem> cardItemArrayList){
        mCardItemArrayList = cardItemArrayList;
    }
    @Override
    public EmpActivePendingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.emp_card_item, parent, false);
        EmpActivePendingHolder csh = new EmpActivePendingHolder(v, mListener);
        return csh;
    }

    @Override
    public void onBindViewHolder(EmpActivePendingHolder holder, int position) {
        EmpCardItem currentItem = mCardItemArrayList.get(position);
        String offerSent = mCardItemArrayList.get(position).getShift_Offer_Sent();
        String accepted = mCardItemArrayList.get(position).getShift_Filled();

        //holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.jobTitleText.setText(currentItem.getShift_JobTitle());
        holder.dateText.setText(currentItem.getShift_Date());
        holder.timeText.setText(currentItem.getShift_StartTime() + " - " + currentItem.getShift_EndTime());
        //holder.endTimeText.setText("End Time: " + currentItem.getShift_EndTime());
        holder.jobDescriptionText.setText(currentItem.getShift_Description());

        if (offerSent.equals("yes") && !(accepted.equals("yes"))) {
            holder.offerSent.setText("Offer Sent");
        }
    }

    @Override
    public int getItemCount() {
        return mCardItemArrayList.size();
    }

}
