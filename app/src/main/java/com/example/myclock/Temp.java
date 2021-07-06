package com.example.myclock;


import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Temp {

    MediaPlayer mp;
    SurfaceView sv;
    View v;

    void fun() {
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//
//                }
//                MotionEvent.ACTION_DOWN;
//                MotionEvent.ACTION_UP;
//                MotionEvent.ACTION_MOVE;
                event.getX();
                event.getY();
                return v.onTouchEvent(event);
            }
        });


        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }
}
