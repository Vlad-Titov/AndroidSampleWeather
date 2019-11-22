package com.example.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    String API = "60d469faae437926cd2e097a743c4509";

    TextView addressTxt, updated_atTxt, statusTxt, tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
            sunsetTxt, windTxt, pressureTxt, humidityTxt;
    ImageView themeSwitchImg, cityPkrImg;
    public String mUpdatedAtText, mTemp, mTempMin, mTempMax, mPressure, mHumidity, mSunrise, mSunset, mWindSpeed, mWeatherDescription, mAddress,
            UPDATED_AT_TEXT = "updated_text", TEMP = "temp", TEMP_MIN = "temp_min", TEMP_MAX = "temp_max", PRESSURE = "pressure", HUMIDITY = "humidity",
            SUNRISE = "sunrise", SUNSET = "sunset", WIND_SPEED = "wind_speed", WEATHER_DESCRIPTION = "weather_desc", ADDRESS = "address";
    protected String mCity = "Kazan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);

        Utils.setBackground(this);

        addressTxt = findViewById(R.id.address);
        updated_atTxt = findViewById(R.id.updated_at);
        statusTxt = findViewById(R.id.status);
        tempTxt = findViewById(R.id.temp);
        temp_minTxt = findViewById(R.id.temp_min);
        temp_maxTxt = findViewById(R.id.temp_max);
        sunriseTxt = findViewById(R.id.sunrise);
        sunsetTxt = findViewById(R.id.sunset);
        windTxt = findViewById(R.id.wind);
        pressureTxt = findViewById(R.id.pressure);
        humidityTxt = findViewById(R.id.humidity);
        themeSwitchImg = findViewById(R.id.themeSwitch);
        cityPkrImg = findViewById(R.id.cityPkr);

        themeSwitchImg.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                switch (getThemeId()) {
                    case R.style.CustomThemeDark:
                        Utils.changeToTheme(MainActivity.this, Utils.THEME_LIGHT);
                        break;
                    case R.style.CustomThemeLight:
                        Utils.changeToTheme(MainActivity.this, Utils.THEME_DEFAULT);
                        break;
                }
            }
        });

        cityPkrImg.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                showEnterCityDialog();
            }
        });

        if (savedInstanceState == null ) {
            new weatherTask().execute();
        } else {
            restoreState(savedInstanceState);
            setUIValues();
        }
    }

    protected void setUIValues(){
        addressTxt.setText(mAddress);
        updated_atTxt.setText(mUpdatedAtText);
        statusTxt.setText(mWeatherDescription);
        tempTxt.setText(mTemp);
        temp_minTxt.setText(mTempMin);
        temp_maxTxt.setText(mTempMax);
        sunriseTxt.setText(mSunrise);
        sunsetTxt.setText(mSunset);
        windTxt.setText(mWindSpeed);
        pressureTxt.setText(mPressure);
        humidityTxt.setText(mHumidity);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(UPDATED_AT_TEXT, mUpdatedAtText);
        outState.putString(TEMP, mTemp);
        outState.putString(TEMP_MIN, mTempMin);
        outState.putString(TEMP_MAX, mTempMax);
        outState.putString(PRESSURE, mPressure);
        outState.putString(HUMIDITY, mHumidity);
        outState.putString(SUNRISE, mSunrise);
        outState.putString(SUNSET, mSunset);
        outState.putString(WIND_SPEED, mWindSpeed);
        outState.putString(WEATHER_DESCRIPTION, mWeatherDescription);
        outState.putString(ADDRESS, mAddress);

        super.onSaveInstanceState(outState);
    }

    protected void restoreState(Bundle savedInstanceState) {
        mUpdatedAtText = savedInstanceState.getString(UPDATED_AT_TEXT);
        mTemp = savedInstanceState.getString(TEMP);
        mTempMin = savedInstanceState.getString(TEMP_MIN);
        mTempMax = savedInstanceState.getString(TEMP_MAX);
        mPressure = savedInstanceState.getString(PRESSURE);
        mHumidity = savedInstanceState.getString(HUMIDITY);

        mSunrise = savedInstanceState.getString(SUNRISE);
        mSunset = savedInstanceState.getString(SUNSET);
        mWindSpeed = savedInstanceState.getString(WIND_SPEED);
        mWeatherDescription = savedInstanceState.getString(WEATHER_DESCRIPTION);

        mAddress = savedInstanceState.getString(ADDRESS);
    }

    protected void showEnterCityDialog(){
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = (getThemeId() == R.style.CustomThemeDark) ?
                new AlertDialog.Builder(this, R.style.DialogDark) :
                new AlertDialog.Builder(this, R.style.DialogLight);

        builder.setTitle("Enter city");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCity = input.getText().toString();
                new weatherTask().execute();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    protected int getThemeId() {
        try {
            Class<?> wrapper = Context.class;
            Method method = wrapper.getMethod("getThemeResId");
            method.setAccessible(true);
            return (Integer) method.invoke(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /* Showing the ProgressBar, Making the main design GONE */
            findViewById(R.id.loader).setVisibility(View.VISIBLE);
            findViewById(R.id.mainContainer).setVisibility(View.GONE);
            findViewById(R.id.errorText).setVisibility(View.GONE);
        }

        protected String doInBackground(String... args) {
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + mCity + "&units=metric&appid=" + API);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                Long updatedAt = jsonObj.getLong("dt");
                mUpdatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                mTemp = main.getString("temp") + "°C";
                mTempMin = "Min Temp: " + main.getString("temp_min") + "°C";
                mTempMax = "Max Temp: " + main.getString("temp_max") + "°C";
                mPressure = main.getString("pressure");
                mHumidity = main.getString("humidity");

                mSunrise = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sys.getLong("sunrise") * 1000));
                mSunset = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sys.getLong("sunset") * 1000));
                mWindSpeed = wind.getString("speed");
                mWeatherDescription = (weather.getString("description")).toUpperCase();

                mAddress = jsonObj.getString("name") + ", " + sys.getString("country");


                /* Populating extracted data into our views */
                setUIValues();

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);


            } catch (JSONException e) {
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }
        }
    }
}
