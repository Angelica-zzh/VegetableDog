package com.example.myclock;

import android.content.Context;
import android.content.Intent;

import android.media.MediaPlayer;

import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myclock.tools.GetPath;

import java.io.File;
import java.io.IOException;

public class VideoActivity extends AppCompatActivity{
    private final String TAG = getClass().getSimpleName();
    ImageView playAndPauseButton;
    View controllerView;
    Context mContext = this;
    MediaPlayer mediaPlayer;
    SurfaceHolder surfaceHolder;
    SurfaceView surfaceView;
    String path;
    VideoViewModel videoViewModel;
    mHandler handler = new mHandler();
    private final static int WHAT = 0;
    private static final int HIDDEN_TIME = 5000;

    private Runnable r = new Runnable() {
        @Override
        public void run() {
            controllerView.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

//        mediaPlayer = new MediaPlayer();

        playAndPauseButton = (ImageView) findViewById(R.id.play_and_pause);
//        pauseButton = (Button)findViewById(R.id.pause_video);
//        stopButton = (Button)findViewById(R.id.stop_video);

        playAndPauseButton.setOnClickListener(listener);
//        pauseButton.setOnClickListener(listener);
//        stopButton.setOnClickListener(listener);

        videoViewModel =new ViewModelProvider(this).get(VideoViewModel.class);
        videoViewModel.getCurrentState().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer == 1){

                }else if(integer == 2){
                    playAndPauseButton.setImageResource(R.mipmap.ic_video_pause);
                }else if(integer == 3){
                    playAndPauseButton.setImageResource(R.mipmap.ic_video_play);
                }
            }
        });

        Intent intent = getIntent();
        path = intent.getStringExtra("path");

        surfaceView = (SurfaceView)findViewById(R.id.surfaceview_videoplayer);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(callback);


        controllerView = findViewById(R.id.popwindow);



        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG,"点击");
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        showOrHideController();
                }
                return true;
            }
        });


    }

    private void showOrHideController(){
        if(controllerView.isShown()){
            Log.d(TAG,"设置不可见");
            controllerView.setVisibility(View.INVISIBLE);
        }else {
            Log.d(TAG,"设置可见");
            controllerView.setVisibility(View.VISIBLE);
            handler.postDelayed(r,HIDDEN_TIME);
        }
    }
    private class mHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
//            switch (msg.what){
//                case WHAT:
//            }
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.play_and_pause:
                    videoViewModel.playAndPause();
                    break;

            }
        }
    };

    @Override
    protected void onDestroy() {
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        super.onDestroy();
    }

    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            Log.d(TAG,"surface被创建");
                videoViewModel.initMedia(path,surfaceHolder);

        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            Log.d(TAG,"surface大小改变");
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            Log.d(TAG,"surface被销毁");
            videoViewModel.destroyMediaPlayer();
        }
    };
}

