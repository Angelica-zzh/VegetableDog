package com.example.myclock;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
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
    private final int FLAG_STATE_PLAYER_PROCESS = 1;
    private final int FLAG_STATE_PLAYER_PAUSE = 2;
    private final int FLAG_STATE_PLAYER_CONTI = 3;
    private final int SPEED_1 = 1;
    private final int SPEED_2 = 2;

    //播放状态
    MutableLiveData<Integer> currentState = new MutableLiveData<>();
    public MutableLiveData<Integer> getCurrentState() {
        return currentState;
    }
    //播放进度
    MutableLiveData<Integer> processState = new MutableLiveData<>();
    public MutableLiveData<Integer> getProcessState() {
        return processState;
    }
    //播放速度
    MutableLiveData<Integer> speedState = new MutableLiveData<>();
    public MutableLiveData<Integer> getCurrentSpeed() {
        return speedState;
    }

    public int getCurrentPosition() {
        currentPosition = mediaPlayer.getCurrentPosition();
        return currentPosition;
    }
    public void setCurrentPosition(){
        currentPosition = mediaPlayer.getCurrentPosition();
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
            processState.postValue(FLAG_STATE_PLAYER_PROCESS);
        }
    };
    //初始化mediaplayer
    public void initMedia(String path, SurfaceHolder surfaceHolder) {
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.prepare();
            mediaPlayerWidth = mediaPlayer.getVideoWidth();
            mediaPlayerHeight = mediaPlayer.getVideoHeight();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "装载完成");
                    currentState.postValue(FLAG_STATE_PLAYER_PAUSE);
                    speedState.postValue(1);
                    speedChange(1);
                    mediaPlayer.start();
                    mediaPlayer.seekTo(currentPosition);
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
//暂停和播放操作
    public void playAndPause(){
        if(currentState.getValue().intValue() == FLAG_STATE_PLAYER_CONTI){
            mediaPlayer.start();
            //设置图标为暂停
            currentState.postValue(FLAG_STATE_PLAYER_PAUSE);
            Log.d(TAG, "继续");
        }else if(currentState.getValue().intValue() == FLAG_STATE_PLAYER_PAUSE){
            mediaPlayer.pause();
            //设置图标为播放
            currentState.postValue(FLAG_STATE_PLAYER_CONTI);
            Log.d(TAG, "暂停");
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
    //倍速播放
    public void speedChange(float speed){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            PlaybackParams playbackParams = mediaPlayer.getPlaybackParams();
            playbackParams.setSpeed(speed);
            mediaPlayer.setPlaybackParams(playbackParams);
        }
    }
    public void setSpeed(){
        if(speedState.getValue() == 1){
            speedChange(2);
            speedState.postValue(SPEED_2);
        }else {
            speedChange(1);
            speedState.postValue(SPEED_1);
        }
    }

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
