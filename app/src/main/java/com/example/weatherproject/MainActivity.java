package com.example.weatherproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    String zipCode;
    APIRunner myThread;
    EditText input;
    ImageButton search;
    ListView output;
    TextView latLong, cityTown, currentTemp, currentDesc;
    CustomAdapter customAdapter;
    ArrayList<Weather> weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input = findViewById(R.id.input);
        search = findViewById(R.id.search);
        output = findViewById(R.id.output);
        latLong = findViewById(R.id.latLong);
        cityTown = findViewById(R.id.cityTown);
        currentTemp = findViewById(R.id.currentTemp);
        currentDesc = findViewById(R.id.currentDesc);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zipCode = input.getText().toString();
                if(zipCode.length() > 5 || zipCode.matches(" [a-zA-Z]") || zipCode.length() < 5){
                    invalidZipcode();
                }
                else {
                    myThread = new APIRunner();
                    myThread.execute(zipCode);
                }
            }
        });
    }

    public void invalidZipcode(){
        Toast toast = Toast.makeText(MainActivity.this,"Please Input A Valid Zipcode!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public class APIRunner extends AsyncTask<String, Void, ArrayList<JSONObject>>{
        @Override
        protected ArrayList<JSONObject> doInBackground(String... strings) {
            String zipCode = strings[0];
            ArrayList<JSONObject> list = new ArrayList<JSONObject>();

            try {
                String urlString = "http://api.openweathermap.org/geo/1.0/zip?zip=" + zipCode + ",US&appid=f4db1eeb4a6382cb8104e37c491a7cea";
                URL geoUrl = new URL(urlString);
                URLConnection geoUrlConnection = geoUrl.openConnection();
                InputStream geoInputStream = geoUrlConnection.getInputStream();
                BufferedReader geoBufferedReader = new BufferedReader(new InputStreamReader(geoInputStream));

                String geoData = "";
                String geoLine = geoBufferedReader.readLine();
                while(geoLine != null){
                    geoData += geoLine;
                    geoLine = geoBufferedReader.readLine();
                }
                JSONObject geoJsonObject = new JSONObject(geoData);
                list.add(geoJsonObject);

                double lat = geoJsonObject.getDouble("lat");
                double lon = geoJsonObject.getDouble("lon");
                URL ocUrl = new URL("https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&exclude=minutely,daily,alerts&appid=f4db1eeb4a6382cb8104e37c491a7cea");
                URLConnection ocUrlConnection = ocUrl.openConnection();
                InputStream ocInputStream = ocUrlConnection.getInputStream();
                BufferedReader ocBufferedReader = new BufferedReader(new InputStreamReader(ocInputStream));

                String ocData = "";
                String ocLine = ocBufferedReader.readLine();
                while(ocLine != null){
                    ocData += ocLine;
                    ocLine = ocBufferedReader.readLine();
                }
                JSONObject ocJsonObject = new JSONObject(ocData);
                list.add(ocJsonObject);

                return list;
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<JSONObject> list){
            try {
                JSONObject geo = list.get(0);
                JSONObject oc = list.get(1);

                cityTown.setText(geo.getString("name"));
                latLong.setText("(" + Math.round(geo.getDouble("lat")*100.0)/100.0 + ", " +Math.round(geo.getDouble("lon")*100.0)/100.0 + ")");
                currentTemp.setText(kToF(oc.getJSONObject("current").getDouble("temp")) + " °F");
                currentDesc.setText(oc.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("description").toUpperCase());

                weather = new ArrayList<Weather>();

                JSONObject h1 = oc.getJSONArray("hourly").getJSONObject(0);
                JSONObject h2 = oc.getJSONArray("hourly").getJSONObject(1);
                JSONObject h3 = oc.getJSONArray("hourly").getJSONObject(2);
                JSONObject h4 = oc.getJSONArray("hourly").getJSONObject(3);

                weather.add(new Weather("http://openweathermap.org/img/wn/" + h1.getJSONArray("weather").getJSONObject(0).getString("icon") + ".png", h1.getDouble("temp"), h1.getLong("dt"), h1.getJSONArray("weather").getJSONObject(0).getString("description")));
                weather.add(new Weather("http://openweathermap.org/img/wn/" + h2.getJSONArray("weather").getJSONObject(0).getString("icon") + ".png", h2.getDouble("temp"), h2.getLong("dt"), h2.getJSONArray("weather").getJSONObject(0).getString("description")));
                weather.add(new Weather("http://openweathermap.org/img/wn/" + h3.getJSONArray("weather").getJSONObject(0).getString("icon") + ".png", h3.getDouble("temp"), h3.getLong("dt"), h3.getJSONArray("weather").getJSONObject(0).getString("description")));
                weather.add(new Weather("http://openweathermap.org/img/wn/" + h4.getJSONArray("weather").getJSONObject(0).getString("icon") + ".png", h4.getDouble("temp"), h4.getLong("dt"), h4.getJSONArray("weather").getJSONObject(0).getString("description")));

                customAdapter = new CustomAdapter(MainActivity.this, R.layout.output, weather);
                output.setAdapter(customAdapter);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public double kToF(double k){
        return Math.round((k*9/5 - 459.67)*100)/100.0;
    }
}

