package com.wbl.rabbitweather;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherHourlyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;


import com.wbl.rabbitweather.util.DateUtil;
import com.wbl.rabbitweather.util.HttpUtil;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class weatherActivity extends AppCompatActivity {
    private TextView weather_name, title_upddate, degree_text, weather_info, tiganwen, fl;
    private static final String TAG = "test";
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    private String mweatherId, mweatherName;
    public DrawerLayout drawerLayout;
    private Button navButton;
    private ScrollView weatherLayout;
    private HorizontalScrollView weatherhory;
    private LinearLayout forecastLayout, hourly_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //版本判定
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //和风天气sdk
        HeConfig.init("HE2209180850211273", "504bc4df5c254b489956bea1ca0e98d5");
        HeConfig.switchToDevService();

        //初始化各个控件
        bingPicImg = findViewById(R.id.bing_pic);//必应图片
        weather_name = findViewById(R.id.title_city); //城市名字
        degree_text = findViewById(R.id.dagree_text);//当前温度
        weather_info = findViewById(R.id.weather_info);//当前天气
        tiganwen = findViewById(R.id.tiganwen);//体感温度
        title_upddate = findViewById(R.id.title_update);//更新时间
        fl = findViewById(R.id.fl);//风力和风向
        swipeRefresh = findViewById(R.id.weather_refresh);
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        bingPicImg = findViewById(R.id.bing_pic);
        forecastLayout = findViewById(R.id.forecast_layout);
        weatherLayout = findViewById(R.id.weather_layout);
        weatherhory = findViewById(R.id.weather_Horilayout);
        hourly_layout = findViewById(R.id.hourly_layout);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic = prefs.getString("bing_pic", null);
        //背景图片判断
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);

        } else {
            sendRequestWithHttpURLConnection();
        }
        //是否有城市缓存
        String weathering = prefs.getString("weatherid", null);
        /*Log.d("test", "测试： aaa" + weathering);*/
        if (weathering != null) {
            mweatherId = prefs.getString("weatherid", null);
            mweatherName = prefs.getString("weathername", null);
            /*Log.d("test", "测试：h " + mweatherId);
            Log.d("test", "测试：h " + mweatherName);*/
            queryWeather(mweatherId, mweatherName);
        } else {
            mweatherId = getIntent().getStringExtra("weather_id");
            mweatherName = getIntent().getStringExtra("weather_name");
           /* Log.d("test", "测试：w " + mweatherId);
            Log.d("test", "测试：w " + mweatherName);*/
            weatherLayout.setVisibility(View.INVISIBLE);
            weatherhory.setVisibility(View.INVISIBLE);
            queryWeather(mweatherId, mweatherName);
        }


        //下拉刷新
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mweatherId = prefs.getString("weatherid", null);
                mweatherName = prefs.getString("weathername", null);
                /*Log.d("test", "测试： xiala" + mweatherId);
                Log.d("test", "测试： xiala" + mweatherName);*/
                queryWeather(mweatherId, mweatherName);
            }
        });
        //滑动菜单
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * 查询当前天气
     *
     * @param weatherid
     */
    public void queryWeather(String weatherid, String weathername) {
        QWeather.getWeatherNow(weatherActivity.this, weatherid, Lang.ZH_HANS, Unit.METRIC, new QWeather.OnResultWeatherNowListener() {


            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: ", e);
                System.out.println("Weather Now Error:" + new Gson());
            }

            @Override
            public void onSuccess(WeatherNowBean weatherBean) {
                //Log.i(TAG, "getWeather onSuccess: " + new Gson().toJson(weatherBean));
                //Log.d(TAG, "获取天气成功： " + new Gson().toJson(weatherBean));
                SharedPreferences.Editor editor = (SharedPreferences.Editor) PreferenceManager
                        .getDefaultSharedPreferences(weatherActivity.this).edit();
                /*Log.d("test", "测试： cr" + weatherid);
                Log.d("test", "测试： cr" + weathername);*/
                editor.putString("weatherid", weatherid);
                editor.putString("weathername", weathername);
                editor.apply();
                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if (Code.OK == weatherBean.getCode()) {
                    WeatherNowBean.NowBaseBean now = weatherBean.getNow();
                    weatheryu(weatherid);
                    hourlys(weatherid);
                    weatherActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /*Log.d(TAG, "更新时间"+time);
                            Log.d(TAG, "当前天气:" + tianqi);
                            Log.d(TAG, "当前温度:" + wendu);
                            Log.d(TAG, "风向：" + fengxiang);
                            Log.d(TAG, "风力：" + fengli + "级");*/
                            weather_name.setText(weathername);
                            title_upddate.setText(DateUtil.times(now.getObsTime()));
                            degree_text.setText(now.getTemp() + "\u2103");
                            weather_info.setText(now.getText());
                            tiganwen.setText("体感：" + now.getFeelsLike() + "\u2103");
                            String flandfx = now.getWindScale() + "|" + now.getWindDir();
                            fl.setText(flandfx);
                            sendRequestWithHttpURLConnection();
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
        sendRequestWithHttpURLConnection();
        swipeRefresh.setRefreshing(false);
    }

    /**
     * 获取3天预报数据
     */
    public void weatheryu(String weatherid) {
        QWeather.getWeather7D(weatherActivity.this, weatherid, Lang.ZH_HANS, Unit.METRIC, new QWeather.OnResultWeatherDailyListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "onError: ", throwable);
                System.out.println("Weather Now Error:" + new Gson());

            }

            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {

                //Log.d(TAG, "获取未来天气成功： " + new Gson().toJson(weatherDailyBean));
                if (Code.OK == weatherDailyBean.getCode()) {
                    List<WeatherDailyBean.DailyBean> dailyBeans = weatherDailyBean.getDaily();
                    //Log.d(TAG, "测试1：" + dailyBeans);
                    weatherActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            forecastLayout.removeAllViews();
                            for (int i = 0; i < dailyBeans.size(); i++) {
                                WeatherDailyBean.DailyBean dailyBean = dailyBeans.get(i);
                                String dataw = dailyBean.getFxDate();
                                dataw = DateUtil.time2(dataw);
                                String weatherin;
                                if (dailyBean.getTextDay().equals(dailyBean.getTextNight())) {
                                    weatherin = dailyBean.getTextDay();
                                } else {
                                    weatherin = dailyBean.getTextDay() + "转" + dailyBean.getTextNight();
                                }
                                String wendu = dailyBean.getTempMax() + "/" + dailyBean.getTempMin() + "\u2103";
                                View view = LayoutInflater.from(weatherActivity.this)
                                        .inflate(R.layout.forecast_item, forecastLayout, false);
                                TextView dataText = view.findViewById(R.id.date_text);
                                TextView infoText = view.findViewById(R.id.info_text);
                                TextView wenText = view.findViewById(R.id.wen_text);
                                dataText.setText(dataw);
                                infoText.setText(weatherin);
                                wenText.setText(wendu);
                                forecastLayout.addView(view);
                            }
                        }
                    });

                } else {
                    //在此查看返回数据失败的原因
                    Code code = weatherDailyBean.getCode();
                    Log.i(TAG, "failed code: " + code);
                }

            }
        });
    }

    /**
     * 获取24小时预报数据
     */
    private void hourlys(String weatherId) {
        QWeather.getWeather24Hourly(weatherActivity.this, weatherId, Lang.ZH_HANS, Unit.METRIC, new QWeather.OnResultWeatherHourlyListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "onError: ", throwable);
                System.out.println("Weather Now Error:" + new Gson());
            }

            @Override
            public void onSuccess(WeatherHourlyBean weatherHourlyBean) {
                //Log.d(TAG, "获取未来天气成功： " + new Gson().toJson(weatherHourlyBean));
                if (Code.OK == weatherHourlyBean.getCode()) {
                    List<WeatherHourlyBean.HourlyBean> hourlyBeans = weatherHourlyBean.getHourly();
                    weatherActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hourly_layout.removeAllViews();
                            for (int i = 0; i < hourlyBeans.size(); i++) {
                                WeatherHourlyBean.HourlyBean hourlyBean = hourlyBeans.get(i);
                                String time = hourlyBean.getFxTime();
                                Log.d(TAG, "时间测试1： " + time);
                                time = DateUtil.times(time);
                                Log.d(TAG, "时间测试2： " + time);
                                String info = hourlyBean.getText();
                                String temp = hourlyBean.getTemp() + "\u2103";
                                View view = LayoutInflater.from(weatherActivity.this)
                                        .inflate(R.layout.hourly_item, hourly_layout, false);
                                TextView hourlyTime = view.findViewById(R.id.hourly_time);
                                TextView hourlyInfo = view.findViewById(R.id.hourly_info);
                                TextView hourlyTemp = view.findViewById(R.id.hourly_temp);
                                hourlyTime.setText(time);
                                hourlyInfo.setText(info);
                                hourlyTemp.setText(temp);
                                hourly_layout.addView(view);

                            }
                        }
                    });
                    weatherLayout.setVisibility(View.VISIBLE);
                    weatherhory.setVisibility(View.VISIBLE);

                } else {
                    //在此查看返回数据失败的原因
                    Code code = weatherHourlyBean.getCode();
                    Log.i(TAG, "failed code: " + code);
                }
            }
        });

    }

    //每日一图
    private void sendRequestWithHttpURLConnection() {
        // 开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                //最原始的网络请求方式
                /*

                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    // 下面对获取到的输入流进行读取
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    parseJSONWithJSONObject(response.toString());
                    // showResponse(response.toString());
                    //Ui线程
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }*/
                //优化图片的请求方式
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1")
                            .build();
                    Response response = null;
                    response = client.newCall(request).execute();
                    parseJSONWithJSONObject(response.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 在这里进行UI操作，将图片显示出来
    private void showResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里进行UI操作，将结果显示到界面上
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(weatherActivity.this).edit();
                editor.putString("bing_pic", response);
                editor.apply();
                Glide.with(weatherActivity.this).load(response).into(bingPicImg);
                //  text.setText(response);
                //Log.i("123", response);
            }
        });
    }

    //解析图片
    void parseJSONWithJSONObject(String jsonData) {
        try {
            // JSONArray jsonArray = new JSONArray(jsonData);

            JSONArray jsonArray = new JSONObject(jsonData).getJSONArray("images");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String url = jsonObject.getString("url");

                Log.d("MainActivity", "url is " + url);
                String url1 = "http://cn.bing.com" + url;
                showResponse(url1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
