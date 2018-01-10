package android.coolweater.com.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.coolweater.com.coolweather.WeatherActivity;
import android.coolweater.com.coolweather.gson.Weather;
import android.coolweater.com.coolweather.util.HttpUtil;
import android.coolweater.com.coolweather.util.Utility;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        //创建定时任务
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 *1000;//8h毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;//设定触发时间
        //定时后台更新
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME,triggerAtTime,pi);

        return super.onStartCommand(intent, flags, startId);
    }

    //更新天气信息
    private void updateWeather(){
        //到SharedPreferences存储内检查是否存在天气信息
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if(weatherString != null) {
            //存在天气信息直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;

            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=a0c9dcb62fd440ae9f33a18c5f0bda30";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseText = response.body().string();//获取服务器返回的数据
                    final Weather weather = Utility.handleWeatherResponse(responseText);//处理服务器返回的数据
                    if (weather != null && "ok".equals(weather.status)) {
                        //添加数据进SharedPreferences存储内
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();//提交
                    }
                }
            });
        }
    }

    //更新每日必应图
    private void updateBingPic(){
        String bingUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(bingUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String responseBing = response.body().string();

                //更新SharedPreferences
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",responseBing);
                editor.apply();

            }
        });
    }
}
