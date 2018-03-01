package nk.mobileapps.weather.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import nk.mobileapps.mysplashslider.PaperOnboardingEngine;
import nk.mobileapps.mysplashslider.PaperOnboardingPage;
import nk.mobileapps.mysplashslider.listeners.PaperOnboardingOnChangeListener;
import nk.mobileapps.weather.R;
import nk.mobileapps.weather.utils.DBHelper;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class SplashActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_main_layout);
        dbHelper = new DBHelper(this);
        if (dbHelper.getCount(DBHelper.Location.TABLE_NAME) > 0) {
            startActivity(new Intent(SplashActivity.this, WeatherLiveActivity.class));
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        PaperOnboardingEngine splashEngine = new PaperOnboardingEngine(findViewById(R.id.onboardingRootView), getDataForSplashboarding(), getApplicationContext());

        splashEngine.setOnChangeListener(new PaperOnboardingOnChangeListener() {
            @Override
            public void onPageChanged(int oldElementIndex, int newElementIndex) {
                if (newElementIndex == 2) {
                    findViewById(R.id.iv_done).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.iv_done).setVisibility(View.GONE);
                }
                if (newElementIndex == 0) {
                    findViewById(R.id.btn_skip).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.btn_skip).setVisibility(View.GONE);
                }
            }
        });

        // making notification bar transparent
        changeStatusBarColor();
        findViewById(R.id.iv_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this, WeatherLiveActivity.class));
                finish();
            }
        });

        findViewById(R.id.btn_skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this, WeatherLiveActivity.class));
                finish();
            }
        });

        askPermissionsforApp();


    }

    private void askPermissionsforApp() {
        if (EasyPermissions.hasPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})) {

        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "Application Permission's",
                    1234,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
        }

    }


    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private ArrayList<PaperOnboardingPage> getDataForSplashboarding() {
        // prepare data 678FB4 65B0B4 9B90BC
        PaperOnboardingPage scr1 = new PaperOnboardingPage("Currently", "Current weather is frequently updated based on location",
                Color.parseColor("#678FB4"), R.drawable.ic_currently, R.drawable.ic_currently_key);
        PaperOnboardingPage scr2 = new PaperOnboardingPage("Hourly", "Hour-by-Hour weather forecast including temperature, RealFeel and chance of precipitation",
                Color.parseColor("#65B0B4"), R.drawable.ic_hourly, R.drawable.ic_hourly_key);
        PaperOnboardingPage scr3 = new PaperOnboardingPage("Daily", "You can search 7 day weather forecast with daily average parameters",
                Color.parseColor("#9B90BC"), R.drawable.ic_daily, R.drawable.ic_daily_key);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);
        return elements;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
