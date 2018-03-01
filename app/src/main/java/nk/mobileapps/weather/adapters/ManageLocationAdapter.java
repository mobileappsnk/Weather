package nk.mobileapps.weather.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import nk.mobileapps.weather.R;
import nk.mobileapps.weather.activity.DBWeatherLiveActivity;
import nk.mobileapps.weather.activity.WeatherLiveActivity;
import nk.mobileapps.weather.utils.Current;
import nk.mobileapps.weather.utils.DBHelper;
import nk.mobileapps.weather.utils.PrefManger;


public class ManageLocationAdapter extends RecyclerView.Adapter<ManageLocationAdapter.LocationViewHolder> {

    private List<List<String>> mLocations;
    private Context mContext;
    private DBHelper dbHelper;
    private FragmentManager mFragmentManager;

    public ManageLocationAdapter(Context context, List<List<String>> locations, FragmentManager fragmentManager) {
        mContext = context;
        mLocations = locations;
        mFragmentManager = fragmentManager;
        dbHelper = new DBHelper(mContext);
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_location_list_item, viewGroup, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LocationViewHolder hourViewHolder, int position) {
        hourViewHolder.bindHour(mLocations.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mLocations.size();
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tv_location, tv_sublocation, tv_humidity_rain;
        public TextView mTemperatureLabel, mTempVariation;
        public ImageView mIconImageView, iv_delete;


        public LocationViewHolder(View itemView) {
            super(itemView);

            tv_location = (TextView) itemView.findViewById(R.id.tv_location);
            tv_sublocation = (TextView) itemView.findViewById(R.id.tv_sublocation);
            mTemperatureLabel = (TextView) itemView.findViewById(R.id.temperatureLabel);
            mTempVariation = (TextView) itemView.findViewById(R.id.tempVariation);
            mIconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);
            tv_humidity_rain = (TextView) itemView.findViewById(R.id.tv_humidity_rain);
            iv_delete = (ImageView) itemView.findViewById(R.id.iv_delete);

            itemView.setOnClickListener(this);

        }

        public void bindHour(final List<String> l_loc, final int position) {
            //gps, place, last_updateTime, weather_obj
            iv_delete.setTag(position);
            tv_location.setTag(position);
            String loc = l_loc.get(2).toString().trim();
            if (loc.contains(",")) {
                tv_location.setText(loc.substring(0, loc.indexOf(",")));
                tv_sublocation.setText(loc.substring(loc.indexOf(",") + 1, loc.length()));
            } else {
                tv_location.setText(loc);
                tv_sublocation.setVisibility(View.GONE);
            }

            try {
                Current current = getCurrentDetails(l_loc.get(4).toString().trim());
                // shared preferences stuff
                if (PrefManger.getSharedPreferencesString(mContext, WeatherLiveActivity.fcKey, "").contains(WeatherLiveActivity.cKey)) {
                    mTemperatureLabel.setText((((current.getTemperature() - 32) * 5) / 9) + "");
                    mTempVariation.setText(mContext.getString(R.string.celsius));
                } else if (PrefManger.getSharedPreferencesString(mContext, WeatherLiveActivity.fcKey, "").contains(WeatherLiveActivity.fKey)) {
                    mTemperatureLabel.setText(current.getTemperature() + "");
                    mTempVariation.setText(mContext.getString(R.string.farenheit_string));
                } else {
                    mTemperatureLabel.setText((((current.getTemperature() - 32) * 5) / 9) + "");
                    mTempVariation.setText(mContext.getString(R.string.celsius));
                }

                if (position == 0) {
                    iv_delete.setVisibility(View.GONE);
                }
                mIconImageView.setImageResource(current.getIconId());
                tv_humidity_rain.setText("Humidity " + current.getHumidity() + " | Rain?Sown " + current.getPrecipChance() + "%");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dbHelper.deleteByValues(DBHelper.Location.TABLE_NAME, new String[]{DBHelper.UID}, new String[]{l_loc.get(0)});
                    mLocations.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(mContext, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                }
            });


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

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, DBWeatherLiveActivity.class);
            List<String> l_sel = mLocations.get((Integer) tv_location.getTag());
            //gps, place, last_updateTime, weather_obj
            intent.putExtra("lat", l_sel.get(1).split("\\|")[0]);
            intent.putExtra("log", l_sel.get(1).split("\\|")[1]);
            intent.putExtra("place", l_sel.get(2).toString().trim());
            intent.putExtra("time", l_sel.get(3).trim().trim());
            intent.putExtra("jsonObj", l_sel.get(4).trim());
            mContext.startActivity(intent);
        }
    }
}
