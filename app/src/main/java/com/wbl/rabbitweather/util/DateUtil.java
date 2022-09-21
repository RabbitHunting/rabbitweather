package com.wbl.rabbitweather.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String times(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mmXXX");
            Log.d("test", "times1: "+time);
            Date times = sdf.parse(time);
            Log.d("test", "times2: "+times);
            String times1 = times0(times);
            return times1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static  String times0(Date time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String times = sdf.format(time);
            Log.d("test", "times3: "+times);
            return times;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String times1() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date());
    }
    public static String time2(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date times =sdf.parse(time);
            String times1 = time3(times);
            return times1;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String time3(Date time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
            String times = sdf.format(time);
            return times;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
