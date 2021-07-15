package com.example.myclock.tools;

import android.app.Activity;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class LightnessControl {
    // 改变亮度
    public static void SetLightness(Activity act, int value) {
        int currentNum;
        try {
            currentNum = Settings.System.getInt(act.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            currentNum += value;
            Settings.System.putInt(act.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,currentNum);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 获取亮度0~255
    public static int GetLightness(Activity act) {
        int brightness;
        WindowManager.LayoutParams lp = act.getWindow().getAttributes();
        Log.d("GetLightness", "lp.screenBrightness=" + lp.screenBrightness);
//        brightness = (int)(lp.screenBrightness * 255f);
        brightness = (int)(lp.screenBrightness);
        if(brightness == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE){
            Log.d("GetLightness", "brightness == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE");
//            brightness = Settings.System.getInt(act.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,255);
            try {
                brightness = Settings.System.getInt(act.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                Log.d("GetLightness", "brightness=" + brightness);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        }

        return brightness;
    }
}
