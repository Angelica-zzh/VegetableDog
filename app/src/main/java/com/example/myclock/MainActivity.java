package com.example.myclock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.myclock.view.ClockView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ClockView clock_view = findViewById(R.id.clock_view);
        clock_view.start();
    }
}