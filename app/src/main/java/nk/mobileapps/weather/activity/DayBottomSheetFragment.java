package nk.mobileapps.weather.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import nk.mobileapps.weather.R;
import nk.mobileapps.weather.utils.Day;
import nk.mobileapps.weather.utils.PrefManger;
import nk.mobileapps.weather.utils.Util;


public class DayBottomSheetFragment extends BottomSheetDialogFragment {

    Day day;

    public static DayBottomSheetFragment newInstance(Day day) {
        DayBottomSheetFragment fragment = new DayBottomSheetFragment();
        Bundle args = new Bundle();
        args.putParcelable("day", day);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            day = getArguments().getParcelable("day");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_day_bottom_sheet, container, false);

        TextView tv_summary = (TextView) v.findViewById(R.id.tv_summary);
        TextView tv_date = (TextView) v.findViewById(R.id.tv_date);
        TextView tv_icon = (TextView) v.findViewById(R.id.tv_icon);
        TextView tv_sunriseTime = (TextView) v.findViewById(R.id.tv_sunriseTime);
        TextView tv_tempMax = (TextView) v.findViewById(R.id.tv_tempMax);
        TextView tv_tempVariationMax = (TextView) v.findViewById(R.id.tv_tempVariationMax);
        TextView tv_tempMin = (TextView) v.findViewById(R.id.tv_tempMin);
        TextView tv_tempVariationMin = (TextView) v.findViewById(R.id.tv_tempVariationMin);
        TextView tv_sunsetTime = (TextView) v.findViewById(R.id.tv_sunsetTime);
        TextView tv_wind = (TextView) v.findViewById(R.id.tv_wind);
        TextView tv_rain = (TextView) v.findViewById(R.id.tv_rain);
        TextView tv_humidity = (TextView) v.findViewById(R.id.tv_humidity);
        TextView tv_pressure = (TextView) v.findViewById(R.id.tv_pressure);
        TextView tv_msg = (TextView) v.findViewById(R.id.tv_msg);
        ImageView iconImageView=(ImageView)v.findViewById(R.id.iconImageView);

        try {
            JSONObject jsonDay = new JSONObject(day.getDayObj());
            tv_summary.setText(jsonDay.getString("summary"));
            tv_date.setText(Util.getFormattedDate(day.getTime(), day.getTimeZone()));
            tv_icon.setText(day.getIcon());
           // tv_icon.setCompoundDrawablesWithIntrinsicBounds(0, 0, day.getIconId(), 0);
            iconImageView.setImageResource(day.getIconId());
            tv_sunriseTime.setText(Util.getFormattedTime(jsonDay.getLong("sunriseTime"), day.getTimeZone()));

            int tempMin = (int) Math.round(jsonDay.getDouble("temperatureMin"));


            if (PrefManger.getSharedPreferencesString(getContext(), WeatherLiveActivity.fcKey, "").contains(WeatherLiveActivity.cKey)) {
                tv_tempMax.setText((((day.getTemperatureMax() - 32) * 5) / 9) + "");
                tv_tempVariationMax.setText(getContext().getString(R.string.celsius));

                tv_tempMin.setText((((tempMin - 32) * 5) / 9) + "");
                tv_tempVariationMin.setText(getContext().getString(R.string.celsius));


            } else if (PrefManger.getSharedPreferencesString(getContext(), WeatherLiveActivity.fcKey, "").contains(WeatherLiveActivity.fKey)) {
                tv_tempMax.setText(day.getTemperatureMax() + "");
                tv_tempVariationMax.setText(getContext().getString(R.string.farenheit_string));

                tv_tempMin.setText(tempMin + "");
                tv_tempVariationMin.setText(getContext().getString(R.string.farenheit_string));


            } else {
                tv_tempMax.setText((((day.getTemperatureMax() - 32) * 5) / 9) + "");
                tv_tempVariationMax.setText(getContext().getString(R.string.celsius));

                tv_tempMin.setText((((tempMin - 32) * 5) / 9) + "");
                tv_tempVariationMin.setText(getContext().getString(R.string.celsius));


            }

            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/weathericons-regular-webfont.ttf");
            String windText = "";
            if (PrefManger.getSharedPreferencesString(getContext(), WeatherLiveActivity.windKey, "").contains(WeatherLiveActivity.w_kmhKey)) {
                //km/h
                windText = getContext().getString(R.string.format_wind,
                        Util.toKmPerHour(Double.parseDouble(jsonDay.getString("windSpeed"))), "km/h",
                        Util.convertBearingToDirection(getContext(), Double.parseDouble(jsonDay.getString("windBearing"))));
            } else if (PrefManger.getSharedPreferencesString(getContext(), WeatherLiveActivity.windKey, "").contains(WeatherLiveActivity.w_msKey)) {
                //m/s
                windText = getContext().getString(R.string.format_wind,
                        Util.toMilesPerSec(Util.toKmPerHour(Double.parseDouble(jsonDay.getString("windSpeed")))), "m/s",
                        Util.convertBearingToDirection(getContext(), Double.parseDouble(jsonDay.getString("windBearing"))));
            } else {
                //mph
                windText = getContext().getString(R.string.format_wind,
                        Double.parseDouble(jsonDay.getString("windSpeed")), "mph",
                        Util.convertBearingToDirection(getContext(), Double.parseDouble(jsonDay.getString("windBearing"))));
            }
            tv_wind.setTypeface(typeface);
            tv_wind.setText(windText);

            tv_sunsetTime.setText(Util.getFormattedTime(jsonDay.getLong("sunsetTime"), day.getTimeZone()));
            tv_humidity.setText(getString(R.string.humidity) + " : " + jsonDay.getDouble("humidity") + "");
            tv_rain.setText(getString(R.string.precip_text) + " : " + jsonDay.getString("precipProbability") + "%");

            String pressureText = getString(R.string.format_pressure, jsonDay.getDouble("pressure"), "hPa");
            tv_pressure.setText(pressureText);
            String time = day.getDayOfTheWeek();
            String temperature = tv_tempMax.getText().toString().trim() + "";
            String summary = day.getSummary();
            final String message = String.format("At %s, it will be %s and %s", time, temperature, summary);

            tv_summary.setText(message);
            tv_msg.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return v;
    }


}
