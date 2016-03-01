package com.example.blackops.photofilter;

/**
 * Created by Black Ops on 22-02-2016.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
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
    LayerDrawable[] flag;
    Bitmap bitmap;
    ImageView imgflag;
    public ViewPagerAdapter(Context context, LayerDrawable[] flag,Bitmap bitmap) {
        this.flag = flag;
        this.context=context;
        this.bitmap=bitmap;
        }

    @Override
    public int getCount() {
        return flag.length+1 ;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
        public Object instantiateItem(ViewGroup container, final int position) {

        // Declare Variables

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container,
                false);
        imgflag=(ImageView)itemView.findViewById(R.id.flag);
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
                    MainActivity.pos = position;
                    MainActivity.mImageView.setImageBitmap(bitmap);
                    MainActivity.comment.setEnabled(true);
                    MainActivity.comment.bringToFront();
                    ((Gallery) context).finish();
                }
            });
        }
        else {
            imgflag.setImageDrawable(flag[position-1]);
            // Add viewpager_item.xml to ViewPager
            ((ViewPager) container).addView(itemView);
            FloatingActionButton fab = (FloatingActionButton) itemView.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("FAB", "Here");
                    MainActivity.pos = position-1;
                    MainActivity.mImageView.setImageDrawable(flag[position-1]);
                    MainActivity.comment.setEnabled(true);
                    MainActivity.comment.bringToFront();
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

