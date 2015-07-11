package com.bharathmg.carpooling.showMapRouteForVehicleOwner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bharathmg.carpooling.PassengerModule.LocationPickerActivity;
import com.bharathmg.carpooling.R;
import com.bharathmg.carpooling.mapnavigator.GetAutoCompletePlaceName;


public class showVehicleOwnerMap extends Activity implements OnItemClickListener {

    private static final String LOG_TAG = "Google Places Autocomplete";
    ImageButton imgButton1;
    ImageButton imgButton2;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

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

        autoCompViewSource.setAdapter(new GetAutoCompletePlaceName(this, R.layout.list_item));
        final AutoCompleteTextView autoCompViewDest = (AutoCompleteTextView) findViewById(R.id.destination);

        autoCompViewDest.setAdapter(new GetAutoCompletePlaceName(this, R.layout.list_item));
        autoCompViewDest.setOnItemClickListener(this);

        imgButton1 = (ImageButton) findViewById(R.id.imageButtonSourceOwner);
        imgButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(showVehicleOwnerMap.this, LocationPickerActivity.class).putExtra("SourceInput", autoCompViewSource.getText().toString());
                intent.putExtra("ImageButton", "Source");
                startActivity(intent);
            }
        });

        imgButton2 = (ImageButton) findViewById(R.id.imageButtonDestOwner);
        imgButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(showVehicleOwnerMap.this, LocationPickerActivity.class).putExtra("DestinationInput", autoCompViewDest.getText().toString());
                intent.putExtra("ImageButton", "Destination");
                startActivity(intent);
            }
        });

        departdate = ((DatePicker)findViewById(R.id.departDatePickerOwner));
        departtime = (TimePicker) findViewById(R.id.departtimePickerOwner);
        departtext= departdate.getDayOfMonth() + "/" + departdate.getMonth() + ";" + departdate.getYear();
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

    public void submit(View v) {
        Intent intent = new Intent(this, showDirectionOnMap_Vehicle.class);
        final AutoCompleteTextView autoCompViewSource = (AutoCompleteTextView) findViewById(R.id.source);
        final AutoCompleteTextView autoCompViewDest = (AutoCompleteTextView) findViewById(R.id.destination);

        intent.putExtra("Source", autoCompViewSource.getText().toString());
        intent.putExtra("Destination", autoCompViewDest.getText().toString());


        startActivity(intent);

    }




}
