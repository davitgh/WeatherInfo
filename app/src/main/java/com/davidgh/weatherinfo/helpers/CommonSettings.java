package com.davidgh.weatherinfo.helpers;

/**
 * Created by davidgh on 2/6/18.
 */

public class CommonSettings {
    public static String API_KEY = "3330f9945a3841ab1ddef9d2ac83b4e3";
    public static String API_LINK = "http://api.openweathermap.org/data/2.5/weather";

    public static String apiRequest(String city){
        return API_LINK + "?id=" + city + "&appid=" + API_KEY + "&units=metric";
    }

    public static String getImage(String icon){
        return String.format("http://openweathermap.org/img/w/s%.png", icon);
    }
}
