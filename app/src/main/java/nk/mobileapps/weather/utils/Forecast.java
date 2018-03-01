package nk.mobileapps.weather.utils;


import nk.mobileapps.weather.R;

public class Forecast {
    private Current mCurrent;
    private Hour[] mHourlyForecast;
    private Day[] mDailyForecast;

    public static int getIconId(String iconString) {
        // clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night
        int iconId;

        switch (iconString) {
            case "clear-day":
                iconId = R.drawable.clear_day;
                break;
            case "clear-night":
                iconId = R.drawable.clear_night;
                break;
            case "rain":
                iconId = R.drawable.rain;
                break;
            case "snow":
                iconId = R.drawable.snow;
                break;
            case "sleet":
                iconId = R.drawable.sleet;
                break;
            case "wind":
                iconId = R.drawable.wind;
                break;
            case "fog":
                iconId = R.drawable.fog;
                break;
            case "cloudy":
                iconId = R.drawable.cloudy;
                break;
            case "partly-cloudy-day":
                iconId = R.drawable.partly_cloudy;
                break;
            case "partly-cloudy-night":
                iconId = R.drawable.cloudy_night;
                break;
            default:
                iconId = R.drawable.clear_day;
                break;
        }

        return iconId;
    }

    public static int getBackGroundId(String iconString, boolean isDay) {
        // clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night
        int iconId;

        switch (iconString) {
            case "clear-day":
                iconId = isDay ? R.drawable.b_01d : R.drawable.b_01n;
                break;
            case "clear-night":
                iconId = isDay ? R.drawable.b_01n : R.drawable.b_01d;
                break;
            case "rain":
                iconId = isDay ? R.drawable.b_09d : R.drawable.b_09n;
                break;
            case "snow":
                iconId = isDay ? R.drawable.b_13d : R.drawable.b_13n;
                break;
            case "sleet":
                iconId = isDay ? R.drawable.b_16d : R.drawable.b_15d;
                break;
            case "wind":
                iconId = isDay ? R.drawable.b_18d : R.drawable.b_18n;
                break;
            case "fog":
                iconId = isDay ? R.drawable.b_50d : R.drawable.b_50n;
                break;
            case "cloudy":
                iconId = isDay ? R.drawable.b_03d : R.drawable.b_03n;
                break;
            case "partly-cloudy-day":
                iconId = isDay ? R.drawable.b_03d : R.drawable.b_03n;
                break;
            case "partly-cloudy-night":
                iconId = isDay ? R.drawable.b_03n : R.drawable.b_03d;
                break;
            default:
                iconId = isDay ? R.drawable.b_01d : R.drawable.b_01n;
                break;
        }

        return iconId;
    }

    public Current getCurrent() {
        return mCurrent;
    }

    public void setCurrent(Current current) {
        mCurrent = current;
    }

    public Hour[] getHourlyForecast() {
        return mHourlyForecast;
    }

    public void setHourlyForecast(Hour[] hourlyForecast) {
        mHourlyForecast = hourlyForecast;
    }

    public Day[] getDailyForecast() {
        return mDailyForecast;
    }

    public void setDailyForecast(Day[] dailyForecast) {
        mDailyForecast = dailyForecast;
    }
}
