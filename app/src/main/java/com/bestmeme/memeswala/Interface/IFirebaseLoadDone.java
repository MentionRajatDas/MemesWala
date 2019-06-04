package com.bestmeme.memeswala.Interface;

import com.bestmeme.memeswala.Model.CategoryItem;

import java.util.List;

public interface IFirebaseLoadDone {

    void onFirebaseLoadSuccess(List<CategoryItem> categoryItemList);
    void onFirebaseLoadFailed(String message);

}
