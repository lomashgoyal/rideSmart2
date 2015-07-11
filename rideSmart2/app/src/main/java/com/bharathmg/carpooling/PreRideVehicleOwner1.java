package com.bharathmg.carpooling;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;

        import java.net.HttpURLConnection;
        import java.net.URL;

        import java.util.HashMap;
        import java.util.List;
        import java.util.Locale;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.Intent;
        import android.graphics.Color;
        import android.location.Address;
        import android.location.Criteria;
        import android.location.Geocoder;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v4.app.FragmentActivity;
        import android.text.Editable;
        import android.text.TextWatcher;
        import android.util.Log;
        import android.view.Menu;
        import android.view.View;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.AdapterView;
        import android.widget.AdapterView.OnItemClickListener;
        import android.widget.AutoCompleteTextView;
        import android.widget.ListView;
        import android.widget.SimpleAdapter;
        import android.widget.TextView;

        import com.bharathmg.carpooling.mapnavigator.GetRouteTaskUtility;
        import com.bharathmg.carpooling.mapnavigator.PlaceDetailsJSONParser;
        import com.bharathmg.carpooling.mapnavigator.PlaceJSONParser;
        import com.google.android.gms.maps.CameraUpdate;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.BitmapDescriptorFactory;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;
        import com.google.android.gms.maps.model.Polyline;
        import com.google.android.gms.maps.model.PolylineOptions;


public class PreRideVehicleOwner1 extends FragmentActivity implements LocationListener {

    AutoCompleteTextView vehicleSource;
    AutoCompleteTextView vehicleDestination;

    DownloadTask placesDownloadTask;
    DownloadTask placeDetailsDownloadTask;
    ParserTask placesParserTask;
    ParserTask placeDetailsParserTask;
    double sourceLat;
    double sourceLong;
    double destLat;
    double destLong;
    GoogleMap googleMap;

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    String directionsUrl=null;
    String directionsJson=null;

    final int PLACES = 0;
    final int PLACES_DETAILS = 1;
    boolean isDestination = false;

    private LocationManager locationManager;
    private String provider;

    static int  DESCRIPTION_STRING_LENGTH=12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preride_vehicle_owner1);
        processMap();

    }
    public void processMap(){
        //Setting the Current Location in From TextBox
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        vehicleSource = (AutoCompleteTextView) findViewById(R.id.vehicle_source);
        vehicleDestination = (AutoCompleteTextView) findViewById(R.id.vehicle_destination);
        // Getting GoogleMap object from the fragment
        googleMap = fm.getMap();

        // Enabling MyLocation Layer of Google Map
        googleMap.setMyLocationEnabled(true);
        vehicleSource.setThreshold(1);
        vehicleDestination.setThreshold(1);

        // Adding textchange listener
        vehicleSource.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Creating a DownloadTask to download Google Places matching "s"
                placesDownloadTask = new DownloadTask(PLACES);
                isDestination = false;
                // Getting url to the Google Places Autocomplete api
                String url = getAutoCompleteUrl(s.toString());

                // Start downloading Google Places
                // This causes to execute doInBackground() of DownloadTask class
                placesDownloadTask.execute(url);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        // Getting a reference to the AutoCompleteTextView for Destination
        vehicleDestination.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Creating a DownloadTask to download Google Places matching "s"
                placesDownloadTask = new DownloadTask(PLACES);
                //Setting isDestination=true so as to set teh marker color
                isDestination = true;

                // Getting url to the Google Places Autocomplete api
                String url = getAutoCompleteUrl(s.toString());

                // Start downloading Google Places
                // This causes to execute doInBackground() of DownloadTask class
                placesDownloadTask.execute(url);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });


        // Setting an item click listener for the AutoCompleteTextView for Source dropdown list
        vehicleSource.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long id) {

                ListView lv = (ListView) arg0;
                SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();

                HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);

                // Creating a DownloadTask to download Places details of the selected place
                placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);

                // Getting url to the Google Places details api
                String url = getPlaceDetailsUrl(hm.get("reference"));

                // Start downloading Google Place Details
                // This causes to execute doInBackground() of DownloadTask class
                placeDetailsDownloadTask.execute(url);

            }
        });
        // Setting an item click listener for the AutoCompleteTextView for Destination dropdown list
        vehicleDestination.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long id) {

                ListView lv = (ListView) arg0;
                SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();

                HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);

                // Creating a DownloadTask to download Places details of the selected place
                placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);

                // Getting url to the Google Places details api
                String url = getPlaceDetailsUrl(hm.get("reference"));

                // Start downloading Google Place Details
                // This causes to execute doInBackground() of DownloadTask class
                placeDetailsDownloadTask.execute(url);

            }
        });

        findCurrentaddress();
    }

    public void findCurrentaddress(){

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
        if(location!=null) {
            sourceLong=location.getLongitude();
            sourceLat=location.getLatitude();
            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
            setMarkerAndZooomMap(point,BitmapDescriptorFactory.HUE_RED);

        }


        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            vehicleSource.setHint("Current Location not available");

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1,this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        //You had this as int. It is advised to have Lat/Loing as double.
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geoCoder.getFromLocation(lat, lng, 1);
            int maxLines = address.get(0).getMaxAddressLineIndex();
            for (int i=0; i<maxLines; i++) {
                String addressStr = address.get(0).getAddressLine(i);
                builder.append(addressStr);
                builder.append(" ");
            }

            String finalAddress = builder.toString(); //This is the complete address.

            //This will display the final address.
            vehicleSource.setText(finalAddress);

            //Setting The Marker
            if(location!=null) {
                LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                setMarkerAndZooomMap(point,BitmapDescriptorFactory.HUE_RED);
            }


        } catch (IOException e) {}
        catch (NullPointerException e) {}
    }



    //utility Function to set the Marker
    public void setMarkerAndZooomMap(LatLng point,float color){
        CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(point);
        CameraUpdate cameraZoom = CameraUpdateFactory.zoomBy(6);

        // Showing the user input location in the Google Map
        googleMap.moveCamera(cameraPosition);
        googleMap.animateCamera(cameraZoom);
        if(googleMap!=null) {
            MarkerOptions options = new MarkerOptions();
            options.position(point);
            options.title("Position");
            options.icon(BitmapDescriptorFactory.defaultMarker(color));
            googleMap.addMarker(options);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private String getAutoCompleteUrl(String place) {

        // Obtain browser key from https://code.google.com/apis/console
        //https://maps.googleapis.com/maps/api/place/autocomplete/xml?input=Amoeba&types=establishment&location=37.76999,-122.44696&radius=500&key=AIzaSyBFbvGI7WdpemPeuuGgTN36aTAr3CIhIuQ
        //https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters

        String key = "key=AIzaSyBFbvGI7WdpemPeuuGgTN36aTAr3CIhIuQ";

        // place to be be searched
        String input = "input=" + place;

        // place type to be searched
        String types = "types=geocode";

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = input + "&" + types + "&" + sensor + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;

        return url;
    }

    private String getPlaceDetailsUrl(String ref) {

        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=AIzaSyBFbvGI7WdpemPeuuGgTN36aTAr3CIhIuQ";

        // reference of place
        String reference = "reference=" + ref;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = reference + "&" + sensor + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/details/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        private int downloadType = 0;

        // Constructor
        public DownloadTask(int type) {
            this.downloadType = type;
        }

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            switch (downloadType) {
                case PLACES:
                    // Creating ParserTask for parsing Google Places
                    placesParserTask = new ParserTask(PLACES);

                    // Start parsing google places json data
                    // This causes to execute doInBackground() of ParserTask class
                    placesParserTask.execute(result);

                    break;

                case PLACES_DETAILS:
                    // Creating ParserTask for parsing Google Places
                    placeDetailsParserTask = new ParserTask(PLACES_DETAILS);

                    // Starting Parsing the JSON string
                    // This causes to execute doInBackground() of ParserTask class
                    placeDetailsParserTask.execute(result);
            }
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        int parserType = 0;

        public ParserTask(int type) {
            this.parserType = type;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<HashMap<String, String>> list = null;

            try {
                jObject = new JSONObject(jsonData[0]);

                switch (parserType) {
                    case PLACES:
                        PlaceJSONParser placeJsonParser = new PlaceJSONParser();
                        // Getting the parsed data as a List construct
                        list = placeJsonParser.parse(jObject);
                        break;
                    case PLACES_DETAILS:
                       // String test=jObject.getString("long_name");

                        PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
                        // Getting the parsed data as a List construct
                        list = placeDetailsJsonParser.parse(jObject);
                }

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            switch (parserType) {
                case PLACES:
                    String[] from = new String[]{"description"};
                    int[] to = new int[]{android.R.id.text1};
                    int[] toDes = new int[]{android.R.id.text2};

                    // Creating a SimpleAdapter for the AutoCompleteTextView
                    SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);
                    SimpleAdapter adapterDest = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);

                    // Setting the adapter

                    vehicleSource.setAdapter(adapter);
                    vehicleDestination.setAdapter(adapterDest);
                   //Log.d("Destination adapter",vehicleDestination.getText().toString());
                   break;
                case PLACES_DETAILS:
                    HashMap<String, String> hm = result.get(0);


                    // Getting latitude from the parsed data
                    double latitude = Double.parseDouble(hm.get("lat"));

                    // Getting longitude from the parsed data
                    double longitude = Double.parseDouble(hm.get("lng"));

                    LatLng point = new LatLng(latitude, longitude);
                    if (isDestination) {
                    String destinationStringTobeparsed=vehicleDestination.getText().toString();

                        if(destinationStringTobeparsed!=null) {
                            int destinationIndex = destinationStringTobeparsed.lastIndexOf("description");
                            String destinationString = destinationStringTobeparsed.substring(destinationIndex + DESCRIPTION_STRING_LENGTH, destinationStringTobeparsed.length() - 1);
                            vehicleDestination.setText(destinationString);
                        }
                        destLat = latitude;
                        destLong = longitude;
                        setMarkerAndZooomMap(point,BitmapDescriptorFactory.HUE_GREEN);

                    } else {
                        String sourceStringTobeparsed=vehicleSource.getText().toString();

                        if(sourceStringTobeparsed!=null&&sourceStringTobeparsed.contains("description")) {
                            int destinationIndex = sourceStringTobeparsed.lastIndexOf("description");
                            String sourceString = sourceStringTobeparsed.substring(destinationIndex + DESCRIPTION_STRING_LENGTH, sourceStringTobeparsed.length() - 1);
                            vehicleSource.setText(sourceString);
                        }
                        sourceLat = latitude;
                        sourceLong = longitude;
                        setMarkerAndZooomMap(point,BitmapDescriptorFactory.HUE_RED);
                    }

                    // Adding the marker in the Google Map
                    String source = ((TextView) findViewById(R.id.vehicle_source)).getText().toString();
                    if (isDestination && !source.isEmpty()) {
                        GetRouteTaskUtility ob1=new GetRouteTaskUtility();

                        new AsyncCaller().execute();

                    }

                    break;
            }
        }

        private class AsyncCaller extends AsyncTask<String, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(PreRideVehicleOwner1.this);
            GetRouteTaskUtility ob1 = new GetRouteTaskUtility();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                //this method will be running on UI thread
                pdLoading.setMessage("\tLoading...");
                pdLoading.show();
                directionsUrl = ob1.makeURL(sourceLat, sourceLong, destLat, destLong);
            }

            @Override
            protected String doInBackground(String... params) {

                //this method will be running on background thread so don't update UI frome here
                //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
                if (directionsUrl != null) {
                    directionsJson = ob1.getJSONFromUrl(directionsUrl);
                }

                return directionsJson;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                //GetRouteTaskUtility ob3=new GetRouteTaskUtility();
                if (directionsJson != null) {

                    try {
                        //Tranform the string into a json object
                        JSONObject json = new JSONObject(result);
                        JSONArray routeArray = json.getJSONArray("routes");
                        JSONObject routes = routeArray.getJSONObject(0);
                        JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                        String encodedString = overviewPolylines.getString("points");
                        List<LatLng> list = ob1.decodePoly(encodedString);

                        for (int z = 0; z < list.size() - 1; z++) {
                            LatLng src = list.get(z);
                            LatLng dest = list.get(z + 1);
                            // Getting reference to the SupportMapFragment of the activity_main.xml
                            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

                            // Getting GoogleMap from SupportMapFragment
                            googleMap = fm.getMap();

                            Polyline line = googleMap.addPolyline(new PolylineOptions()
                                    .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
                                    .width(10)
                                    .color(Color.BLUE).geodesic(true));


                        }


                    } catch (JSONException e) {

                    }
                    finally {
                        closingKeyBoard();
                    }

                }
                //this method will be running on UI thread
                pdLoading.dismiss();
                // return "SUCCESS";
            }
        }
    }


    public void closingKeyBoard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(vehicleDestination.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(vehicleSource.getWindowToken(), 0);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void scheduleNowStageTwo(View v){
        Intent i = new Intent(this,PostRidetwo.class);
        i.putExtra("from", vehicleSource.getText().toString());
        i.putExtra("to", vehicleDestination.getText().toString());
        startActivity(i);
    }






}


