package com.bestmeme.memeswala.Helpers;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;

public class SaveImageHelper implements Target {

    private Context context;
    private WeakReference<AlertDialog> alertDialogWeakReference;
    private WeakReference<ContentResolver> contentResolverWeakReference;
    public String name;
    private String decs;

    public SaveImageHelper(Context context, AlertDialog alertDialog, ContentResolver contentResolver, String name, String decs) {
        this.context = context;
        this.alertDialogWeakReference = new WeakReference<AlertDialog>(alertDialog);
        this.contentResolverWeakReference = new WeakReference<ContentResolver>(contentResolver);
        this.name = name;
        this.decs = decs;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        ContentResolver r =contentResolverWeakReference.get();
        AlertDialog alertDialog = alertDialogWeakReference.get();
        if (r != null)
            MediaStore.Images.Media.insertImage(r, bitmap, name, decs);
        alertDialog.dismiss();

        Toast.makeText(context.getApplicationContext(), "Downloaded",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
    }
}
