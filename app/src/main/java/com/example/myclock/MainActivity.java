package com.example.myclock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.QuoteSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.myclock.tools.GetPath;
import com.example.myclock.view.ClockView;
import com.example.myclock.view.PlayerLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {
    private final String TAG = getClass().getSimpleName();
    private final int REQUEST_CODE_PHOTO = 1;
    private String filePath = null;
    private ClockView clockView;
    private TextView textView1;
    private Button videoButton;
    private TimerHandler mHandler = new TimerHandler();
    private Timer timer;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
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
                openGallery();
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
        checkPermissions();
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
        Calendar calendar = Calendar.getInstance();
        String s = format.format(calendar.getTime());
        textView1.setText(s);
        clockView.setTime(calendar);


    }
    //视频选择
    public void openGallery() {
        //从相册中选择
        //Intent openAlbumIntent = new Intent(Intent.ACTION_PICK);
        //openAlbumIntent.setType("image/*");
        //startActivityForResult(openAlbumIntent, REQUST_CODE_PHOTO);
        //按文件夹选择
        Intent openAlbumIntent = new Intent(Intent.ACTION_PICK);
        openAlbumIntent.setType("*/*");
        Intent wrapperIntent = Intent.createChooser(openAlbumIntent, null);
        startActivityForResult(wrapperIntent, REQUEST_CODE_PHOTO);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        filePath = null;
        if (requestCode == REQUEST_CODE_PHOTO) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri originalUri = data.getData();
                    if (originalUri == null || originalUri.equals("")) {
                        return;
                    }
                    Log.e(TAG, "originalUri=" + originalUri);
                    filePath = GetPath.getPathFromUri(this, originalUri);
                    Log.e(TAG, "filePath=" + filePath);
                }
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                intent.putExtra("path",filePath);
                startActivity(intent);
            }
        }

        if (TextUtils.isEmpty(filePath)) {
            return;
        }
    }


}