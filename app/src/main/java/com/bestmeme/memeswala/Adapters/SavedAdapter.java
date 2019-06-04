package com.bestmeme.memeswala.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bestmeme.memeswala.Model.SavedItem;
import com.bestmeme.memeswala.R;
import com.bestmeme.memeswala.ViewHolder.SavedViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SavedAdapter extends RecyclerView.Adapter<SavedViewHolder> {

    Context context;
    ArrayList<SavedItem> savedItems;


    public SavedAdapter(Context context, ArrayList<SavedItem> savedItems) {
        this.context = context;
        this.savedItems = savedItems;
    }

    @NonNull
    @Override
    public SavedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_item_layout, parent, false);
        return new SavedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedViewHolder holder, int position) {

        SavedItem s = savedItems.get(position);

        Picasso.get()
                .load(s.getImageUrl())
                .into(holder.SavedImageView);

    }

    @Override
    public int getItemCount() {
        return savedItems.size();
    }
}
