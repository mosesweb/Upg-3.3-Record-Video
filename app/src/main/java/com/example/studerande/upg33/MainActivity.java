package com.example.studerande.upg33;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
static final int REQUEST_VIDEO_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button video_button = (Button) findViewById(R.id.video_record_button);
        video_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                video_button.setText("RECORDING");
                runVideoRecord();
            }
        });
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
        }
    }

}
