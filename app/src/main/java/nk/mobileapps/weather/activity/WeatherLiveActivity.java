package nk.mobileapps.weather.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import nk.mobileapps.weather.R;
import nk.mobileapps.weather.adapters.HourAdapter;
import nk.mobileapps.weather.utils.Colors;
import nk.mobileapps.weather.utils.Current;
import nk.mobileapps.weather.utils.DBHelper;
import nk.mobileapps.weather.utils.Day;
import nk.mobileapps.weather.utils.Forecast;
import nk.mobileapps.weather.utils.Hour;
import nk.mobileapps.weather.utils.PrefManger;
import nk.mobileapps.weather.utils.RateMyApp;
import nk.mobileapps.weather.utils.Util;

public class WeatherLiveActivity extends AppCompatActivity {

    public static final String DAILY_FORECAST = "DAILY_FORECAST";
    public static final String fcKey = "FC";
    public static final String fKey = "F";
    public static final String cKey = "C";
    public static final String windKey = "wind";
    public static final String w_mphKey = "mph";
    public static final String w_msKey = "m/s";
    public static final String w_kmhKey = "km/h";
    public static final String lastUpdateKey = "last";
    public static final String lastPlaceKey = "lastplace";
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final int PLACE_PICKER_REQUEST = 2;
    public static final int MANAGE_LOCATION_REQUEST = 3;
    private final int GPS_REQ = 13;
    String TAG = "WeatherLiveActivity";
    double mLatitude;
    double mLongitude;
    Forecast mForecast;
    Colors mColors = new Colors();
    SwipeRefreshLayout swipe_refresh;
    SimpleDraweeView backdrop;
    TextView temperatureLabel, tempVariation;
    TextView header_wind, header_humidity, header_pressure, header_rain;
    ImageView iv_icon, iv_moreOptions;
    TextView tv_place, tv_datetime, tv_timemsg, tv_condition;
    TextView tv_temperatureLabel, tv_tempVariation;
    LinearLayout ll_next3days;
    RecyclerView rv_hourly;
    HourAdapter hadapter;
    int color;
    DBHelper dbHelper;
    //test

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_live);
        dbHelper = new DBHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        hideShowToolbar();
        findViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dbHelper.getCount(DBHelper.Location.TABLE_NAME) > 0) {
            ((ImageView) findViewById(R.id.iv_search_list)).setImageResource(R.drawable.ic_list_add);
        } else {
            //gps, place, last_updateTime, weather_obj
            dbHelper.insertintoTable(DBHelper.Location.TABLE_NAME, DBHelper.Location.cols, new String[]{"", "", "", ""});
            ((ImageView) findViewById(R.id.iv_search_list)).setImageResource(R.drawable.ic_search);
        }
        startUpdateWeather();
    }

    public void refresh(View v) {
        swipe_refresh.setRefreshing(true);
        startUpdateWeather();
    }

    private void startUpdateWeather() {
        try {

            weatherStatus();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


    private void weatherStatus() throws IOException, JSONException {

        String apiKey = "2b8508abfcf79ade07424212222ee734";
        // get your own API KEY from developer.forecast.io and fill it in.
        final String forecastUrl = "https://api.forecast.io/forecast/" + apiKey + "/" + mLatitude + "," + mLongitude;
        if (Util.isNetworkAvailable(getApplicationContext())) {
            if (mLatitude == 0.0 && mLongitude == 0.0) {
                Intent intent = new Intent(WeatherLiveActivity.this, GPSActivity.class);
                //intent.putExtra(GPSActivity.PROVIDER,GPSActivity.GPS_PROVIDER);
                startActivityForResult(intent, GPS_REQ);
            } else {
                getForecast(forecastUrl);
            }

        } else {
            alertUserAboutNetwork();
        }
        color = mColors.getColor();
        swipe_refresh.setBackgroundColor(color);
        tempVariation.setText(getString(R.string.celsius));

        // generate a new color based on the background color for the status bar
        // using hsv because it makes it super easy bruh.
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.75f; // value component
        int statusBarColor = Color.HSVToColor(hsv);
        // set status bar to a darker color of the background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.setStatusBarColor(statusBarColor);
        }
    }

    private void getForecast(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                alertUserAboutError();
                Log.e(TAG, "Exception caught", e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String jsonData = response.body().string();
                    Log.v(TAG, jsonData);
                    if (response.isSuccessful()) {

                        PrefManger.putSharedPreferencesString(WeatherLiveActivity.this, lastUpdateKey, jsonData);
                        //gps, place, last_updateTime, weather_obj
                        dbHelper.updateByValues(DBHelper.Location.TABLE_NAME, DBHelper.Location.cols,
                                new String[]{mLatitude + "|" + mLongitude, tv_place.getText().toString().trim(), Util.getTodayTime(), jsonData},
                                new String[]{DBHelper.UID}, new String[]{"1"});
                        mForecast = getForecastDetails(jsonData);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                updateDisplay(true);
                                swipe_refresh.setRefreshing(false);
                                updatePreferenceKeys();
                                updateHourlyData();
                                updateNext3DaysData();
                                updateNext7DaysData();

                            }
                        });
                    } else {
                        alertUserAboutError();

                    }
                } catch (IOException | JSONException e) {
                    Log.e(TAG, "Exception caught: ", e);
                }
            }
        });
    }

    private void getLastUpdateWeatherReport() throws JSONException {
        String lastUpdate = PrefManger.getSharedPreferencesString(WeatherLiveActivity.this, lastUpdateKey, "");
        String place = PrefManger.getSharedPreferencesString(WeatherLiveActivity.this, lastPlaceKey, "");
        if (!lastUpdate.equals("")) {
            tv_place.setText(place.trim().equals("") ? "Please Wait! Updating..." : place);
            mForecast = getForecastDetails(lastUpdate);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //swipe_refresh.setRefreshing(false);
                    updateDisplay(false);
                    updatePreferenceKeys();
                    updateNext3DaysData();
                    updateNext7DaysData();
                    updateHourlyData();


                }
            });
        }

    }

    private void updateNext3DaysData() {
        if (mForecast.getDailyForecast().length > 0) {
            Current current = mForecast.getCurrent();
            ll_next3days.removeAllViews();
            final LayoutInflater linflater = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            for (int i = 0; i < 3; i++) {
                final Day day = mForecast.getDailyForecast()[i];

                final View itemView = linflater.inflate(R.layout.row_3day_list_item, null);
                TextView tv_timedate_msg = (TextView) itemView.findViewById(R.id.tv_timedate_msg);
                TextView mTimeLabel = (TextView) itemView.findViewById(R.id.timeLabel);
                TextView mSummaryLabel = (TextView) itemView.findViewById(R.id.summaryLabel);
                TextView mTemperatureLabel = (TextView) itemView.findViewById(R.id.temperatureLabel);
                TextView mTempVariation = (TextView) itemView.findViewById(R.id.tempVariation);
                ImageView mIconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);

                if (i == 0) {
                    tv_timedate_msg.setText(current.getMsgOnTime());
                    mTimeLabel.setText("Today");
                } else if (i == 1) {
                    tv_timedate_msg.setText(current.getFormattedTime(day.getTime()));
                    mTimeLabel.setText("Tomorrow");
                } else {
                    tv_timedate_msg.setText(current.getFormattedTime(day.getTime()));
                    mTimeLabel.setText(day.getDayOfTheWeek());
                }

                mSummaryLabel.setText(day.getIcon());

                if (PrefManger.getSharedPreferencesString(getApplicationContext(), WeatherLiveActivity.fcKey, "").contains(WeatherLiveActivity.cKey)) {
                    mTemperatureLabel.setText((((day.getTemperatureMax() - 32) * 5) / 9) + "");
                    mTempVariation.setText(getString(R.string.celsius));
                } else if (PrefManger.getSharedPreferencesString(getApplicationContext(), WeatherLiveActivity.fcKey, "").contains(WeatherLiveActivity.fKey)) {
                    mTemperatureLabel.setText(day.getTemperatureMax() + "");
                    mTempVariation.setText(getString(R.string.farenheit_string));
                } else {
                    mTemperatureLabel.setText((((day.getTemperatureMax() - 32) * 5) / 9) + "");
                    mTempVariation.setText(getString(R.string.celsius));
                }
                mIconImageView.setImageResource(day.getIconId());

                String time = mTimeLabel.getText().toString();
                String temperature = mTemperatureLabel.getText().toString();
                String summary = mSummaryLabel.getText().toString();
                final String message = String.format("At %s, it will be %s and %s", time, temperature, summary);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        new DayBottomSheetFragment()
                                .newInstance(day)
                                .show(getSupportFragmentManager(), "forecastBottomSheet");
                    }
                });

                ll_next3days.addView(itemView);
            }
        }

    }

    private void updateNext7DaysData() {
        //dadapter = new DayRVAdapter(this, mForecast.getDailyForecast());
        //rv_daily.setAdapter(dadapter);
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        // rv_daily.setLayoutManager(layoutManager);
        //rv_daily.setHasFixedSize(true);
    }

    private void updateHourlyData() {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_hourly.setLayoutManager(layoutManager);
        rv_hourly.setHasFixedSize(true);
       // rv_hourly.setNestedScrollingEnabled(false);

        hadapter = new HourAdapter(this, mForecast.getHourlyForecast());
        rv_hourly.setAdapter(hadapter);

    }

    private void updateDisplay(boolean isLive) {
        Current current = mForecast.getCurrent();
        temperatureLabel.setText((((current.getTemperature() - 32) * 5) / 9) + "");
        tv_temperatureLabel.setText((((current.getTemperature() - 32) * 5) / 9) + "");

        Drawable drawable = getResources().getDrawable(current.getIconId());
        iv_icon.setImageDrawable(drawable);
        backdrop.setImageResource(current.getBackGroundId());
        if (isLive) {
            tv_datetime.setText(current.getFormattedTime());
        } else {
            tv_datetime.setText("Last Update: " + current.getLastFormattedTime());
        }

        tv_condition.setText(current.getSummary());
        String windText = getString(R.string.format_wind,
                current.getWindSpeed(), "mph", Util.convertBearingToDirection(getApplicationContext(), current.getWindBearing()));
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                "fonts/weathericons-regular-webfont.ttf");
        header_wind.setTypeface(typeface);
        header_wind.setText(windText);
        header_humidity.setText(getString(R.string.humidity) + " : " + current.getHumidity() + "");
        String pressureText = getString(R.string.format_pressure, current.getPressure(), "hPa.");
        header_pressure.setText(pressureText);
        header_pressure.setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.tv_icon)).setText(current.getIcon());

        header_rain.setText(getString(R.string.precip_text) + " : " + current.getPrecipChance() + "%");
        tv_timemsg.setText(current.getMsgOnTime());
        tv_timemsg.setVisibility(View.GONE);

    }

    private void updatePreferenceKeys() {
        if (PrefManger.getSharedPreferencesString(WeatherLiveActivity.this, fcKey, "").contains(fKey)) {
            changeToFahrenheit();
        }
        if (PrefManger.getSharedPreferencesString(WeatherLiveActivity.this, fcKey, "").contains(cKey)) {
            changeToCelsius();
        }
    }

    private void changeToCelsius() {
        Current current = mForecast.getCurrent();
        tempVariation.setText(getString(R.string.celsius));
        temperatureLabel.setText((((current.getTemperature() - 32) * 5) / 9) + "");
        tv_tempVariation.setText(getString(R.string.celsius));
        tv_temperatureLabel.setText((((current.getTemperature() - 32) * 5) / 9) + "");

        String windText = getString(R.string.format_wind,
                current.getWindSpeed(), "mph", Util.convertBearingToDirection(getApplicationContext(), current.getWindBearing()));
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                "fonts/weathericons-regular-webfont.ttf");
        header_wind.setTypeface(typeface);
        header_wind.setText(windText);
        updateNext3DaysData();
        if (hadapter != null)
            hadapter.notifyDataSetChanged();


    }

    private void changeToFahrenheit() {
        Current current = mForecast.getCurrent();
        tempVariation.setText(getString(R.string.farenheit_string));
        temperatureLabel.setText(current.getTemperature() + "");
        tv_tempVariation.setText(getString(R.string.farenheit_string));
        tv_temperatureLabel.setText(current.getTemperature() + "");

        String windText = getString(R.string.format_wind,
                current.getWindSpeed(), "mph", Util.convertBearingToDirection(getApplicationContext(), current.getWindBearing()));
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                "fonts/weathericons-regular-webfont.ttf");
        header_wind.setTypeface(typeface);
        header_wind.setText(windText);
        updateNext3DaysData();
        if (hadapter != null)
            hadapter.notifyDataSetChanged();

    }

    private Forecast getForecastDetails(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();

        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));

        return forecast;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] days = new Day[data.length()];

        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonDay = data.getJSONObject(i);
            Day day = new Day();

            day.setSummary(jsonDay.getString("summary"));
            day.setIcon(jsonDay.getString("icon"));
            day.setTemperatureMax(jsonDay.getDouble("temperatureMax"));
            day.setTime(jsonDay.getLong("time"));
            day.setTimeZone(timezone);
            day.setDayObj(jsonDay.toString().trim());

            days[i] = day;
        }

        return days;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        Hour[] hours = new Hour[data.length()];

        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonHour = data.getJSONObject(i);
            Hour hour = new Hour();

            hour.setSummary(jsonHour.getString("summary"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setIcon(jsonHour.getString("icon"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setTimeZone(timezone);
            hour.setObj(jsonHour.toString().trim());

            hours[i] = hour;
        }

        return hours;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject currently = forecast.getJSONObject("currently");
        Current current = new Current();
        current.setTime(currently.getLong("time"));
        current.setSummary(currently.getString("summary"));
        current.setIcon(currently.getString("icon"));
        current.setPrecipChance(currently.getDouble("precipProbability"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setHumidity(currently.getDouble("humidity"));
        current.setPressure(currently.getDouble("pressure"));
        current.setWindSpeed(currently.getDouble("windSpeed"));
        current.setWindGust(currently.getDouble("windGust"));
        current.setWindBearing(currently.getDouble("windBearing"));
        current.setTimeZone(timezone);

        return current;
    }

    private void findViews() {
        ll_next3days = (LinearLayout) findViewById(R.id.ll_next3days);
        backdrop = (SimpleDraweeView) findViewById(R.id.backdrop);
        temperatureLabel = (TextView) findViewById(R.id.temperatureLabel);
        tempVariation = (TextView) findViewById(R.id.tempVariation);
        header_wind = (TextView) findViewById(R.id.header_wind);
        header_humidity = (TextView) findViewById(R.id.header_humidity);
        header_pressure = (TextView) findViewById(R.id.header_pressure);
        header_rain = (TextView) findViewById(R.id.header_rain);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        iv_moreOptions = (ImageView) findViewById(R.id.iv_moreOptions);
        tv_place = (TextView) findViewById(R.id.tv_place);
        tv_datetime = (TextView) findViewById(R.id.tv_datetime);
        tv_timemsg = (TextView) findViewById(R.id.tv_timemsg);
        tv_condition = (TextView) findViewById(R.id.tv_condition);
        tv_temperatureLabel = (TextView) findViewById(R.id.tv_temperatureLabel);
        tv_tempVariation = (TextView) findViewById(R.id.tv_tempVariation);
        rv_hourly = (RecyclerView) findViewById(R.id.rv_hourly);
        try {
            getLastUpdateWeatherReport();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getLocation();
    }

    private void hideShowToolbar() {
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        int top_to_padding = 100;
        swipe_refresh.setProgressViewOffset(false, 0, top_to_padding);
        swipe_refresh.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorAccent);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!Util.isNetworkAvailable(getApplicationContext())) {
                    swipe_refresh.setRefreshing(false);
                    alertUserAboutNetwork();
                }
                startUpdateWeather();
            }
        });


        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                swipe_refresh.setEnabled(verticalOffset == 0);
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    findViewById(R.id.rl_temp).setVisibility(View.VISIBLE);
                    findViewById(R.id.iv_refresh).setVisibility(View.GONE);
                    isShow = true;
                } else if (isShow) {
                    findViewById(R.id.rl_temp).setVisibility(View.GONE);
                    findViewById(R.id.iv_refresh).setVisibility(View.VISIBLE);
                    isShow = false;
                }
            }
        });
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    private void alertUserAboutLocation() {
        LocationDialogFragment dialogFragment = new LocationDialogFragment();
        dialogFragment.show(getFragmentManager(), "location_dialog");
    }

    private void alertUserAboutNetwork() {
        NetworkDialogFragment networkDialogFragment = new NetworkDialogFragment();
        networkDialogFragment.show(getFragmentManager(), "network_dialog");
    }

    private void updateAddress() {

        // Get current city that user is in.
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(mLatitude, mLongitude, 1);
            if (addresses.size() > 0) {
                // change location label to user's current location
                String getLocality = addresses.get(0).getSubLocality() + "," + addresses.get(0).getLocality();// + "," + addresses.get(0).getAdminArea();
                tv_place.setText(getLocality);
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception caught: ", e);

        }
    }

    public void onClick_Search(View v) {

        if (dbHelper.getCount(DBHelper.Location.TABLE_NAME) > 0) {
            try {
                Intent intent = new Intent(this, ManageLocation.class);
                intent.putExtra("background", color);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(WeatherLiveActivity.this, "Please Wait!", Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                // Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
                // startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException e) {
                // TODO: Handle the error.
                System.out.println("e:" + e.getMessage());
            } catch (GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
                System.out.println("e:" + e.getMessage());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());

                mLatitude = place.getLatLng().latitude;
                mLongitude = place.getLatLng().longitude;
                tv_place.setText(place.getAddress());
                startUpdateWeather();

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                mLatitude = place.getLatLng().latitude;
                mLongitude = place.getLatLng().longitude;
                tv_place.setText(place.getAddress());
                startUpdateWeather();
            }
        } else if (requestCode == MANAGE_LOCATION_REQUEST) {
            if (resultCode == RESULT_OK) {

            }
        } else if (requestCode == GPS_REQ && resultCode == RESULT_OK) {
            String gps[] = data.getStringExtra(GPSActivity.LOC_DATA).trim().split("\\^");
            mLatitude = Double.parseDouble(gps[0]);
            mLongitude = Double.parseDouble(gps[1]);
            try {
                updateAddress();
                weatherStatus();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Monitor launch times and interval from installation
        RateMyApp.onStart(this);
        // Show a dialog if criteria is satisfied
        RateMyApp.showRateDialogIfNeeded(this);
    }


    public void onClick_moreOptions(View v) {
        changeUnits();
    }

    public void onClick_Next7Days(View v) {

        try {
            Intent intent = new Intent(this, Next7DaysForeCast.class);
            intent.putExtra(DAILY_FORECAST, mForecast.getDailyForecast());
            intent.putExtra("place", tv_place.getText().toString().trim());
            intent.putExtra("background", color);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(WeatherLiveActivity.this, "Please Wait! Getting Current Weather...", Toast.LENGTH_SHORT).show();
        }
    }


    private void changeUnits() {
        final CharSequence units[] = new CharSequence[]{"Fahrenheit", "Celsius"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a temperature unit");
        builder.setItems(units, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[units]
                if (units[which].toString() == units[1]) {
                    PrefManger.putSharedPreferencesString(WeatherLiveActivity.this, fcKey, cKey);
                    changeToCelsius();

                } else if (units[which].toString() == units[0]) {
                    PrefManger.putSharedPreferencesString(WeatherLiveActivity.this, fcKey, fKey);
                    changeToFahrenheit();
                }
            }
        });
        builder.show();
    }

    private void getLocation() {
        // Get Location through the network
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false, network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            alertUserAboutLocation();
        } else {
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        mLatitude = location.getLatitude();
                        mLongitude = location.getLongitude();
                        float accuracyf = location.getAccuracy();
                        String providerShown = location.getProvider();
                        Log.i(TAG, "Provider: " + providerShown + ", Accuracy: " + accuracyf);
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String provider = lm.getBestProvider(criteria, true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location mostRecentLocation = lm.getLastKnownLocation(provider);
            if (mostRecentLocation != null) {
                mLatitude = mostRecentLocation.getLatitude();
                mLongitude = mostRecentLocation.getLongitude();
                //last update location & time

            }

            lm.requestLocationUpdates(provider, 2000, 10, locationListener);
        }

        // Get current city that user is in.
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(mLatitude, mLongitude, 1);


            if (addresses.size() > 0) {
                // change location label to user's current location
                String getLocality = addresses.get(0).getLocality() + "," + addresses.get(0).getSubLocality();// + "," + addresses.get(0).getAdminArea();
                tv_place.setText(getLocality);
                PrefManger.putSharedPreferencesString(WeatherLiveActivity.this, lastPlaceKey, getLocality);
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception caught: ", e);
        }

    }


}
