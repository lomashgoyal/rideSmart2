package com.bharathmg.carpooling.PassengerModule;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import com.bharathmg.carpooling.PostRidetwo;
import com.bharathmg.carpooling.R;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONObject;

import java.io.InputStream;

public class passengerMain extends FragmentActivity  {
    AutoCompleteTextView passengerSource;
    AutoCompleteTextView passengerDestination;
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
    ImageButton imgButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_main);
       // processMap();
        imgButton =(ImageButton)findViewById(R.id.imageButton1);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(getApplicationContext(),"You download is resumed",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(passengerMain.this, LocationPickerActivity.class);
                startActivity(intent);
            }
        });
    }




    public void closingKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(passengerDestination.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(passengerSource.getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void scheduleNowStageTwo(View v) {
        Intent i = new Intent(this, PostRidetwo.class);
        i.putExtra("from", passengerSource.getText().toString());
        i.putExtra("to", passengerDestination.getText().toString());
        startActivity(i);
    }
}