package android.coolweater.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Ms.zhan on 2017/12/31.
 */

public class Weather {

    public String status;//请求状态

    public Basic basic;//基本信息

    public AQI aqi;//当前空气质量状态

    public Now now;//当前空气信息

    public Suggestion suggestion;//生活建议

    @SerializedName("daily_forecast")
    public List<Forecast> forecastsList;//未来几天天气信息
}
