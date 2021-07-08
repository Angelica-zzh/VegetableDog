package com.example.myclock;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Button;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myclock.tools.ThreadUtils;

import java.io.IOException;

public class VideoViewModel extends ViewModel {
    private final String TAG = getClass().getSimpleName();
    MediaPlayer mediaPlayer = new MediaPlayer();
    int currentPosition = 0;
//    private final int FLAG_STATE_PLAYER_PLAY = 1;
    private final int FLAG_STATE_PLAYER_PAUSE = 2;
    private final int FLAG_STATE_PLAYER_CONTI = 3;

    MutableLiveData<Integer> currentState;

    public MutableLiveData<Integer> getCurrentState() {
        if (currentState == null) {
            currentState = new MutableLiveData<Integer>();
        }
        return currentState;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void initMedia(String path, SurfaceHolder surfaceHolder) {
        try {
//            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.setDisplay(surfaceHolder);
            Log.d(TAG, "开始装载");
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "装载完成");
                    mediaPlayer.start();
                    mediaPlayer.seekTo(currentPosition);
                }
            });
            //设置循环播放
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    currentState.postValue(FLAG_STATE_PLAYER_CONTI);
                }
            });
            //播放过程中发生错误的处理
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    replay(path, surfaceHolder);
                    return false;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void playAndPause(){
        if(currentState.getValue().intValue() == FLAG_STATE_PLAYER_PAUSE){
            mediaPlayer.pause();
            //设置图标为播放
            currentState.postValue(FLAG_STATE_PLAYER_CONTI);
        }else {
            mediaPlayer.start();
            //设置图标为暂停
            currentState.postValue(FLAG_STATE_PLAYER_PAUSE);
        }
    }

    //重新播放
    public void replay(String path, SurfaceHolder surfaceHolder){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(0);
            return;
        }
        mediaPlayer.start();
        //设置图标为暂停
        currentState.postValue(FLAG_STATE_PLAYER_PAUSE);

    }
//    //停止播放
//    public void mediaStop(){
//        if(mediaPlayer != null && mediaPlayer.isPlaying()){
//            mediaPlayer.stop();
//            mediaPlayer.release();
//            mediaPlayer=null;
////            playButton.setEnabled(true);
////            Toast.makeText(VideoActivity.this,"停止播放",Toast.LENGTH_LONG).show();
//        }
//        currentState.postValue(FLAG_STATE_PLAYER_STOP);
//    }
    //destroy mediaPlayer
    public void destroyMediaPlayer(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
        }
    }
}
