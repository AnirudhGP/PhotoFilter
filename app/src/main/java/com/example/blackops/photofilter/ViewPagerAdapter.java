package com.example.blackops.photofilter;

/**
 * Created by Black Ops on 22-02-2016.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

public class ViewPagerAdapter extends PagerAdapter {
    // Declare Variables
    Context context;
    LayerDrawable[] filters;
    int width,height;
    Bitmap bitmap;
    Bitmap b;
    ImageView imgflag;
    public ViewPagerAdapter(Context context, LayerDrawable[] filters,Bitmap bitmap,int width,int height) {
        this.filters =filters;
        this.context=context;
        this.bitmap=bitmap;
        this.width=width;
        this.height=height;
        }

    @Override
    public int getCount() {
        return filters.length+1 ;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
        public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container,
                false);
        imgflag=(ImageView)itemView.findViewById(R.id.image_filter);
        imgflag.getLayoutParams().height = height;
        imgflag.getLayoutParams().width = width;
        imgflag.requestLayout();
        if(position==0)
        {
            Log.d("bitmap",bitmap.getHeight()+" "+bitmap.getWidth());
            imgflag.setImageBitmap(bitmap);
            ((ViewPager) container).addView(itemView);
            Toast.makeText(context,"Swipe right for filters",Toast.LENGTH_LONG).show();
            FloatingActionButton fab = (FloatingActionButton) itemView.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("FAB", "Here");
                    MainActivity.setImage(0);
                    ((Gallery) context).finish();
                }
            });
        }
        else {
            b = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            filters[position-1].setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            filters[position-1].draw(new Canvas(b));
            imgflag.setImageBitmap(b);
            ((ViewPager) container).addView(itemView);
            FloatingActionButton fab = (FloatingActionButton) itemView.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("FAB", "Here");
                    MainActivity.pos = position-1;
                    MainActivity.setImage(1);
                    ((Gallery) context).finish();
                }
            });
        }
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}

