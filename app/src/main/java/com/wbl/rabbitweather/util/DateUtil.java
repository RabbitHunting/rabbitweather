package com.wbl.rabbitweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String times() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    public static String times1() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date());
    }
}
