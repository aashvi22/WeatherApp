package com.example.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    TextView textView;
    ArrayList weather = new ArrayList<DailyWeather>();
    ListView listView;
    TextView temp;
    EditText zipcode;
    TextView weatherDes;
    ImageView currentImage;
    //Button go;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("tag", "hello");

        listView = findViewById(R.id.id_listView);
        temp = findViewById(R.id.id_temperature);
        zipcode = findViewById(R.id.editText);
        weatherDes = findViewById(R.id.id_description);
        //go = findViewById(R.id.id_button);
        currentImage = findViewById(R.id.id_currentIcon);

        AsyncThread myThread = new AsyncThread();
        myThread.execute();

        CustomAdapter customAdapter = new CustomAdapter(this, R.layout.adapter_custom, weather);
        listView.setAdapter(customAdapter);

        //go.setOnClickListener(View.onCLickListener);

    }


    public class AsyncThread extends AsyncTask<Void,Void, ArrayList<String>> { //put background work on this thread

        String currentWeather;
        String currentTemp;
        String currentIcon;
        ArrayList a = new ArrayList<>();


        /*Context context;
        public AsyncThread(Context context){
            this.context=context;

        }
        public void UpdateTemp(String Temp){
            TextView temp = ((Activity)context).findViewById(R.id.id_temperature);
            temp.setText(Temp + "°F");
        }
        public void UpdateWeather(String Weather){
            TextView weather = ((Activity)context).findViewById(R.id.id_description);
            weather.setText(Weather);
        }
        public void UpdateIcon(String iconID){
            //ImageView currentIcon  = ((Activity)context).findViewById(R.id.id_currentIcon);
            //Picasso.get().load("http://openweathermap.org/img/w/" + iconID + ".png").into(currentIcon);
        }
        public String getZip(){
            EditText zipcode = ((Activity)context).findViewById(R.id.editText);
            return zipcode.getText().toString();
        }

         */



        protected void onPreExecute() {
        }


        @Override
        protected ArrayList<String> doInBackground(Void... params) {


            //"http://api.openweathermap.org/data/2.5/forecast?zip=08810&APPID=925c8470885efec2b3c6cf6e5905465c"
            try {

                System.out.println("Do in background running ");
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?zip=" + "08852" + "&APPID=925c8470885efec2b3c6cf6e5905465c");
                URLConnection urlConnection = url.openConnection();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                StringBuilder stringBuilder = new StringBuilder();
                String line;
                line = bufferedReader.readLine();
                Log.d("tag", line);

                bufferedReader.close();

                String jsonString = stringBuilder.toString();
                System.out.println(jsonString);
                //Log.d("tag", jsonString);

                JSONObject jsonObject = new JSONObject(line);
                Log.d("tag", jsonString);

                ArrayList tempArray = new ArrayList<String>(); //temperature every 3 hours
                ArrayList weatherArray = new ArrayList<String>();
                ArrayList time = new ArrayList<String>();
                ArrayList icon = new ArrayList<String>();

                //JSONObject search = (JSONObject) jsonObject.get("list");//1
                JSONArray list = (JSONArray) jsonObject.get("list");//2

                for (int i = 0; i < list.length(); i++) {

                    //System.out.println(list.get(i) + " " + i);
                    JSONObject jsonObject1 = (JSONObject)list.get(i);

                    /*


                    TimeZone tz = TimeZone.getTimeZone("EST");

                    String formatted = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date((Long.parseLong(jsonObject1.getString("dt"))*1000)));

                    */
                    Date d = new Date((Long.parseLong(jsonObject1.getString("dt"))*1000));

                    // timezone symbol (z) included in the format pattern for debug
                    DateFormat myFormat = new SimpleDateFormat("MM/dd HH:mm:ss a z");

                    String formatted = myFormat.format(d);

                    if(i == 0){
                        //temp.setText((int)(1.8*((Double.parseDouble(jsonObject1.getJSONObject("main").getString("temp"))) - 273.15)+32.0) + "");
                        currentTemp = (int)(1.8*((Double.parseDouble(jsonObject1.getJSONObject("main").getString("temp"))) - 273.15)+32.0) + "";
                        //UpdateTemp(currentTemp);

                    }

                    if(i == 0){
                        JSONArray weatherList = (JSONArray)jsonObject1.get("weather");

                        for(int j = 0; j < weatherList.length(); j++) {
                            JSONObject jsonObject2 = (JSONObject) weatherList.get(j);
                            weatherArray.add(jsonObject2.getString("description"));
                            //System.out.println("weather is "+ jsonObject2.getString("description"));
                            icon.add(jsonObject2.getString("icon"));


                            currentWeather = (jsonObject2).getString("description");
                            //UpdateWeather(currentWeather);
                            System.out.println("CURRENT WEATHER: " + currentWeather);
                            currentIcon = jsonObject2.getString("icon");
                            //UpdateIcon(currentIcon);
                            //System.out.println("CURRENT ICON: " + currentIcon);
                        }
                    }

                    if(formatted.substring(6).equals("13:00:00 PM EST")){ //it's fine if it's at 1pm instead of noon right

                       int TFarenheit = (int)(1.8*((Double.parseDouble(jsonObject1.getJSONObject("main").getString("temp"))) - 273.15)+32.0);

                       tempArray.add(TFarenheit + "");

                       time.add(formatted);

                       JSONArray weatherList = (JSONArray)jsonObject1.get("weather");

                       for(int j = 0; j < weatherList.length(); j++){
                           JSONObject jsonObject2 = (JSONObject)weatherList.get(j);
                           weatherArray.add(jsonObject2.getString("description"));
                           //System.out.println("weather is "+ jsonObject2.getString("description"));
                           icon.add(jsonObject2.getString("icon"));


                       }


                   }

                }


                for(int i = 0; i< tempArray.size();i++){

                    weather.add(new DailyWeather(tempArray.get(i).toString(), weatherArray.get(i).toString(), time.get(i).toString(), icon.get(i).toString()));
                }




            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("tag", e.toString());

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("tag", e.toString());

            } catch (JSONException e) {
                Log.d("tag", e.toString());

                e.printStackTrace();
            }


            a.add(currentWeather);
            a.add(currentTemp);
            a.add(currentIcon);
            return a;

        }
        @Override
        protected void onPostExecute(ArrayList<String> a) {
            super.onPostExecute(a);

            //quotes.add("");

            temp.setText(a.get(1) + "°F");
            weatherDes.setText(a.get(0));
            Picasso.get().load("http://openweathermap.org/img/w/" + a.get(2) + ".png").into(currentImage);

        }
    }
    public class DailyWeather{
        String temperature;
        String weatherDes;
        String time;
        String icon;
        public DailyWeather(String temp, String w,String t, String i){
            temperature = temp;
            weatherDes = w;
            time = t;
            icon =i;
        }
        public String getTemperature(){return temperature;}
        public String getWeatherDes(){return weatherDes;}
        public String getTime(){return time;}
        public String getIcon(){return icon;}
    }



    /*public class CurrentWeather extends MainActivity{
        String temperature = "";
        String weatherDes = "";
        Context context;
        public CurrentWeather(Context context){
            this.context=context;
        }

        public void UpdateTemp(String Temp) {
            TextView temp = (TextView) ((Activity) context).findViewById(R.id.id_temperature);

            temp.setText(Temp);

        }
        public void UpdateWeather(String Weather) {
            TextView weather = (TextView) ((Activity) context).findViewById(R.id.id_description);

            weather.setText(Weather);

        } */

        /*public void setTemperature(String t){temperature = t;}
        public String getTemperature(){return temperature;}
        public void setCurrentDes(String w){weatherDes = w;}
        public String getWeatherDes(){return weatherDes;}

    }*/


    public class CustomAdapter extends ArrayAdapter {

        Context context;
        int xmlResource;
        List<DailyWeather> list;

        public CustomAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);

            this.context = context;
            xmlResource = resource;
            list = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View adapterView = layoutInflater.inflate(xmlResource, null);
            TextView adapterDate = adapterView.findViewById(R.id.id_adapterDate);
            TextView adapterTemp = adapterView.findViewById(R.id.id_adapterTemp);
            TextView adapterDes = adapterView.findViewById(R.id.id_adapterDes);
            ImageView adapterImage = adapterView.findViewById(R.id.id_adapterImage);

            Log.d("tag", "writing forcast");
            adapterDate.setText(list.get(position).getTime().substring(0,5) + "");
            adapterTemp.setText(list.get(position).getTemperature() + "°F");
            adapterDes.setText(list.get(position).getWeatherDes() + "");

            Picasso.get().load("http://openweathermap.org/img/w/" + list.get(position).getIcon() + ".png").into(adapterImage);

            return adapterView;
        }
    }

}
