package android.coolweater.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ms.zhan on 2017/12/31.
 */

public class Now {

    @SerializedName("tmp")
    public String nowTmp;

    @SerializedName("cond")
    public More more;

    public class More{

        @SerializedName("txt")
        public String info;
    }
}
