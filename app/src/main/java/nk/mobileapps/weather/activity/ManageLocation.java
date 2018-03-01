package nk.mobileapps.weather.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.List;

import nk.mobileapps.weather.R;
import nk.mobileapps.weather.adapters.ManageLocationAdapter;
import nk.mobileapps.weather.utils.DBHelper;
import nk.mobileapps.weather.utils.Util;

public class ManageLocation extends AppCompatActivity {

    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final int PLACE_PICKER_REQUEST = 2;

    RecyclerView mRecyclerView;
    DBHelper dbHelper;
    String TAG = "ManageLocation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_location);
        dbHelper = new DBHelper(this);
        Intent intent = getIntent();
        if (getIntent().getExtras().containsKey("background")) {
            int color = intent.getExtras().getInt("background");
            findViewById(R.id.manageLocationLayout).setBackgroundColor(color);
            changeStatusBarColor(color);
            findViews();
        } else {
            finish();
        }
    }

    private void findViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        List<List<String>> ll_locations = dbHelper.getTableData(DBHelper.Location.TABLE_NAME);
        ManageLocationAdapter adapter = new ManageLocationAdapter(this,
                ll_locations,
                getSupportFragmentManager());
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<List<String>> ll_locations = dbHelper.getTableData(DBHelper.Location.TABLE_NAME);
        ManageLocationAdapter adapter = new ManageLocationAdapter(this,
                ll_locations,
                getSupportFragmentManager());
        mRecyclerView.setAdapter(adapter);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();

                break;
            case R.id.iv_search:
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
                break;
            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());


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
                Intent intent = new Intent(ManageLocation.this, DBWeatherLiveActivity.class);
                intent.putExtra("lat", place.getLatLng().latitude + "");
                intent.putExtra("log", place.getLatLng().longitude + "");
                intent.putExtra("place", place.getAddress().toString().trim());
                intent.putExtra("time", Util.getTodayTime());
                intent.putExtra("jsonObj", "");
                startActivity(intent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
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
}
