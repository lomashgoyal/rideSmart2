package com.bharathmg.carpooling.showMapRouteForVehicleOwner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bharathmg.carpooling.PassengerModule.LocationPickerActivity;
import com.bharathmg.carpooling.R;
import com.google.android.gms.maps.model.LatLng;


public class showVehicleOwnerMap extends Activity implements OnItemClickListener {

    private static final String LOG_TAG = "Google Places Autocomplete";
    ImageButton imgButton1;
    ImageButton imgButton2;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    Double sourceLatitude;
    Double destinationLatitude;
    Double sourceLongitude;
    Double destinationLongitude;
    TimePicker departtime;
    DatePicker departdate;
    String departtext=null;
    String departtimetext=null;
    EditText noofSeats=null;


    private static final String API_KEY = "AIzaSyBFbvGI7WdpemPeuuGgTN36aTAr3CIhIuQ";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_vehicle_owner_map);
        final AutoCompleteTextView autoCompViewSource = (AutoCompleteTextView) findViewById(R.id.source);

        autoCompViewSource.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));


        final AutoCompleteTextView autoCompViewDest = (AutoCompleteTextView) findViewById(R.id.destination);

        autoCompViewDest.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        autoCompViewDest.setOnItemClickListener(this);

        imgButton1 = (ImageButton) findViewById(R.id.imageButtonSourceOwner);
        imgButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(getApplicationContext(),"You download is resumed",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(showVehicleOwnerMap.this, LocationPickerActivity.class).putExtra("SourceInput", autoCompViewSource.getText().toString());
                intent.putExtra("ImageButton", "Source");
                startActivity(intent);
            }
        });

        imgButton2 = (ImageButton) findViewById(R.id.imageButtonDestOwner);
        imgButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(getApplicationContext(),"You download is resumed",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(showVehicleOwnerMap.this, LocationPickerActivity.class).putExtra("DestinationInput", autoCompViewDest.getText().toString());
                intent.putExtra("ImageButton", "Destination");
                startActivity(intent);
            }
        });

        departdate = ((DatePicker)findViewById(R.id.departDatePickerOwner));
        //returndate = (EditText) findViewById(R.id.return_date);
        departtime = (TimePicker) findViewById(R.id.departtimePickerOwner);
        departtext= departdate.getDayOfMonth() + "/" + departdate.getMonth() + ";" + departdate.getYear();
        //returntext = returndate.getText().toString();
        departtimetext = departtime.getCurrentHour() + ":" + departtime.getCurrentMinute();
        noofSeats=(EditText)findViewById(R.id.seatsOwner);


        Button showMapforOwner = (Button) findViewById(R.id.showMapforOwner);
        showMapforOwner.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(showVehicleOwnerMap.this, showDirectionOnMap_Vehicle.class);
                final AutoCompleteTextView autoCompViewSource = (AutoCompleteTextView) findViewById(R.id.source);
                final AutoCompleteTextView autoCompViewDest = (AutoCompleteTextView) findViewById(R.id.destination);
                String sourceTest = "source";
                intent.putExtra("SourceAddress",autoCompViewSource.getText().toString());
                intent.putExtra("DestinationAddress",autoCompViewDest.getText().toString());
                intent.putExtra("departDate",departtext);
                intent.putExtra("departTime",departtimetext);
                intent.putExtra("OwnerSeats", noofSeats.getText().toString());
                startActivity(intent);

            }
        });

    }

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

    }

    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:in");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return (String) resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    public void submit(View v) {
        Intent intent = new Intent(this, showDirectionOnMap_Vehicle.class);
        final AutoCompleteTextView autoCompViewSource = (AutoCompleteTextView) findViewById(R.id.source);
        final AutoCompleteTextView autoCompViewDest = (AutoCompleteTextView) findViewById(R.id.destination);

        intent.putExtra("Source", autoCompViewSource.getText().toString());
        intent.putExtra("Destination", autoCompViewDest.getText().toString());


        startActivity(intent);

    }

    public class FetchCoordinatesFromAddress extends AsyncTask<String, Void,LatLng> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(LatLng result) {
            if(sourceLatitude==null) {
                sourceLatitude = result.latitude;
                sourceLongitude = result.longitude;
            }
            else{
                destinationLatitude=result.latitude;
                destinationLongitude=result.longitude;
            }
        }

        @Override
        protected LatLng doInBackground(String... params) {

            String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
                    URLEncoder.encode(params[0]) + "&sensor=" + URLEncoder.encode("false");
            HttpGet httpGet = new HttpGet(uri);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            StringBuilder stringBuilder = new StringBuilder();

            try {
                response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                int b;
                while ((b = stream.read()) != -1) {
                    stringBuilder.append((char) b);
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Double lat=null;
            Double lng=null;
            LatLng pointToreturn = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = new JSONObject(stringBuilder.toString());

                lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");

//                if(params[1].contains("source")){
//                    sourceLatitude=lat;
//                    sourceLongitude=lng;
//                }
//                else{
//                    destinationLatitude=lat;
//                    destinationLatitude=lng;
//
//                }
                pointToreturn = new LatLng(lat, lng);
//            Log.d("latitude", lat);
//            Log.d("longitude", lng);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return pointToreturn;

        }

    }





}
