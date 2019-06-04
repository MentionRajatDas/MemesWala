package com.bestmeme.memeswala.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bestmeme.memeswala.Activities.ImageDetail;
import com.bestmeme.memeswala.Common.Common;
import com.bestmeme.memeswala.Interface.ItemClickListener;
import com.bestmeme.memeswala.Model.CategoryItem;
import com.bestmeme.memeswala.R;
import com.bestmeme.memeswala.ViewHolder.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MemesFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseRecyclerOptions<CategoryItem> options;
    FirebaseRecyclerAdapter<CategoryItem, CategoryViewHolder> adapter;
    private ProgressBar progressBar;


    public MemesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_memes, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.MemeProgressBar);
        recyclerView = (RecyclerView) view.findViewById(R.id.MemesRecyclerView);
        recyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter.startListening();
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Memes");

        options = new FirebaseRecyclerOptions.Builder<CategoryItem>()
                .setQuery(databaseReference, CategoryItem.class).build();

        adapter = new FirebaseRecyclerAdapter<CategoryItem, CategoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder holder, final int position, @NonNull final CategoryItem model) {
                progressBar.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(model.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .fit()
                        .into(holder.iv, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                holder.tv.setText(model.getName());

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int postion) {
                        Intent intent=  new Intent(getContext(), ImageDetail.class);
                        Common.SELECT_BACKGROUND = model;
                        Common.SELECT_BACKGROUND_KEY=adapter.getRef(position).getKey();
                        Common.sPositon = position;
                        startActivity(intent);
                    }
                });

            }
            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.meme_item_cardview, viewGroup, false);

                return new CategoryViewHolder(view);
            }
        };

        /*GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter.startListening();
        recyclerView.setAdapter(adapter);*/


    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        if (adapter != null)
            adapter.stopListening();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
    }
}
