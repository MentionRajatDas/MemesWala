package com.bestmeme.memeswala.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bestmeme.memeswala.Adapters.LoopViewAdapter;
import com.bestmeme.memeswala.Common.Common;
import com.bestmeme.memeswala.Helpers.SaveImageHelper;
import com.bestmeme.memeswala.Interface.IFirebaseLoadDone;
import com.bestmeme.memeswala.Model.CategoryItem;
import com.bestmeme.memeswala.R;
import com.bestmeme.memeswala.Transformers.DepthPageTransformer;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class ImageDetail extends AppCompatActivity implements IFirebaseLoadDone {

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 100;
    private static final String PREF_CAMERA_REQUESTED = "cameraRequested";

    private IFirebaseLoadDone iFirebaseLoadDone;
    private FloatingActionButton setWallaperButtton;
    private FloatingActionButton saveWallaperButtton;
    private FloatingActionButton shareWallpaperButton;
    private FloatingActionMenu floatingActionMenu;
    private List<CategoryItem> categoryItemList;

    private ShareDialog shareDialog;
    private CallbackManager callbackManager;
    private RelativeLayout rootLayout;
    private ImageView mImage;
    private Bitmap bmp;

    ViewPager viewPager;
    LoopViewAdapter adapter;
    DatabaseReference databaseReference;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
            try {
                manager.setBitmap(bitmap);
                rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
                Snackbar.make(rootLayout,"Wallpaper is Set",Snackbar.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) { }
        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) { }
    };

    Target facebookConvertBitmap = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto sharePhoto= new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if (ShareDialog.canShow(SharePhotoContent.class)){
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) { }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) { }
    };

    Target targetShare = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            bmp = bitmap;
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bmp, "SomeText", null);
            Log.d("Path", path);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "Downloaded this image from MemesWala App");
            Uri screenshotUri = Uri.parse(path);
            intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            intent.setType("image/*");
            startActivity(Intent.createChooser(intent, "Share image via..."));
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode)
//        {
//            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE:
//            {
//                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                    Toast.makeText(this, "Permission Granted",Toast.LENGTH_SHORT).show();
//                else
//                    Toast.makeText(this, "Permission Denied",Toast.LENGTH_SHORT).show();
//            }
//                break;
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
        floatingActionMenu = findViewById(R.id.FABMENU);
        databaseReference = FirebaseDatabase.getInstance().getReference("Memes");
        iFirebaseLoadDone = this;
        LoadMemeImages();
        viewPager = findViewById(R.id.ViewPagerDetailID);
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        setWallaperButtton = findViewById(R.id.setAsWallpaperFABID);
        saveWallaperButtton = findViewById(R.id.saveWallpaper);
        shareWallpaperButton = findViewById(R.id.shareWallpaperID);
        mImage = (ImageView) findViewById(R.id.ViewPagerImage);
        categoryItemList  = new ArrayList<>();

        // Save Wallpaper
        saveWallaperButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (ActivityCompat.checkSelfPermission(ImageDetail.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
//                        PackageManager.PERMISSION_GRANTED)
//                    {
//                        Toast.makeText(ImageDetail.this, "You should grant permission", Toast.LENGTH_SHORT).show();
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            requestPermissions(new String[]{
//                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                            }, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
//                        }
//                        return;
//                    }
//                    else {
//                    dialogLoadSaveIamge();
////                    AlertDialog alertDialog = new SpotsDialog(ImageDetail.this);
////                    alertDialog.show();
////                    alertDialog.setMessage("Please wait.....");
////
////                    String filename = UUID.randomUUID().toString()+".png";
////                    int position = viewPager.getCurrentItem();
////                    CategoryItem SelectedItem = categoryItemList.get(position);
////                    Picasso.get()
////                            .load(SelectedItem.getImageUrl())
////                            .into(new SaveImageHelper(ImageDetail.this,
////                                    alertDialog,
////                                    getContentResolver(), filename, "Image Description"));
//                }
                int position = viewPager.getCurrentItem();
                CategoryItem SelectedItem = categoryItemList.get(position);
                String url = SelectedItem.getImageUrl();
                saveImage(url, "MemesWala"+" "+SelectedItem.getName()+System.currentTimeMillis()+ ".png");

                floatingActionMenu.close(true);
            }
        });

        // Share Wallpaper
        shareWallpaperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewPager.getCurrentItem();
                CategoryItem SelectedItem = categoryItemList.get(position);
                Picasso.get()
                        .load(SelectedItem.getImageUrl())
                        .into(targetShare);
                floatingActionMenu.close(true);
            }
        });


        //Set Wallpaper
        setWallaperButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewPager.getCurrentItem();
                CategoryItem SelectedItem = categoryItemList.get(position);
                Picasso.get()
                        .load(SelectedItem.getImageUrl())
                        .into(target);
                floatingActionMenu.close(true);
            }
        });
    }

    private void saveImage(String url, final String imgName) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "In progress...", Toast.LENGTH_SHORT).show();

            Picasso.get()
                    .load(url)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                            File folder = new File(sd, "/Memes Wala/");
                            if (!folder.exists()) {
                                if (!folder.mkdir()) {
                                    Log.e("ERROR", "Cannot create a directory!");
                                } else {
                                    folder.mkdir();
                                }
                            }

                            File fileName = new File(folder, imgName);

                            try {
                                fileName.createNewFile();
                                FileOutputStream ostream = new FileOutputStream(fileName);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                ostream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(ImageDetail.this, "Image Saved Successfully!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            Toast.makeText(ImageDetail.this, "Image Failed to Save", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
        } else {
            requestWriteExternalPermission();
        }
    }

    private void requestWriteExternalPermission() {
        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            if (!isPermissionRequested(PREF_CAMERA_REQUESTED)) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                setPermissionRequested(PREF_CAMERA_REQUESTED);
            } else {
                Toast.makeText(ImageDetail.this, "Please grant storage permission to save images", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setPermissionRequested(String permission) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(permission, true);
        editor.apply();
    }

    private boolean isPermissionRequested(String permission) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean(permission, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("SUCCESS", "Write External permission granted");
                int position = viewPager.getCurrentItem();
                CategoryItem SelectedItem = categoryItemList.get(position);
                String url = SelectedItem.getImageUrl();
                saveImage(url, "MemeWala" + UUID.randomUUID().toString() + ".jpg");
                return;
            }
            Log.e("ERROR", "Permission not granted: results len = " + grantResults.length +
                    " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
            finish();
        }
        Log.d("ERROR", "Got unexpected permission result: " + requestCode);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void dialogLoadSaveIamge() {
        AlertDialog dialog = new SpotsDialog(ImageDetail.this);
        dialog.show();
        dialog.setMessage("Please waiting....");

        String file_name = "MemesWala"+UUID.randomUUID().toString() + ".png";

        int position = viewPager.getCurrentItem();
        CategoryItem SelectedItem = categoryItemList.get(position);
        Picasso.get().load(SelectedItem.getImageUrl())
                .into(new SaveImageHelper(getBaseContext(),
                        dialog,
                        getApplicationContext().getContentResolver(),
                        file_name,
                        "Farooq creation Live Wallpaper"));
        floatingActionMenu.close(true);
    }

    private void LoadMemeImages() {
        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot memesSnapshot : dataSnapshot.getChildren())
                    categoryItemList.add(memesSnapshot.getValue(CategoryItem.class));
                iFirebaseLoadDone.onFirebaseLoadSuccess(categoryItemList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iFirebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.favourite:
                Toast.makeText(this, item+"Clicked", Toast.LENGTH_LONG).show();
                break;
            case R.id.share_image:
                int position = viewPager.getCurrentItem();
                CategoryItem SelectedItem = categoryItemList.get(position);
                Picasso.get()
                        .load(SelectedItem.getImageUrl())
                        .into(targetShare);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFirebaseLoadSuccess(List<CategoryItem> categoryItemList) {
        adapter = new LoopViewAdapter(this, categoryItemList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(Common.sPositon);
    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
    }
}