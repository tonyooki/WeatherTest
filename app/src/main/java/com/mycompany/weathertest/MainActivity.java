package com.mycompany.weathertest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mycompany.weathertest.http.HttpCallbackListener;
import com.mycompany.weathertest.http.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {
    public static final int SHOW_RESPONSE = 0;
//    private TextView cityNameTv;
    private TextView tempTv;
    private ListView listView;
//    private Button getCityBtn;
    private Button choiceBtn;
    private String jsonDate;
    private String cityName;
    private final String httpAddress = "https://api.heweather.com/x3/weather?city=";
    private final String key = "&key=072231a998bb4d5da614bb97afb2a84d";
    private final String TAG = "MainActivity";
    private String http;
    private JSONArray jsonArray;
    Handler handler = new Handler();
    private String yesterdaySay = "日可待成追忆";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        cityNameTv = (TextView) findViewById(R.id.tv_city_name);
        tempTv = (TextView) findViewById(R.id.tv_temp);
//        getCityBtn = (Button) findViewById(R.id.bt_get_city);
        choiceBtn = (Button) findViewById(R.id.bt_choice_city);
//        getCityBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onRefresh();
//            }
//        });
        choiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChoiceCityActivity.class);
                startActivity(intent);
            }
        });
        onRefresh();
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        onRefresh();
        Log.d(TAG, "onStart");
    }

    //刷新功能
    private void onRefresh() {
        getHttp();
        getJson();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                getJson();
//            }
//        }, 2000);
    }

    //获得json数据,并解析输出
    private void getJson() {
        HttpUtil.sendHttpRequst(http, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                jsonDate = response;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        ayJson();
                        getDailyArray();
                        setTextView();
                    }
                }, 500);
//                ayJson();
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    //根据城市名字获得天气地址
    private void getHttp() {
        SharedPreferences pref = getSharedPreferences(ChoiceCityActivity.DATA_NAME, MODE_PRIVATE);
        cityName = pref.getString(ChoiceCityActivity.CITY_NAME, "北京");
        String cityName1 = cityName.replace("市", "")
                .replace("省", "")
                .replace("自治区", "")
                .replace("特别行政区", "")
                .replace("地区", "")
                .replace("盟", "");
        http = httpAddress + cityName1 + key;
    }

    //从json数据中提取每日天气情况
    private void getDailyArray() {
        try {
            JSONArray jsonArray = new JSONObject(jsonDate)
                    .getJSONArray("HeWeather data service 3.0").getJSONObject(0)
                    .getJSONArray("daily_forecast");
            this.jsonArray = jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //从json数据中获得相应时间天气情况的字符串
    private String getWeatherString(int day) {
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(day);
            String tempMin = jsonObject.getJSONObject("tmp").getString("min");
            String temMax = jsonObject.getJSONObject("tmp").getString("max");
            String condDay = jsonObject.getJSONObject("cond").getString("txt_d");
            String condNight = jsonObject.getJSONObject("cond").getString("txt_n");
            String string = tempMin + "~" + temMax + "℃";
            if (condDay.equals(condNight)) {
                string = condDay + "  " + string;
            } else {
                string = condDay + "转" + condNight + "  " + string;
            }
            return string;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //设置textView
    private void setTextView() {
        choiceBtn.setText(cityName);
        String yesterday = "昨:" + yesterdaySay + "\n";
        String today = "今:" + getWeatherString(0) + "\n";
        String tomorrow = "明:" + getWeatherString(1) + "\n";
        String after = "后:" + getWeatherString(2);
        String string = yesterday + today + tomorrow + after;
        tempTv.setText(string);
    }

    //解析json数据,废弃不用
    private void ayJson() {
        try {
            JSONObject jsonObject = new JSONObject(jsonDate)
                    .getJSONArray("HeWeather data service 3.0").getJSONObject(0)
                    .getJSONArray("daily_forecast").getJSONObject(0)
                    .getJSONObject("tmp");
//            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String tempMin = jsonObject.getString("min");
            String tempMax = jsonObject.getString("max");
            tempTv.setText("温度:" + tempMin + "~" + tempMax + "℃");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        cityNameTv.setText("地区:" + cityName);
    }

}
