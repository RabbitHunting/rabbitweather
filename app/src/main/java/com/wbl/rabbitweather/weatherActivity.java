package com.wbl.rabbitweather;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;
import com.wbl.rabbitweather.util.DateUtil;

import java.sql.Time;
import java.text.SimpleDateFormat;

public class weatherActivity extends AppCompatActivity {
    private TextView tv_tianqi, tv_wendu, tv_fengxiang, tv_fengli;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        HeConfig.init("HE2209180850211273", "504bc4df5c254b489956bea1ca0e98d5");
        HeConfig.switchToDevService();
        String weatherId = getIntent().getStringExtra("weather_id");
        Log.d("test", "测试： "+weatherId);
        queryWeather(weatherId);

    }


    public void queryWeather(String weatherid) {
        QWeather.getWeatherNow(weatherActivity.this, weatherid, Lang.ZH_HANS, Unit.METRIC, new QWeather.OnResultWeatherNowListener() {
            public static final String TAG = "test";

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
                    String tianqi = now.getText();
                    String wendu = now.getTemp() + "℃";
                    String fengli = now.getWindScale();
                    String fengxiang = now.getWindDir();
                    String time= DateUtil.times();
                    Log.d(TAG, "更新时间"+time);
                    Log.d(TAG, "当前天气:" + tianqi);
                    Log.d(TAG, "当前温度:" + wendu);
                    Log.d(TAG, "风向：" + fengxiang);
                    Log.d(TAG, "风力：" + fengli + "级");

                } else {
                    //在此查看返回数据失败的原因
                    Code code = weatherBean.getCode();
                    //System.out.println("失败代码: " + code);
                    Log.i(TAG, "failed code: " + code);
                }
            }

        });
    }

}
