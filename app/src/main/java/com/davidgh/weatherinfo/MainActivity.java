package com.davidgh.weatherinfo;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.davidgh.weatherinfo.helpers.CommonSettings;
import com.davidgh.weatherinfo.helpers.NetworkUtils;
import com.davidgh.weatherinfo.models.OpenWeatherData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity {

    // Android Layout
    private Spinner userList;
    private ImageView icon;
    private TextView maxTemp, minTemp, humidity, windSpeed;

    OpenWeatherData mWeatherData = new OpenWeatherData();

    private static final String [] cities = {
            "Yerevan",
            "Gyumry",
            "Aparan",
            "Vanadzor",
            "Goris"
    };
    private static long [] ids = {
            616051,
            616635,
            616953,
            616530,
            174895
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Android Layout
        userList = (Spinner) findViewById(R.id.user_list);
        icon = (ImageView) findViewById(R.id.icon);
        maxTemp = (TextView) findViewById(R.id.tv_max_temp);
        minTemp = (TextView) findViewById(R.id.tv_min_temp);
        humidity = (TextView) findViewById(R.id.tv_humidity);
        windSpeed = (TextView) findViewById(R.id.tv_wind_speed);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, cities);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userList.setAdapter(adapter);

        userList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (NetworkUtils.isNetworkAvailable(MainActivity.this)){
                    new GetWeather().execute(CommonSettings.apiRequest(String.valueOf(ids[i])));
                } else {
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder;

                    builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Internet Connection Error")
                            .setMessage("Could you please enable celular data or WiFi?")
                            .setPositiveButton("WiFi", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    getWifiPermission();
                                }
                            })
                            .setNegativeButton("Celular data", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    getCelularPermission();
                                }
                            }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void getWifiPermission(){
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void getCelularPermission(){
        Intent cellular = new Intent(Intent.ACTION_MAIN, null);
        cellular.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cnn = new ComponentName("com.android.settings",
                "com.android.settings.Settings$DataUsageSummaryActivity");
        cellular.setComponent(cnn);
        cellular.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(cellular);
    }

    private class GetWeather extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mWeatherData = new OpenWeatherData();
            
        }

        @Override
        protected String doInBackground(String... strings) {

            String stream = null;
            String urlString = strings[0];

            NetworkUtils networkUtils = new NetworkUtils();
            stream = networkUtils.getHttpData(urlString);

            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Gson gson = new Gson();
            Type type = new TypeToken<OpenWeatherData>(){}.getType();

            mWeatherData = gson.fromJson(s, type);

            //Picasso.with(MainActivity.this).load(CommonSettings.getImage(mWeatherData.getWeatherList().get(0).getIcon())).into(icon);
            Log.d("Image : ", "onPostExecute: " + mWeatherData.getWeatherList());
            maxTemp.setText("Max : " + mWeatherData.getMain().getTemp_max());
            minTemp.setText("Min : " + mWeatherData.getMain().getTemp_min());
            humidity.setText("Humidity : " + mWeatherData.getMain().getHumidity());
            windSpeed.setText("Wind Speed : " + mWeatherData.getWind().getSpeed());
        }
    }
}
