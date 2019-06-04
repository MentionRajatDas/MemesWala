package com.bestmeme.memeswala.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestmeme.memeswala.Interface.ItemClickListener;
import com.bestmeme.memeswala.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tv;
    public ImageView iv;

    ItemClickListener itemClickListener;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);

        tv = (TextView) itemView.findViewById(R.id.MemeTitle);
        iv = (ImageView) itemView.findViewById(R.id.MemeItemImage);
        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition());
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
