package com.company.chirag.eventize;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;


import com.company.chirag.eventize.Imaging.Tools;
import com.company.chirag.eventize.R;
import com.company.chirag.eventize.TessTool.TessEngine;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.Query;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOperations;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncTable;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.company.chirag.eventize.R.layout.custom_fullimage_dialog;
import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.*;

public class MainActivity extends Activity {

    public String ans;
    public static String TAG = "OCR";
    Parser parse;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button captureImage;
    private Bitmap bitmap;
    private MobileServiceClient mClient;
    private boolean first;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<ToDoItem> mToDoTable;

    //Offline Sync
    /**
     * Mobile Service Table used to access and Sync data
     */
    //private MobileServiceSyncTable<ToDoItem> mToDoTable;

    /**
     * Adapter to sync the items list with the view
     */


    /**
     * EditText containing the "New To Do" text
     */
    private EditText mTextNewToDo;

    /**
     * Progress spinner to use for table operations
     */
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!hasCamera())
            captureImage.setEnabled(false);
        captureImage = (Button) findViewById(R.id.captureImage);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        try {
            mClient = new MobileServiceClient(
                    "https://eventeyes.azure-mobile.net/",
                    "ykaEaIPEUWYxjiDlfowHUrSiuyGeJt17",
                    this
            );


        }
        catch(Exception e)
        {
            Log.v(TAG,"DOES NOT WORK");
        }
    }

    public void launchList(View view){

        finish();
        
        Intent i = new Intent(this, TrendingActivity.class);
        startActivity(i);
    }





    private boolean finished;

    public void onResume(){
        super.onResume();
        Log.v(TAG, "Got to onResume");
        if (ans != null) {
            new BackgroundLoadingTask().execute("");

        }


    }


    public void loadCalendar(Bitmap image){

        TessEngine engine = new TessEngine(getApplicationContext());
        ans = engine.detectText(image);
        Log.v(TAG, "YES NO MAYBE SO");
    //  Bitmap bp = BitmapFactory.decodeResource(getResources(), R.drawable.flyer);
     // ans = engine.detectText(bp);
    //    ans = ans.replaceAll("[^a-zA-Z0-9]+", " ");

        Log.d(TAG, "Got the answer: " + ans);

    }



    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.company.chirag.eventize.MainActivity";


    public void launchCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Create the File where the photo should go
            File path = null;
                path = new File(getFilesDir() + "/tesseract/");
                if (!path.exists()) path.mkdirs();
                File image = new File(path, "image.jpg");
        Log.d(TAG, "b4");
                Uri imageUri = FileProvider.getUriForFile(this, CAPTURE_IMAGE_FILE_PROVIDER, image);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);


                // Error occurred while creating the File
                File myFile = this.getFileStreamPath("image.jpeg");
                if(myFile != null){
                    Log.d(TAG, "File found, file description: "+myFile.toString());
                }else{
                    Log.d(TAG, "File not found!");
                }

        //take picture and pass results to onActivityResult

    }

    //if you want to return image taken
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        first = true;
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {


            Log.d(TAG, "Finished the photoshoot");
            File file = new File(getFilesDir() + "/tesseract/image.jpg");
            Log.d(TAG, "THis file is of size " + file.length());
            bitmap = BitmapFactory.decodeFile(file.getPath());
            if (bitmap == null) Log.d(TAG, "BITMAP IS NULL");
            Log.d(TAG, "Got the photo");

            final int maxSize = 900;
            int outWidth;
            int outHeight;
            int inWidth = bitmap.getWidth();
            int inHeight = bitmap.getHeight();
            if(inWidth > inHeight){
                outWidth = maxSize;
                outHeight = (inHeight * maxSize) / inWidth;
            } else {
                outHeight = maxSize;
                outWidth = (inWidth * maxSize) / inHeight;
            }

            bitmap = Tools.rotateBitmap(bitmap, 90);
            bitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, true);

            ImageView iv = new ImageView(getApplicationContext());
            iv.setImageBitmap(bitmap);
            //captureImage.setBackground(new BitmapDrawable(getResources(), bitmap));*/

            loadPhoto(iv, bitmap.getWidth(), bitmap.getHeight());


        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }

    }

    private void loadPhoto(ImageView imageView, int width, int height) {

        ImageView tempImageView = imageView;


        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(custom_fullimage_dialog,
                (ViewGroup) findViewById(R.id.layout_root));
        ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
         image.setImageDrawable(tempImageView.getDrawable());
        imageDialog.setView(layout);
        imageDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                ans = "test";
                onResume();
                dialog.dismiss();

            }

        });

       imageDialog.setNegativeButton("No", new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int which) {
                ans = "";
                dialog.dismiss();

            }

        });


        imageDialog.create();
        imageDialog.show();
    }

    public static Bitmap createContrast(Bitmap src, double value) {
// image size
        int width = src.getWidth();
        int height = src.getHeight();
// create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
// color information
        int A, R, G, B;
        int pixel;
// get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

// scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int) (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (R < 0) {
                    R = 0;
                } else if (R > 255) {
                    R = 255;
                }

                G = Color.red(pixel);
                G = (int) (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (G < 0) {
                    G = 0;
                } else if (G > 255) {
                    G = 255;
                }

                B = Color.red(pixel);
                B = (int) (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (B < 0) {
                    B = 0;
                } else if (B > 255) {
                    B = 255;
                }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }

    private int truncate(int value) {
        if (value < 0) {
            return 0;
        } else if (value > 255) {
            return 255;
        }

        return value;
    }

    class BackgroundLoadingTask extends AsyncTask<String, Void, String> {

        Intent i;
        @Override
        public void onPreExecute () {
            setContentView(R.layout.progress_bar);
            findViewById(R.id.marker_progress).setVisibility(View.VISIBLE);
        }


        @Override
        protected String doInBackground(String... params) {
            loadCalendar(bitmap);

            parse = new Parser();

            Log.v(TAG, "ANS: " + ans);
            i = parse.filterTest(ans);
            finished = true;

            ans = null;



            Log.v(TAG, "ANS: " + ans);

            return null;

        }


        @Override
        protected void onPostExecute(String result) {
            SimpleDateFormat sdf = new SimpleDateFormat(("MM/dd/yyyy"));
            ToDoItem item = new ToDoItem();
            if(parse.getTo() != null && parse.getFrom()!= null) {
                item = new ToDoItem(parse.getEvent(), parse.getLocation(), sdf.format(parse.getTo().getTime()), sdf.format(parse.getFrom().getTime()));
            }
            else
            {
                item = new ToDoItem(parse.getEvent(), parse.getLocation(), "","");
            }
            mClient.getTable(ToDoItem.class).insert(item);
            // hide the progress bar
            findViewById(R.id.marker_progress).setVisibility(View.GONE);
            setContentView(R.layout.activity_main);
            startActivity(i);
        }
    }

}


