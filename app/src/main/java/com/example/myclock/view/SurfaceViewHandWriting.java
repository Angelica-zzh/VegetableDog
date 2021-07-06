package com.example.myclock.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class SurfaceViewHandWriting extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    public SurfaceViewHandWriting(Context context) {
        super(context);
    }

    public SurfaceViewHandWriting(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SurfaceViewHandWriting(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void run() {

    }
}
