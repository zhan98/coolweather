package android.coolweater.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ms.zhan on 2017/12/31.
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    //内部类
    public class Update{

        @SerializedName("loc")
        public String updateTime;
    }
}
