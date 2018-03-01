package nk.mobileapps.weather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import nk.mobileapps.weather.R;

/**
 * Created by user on 1/12/17.
 * <p>
 * https://github.com/martykan/forecastie/tree/master/app/src/main/java/cz/martykan/forecastie/widgets
 */

public class Util {


    public static final String isFirstTime = "isFirstTime";
    String degree = "°";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    public static String convertBearingToDirection(Context context, double bearing) {
        String[] arrows = context.getResources().getStringArray(R.array.windDirection_arrows);
        String direction;
        if (bearing >= 0 && bearing < 22.5) direction = "N" + " " + arrows[0];
        else if (bearing >= 22.5 && bearing < 67.5) direction = "NE" + " " + arrows[1];
        else if (bearing >= 67.5 && bearing < 112.5) direction = "E" + " " + arrows[2];
        else if (bearing >= 112.5 && bearing < 157.5) direction = "SE" + " " + arrows[3];
        else if (bearing >= 157.5 && bearing < 202.5) direction = "S" + " " + arrows[4];
        else if (bearing >= 202.5 && bearing < 247.5) direction = "SW" + " " + arrows[5];
        else if (bearing >= 247.5 && bearing < 292.5) direction = "W" + " " + arrows[6];
        else if (bearing >= 292.5 && bearing < 337.5) direction = "NW" + " " + arrows[7];
        else direction = "N" + " " + arrows[0];
        return direction.trim();
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static String getFormattedDate(long timezone, String timeZone) {
        Date dateTime = new Date(timezone * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("EE, dd MMM yyyy", Locale.getDefault());
        // SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        return formatter.format(dateTime);
    }

    public static String getFormattedDateTime(long timezone, String timeZone) {
        Date dateTime = new Date(timezone * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("EE, dd MMM yyyy, h:mm a", Locale.getDefault());
        // SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        return formatter.format(dateTime);
    }

    public static String getFormattedTime(long timezone, String timeZone) {
        Date dateTime = new Date(timezone * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a", Locale.getDefault());
        // SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        return formatter.format(dateTime);
    }

    public static String getTodayTime() {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        return dateFormat.format(new Date());
    }

    public static void InstallAPK(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            try {
                String command;
                command = "adb install -r " + filename;
                Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
                proc.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getCompleteAddressString(Context m_context, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(m_context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(m_context, "Sorry, Your location cannot be retrieved !" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return strAdd;
    }

    public static double convertFahrenheitToCelcius(double degreesInFahrenheit) {
        return (degreesInFahrenheit - 32) * 5 / 9;
    }

    public static double convertCelciusToFahrenheit(double degreesInCelcius) {
        return degreesInCelcius * 9 / 5 + 32;
    }

    public static double toMilesPerHour(double kmPerHour) {
        return kmPerHour * 0.62137119;
    }

    public static double toKmPerHour(double milesPerHour) {
        return milesPerHour * 1.609344;
    }

    public static double toMilesPerSec(Double kmPerHour) {
        return kmPerHour * 0.277778;
    }

    public static float convertPressure(float pressure, SharedPreferences sp) {
        if (sp.getString("pressureUnit", "hPa").equals("kPa")) {
            return pressure / 10;
        } else if (sp.getString("pressureUnit", "hPa").equals("mm Hg")) {
            return (float) (pressure * 0.750061561303);
        } else if (sp.getString("pressureUnit", "hPa").equals("in Hg")) {
            return (float) (pressure * 0.0295299830714);
        } else {
            return pressure;
        }
    }

    public static double convertWind(double wind, SharedPreferences sp) {
        if (sp.getString("speedUnit", "m/s").equals("kph")) {
            return wind * 3.6;
        } else if (sp.getString("speedUnit", "m/s").equals("mph")) {
            return wind * 2.23693629205;
        } else if (sp.getString("speedUnit", "m/s").equals("kn")) {
            return wind * 1.943844;
        } else if (sp.getString("speedUnit", "m/s").equals("bft")) {
            if (wind < 0.3) {
                return 0; // Calm
            } else if (wind < 1.5) {
                return 1; // Light air
            } else if (wind < 3.3) {
                return 2; // Light breeze
            } else if (wind < 5.5) {
                return 3; // Gentle breeze
            } else if (wind < 7.9) {
                return 4; // Moderate breeze
            } else if (wind < 10.7) {
                return 5; // Fresh breeze
            } else if (wind < 13.8) {
                return 6; // Strong breeze
            } else if (wind < 17.1) {
                return 7; // High wind
            } else if (wind < 20.7) {
                return 8; // Gale
            } else if (wind < 24.4) {
                return 9; // Strong gale
            } else if (wind < 28.4) {
                return 10; // Storm
            } else if (wind < 32.6) {
                return 11; // Violent storm
            } else {
                return 12; // Hurricane
            }
        } else {
            return wind;
        }
    }

    public static String getBeaufortName(int wind) {
        if (wind == 0) {
            return "Calm";
        } else if (wind == 1) {
            return "Light air";
        } else if (wind == 2) {
            return "Light breeze";
        } else if (wind == 3) {
            return "Gentle breeze";
        } else if (wind == 4) {
            return "Moderate breeze";
        } else if (wind == 5) {
            return "Fresh breeze";
        } else if (wind == 6) {
            return "Strong breeze";
        } else if (wind == 7) {
            return "High wind";
        } else if (wind == 8) {
            return "Gale";
        } else if (wind == 9) {
            return "Strong gale";
        } else if (wind == 10) {
            return "Storm";
        } else if (wind == 11) {
            return "Violent storm";
        } else {
            return "Hurricane";
        }
    }


    //convert MilliBars to mmHg.
    private Double convertMbToMmHg(Double pressureInMb) {
        return pressureInMb * 0.750062;
    }

    /*Glide.with(getContext())
        .load(url)
        .dontAnimate()
        .placeholder(R.drawable.shape_empt_night)
        .error(R.drawable.ic_launcher)
        .into(mIv);*/
}
