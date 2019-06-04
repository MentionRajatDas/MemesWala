package com.bestmeme.memeswala.Fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bestmeme.memeswala.Adapters.SavedAdapter;
import com.bestmeme.memeswala.Model.SavedItem;
import com.bestmeme.memeswala.R;

import java.io.File;
import java.util.ArrayList;

public class SavedMemesFragment extends Fragment {

    View view;
    Context context;
    private RecyclerView recyclerView;


    public SavedMemesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_saved, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.RecyclerViewSaved);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(new SavedAdapter(getContext(), getData()));


        return view;
    }

    private ArrayList<SavedItem> getData() {
        ArrayList<SavedItem> savedItems = new ArrayList<>();
        //TARGET FOLDER
        File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File folder = new File(sd, "/Memes Wala/");

        SavedItem s;

        if (folder.exists()) {
            //GET ALL FILES IN DOWNLOAD FOLDER
            File[] files = folder.listFiles();

            //LOOP THRU THOSE FILES GETTING NAME AND URI
            for (int i = 0; i < files.length; i++) {
                File file = files[i];

                s = new SavedItem();
                s.setImageUrl(Uri.fromFile(file));

                savedItems.add(s);
            }
        }


        return savedItems;
    }



}
