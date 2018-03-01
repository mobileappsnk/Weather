package nk.mobileapps.weather.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import nk.mobileapps.weather.R;
import nk.mobileapps.weather.activity.WeatherLiveActivity;
import nk.mobileapps.weather.utils.Hour;
import nk.mobileapps.weather.utils.PrefManger;
import nk.mobileapps.weather.utils.Util;


public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder> {

    private Hour[] mHours;
    private Context mContext;

    public HourAdapter(Context context, Hour[] hours) {
        mContext = context;
        mHours = hours;
    }

    @Override
    public HourViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_hourly_list_item, viewGroup, false);
        return new HourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HourViewHolder hourViewHolder, int position) {
        hourViewHolder.bindHour(mHours[position]);
    }

    @Override
    public int getItemCount() {
        return mHours.length;
    }

    public class HourViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTimeLabel;
        public TextView mSummaryLabel;
        public TextView mTemperatureLabel, mTempVariation;
        public ImageView mIconImageView;

        public TextView tv_wind, tv_rain, tv_humidity, tv_pressure;


        public HourViewHolder(View itemView) {
            super(itemView);

            mTimeLabel = (TextView) itemView.findViewById(R.id.timeLabel);
            mSummaryLabel = (TextView) itemView.findViewById(R.id.summaryLabel);
            mTemperatureLabel = (TextView) itemView.findViewById(R.id.temperatureLabel);
            mTempVariation = (TextView) itemView.findViewById(R.id.tempVariation);
            mIconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);

            tv_wind = (TextView) itemView.findViewById(R.id.tv_wind);
            tv_rain = (TextView) itemView.findViewById(R.id.tv_rain);
            tv_humidity = (TextView) itemView.findViewById(R.id.tv_humidity);
            tv_pressure = (TextView) itemView.findViewById(R.id.tv_pressure);

            itemView.setOnClickListener(this);
        }

        public void bindHour(Hour hour) {
            mTimeLabel.setText(hour.getHour());
            mSummaryLabel.setText(hour.getSummary());

            // shared preferences stuff
            if (PrefManger.getSharedPreferencesString(mContext, WeatherLiveActivity.fcKey, "").contains(WeatherLiveActivity.cKey)) {
                mTemperatureLabel.setText((((hour.getTemperature() - 32) * 5) / 9) + "");
                mTempVariation.setText(mContext.getString(R.string.celsius));
            } else if (PrefManger.getSharedPreferencesString(mContext, WeatherLiveActivity.fcKey, "").contains(WeatherLiveActivity.fKey)) {
                mTemperatureLabel.setText(hour.getTemperature() + "");
                mTempVariation.setText(mContext.getString(R.string.farenheit_string));
            } else {
                mTemperatureLabel.setText((((hour.getTemperature() - 32) * 5) / 9) + "");
                mTempVariation.setText(mContext.getString(R.string.celsius));
            }

            mIconImageView.setImageResource(hour.getIconId());

            try {
                Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/weathericons-regular-webfont.ttf");
                JSONObject jsonHour = new JSONObject(hour.getObj());
                String windText = "";
                if (PrefManger.getSharedPreferencesString(mContext, WeatherLiveActivity.windKey, "").contains(WeatherLiveActivity.w_kmhKey)) {
                    //km/h
                    windText = mContext.getString(R.string.format_wind,
                            Util.toKmPerHour(Double.parseDouble(jsonHour.getString("windSpeed"))), "km/h",
                            Util.convertBearingToDirection(mContext, Double.parseDouble(jsonHour.getString("windBearing"))));
                } else if (PrefManger.getSharedPreferencesString(mContext, WeatherLiveActivity.windKey, "").contains(WeatherLiveActivity.w_msKey)) {
                    //m/s
                    windText = mContext.getString(R.string.format_wind,
                            Util.toMilesPerSec(Util.toKmPerHour(Double.parseDouble(jsonHour.getString("windSpeed")))), "m/s",
                            Util.convertBearingToDirection(mContext, Double.parseDouble(jsonHour.getString("windBearing"))));
                } else {
                    //mph
                    windText = mContext.getString(R.string.format_wind,
                            Double.parseDouble(jsonHour.getString("windSpeed")), "mph",
                            Util.convertBearingToDirection(mContext, Double.parseDouble(jsonHour.getString("windBearing"))));
                }
                tv_wind.setTypeface(typeface);
                tv_wind.setText(windText);

                tv_rain.setText(mContext.getString(R.string.precip_text) + " : " + jsonHour.getString("precipProbability") + "%");
                tv_humidity.setText(mContext.getString(R.string.humidity) + " : " + jsonHour.getString("humidity") + "");
                String pressureText = mContext.getString(R.string.format_pressure, Double.parseDouble(jsonHour.getString("pressure")), "hPa");
                tv_pressure.setText(pressureText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
            String time = mTimeLabel.getText().toString();
            String temperature = mTemperatureLabel.getText().toString();
            String summary = mSummaryLabel.getText().toString();
            String message = String.format("At %s, it will be %s and %s", time, temperature, summary);
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        }
    }
}
