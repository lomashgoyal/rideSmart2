package com.bharathmg.carpooling.mapnavigator;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * Created by lomashg on 11/07/2015 AD.
 */
public class FetchCoordinateFromLocation {
    Double latitude;
    Double longitude;
    LatLng coordinatesResult=null;


    public FetchCoordinateFromLocation(String source, String destination) {
        new getCoordinates().execute(source, destination);
    }
    public LatLng getCoordinatesResult(){
        if(coordinatesResult!=null){
            return coordinatesResult;
        }
        else{
            return null;
        }
    }

    public class getCoordinates extends AsyncTask<String, Void, LatLng> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(LatLng result) {
            coordinatesResult=result;
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
            Double lat = null;
            Double lng = null;
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



