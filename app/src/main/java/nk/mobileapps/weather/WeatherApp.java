package nk.mobileapps.weather;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by user on 20/11/17.
 */

public class WeatherApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }


}
