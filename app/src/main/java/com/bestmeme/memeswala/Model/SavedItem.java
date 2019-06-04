package com.bestmeme.memeswala.Model;

import android.net.Uri;

public class SavedItem {

    public Uri imageUrl;

    public SavedItem(Uri imageUrl) {
        this.imageUrl = imageUrl;
    }

    public SavedItem() {
    }

    public Uri getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Uri imageUrl) {
        this.imageUrl = imageUrl;
    }
}
