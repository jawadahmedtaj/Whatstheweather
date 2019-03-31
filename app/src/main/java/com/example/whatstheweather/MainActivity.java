package com.example.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityText;
    TextView weatherUpdate;

    public void findWeather(View view){

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityText.getWindowToken(), 0);
        weatherUpdate.setText("");
        String encodedCityName = "";
        try {
            encodedCityName = URLEncoder.encode(cityText.getText().toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getWeather weather = new getWeather();
        weather.execute("https://api.openweathermap.org/data/2.5/weather?q="+encodedCityName+"&appid=5ffcc46abfe5a0de125158fadc1058e9");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityText = (EditText) findViewById(R.id.cityText);
        weatherUpdate = (TextView) findViewById(R.id.weatherUpdate);
    }

    public class getWeather extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data!=-1){
                    char current = (char) data;
                    result+=current;
                    data = reader.read();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find Weather", Toast.LENGTH_LONG).show();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String message = "";

            try {
                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray arr = new JSONArray(weatherInfo);

                for (int i=0;i<arr.length();i++){
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String desc = jsonPart.getString("description");

                    if(main!="" && desc!=""){
                        message+=main+": "+desc+"\r\n";
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Could not find Weather", Toast.LENGTH_LONG).show();
                    }
                 }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Could not find Weather", Toast.LENGTH_LONG).show();
            }

            if(message!=""){
                weatherUpdate.setText(message);
            }

        }
    }


}
