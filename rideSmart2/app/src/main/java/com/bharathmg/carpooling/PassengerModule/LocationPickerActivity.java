package com.bharathmg.carpooling.PassengerModule;

import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;

import com.bharathmg.carpooling.R;

public class LocationPickerActivity extends Activity {

    private WebView locationPickerView;
    private EditText searchText;
    private Button searchButton;

    private double latitude = 12.931833900000000000;
    private double longitude = 77.682244200000010000;
    private Integer zoom = 5;
    private String locationName;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);
        searchText = (EditText) findViewById(R.id.searchText);
        if (savedInstanceState!=null) {
            latitude = savedInstanceState.getDouble("latitude");
            longitude = savedInstanceState.getDouble("longitude");
            zoom = savedInstanceState.getInt("zoom");
            locationName = savedInstanceState.getString("locationName");
        }
        String clickImageSource=getIntent().getStringExtra("ImageButton");

        if(clickImageSource!=null&&clickImageSource.contains("Source")){
            String searchtextBoxInput=getIntent().getStringExtra("SourceInput");
        if(searchtextBoxInput!=null){
            searchText.setText(searchtextBoxInput, TextView.BufferType.EDITABLE);
        }}
        else{
            if(clickImageSource!=null&&clickImageSource.contains("Destination")){
                String searchtextBoxInput=getIntent().getStringExtra("DestinationInput");
                if(searchtextBoxInput!=null){
                    searchText.setText(searchtextBoxInput);
                }
            }
        }

        // LOCATION PICKER WEBVIEW SETUP
        locationPickerView = (WebView) findViewById(R.id.locationPickerView);
        locationPickerView.setScrollContainer(false);
        locationPickerView.getSettings().setDomStorageEnabled(true);
        locationPickerView.getSettings().setJavaScriptEnabled(true);
        locationPickerView.addJavascriptInterface(new LocationPickerJSInterface(), "AndroidFunction");

        locationPickerView.loadUrl("file:///android_asset/locationPickerPage/index.html");

        locationPickerView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    locationPickerView.loadUrl("javascript:activityInitialize(" + latitude + "," + longitude + "," + zoom + ")");
                }
            }
        });
        // ^^^

        // EVENT HANDLER FOR PERFORMING SEARCH IN WEBVIEW
        searchText = (EditText) findViewById(R.id.searchText);
        searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                locationPickerView.loadUrl("javascript:if (typeof activityPerformSearch == 'function') {activityPerformSearch(\"" + searchText.getText().toString() + "\")}");
            }
        });
        // ^^^

        // EVENT HANDLER FOR ZOOM IN WEBVIEW
        Button zoomIncreaseButton = (Button) findViewById(R.id.zoomIncreaseButton);
        zoomIncreaseButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                locationPickerView.loadUrl("javascript:activityPerformZoom(1)");
            }
        });


        // ^^^

        // EVENT HANDLER FOR SAMPLE QUERY BUTTON
        Button sampleQueryButton = (Button) findViewById(R.id.queryButton);
        sampleQueryButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                AlertDialog alertDialog = new AlertDialog.Builder(LocationPickerActivity.this).create();
                alertDialog.setTitle("Data");
                alertDialog.setMessage("lat=" + latitude + ", lng=" + longitude + ", zoom=" + zoom + "\nloc=" + locationName);
                alertDialog.show();
            }
        });

        Button goBack = (Button) findViewById(R.id.zoomIncreaseButton);
        goBack.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View arg0) {
               ;
            }
        });


    }


    public class LocationPickerJSInterface {
        @JavascriptInterface
        public void getValues(String latitude, String longitude, String zoom, String locationName){
            LocationPickerActivity.this.latitude = Float.parseFloat(latitude);
            LocationPickerActivity.this.longitude = Float.parseFloat(longitude);
            LocationPickerActivity.this.zoom = Integer.parseInt(zoom);
            LocationPickerActivity.this.locationName = locationName;
        }

        // to ease debugging
        public void showToast(String toast){
            Toast.makeText(LocationPickerActivity.this, toast, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putDouble("latitude", latitude);
        outState.putDouble("longitude", longitude);
        outState.putInt("zoom", zoom);
        outState.putString("locationName", locationName);
    }

    public void GoBack(){
        this.finish();
    }

}