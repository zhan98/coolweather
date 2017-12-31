package android.coolweater.com.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Ms.zhan on 2017/12/4.
 */
//省
public class Province extends DataSupport {
    private int id;//每个省份独有id
    private String provinceName;//省份名
    private int provinceCode;//省份代号

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}
