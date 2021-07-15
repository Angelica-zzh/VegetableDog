package com.example.myclock;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.LightingColorFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;

import android.os.BatteryManager;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myclock.tools.DisplayUtils;
import com.example.myclock.tools.GetPath;
import com.example.myclock.tools.LightnessControl;
import com.example.myclock.view.BatteryView;
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
    private TextView durationTime;
    private TextView speed;
    private SeekBar processBar;
    private View controllerView;
    private BatteryView batteryView;
    private PlayerLayout surfaceLayout;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private String path;
    private VideoViewModel videoViewModel;
    private mHandler handler = new mHandler();
    private static final int HIDDEN_TIME = 5000;
    private  int downX,downY;
    private Context context;

    //使一段时间后控制栏消失
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            if(controllerView.isShown()){
                controllerView.setVisibility(View.INVISIBLE);
                }
            }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        //播放和暂停按键
        playAndPauseButton = (ImageView) findViewById(R.id.play_and_pause);
        playAndPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoViewModel.playAndPause();
            }
        });

        //进度条操作
        processBar = (SeekBar)findViewById(R.id.progressBar);
        processBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //进度条改变之后
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    videoViewModel.progressChange(progress);
                }
            }
            //进度条拖动开始拖动的时候调用
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(r);
            }
            //进度条停止拖动时调用
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //一段时间之后隐藏工具栏
                handler.postDelayed(r,HIDDEN_TIME);
            }
        });

        //全屏按键
        fullScreen = (ImageView) findViewById(R.id.full_screen);
        surfaceLayout = (PlayerLayout)findViewById(R.id.surfacelayout);
        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(r,HIDDEN_TIME);
                //横屏改为竖屏
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    surfaceLayout.getLayoutParams().width = WindowManager.LayoutParams.MATCH_PARENT;
                    surfaceLayout.getLayoutParams().height = DisplayUtils.dp2px(VideoActivity.this,260);
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_VISIBLE);

                } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    surfaceLayout.getLayoutParams().width = WindowManager.LayoutParams.MATCH_PARENT;
                    surfaceLayout.getLayoutParams().height = WindowManager.LayoutParams.MATCH_PARENT;
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    |View.SYSTEM_UI_FLAG_FULLSCREEN
                                    |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    );
                }
                surfaceLayout.setLayoutParams(surfaceLayout.getLayoutParams());
            }
        });
        //初始化音量
        initVolume();
        //初始化亮度
        initBrightness();

        //设置电池
        batteryView = (BatteryView) findViewById(R.id.my_battery);
        initBattery();

        //控制栏
        controllerView = findViewById(R.id.popwindow);

        //获取播放路径
        Intent intent = getIntent();
        path = intent.getStringExtra("path");

        //给surfaceView添加回调办法
        surfaceView = (SurfaceView)findViewById(R.id.surfaceview_videoplayer);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(callback);
        //surfaceView点击操作
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"点击");
                showOrHideController();
            }
        });

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        downX = (int)event.getX();
                        downY = (int)event.getY();
                        Log.d(TAG,"点击坐标"+downX+" "+downY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int distanceX = (int)event.getX() - downX;
                        int distanceY = (int)event.getY() - downY;
                       if(event.getX()>DisplayUtils.getScreenWidthPixels(VideoActivity.this)/2){
                           changeVolume(distanceX,distanceY);
                       }else {
                           changeBrightness(distanceX,distanceY);
                       }
                        break;
                }
                return false;
            }
        });

        //视频进行时间
        processTime = (TextView)findViewById(R.id.time_table);
        durationTime = (TextView)findViewById(R.id.time_duration);
        //ViewModel
        videoViewModel =new ViewModelProvider(this).get(VideoViewModel.class);
        videoViewModel.getProcessState().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer == 1) {
                    if (videoViewModel.mediaExist()) {
                        if (videoViewModel.getCurrentPosition() > 0) {
                            processTime.setText(formatTime(videoViewModel.getCurrentPosition()));
                            int progress = (int) ((videoViewModel.getCurrentPosition() /
                                    (float) videoViewModel.getDuration()) * 100);
                            processBar.setProgress(progress);
                        } else {
                            processTime.setText("00:00");
                            processBar.setProgress(0);
                        }
                    }
                }
                initBattery();
            }
        });
        //切换播放暂停状态
        videoViewModel.getCurrentState().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 2) {
                    playAndPauseButton.setImageResource(R.mipmap.ic_video_pause);
                } else if (integer == 3) {
                    playAndPauseButton.setImageResource(R.mipmap.ic_video_play);
                }
            }
        });
        //倍速播放
        speed = (TextView)findViewById(R.id.play_speed);
        speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoViewModel.setSpeed();
            }
        });
        videoViewModel.getCurrentSpeed().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer == 1){
                    speed.setText("1.0X");
                }else if(integer == 2){
                    speed.setText("2.0X");
                }
            }
        });

    }

    //控制栏可见和不可见
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
    //
    private class mHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    }
    //进度条时间格式
    private String formatTime(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        return simpleDateFormat.format(new Date(time));
    }

    @Override
    protected void onPause() {
        videoViewModel.playAndPause();
//        videoViewModel.getCurrentPosition();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        videoViewModel.destroyMediaPlayer();
        super.onDestroy();
    }

    //surfaceView的回调办法
    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            Log.d(TAG,"surface被创建");

            videoViewModel.initMedia(path,surfaceHolder);
            surfaceLayout.setAspectRatio(videoViewModel.getMediaPlayerWidth()
                    ,videoViewModel.getMediaPlayerHeight());
            durationTime.setText(formatTime(videoViewModel.getDuration()));
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            Log.d(TAG,"surface大小改变");
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            Log.d(TAG,"surface被销毁");
            Log.d(TAG,""+videoViewModel.getCurrentPosition());
            videoViewModel.setCurrentPosition();
            videoViewModel.destroyMediaPlayer();
        }
    };
    //获取当前电量
    private void initBattery(){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level * 100 / (float)scale;
        batteryView.setPower((int)batteryPct);
    }

    //初始化音量
    private AudioManager audioManager;
    private RelativeLayout volume_layout;
    private ImageView volume_picture;
    private TextView volumeNum;
    private int maxVolume,currentVolume;

    private void initVolume(){
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        volume_picture =(ImageView)findViewById(R.id.volume_picture);
        volumeNum = (TextView)findViewById(R.id.volume);
        volume_layout = findViewById(R.id.player_volume);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volume_layout.setVisibility(View.INVISIBLE);
        Log.d("当前音量",currentVolume+"");
    }

    //改变音量
    private void changeVolume(int x,int y){
        volume_layout.setVisibility(View.VISIBLE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if(currentVolume == 0){
            volume_picture.setImageResource(R.mipmap.mn_player_volume_close);
        }else {
            volume_picture.setImageResource(R.mipmap.mn_player_volume_open);
        }
        if(Math.abs(x)<50 && y>150){
            //降低音量
            if (currentVolume>0){
                currentVolume--;
                Log.d("音量减少",currentVolume+"");
            }
        }else if(Math.abs(x)<50 && y< -150){
            if(currentVolume<maxVolume){
                currentVolume++;
                Log.d("音量增加",currentVolume+"");
            }
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume,AudioManager.FLAG_SHOW_UI);
        int percentage = (currentVolume*100)/maxVolume;
        volumeNum.setText("音量："+percentage+"%");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(volume_layout.isShown()){
                    volume_layout.setVisibility(View.INVISIBLE);
                }
            }
        },HIDDEN_TIME);
    }

    private RelativeLayout brightness_layout;
    private TextView brightnessNum;
    private int currentLight;

//    初始化亮度
    private void initBrightness(){
        brightness_layout = (RelativeLayout)findViewById(R.id.player_brightness);
        brightnessNum = (TextView)findViewById(R.id.brightness);
        brightness_layout.setVisibility(View.INVISIBLE);

        ContentResolver contentResolver = this.getContentResolver();
        try {
            int mode = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }
    //改变亮度
    private void changeBrightness(int x,int y){
        brightness_layout.setVisibility(View.VISIBLE);

        if(Math.abs(x)<50 && y>150){
            //降低亮度
            if(LightnessControl.GetLightness(this)>10){
                LightnessControl.SetLightness(this, -10);
                Log.d("亮度减少",LightnessControl.GetLightness(this)+"");
            }
        }else if(Math.abs(x)<50 && y< -150){
            //增加亮度
            if(LightnessControl.GetLightness(this)<245){
                LightnessControl.SetLightness(this, 10);
                Log.d("亮度增加",LightnessControl.GetLightness(this)+"");
            }
        }
        currentLight = LightnessControl.GetLightness(this);
        int percentage = (currentLight*100)/255;
        brightnessNum.setText("亮度："+percentage+"%");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(brightness_layout.isShown()){
                    brightness_layout.setVisibility(View.INVISIBLE);
                }
            }
        },HIDDEN_TIME);
    }



}

