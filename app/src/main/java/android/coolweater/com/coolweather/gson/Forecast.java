package android.coolweater.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ms.zhan on 2017/12/31.
 */

public class Forecast {

    public String date;

    @SerializedName("cond")
    public More more;
    public class More{

        @SerializedName("txt_d")
        public String info;
    }

    @SerializedName("tmp")
    public Temperature temperature;
    public class Temperature{

        public String max;
        public String min;
    }
}
