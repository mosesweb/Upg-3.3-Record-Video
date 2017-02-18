package com.example.studerande.upg33;

import android.Manifest;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import java.lang.ref.PhantomReference;
import java.net.URI;

import static java.net.URI.create;

public class MainActivity extends AppCompatActivity {
public static final int REQUEST_VIDEO_CAPTURE = 1;
public static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 1;
public static final String PREV_VIDEO_PREFS = "video_prefs";
public static boolean hasaccesstoread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button video_button = (Button) findViewById(R.id.video_record_button);
        final Button video_play_button = (Button) findViewById(R.id.vid_play_button);

        SharedPreferences settings = getSharedPreferences(PREV_VIDEO_PREFS, 0);

        // get stored string
        String prevFileUriString = settings.getString("prev_vid_path", "");

        TextView textview = (TextView) findViewById(R.id.textView);
        textview.setText(prevFileUriString);

        if(prevFileUriString != "") {
            // re-define hasaccesstoread
            requestReadAccess();

            if(hasaccesstoread == true) {
            // parse string to uri
            Uri vidUri;
            vidUri = Uri.parse(prevFileUriString);
            VideoView mVideoView = (VideoView) findViewById(R.id.mainvideoView);
            mVideoView.setVideoURI(vidUri);
            runVideo();
        }
    }



        video_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                video_button.setText("RECORDING");
                runVideoRecord();
            }
        });
        video_play_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                runVideo();
            }
        });
    }
    protected void requestReadAccess()
    {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else
        {
            // ok we have permission.. it is cool!
            hasaccesstoread = true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    hasaccesstoread = true;
                    SharedPreferences settings = getSharedPreferences(PREV_VIDEO_PREFS, 0);
                    String prevFileUriString = settings.getString("prev_vid_path", "");
                    if(prevFileUriString != "") {
                        Uri vidUri;
                        vidUri = Uri.parse(prevFileUriString);
                        VideoView mVideoView = (VideoView) findViewById(R.id.mainvideoView);
                        mVideoView.setVideoURI(vidUri);
                        runVideo();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    hasaccesstoread = false;

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    protected void runVideo()
    {
        VideoView mVideoView = (VideoView) findViewById(R.id.mainvideoView);
        mVideoView.start();
    }
    protected void runVideoRecord()
    {
        dispathTakeVideoIntent();
    }

    private void dispathTakeVideoIntent()
    {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if(takeVideoIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        VideoView mVideoView = (VideoView) findViewById(R.id.mainvideoView);
        if(requestCode == REQUEST_VIDEO_CAPTURE
                && resultCode == RESULT_OK)
        {
            Uri videoUri = intent.getData();
            mVideoView.setVideoURI(videoUri);
            mVideoView.start();


            final Button video_button = (Button) findViewById(R.id.video_record_button);
            //video_button.setText(getRealPathFromURI(videoUri));
            String vid_path = getRealPathFromURI(videoUri);
            String videoUriString = videoUri.toString();

            SharedPreferences settings = getSharedPreferences(PREV_VIDEO_PREFS, 0);
            SharedPreferences.Editor editor = settings.edit();

            // store string of uri
            editor.putString("prev_vid_path", videoUriString);
            TextView mTextView = (TextView) findViewById(R.id.textView);
            mTextView.setText(videoUriString);
            editor.commit();
        }

    }

}
