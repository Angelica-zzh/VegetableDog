package com.example.myclock;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Button;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myclock.tools.ThreadUtils;
import com.example.myclock.view.PlayerLayout;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class VideoViewModel extends ViewModel {
    private final String TAG = getClass().getSimpleName();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private int currentPosition = 0;
    private int mediaPlayerWidth;
    private int mediaPlayerHeight;
    private final int FLAG_STATE_PLAYER_PLAY = 1;
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
        currentPosition = mediaPlayer.getCurrentPosition();
        return currentPosition;
    }
    public int getDuration(){
        int playerDuration = mediaPlayer.getDuration();
        return playerDuration;
    }
    public boolean mediaExist(){
        return mediaPlayer!=null;
    }
    public int getMediaPlayerWidth(){ return mediaPlayerWidth;}
    public int getMediaPlayerHeight(){ return mediaPlayerHeight;}

    Runnable r =new Runnable() {
        @Override
        public void run() {
            currentState.postValue(FLAG_STATE_PLAYER_PLAY);
        }
    };
    public void initMedia(String path, SurfaceHolder surfaceHolder) {
        try {
//            mediaPlayer = new MediaPlayer();;
//            playerLayout.setAspectRatio(mediaPlayer.getVideoWidth(),mediaPlayer.getVideoHeight());
            mediaPlayer.setDataSource(path);
            mediaPlayer.setDisplay(surfaceHolder);

            mediaPlayer.prepare();
            Log.d(TAG, "视频长宽"+mediaPlayer.getVideoWidth()+" "+mediaPlayer.getVideoHeight());
            mediaPlayerWidth = mediaPlayer.getVideoWidth();
            mediaPlayerHeight = mediaPlayer.getVideoHeight();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "装载完成");
                    mediaPlayer.start();
                    mediaPlayer.seekTo(currentPosition);
                    currentState.postValue(FLAG_STATE_PLAYER_PAUSE);
                    ThreadUtils.getInstance().scheduleExecure(r,0,1000, TimeUnit.MILLISECONDS);
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
    //进度条改变
    public void progressChange(int progress){
        int playtime = progress * mediaPlayer.getDuration() / 100;
        mediaPlayer.seekTo(playtime);
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
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
