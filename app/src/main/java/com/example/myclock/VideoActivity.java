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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myclock.tools.GetPath;
import com.example.myclock.view.PlayerLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoActivity extends AppCompatActivity{
    private final String TAG = getClass().getSimpleName();
    private ImageView playAndPauseButton;
    private ImageView fullScreen;
    private TextView processTime;
    private SeekBar processBar;
    private View controllerView;
    private PlayerLayout playerLayout;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private String path;
    private VideoViewModel videoViewModel;
    private mHandler handler = new mHandler();
    private final static int UPDATE = 0;
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

        playAndPauseButton = (ImageView) findViewById(R.id.play_and_pause);
        processBar = (SeekBar)findViewById(R.id.progressBar);
        fullScreen = (ImageView) findViewById(R.id.full_screen);
        processTime = (TextView)findViewById(R.id.time_table);

        playAndPauseButton.setOnClickListener(listener);



        videoViewModel =new ViewModelProvider(this).get(VideoViewModel.class);
        videoViewModel.getCurrentState().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer == 1){
                    if(videoViewModel.mediaExist()){
                        if(videoViewModel.getCurrentPosition() > 0){
                            processTime.setText(formatTime(videoViewModel.getCurrentPosition()));
                            int progress = (int)((videoViewModel.getCurrentPosition()/
                                    (float) videoViewModel.getDuration()) * 100);
                            processBar.setProgress(progress);
                        }else {
                            processTime.setText("00:00");
                            processBar.setProgress(0);
                        }
                    }
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
        playerLayout = new PlayerLayout(this);


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
        processBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    videoViewModel.progressChange(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(r);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                handler.postDelayed(r,HIDDEN_TIME);
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
        }
    }
    private String formatTime(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        return simpleDateFormat.format(new Date(time));
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
        videoViewModel.destroyMediaPlayer();
        super.onDestroy();
    }

    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            Log.d(TAG,"surface被创建");

            videoViewModel.initMedia(path,surfaceHolder);
            playerLayout.setAspectRatio(videoViewModel.getMediaPlayerWidth()
                    ,videoViewModel.getMediaPlayerHeight());
            Log.d(TAG,"视频比例"+surfaceView.getWidth()/videoViewModel.getMediaPlayerWidth()
                    +" "+surfaceView.getHeight()/videoViewModel.getMediaPlayerHeight()+"surfaceview长宽"
            +surfaceView.getHeight()+" "+surfaceView.getWidth());
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

