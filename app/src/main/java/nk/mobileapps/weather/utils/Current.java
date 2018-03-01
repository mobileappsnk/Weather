package nk.mobileapps.weather.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class Current {
    private String mIcon;
    private long mTime;
    private double mTemperature;
    private double mHumidity;
    private double mPrecipChance;
    private String mSummary;
    private String mTimeZone;

    private double pressure;
    private double windSpeed;
    private double windGust;
    private double windBearing;

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getWindGust() {
        return windGust;
    }

    public void setWindGust(double windGust) {
        this.windGust = windGust;
    }

    public double getWindBearing() {
        return windBearing;
    }

    public void setWindBearing(double windBearing) {
        this.windBearing = windBearing;
    }


    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public int getIconId() {
        return Forecast.getIconId(mIcon);
    }

    public int getBackGroundId() {
        return Forecast.getBackGroundId(mIcon, isDay());
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public boolean isDay() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            return true;
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            return true;
        } else if (timeOfDay >= 16 && timeOfDay < 18) {
            return true;
        } else if (timeOfDay >= 18 && timeOfDay < 24) {
            return false;
        }

        return true;
    }

    public String getMsgOnTime() {
        Calendar c = Calendar.getInstance();
        Date dateTime = new Date(getTime() * 1000);
        c.setTime(dateTime);
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            return "Good Morning";
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            return "Good Afternoon";
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            return "Good Evening";
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            return "Good Night";
        }

        return "Current Weather Report";
    }

    public String getFormattedTime() {
        Date dateTime = new Date(getTime() * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("EE, dd MMM yyyy, h:mm a", Locale.getDefault());
        // SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        return formatter.format(dateTime);
    }

    public String getFormattedDateTime(long timezone) {
        Date dateTime = new Date(timezone * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("EE, dd MMM yyyy, h:mm a", Locale.getDefault());
        // SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        return formatter.format(dateTime);
    }

    public String getFormattedTime(long timezone) {
        Date dateTime = new Date(timezone * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        // SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        return formatter.format(dateTime);
    }

    public String getLastFormattedTime() {
        Date dateTime = new Date(getTime() * 1000);
        //SimpleDateFormat formatter = new SimpleDateFormat("EE, dd MMM yyyy, h:mm a", Locale.getDefault());
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        return formatter.format(dateTime);
    }

    public int getTemperature() {
        return (int) Math.round(mTemperature);
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public int getPrecipChance() {
        double precipPercentage = mPrecipChance * 100;
        return (int) Math.round(precipPercentage);
    }

    public void setPrecipChance(double precipChance) {
        mPrecipChance = precipChance;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }
}
