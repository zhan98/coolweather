package android.coolweater.com.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Ms.zhan on 2017/12/4.
 */
//市
public class City extends DataSupport {
    private int id;
    private String cityName;//市名称
    private String cityCode;//市代号
    private int provinceId;//所属省id

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
