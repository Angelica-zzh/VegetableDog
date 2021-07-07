package com.example.myclock;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    EditText editText;
    Button playButton;
    Button pauseButton;
    Button stopButton;
    MediaPlayer mediaPlayer;
    SurfaceHolder surfaceHolder;
    SurfaceView surfaceView;
    String path;
    VideoViewModel videoViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

//        mediaPlayer = new MediaPlayer();
        editText = (EditText)findViewById(R.id.video_path);
        playButton = (Button)findViewById(R.id.play_video);
        pauseButton = (Button)findViewById(R.id.pause_video);
        stopButton = (Button)findViewById(R.id.stop_video);

        playButton.setOnClickListener(listener);
        pauseButton.setOnClickListener(listener);
        stopButton.setOnClickListener(listener);

        videoViewModel =new ViewModelProvider(this).get(VideoViewModel.class);
        videoViewModel.getCurrentState().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer == 1){
                    playButton.setEnabled(false);
                }else if(integer == 2){
                    pauseButton.setText("继续");
                }else if(integer == 3){
                    pauseButton.setText("暂停");
                }else if(integer == 4){
                    playButton.setEnabled(true);
                }
            }
        });

        Intent intent = getIntent();
        path = intent.getStringExtra("path");

        surfaceView = (SurfaceView)findViewById(R.id.surfaceview_videoplayer);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(callback);
//        surfaceHolder = ((SurfaceView)findViewById(R.id.surfaceview_videoplayer)).getHolder();
//        surfaceHolder.setFixedSize(mediaPlayer.getVideoWidth(),mediaPlayer.getVideoHeight());



    }
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.play_video:
                    play();
                    break;
                case R.id.pause_video:
                    pause();
                    break;
                case R.id.stop_video:
                    stop();
                    break;
            }
        }
    };
    private void play(){
        videoViewModel.playMedia(path,surfaceHolder);

    }
    private void pause(){
        if(pauseButton.getText().toString().equals("暂停")){
            videoViewModel.mediaPause();
        }else {
            videoViewModel.mediaContinue();
        }


    }
    private void stop(){
        videoViewModel.mediaStop();
    }
//    //播放
//    private void playMedia(int currentPosition){
//
//            try {
//                mediaPlayer = new MediaPlayer();
//                mediaPlayer.setDataSource(path);
//                mediaPlayer.setDisplay(surfaceHolder);
//                mediaPlayer.prepareAsync();
//                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        Toast.makeText(VideoActivity.this,"开始播放",Toast.LENGTH_LONG).show();
//                        mediaPlayer.start();
//                        mediaPlayer.seekTo(currentPosition);
//                        playButton.setEnabled(false);
//
//                    }
//                });
//                //设置循环播放
//                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        playButton.setEnabled(true);
//                    }
//                });
//                //播放过程中发生错误的处理
//                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                    @Override
//                    public boolean onError(MediaPlayer mp, int what, int extra) {
//                        replay();
//                        return false;
//                    }
//                });
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(VideoActivity.this,"播放失败",Toast.LENGTH_LONG).show();
//            }
////        }
//
//    }
//
//    //暂停播放
//    private void mediaPause(){
//        if((pauseButton.getText().toString()).equals("继续")){
//            pauseButton.setText("暂停");
//            mediaPlayer.start();
//            Toast.makeText(VideoActivity.this,"继续播放",Toast.LENGTH_LONG).show();
//            return;
//        }
//        if(mediaPlayer != null && mediaPlayer.isPlaying()){
//            mediaPlayer.pause();
//            pauseButton.setText("继续");
//            Toast.makeText(VideoActivity.this,"暂停播放",Toast.LENGTH_LONG).show();
//        }
//    }
//    //重新播放
//    private void replay(){
//        if(mediaPlayer != null && mediaPlayer.isPlaying()){
//            mediaPlayer.seekTo(0);
//            Toast.makeText(VideoActivity.this,"重新播放",Toast.LENGTH_LONG).show();
//            pauseButton.setText("暂停");
//            return;
//        }
//        playMedia(currentPosition);
//
//    }
//    //停止播放
//    private void mediaStop(){
//        if(mediaPlayer != null && mediaPlayer.isPlaying()){
//            mediaPlayer.stop();
//            mediaPlayer.release();
//            mediaPlayer=null;
//            playButton.setEnabled(true);
//            Toast.makeText(VideoActivity.this,"停止播放",Toast.LENGTH_LONG).show();
//        }
//    }

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
            if(videoViewModel.getCurrentPosition() > 0){
                videoViewModel.playMedia(path,surfaceHolder);
            }
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

