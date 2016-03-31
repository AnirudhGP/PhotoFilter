package com.example.blackops.photofilter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.GLES10;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri mImageUri;
    public static int width,height;
    public static ImageView mImageView;
    String mCurrentPhotoPath;
    public static int pos;
    public static LayerDrawable filters[];
    Bitmap filter;
    LayerDrawable layerDrawable;
    Drawable[] layers;
    private int screenwidth;
    public static EditText comment;
    public static Bitmap bitmap;
    int poss=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mImageView = (ImageView) findViewById(R.id.imageView);
        comment = (EditText) findViewById(R.id.editText);
        comment.setEnabled(false);
                poss=0;
        screenwidth = 0;

        filters = new LayerDrawable[6];
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Camera", "Here");
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mImageUri = Uri.fromFile(photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                mImageUri);
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }
            });

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        Log.d("image",image.length()+"");
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            this.grabImage();
        }
    }
    public static void setImage()
    {
        Bitmap b = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        filters[pos].setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        filters[pos].draw(new Canvas(b));

        mImageView.setImageBitmap(b);
    }
    public void grabImage()
    {
        this.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = this.getContentResolver();
        try
        {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri); //image from camera
            Log.d("bitmap",bitmap.getHeight()+" "+bitmap.getWidth());
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            height = displaymetrics.heightPixels;
            width = displaymetrics.widthPixels;
            Bitmap bitmap_temp = decodeFile(new File(mImageUri.getPath()));
            bitmap = bitmap_temp;
            Log.d("bitmap2",bitmap.getHeight()+" "+bitmap.getWidth());
            Gallery.bitmap=bitmap;
             for(int i = 0;i<6;i++) {
                String fileName = "ccborder" + i;
                int resID= getApplicationContext().getResources().getIdentifier(fileName, "drawable", getApplicationContext().getPackageName());
                filter = decodeSampledBitmapFromResource(getResources(), resID, bitmap.getHeight(),bitmap.getWidth());
                layers = new Drawable[2];
                layers[0] = new BitmapDrawable(getResources(),bitmap);
                layers[1] = new BitmapDrawable(getResources(),filter);
                layerDrawable = new LayerDrawable(layers);
                filters[i]=layerDrawable;
            }
            Intent i = new Intent(MainActivity.this,Gallery.class);
            startActivity(i);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d("vav", "Failed to load", e);
        }
    }
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }
        return inSampleSize;
    }
    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            //Find the correct scale value. It should be the power of 2.
            Log.d("original",o.outWidth+" : "+o.outHeight);
            Log.d("screen",width+" : "+height);
            final int REQUIRED_SIZE=70;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp<width && height_tmp<height)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale++;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}