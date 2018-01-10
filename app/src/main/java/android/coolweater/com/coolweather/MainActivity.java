package android.coolweater.com.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //SharedPreferences缓存数据判断
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString("weather",null);
//        editor.apply();
        //已经请求过天气数据了，那么不用再次选择城市，直接跳转WeatherActivity即可
        if(prefs.getString("weather",null) != null){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
