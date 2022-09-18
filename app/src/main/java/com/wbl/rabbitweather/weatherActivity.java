package com.wbl.rabbitweather;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qweather.sdk.bean.Basic;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;
import com.wbl.rabbitweather.util.DateUtil;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;

public class weatherActivity extends AppCompatActivity {
    private TextView weather_name,title_upddate,degree_text,weather_info,tiganwen,fl;
    public static final String TAG = "test";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        HeConfig.init("HE2209180850211273", "504bc4df5c254b489956bea1ca0e98d5");
        HeConfig.switchToDevService();

        weather_name = findViewById(R.id.title_city); //城市名字
        degree_text = findViewById(R.id.dagree_text);//当前温度
        weather_info = findViewById(R.id.weather_info);//当前天气
        tiganwen = findViewById(R.id.tiganwen);//体感温度
        title_upddate = findViewById(R.id.title_update);//更新时间
        fl = findViewById(R.id.fl);//风力和风向
        String weatherId = getIntent().getStringExtra("weather_id");
        String weathername = getIntent().getStringExtra("weather_name");
        Log.d("test", "测试： "+weatherId);
        Log.d("test", "测试： "+weathername);
        weather_name.setText(weathername);
        queryWeather(weatherId);
    }

    /**
     * 查询当前天气
     * @param weatherid
     */
    public void queryWeather(String weatherid) {
        QWeather.getWeatherNow(weatherActivity.this, weatherid, Lang.ZH_HANS, Unit.METRIC, new QWeather.OnResultWeatherNowListener() {

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: ", e);
                System.out.println("Weather Now Error:" + new Gson());
            }
            @Override
            public void onSuccess(WeatherNowBean weatherBean) {
                //Log.i(TAG, "getWeather onSuccess: " + new Gson().toJson(weatherBean));
                Log.d(TAG, "获取天气成功： " + new Gson().toJson(weatherBean));
                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if (Code.OK == weatherBean.getCode()) {
                    WeatherNowBean.NowBaseBean now = weatherBean.getNow();
                    weatheryu(weatherid);
                    weatherActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /*Log.d(TAG, "更新时间"+time);
                            Log.d(TAG, "当前天气:" + tianqi);
                            Log.d(TAG, "当前温度:" + wendu);
                            Log.d(TAG, "风向：" + fengxiang);
                            Log.d(TAG, "风力：" + fengli + "级");*/
                            title_upddate.setText(DateUtil.times1());
                            degree_text.setText(now.getTemp() +"\u2103");
                            weather_info.setText(now.getText());
                            tiganwen.setText("体感："+now.getFeelsLike()+"\u2103");
                            String flandfx = now.getWindScale() +"|" + now.getWindDir();
                            fl.setText(flandfx);
                        }
                    });


                } else {
                    //在此查看返回数据失败的原因
                    Code code = weatherBean.getCode();
                    //System.out.println("失败代码: " + code);
                    Log.i(TAG, "failed code: " + code);
                }
            }
        });
    }

    /**
     * 获取3天预报数据
     *
     */
    public void weatheryu(String weatherid) {
        QWeather.getWeather3D(weatherActivity.this, weatherid, Lang.ZH_HANS, Unit.METRIC, new QWeather.OnResultWeatherDailyListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "onError: ", throwable);
                System.out.println("Weather Now Error:" + new Gson());

            }
            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {
                //Log.d(TAG, "获取天气成功： " + new Gson().toJson(weatherDailyBean));
                if (Code.OK == weatherDailyBean.getCode()) {
                    List<WeatherDailyBean.DailyBean> dailyBeans = weatherDailyBean.getDaily();

                } else {
                    //在此查看返回数据失败的原因
                    Code code = weatherDailyBean.getCode();
                    Log.i(TAG, "failed code: " + code);
                }

            }
        }) ;
    }


}
