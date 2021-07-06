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

import androidx.annotation.Nullable;

import com.example.myclock.tools.GetPath;

import java.io.File;
import java.io.IOException;

public class VideoActivity extends Activity {
    private final String TAG = getClass().getSimpleName();

    EditText editText;
    Button playButton;
    Button pauseButton;
    Button stopButton;
    MediaPlayer mediaPlayer;
    String path;
//    private static final int MEDIAPLAYER_IS_PLAYING = 1;
//    private static final int MEDIAPLAYER_IS_PAUSE = 2;
//    private static final int MEDIAPLAYER_IS_STOP = 3;
//    private static final int MEDIAPLAYER_STATE = MEDIAPLAYER_IS_STOP;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mediaPlayer = new MediaPlayer();
        editText = (EditText)findViewById(R.id.video_path);
        playButton = (Button)findViewById(R.id.play_video);
        pauseButton = (Button)findViewById(R.id.pause_video);
        stopButton = (Button)findViewById(R.id.stop_video);

        playButton.setOnClickListener(listener);
        pauseButton.setOnClickListener(listener);
        stopButton.setOnClickListener(listener);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");

    }
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.play_video:
                   playMedia();
                    break;
                case R.id.pause_video:
                    mediaPause();
                    break;
                case R.id.stop_video:
                    mediaStop();
                    break;
            }
        }
    };
    //初始化MediaPlayer对象
    private void playMedia(){
//        path = editText.getText().toString();
        if(path != null){
            if(!(new File(path).exists())){
                Toast.makeText(this,"指定媒体文件不存在",Toast.LENGTH_LONG).show();
                return;
            }
            Log.d(TAG,path);
            try {
                mediaPlayer.setDataSource(path);
                Log.d(TAG, path);
                if(path.substring(path.length()-4,path.length()).equals(".mp4")){
                    SurfaceHolder surfaceHolder = ((SurfaceView)findViewById(R.id.surfaceview_videoplayer)).getHolder();
                    surfaceHolder.setFixedSize(mediaPlayer.getVideoWidth(),mediaPlayer.getVideoHeight());
                    mediaPlayer.setDisplay(surfaceHolder);
                }
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        Toast.makeText(VideoActivity.this,"开始播放",Toast.LENGTH_LONG).show();
                        mediaPlayer.start();
                        playButton.setEnabled(false);
//                        if(path.substring(path.length()-4,path.length()).equals(".mp4")){
//                            SurfaceHolder surfaceHolder = ((SurfaceView)findViewById(R.id.surfaceview_videoplayer)).getHolder();
//                            surfaceHolder.setFixedSize(mediaPlayer.getVideoWidth(),mediaPlayer.getVideoHeight());
//                            mediaPlayer.setDisplay(surfaceHolder);
//                        }
                    }
                });
                //设置循环播放
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        playButton.setEnabled(true);
                    }
                });
                //播放过程中发生错误的处理
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        replay();
                        return false;
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(VideoActivity.this,"播放失败",Toast.LENGTH_LONG).show();
            }
        }

    }

    //暂停播放
    private void mediaPause(){
        if((pauseButton.getText().toString()).equals("继续")){
            pauseButton.setText("暂停");
            mediaPlayer.start();
            Toast.makeText(VideoActivity.this,"继续播放",Toast.LENGTH_LONG).show();
            return;
        }
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            pauseButton.setText("继续");
            Toast.makeText(VideoActivity.this,"暂停播放",Toast.LENGTH_LONG).show();
        }
    }
    //重新播放
    private void replay(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(0);
            Toast.makeText(VideoActivity.this,"重新播放",Toast.LENGTH_LONG).show();
            pauseButton.setText("暂停");
            return;
        }
        playMedia();

    }
    //停止播放
    private void mediaStop(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
            playButton.setEnabled(true);
            Toast.makeText(VideoActivity.this,"停止播放",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        super.onDestroy();
    }
}

