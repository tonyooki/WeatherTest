package com.mycompany.weathertest;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mycompany.weathertest.db.DBManager;
import com.mycompany.weathertest.db.DBOperate;
import com.mycompany.weathertest.model.City;
import com.mycompany.weathertest.model.Province;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ChoiceCityActivity extends AppCompatActivity {

    private DBManager mDBManager;
    private DBOperate mDBOperate;

    private ListView choseListView;
    private Province province;
    private City city;
    private List<Province> provinceList;
    private List<City> cityList;
    private ArrayList<String> dataList = new ArrayList<>();//申明这个变量时候一定要写 = 后头的东西
    private ArrayAdapter<String> adapter;

    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    private int currentLevel = 0;

    public static final String DATA_NAME = "data";
    public static final String CITY_NAME = "city";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_city);
        mDBManager = new DBManager(this);
        mDBManager.openDatabase();
        mDBOperate = new DBOperate(this);
        choseListView = (ListView) findViewById(R.id.list_view_chose);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        choseListView.setAdapter(adapter);
        choseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    province = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    city = cityList.get(position);
                    save(city.getCityName());
                    finish();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询所有的省,并把省名存储到dataList
     */
    private void queryProvinces() {
        provinceList = mDBOperate.loadProvinces(mDBManager.getDatabase());
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProName());
            }
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_PROVINCE;
        }
    }

    /**
     * 查询选中省份所有的市,并把市名存储到dataList
     */
    private void queryCities() {
        cityList = mDBOperate.loadCities(mDBManager.getDatabase(), province.getProSort());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_CITY;
        }
    }

    /**
     * 存储选中的城市名字到SharedPreferences中
     * @param inputText
     */
    private void save(String inputText) {
        SharedPreferences.Editor editor = getSharedPreferences(DATA_NAME, MODE_APPEND).edit();
        editor.putString(CITY_NAME, inputText);
        editor.commit();
    }

    /**
     * 重写Back方法,根据当前级别判断,返回省级列表还是退出
     */
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        }else {
            finish();
        }
    }
}
