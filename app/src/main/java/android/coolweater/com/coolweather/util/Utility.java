package android.coolweater.com.coolweather.util;

import android.coolweater.com.coolweather.db.City;
import android.coolweater.com.coolweather.db.County;
import android.coolweater.com.coolweather.db.Province;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Ms.zhan on 2017/12/4.
 */

public class Utility {

    //取出json字符，设置进LitePal数据库

    /**
     *解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){//判断字符是否为空
            try{
                JSONArray allProvinces = new JSONArray(response);
                for(int i = 0; i < allProvinces.length() ; i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /*
    * 解析和处理服务器返回的市级数据
    * */
    public static boolean handleCityResponse(String response , int provinceId){
       if(!TextUtils.isEmpty(response)){
           try {
               JSONArray allCity = new JSONArray(response);
               for(int i = 0 ; i < allCity.length() ; i++){
                   JSONObject cityObject = allCity.getJSONObject(i);
                   City city = new City();
                   city.setCityCode(cityObject.getInt("id"));
                   city.setCityName(cityObject.getString("name"));
                   city.setProvinceId(provinceId);
                   city.save();
               }
               return true;
           }catch (Exception e){
               e.printStackTrace();
           }
       }
        return false;
    }


    /*
    *j解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String response , int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounty = new JSONArray(response);//将集合内元素转化为array数组
                for(int i = 0;i < allCounty.length() ; i++){
                    JSONObject countyObject = allCounty.getJSONObject(i); //取出数组内元素，object类型
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }
}
