package com.bestmeme.memeswala.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bestmeme.memeswala.Interface.ItemClickListener;
import com.bestmeme.memeswala.R;

public class SavedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView SavedImageView;

    ItemClickListener itemClickListener;

    public SavedViewHolder(@NonNull View itemView) {
        super(itemView);


        SavedImageView = (ImageView) itemView.findViewById(R.id.SavedItemImageView);
        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition());
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
