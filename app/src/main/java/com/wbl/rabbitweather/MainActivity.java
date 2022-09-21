package com.wbl.rabbitweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> permissionList = new ArrayList<>();

        boolean b =  checkNetworkState();
        if (b){
            //缓存数据判断
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (prefs.getString("weatherid", null) != null) {
                Intent intent = new Intent(this, weatherActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            Toast.makeText(MainActivity.this,"网络未连接!",Toast.LENGTH_SHORT).show();
        }


    }


    private boolean checkNetworkState() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);//1.通过系统服务获取ConnectivityManager类的对象
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();//2.调用getActiveNetworkInfo()获取当前活动的网络NetworkInfo对象
        if (networkInfo == null || networkInfo.isConnected() == false) {//3.判断当前网络状态是否为连接状态，如果当前没有网络是活动的，则返回null
            return false;
        } else {
            return true;
        }
    }
}