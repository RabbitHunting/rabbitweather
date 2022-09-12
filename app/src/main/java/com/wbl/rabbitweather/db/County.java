package com.wbl.rabbitweather.db;

import org.litepal.crud.LitePalSupport;

public class County extends LitePalSupport {

    private int id;
    private String countyName;
    private String weatherId;
    private int cityId;

    /**
     * 县id，名字 和代码 和市id
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
