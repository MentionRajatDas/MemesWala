package com.bestmeme.memeswala.Model;

public class CategoryItem {

    public String name, imageUrl;

    public CategoryItem(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public CategoryItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
