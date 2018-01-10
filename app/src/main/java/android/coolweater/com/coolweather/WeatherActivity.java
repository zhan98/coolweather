package android.coolweater.com.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.coolweater.com.coolweather.gson.Forecast;
import android.coolweater.com.coolweather.gson.Weather;
import android.coolweater.com.coolweather.service.AutoUpdateService;
import android.coolweater.com.coolweather.util.HttpUtil;
import android.coolweater.com.coolweather.util.Utility;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    //activity_weather.xml
    private ScrollView weatherLayout;

    //title.xml
    private ImageButton weatherBack;
    private TextView titleCity;
    private TextView titleUpdateTime;

    //now.xml
    private TextView degreeText;
    private TextView weatherInfoText;

    //forecast.xml
    private LinearLayout forecastLayout;

    //aqi.xml
    private TextView aqiText;
    private TextView pm25Text;

    //suggest.xml
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    SharedPreferences prefs;

    //bing每日一图
    private ImageView bingPicImage;

    //下拉刷新
    public SwipeRefreshLayout swipeRefresh;

    //滑动菜单
    public DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //判断版本号，这功能只能5.0以上才能支持
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();//获取当前活动的DecorView
            //Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住。
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            //将状态栏设置成透明色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);
        //初始化各控件
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);

        weatherBack = (ImageButton)findViewById(R.id.weather_back);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);

        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);

        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);

        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text = (TextView)findViewById(R.id.pm2_5_text);

        comfortText = (TextView)findViewById(R.id.comfort_text);
        carWashText = (TextView)findViewById(R.id.car_wash_text);
        sportText = (TextView)findViewById(R.id.sptor_text);

        //下拉刷新
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);//更改下拉刷新进度条颜色


        final String weatherId;

        //到SharedPreferences存储内检查是否存在天气信息
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if(weatherString != null){
            //存在天气信息直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            //无缓存时去服务器查询天气
            weatherId = getIntent().getStringExtra("weather_id");//activity之间数据传递
            weatherLayout.setVisibility(View.INVISIBLE);//ScrollView设置为不可见
            requestWeather(weatherId);
        }

        //下拉刷新监听器
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

        //返回键
        weatherBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //WeatherActivity.this.finish();
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        bingPicImage = (ImageView)findViewById(R.id.bing_pic_img);
        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImage);
        }else{
            loadBingPic();
        }
    }


    /*
    * 根据天气 id 到服务器上获取天气信息
    * */
    public void requestWeather(final String weatherId){

        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=a0c9dcb62fd440ae9f33a18c5f0bda30";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                //返回主线程更新界面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();//获取服务器返回的数据
                final Weather weather = Utility.handleWeatherResponse(responseText);//处理服务器返回的数据
                //返回主线程更新界面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //得到数据不为null，并且status请求状态为"ok"，展示数据
                        if(weather != null && "ok".equals(weather.status)){
                            //添加数据进SharedPreferences存储内
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("weather",responseText);
                            editor.apply();//提交
                            showWeatherInfo(weather);

                            //启动服务
                            Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
                            startService(intent);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    /*
    * 处理并展示Weather实体类中数据
    * */
    private void showWeatherInfo(Weather weather){

        String cityName = weather.basic.cityName;
        String cityUpdateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.nowTmp + "℃";
        String info = weather.now.more.info;

        titleCity.setText(cityName);
        titleUpdateTime.setText(cityUpdateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(info);

        forecastLayout.removeAllViews();//移除所有子视图
        for(Forecast forecast : weather.forecastsList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);//动态加载布局
            //初始化forecast_item.xml控件
            TextView dateText = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView)view.findViewById(R.id.min_text);
            //添加控件数据
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);//动态添加view组件
        }

        if(weather.aqi != null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运动建议："+ weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);

        weatherLayout.setVisibility(View.VISIBLE);//将ScrollView设置为可见
    }


    //加载必应每日一图
    private void loadBingPic(){
        String bingUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(bingUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(WeatherActivity.this,"获取每日一图失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String responseBing = response.body().string();

                //更新SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("bing_pic",responseBing);
                editor.apply();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(responseBing).into(bingPicImage);
                    }
                });
            }
        });
    }
}
