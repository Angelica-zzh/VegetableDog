package com.example.myclock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.QuoteSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.myclock.view.ClockView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    ClockView clockView;
    TextView textView1;
    Button videoButton;
    TimerHandler mHandler = new TimerHandler();
    private Timer timer;
    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");

    private static final int MSG_CLOCK = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1 = (TextView) findViewById(R.id.text);
        clockView= (ClockView)findViewById(R.id.clock_view);
        videoButton = (Button)findViewById(R.id.videoplayer);

        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoView.class);
                startActivity(intent);
            }
        });

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message =mHandler.obtainMessage();
                message.what = MSG_CLOCK;
                mHandler.sendMessage(message);
            }
        },0,1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
//            clockView.stop();
        }
    }

    private class TimerHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==MSG_CLOCK){
                updateClock();
            }
        }
    }

    public void updateClock(){
        Log.d(TAG,"update");
        Calendar calendar = Calendar.getInstance();
        String s = format.format(calendar.getTime());
        Log.d(TAG,s);
        textView1.setText(s);
        clockView.setTime(calendar);


    }


}