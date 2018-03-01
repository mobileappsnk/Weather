package nk.mobileapps.weather.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import nk.mobileapps.weather.R;
import nk.mobileapps.weather.activity.WeatherLiveActivity;
import nk.mobileapps.weather.utils.Day;
import nk.mobileapps.weather.utils.PrefManger;


public class DayRVAdapter extends RecyclerView.Adapter<DayRVAdapter.DayViewHolder> {

    private Day[] mDays;
    private Context mContext;

    public DayRVAdapter(Context context, Day[] days) {
        mContext = context;
        mDays = days;
    }

    @Override
    public DayViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_day_list_item, viewGroup, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DayViewHolder hourViewHolder, int position) {
        hourViewHolder.bindHour(mDays[position], position);
    }

    @Override
    public int getItemCount() {
        return mDays.length;
    }

    public class DayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTimeLabel;
        public TextView mSummaryLabel;
        public TextView mTemperatureLabel, mTempVariation;
        public ImageView mIconImageView;


        public DayViewHolder(View itemView) {
            super(itemView);

            mTimeLabel = (TextView) itemView.findViewById(R.id.timeLabel);
            mSummaryLabel = (TextView) itemView.findViewById(R.id.summaryLabel);
            mTemperatureLabel = (TextView) itemView.findViewById(R.id.temperatureLabel);
            mTempVariation = (TextView) itemView.findViewById(R.id.tempVariation);
            mIconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);

            itemView.setOnClickListener(this);
        }

        public void bindHour(Day day, int position) {
            //Time or Day Label
            if (position == 0) {
                mTimeLabel.setText("Today");
            } else {
                mTimeLabel.setText(day.getDayOfTheWeek());
            }

            mSummaryLabel.setText(day.getSummary());

            // shared preferences stuff
            if (PrefManger.getSharedPreferencesString(mContext, WeatherLiveActivity.fcKey, "").contains(WeatherLiveActivity.cKey)) {
                mTemperatureLabel.setText((((day.getTemperatureMax() - 32) * 5) / 9) + "");
                mTempVariation.setText(mContext.getString(R.string.celsius));
            } else if (PrefManger.getSharedPreferencesString(mContext, WeatherLiveActivity.fcKey, "").contains(WeatherLiveActivity.fKey)) {
                mTemperatureLabel.setText(day.getTemperatureMax() + "");
                mTempVariation.setText(mContext.getString(R.string.farenheit_string));
            } else {
                mTemperatureLabel.setText((((day.getTemperatureMax() - 32) * 5) / 9) + "");
                mTempVariation.setText(mContext.getString(R.string.celsius));
            }

            mIconImageView.setImageResource(day.getIconId());
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
