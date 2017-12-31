package android.coolweater.com.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Ms.zhan on 2017/12/4.
 */
//县
public class County extends DataSupport {
    private int id;
    private String countyName;//县名
    private String weatherId;//天气id
    private int cityId;//所属市id

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
