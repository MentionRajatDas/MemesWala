package com.bestmeme.memeswala.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bestmeme.memeswala.Model.CategoryItem;
import com.bestmeme.memeswala.R;
import com.squareup.picasso.Picasso;
import java.util.List;

public class LoopViewAdapter extends PagerAdapter {

    private LayoutInflater layoutInflater;
    private Context context;
    private List<CategoryItem> categoryItemList;
    private CategoryItem ci;

    public LoopViewAdapter(Context context, List<CategoryItem> categoryItemList) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.categoryItemList = categoryItemList;
    }

    @Override
    public int getCount() {
        return categoryItemList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        ci = categoryItemList.get(position);
        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView=layoutInflater.inflate(R.layout.detail_viewpager_item,container,false);

//        ImageView meme_image = (ImageView) itemView.findViewById(R.id.ViewPagerImage);

//        Picasso.get().load(categoryItemList.get(position).getImageUrl()).into(meme_image);


        ImageView mImage = (ImageView) itemView.findViewById(R.id.ViewPagerImage);

        Picasso.get()
                .load(ci.getImageUrl())
                .into(mImage);

        container.addView(itemView);
        return itemView;
    }
}