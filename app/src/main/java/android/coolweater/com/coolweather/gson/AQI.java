package android.coolweater.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ms.zhan on 2017/12/31.
 */

public class AQI {

    public AQICity city;

    public class AQICity{

        public String aqi;
        public String pm25;
    }
}
