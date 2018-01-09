package android.coolweater.com.coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.coolweater.com.coolweather.db.City;
import android.coolweater.com.coolweather.db.County;
import android.coolweater.com.coolweather.db.Province;
import android.coolweater.com.coolweather.util.HttpUtil;
import android.coolweater.com.coolweather.util.Utility;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by Ms.zhan on 2017/12/5.
 */

/*
*
* */
public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>(); //列表数据

    //(类)
    //省列表
    private List<Province> provinceList;
    //市列表
    private List<City> cityList;
    //县列表
    private List<County> countyList;

    //选中的省份
    private Province selectProvince;
    //选中的市
    private City selectCity;
    //选中的县
    private County selectCounty;

    //当前选中级别
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false); //加载碎片布局
        //实例化
        titleText = (TextView)view.findViewById(R.id.title_text);
        backButton = (Button)view.findViewById(R.id.back_button);
        listView = (ListView)view.findViewById(R.id.list_view);
        //ArrayList
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //跳转显示对应列表
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){ //当前级别为省
                    selectProvince = provinceList.get(position);//定位选中的省
                    queryCity();//查询选中省下的市
                }else if(currentLevel == LEVEL_CITY){  //当前级别为市
                    selectCity = cityList.get(position);//定位选中的市
                    queryCounty();//查询选中市下的县
                }else if(currentLevel == LEVEL_COUNTY){ //当前级别为县
                    selectCounty = countyList.get(position);//定位选中的县
                    String weatherId = selectCounty.getWeatherId();//读取选中县的weatherId
                    //Activity间传递值
                    Intent intent = new Intent(getActivity(),WeatherActivity.class);
                    intent.putExtra("weather_id",weatherId);
                    startActivity(intent);
                    //getActivity().finish();
                }
            }
        });

        //返回上级列表
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel == LEVEL_COUNTY){ //当前级别为县
                    queryCity();
                }else if(currentLevel == LEVEL_CITY){ //当前级别为市
                    queryProvinces();
                }
            }
        });


        //初始显示列表级别为省
        queryProvinces();
    }

    //查询全国所有的省，优先从数据库上查询，如果没有查到到服务器上查询
    private void queryProvinces(){

        titleText.setText("中国");
        backButton.setVisibility(View.GONE);//将返回键设置为不可见
        provinceList = DataSupport.findAll(Province.class); //查找PROVINCE表内所有数据
        if(provinceList.size() > 0) { //如果查找个数不为0
            dataList.clear(); //清空dataList列表数据
            for(Province province : provinceList) { //将provinceList内省名字放入dataList中
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged(); //更新adapter
            listView.setSelection(0); //列表默认选中第一个省名
            currentLevel = LEVEL_PROVINCE; //将LEVEL设置为1
        }else{ //否则传递URL到服务器上查询数据
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    //查询选中省内所有的市，优先从数据库上查询，如果没有查到到服务器上查询
    private void queryCity(){
        titleText.setText(selectProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE); //返回键显示可见
        cityList = DataSupport.where("provinceId = ?",String.valueOf(selectProvince.getId())).find(City.class);//select * from City where id = selectProvince.id
        if(cityList.size() > 0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName()); //列表显示市名
            }
            adapter.notifyDataSetChanged(); //更新适配器，界面刷新
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else{
            String address = "http://guolin.tech/api/china/" + selectProvince.getProvinceCode();
            queryFromServer(address,"city");
        }
    }

    //查询选中市内所有的县，优先从数据库上查询，如果没有查到到服务器上查询
    private void queryCounty(){

        titleText.setText(selectCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityId = ?",String.valueOf(selectCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            String address = "http://guolin.tech/api/china/" + selectProvince.getProvinceCode() + "/" + selectCity.getCityCode();
            queryFromServer(address,"county");
        }
    }

    //根据传入地址和服务器类型从服务器上查询省市县数据
    private void queryFromServer(String address,final String type){

        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address,new okhttp3.Callback(){

            @Override
            public void onFailure(Call call, IOException e) {
                //在这里对异常情况进行处理
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();//关闭对话框
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //得到服务器返回的具体内容
                String responseData = response.body().string(); //获取服务器返回的数据
                boolean result = false;

                //根据不同的类型，选择相应的数据处理
                if(type.equals("province")){
                    result = Utility.handleProvinceResponse(responseData);
                }else if (type.equals("city")){
                    result = Utility.handleCityResponse(responseData,selectProvince.getId());
                }else if(type.equals("county")){
                    result = Utility.handleCountyResponse(responseData,selectCity.getId());
                }

                //解析成功，更新界面
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if(type.equals("province")){
                                queryProvinces();
                            }else if(type.equals("city")){
                                queryCity();
                            }else if(type.equals("county")){
                                queryCounty();
                            }
                        }
                    });
                }
            }
        });
    }

    //显示进度对话框
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    //关闭进度对话框
    private void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
