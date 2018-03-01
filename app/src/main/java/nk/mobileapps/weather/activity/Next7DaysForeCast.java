package nk.mobileapps.weather.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.Arrays;

import nk.mobileapps.weather.R;
import nk.mobileapps.weather.adapters.Next7DaysAdapter;
import nk.mobileapps.weather.utils.Day;

public class Next7DaysForeCast extends AppCompatActivity {

    private Day[] mDays;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next7_days_fore_cast);
        Intent intent = getIntent();
        if (getIntent().getExtras().containsKey(WeatherLiveActivity.DAILY_FORECAST)) {
            Parcelable[] parcelables = intent.getParcelableArrayExtra(WeatherLiveActivity.DAILY_FORECAST);
            int color = intent.getExtras().getInt("background");
            String place = intent.getExtras().getString("place");
            ((TextView) findViewById(R.id.tv_title)).setText(place);
            findViewById(R.id.next7daysLayout).setBackgroundColor(color);
            changeStatusBarColor(color);
            // get array of items from parcelable extra
            mDays = Arrays.copyOf(parcelables, parcelables.length, Day[].class);
            findViews();
        } else {
            finish();
        }
    }

    private void findViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        Next7DaysAdapter adapter = new Next7DaysAdapter(this,
                mDays,
                getSupportFragmentManager());
        mRecyclerView.setAdapter(adapter);

    }



    public void changeStatusBarColor(int color) {
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

    @Override
    public void onBackPressed() {
        finish();
    }
}
