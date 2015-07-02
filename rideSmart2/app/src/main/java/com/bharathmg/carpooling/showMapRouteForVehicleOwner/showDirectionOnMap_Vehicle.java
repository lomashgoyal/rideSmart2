package com.bharathmg.carpooling.showMapRouteForVehicleOwner;
import com.bharathmg.carpooling.Driveoptions;
import com.bharathmg.carpooling.mapnavigator.Navigator;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bharathmg.carpooling.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class showDirectionOnMap_Vehicle  extends FragmentActivity {

    EditText etLng;
    EditText etLat;
    GoogleMap googleMap;
    Double sourceLatitude;
    Double destinationLatitude;
    Double sourceLongitude;
    Double destinationLongitude;
    EditText cost,seats,notes;
    private String username;
    ProgressBar pro;
    ArrayList<Marker> markers=null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_direction_on_map__vehicle);

        markers=new ArrayList<Marker>();
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.show_owner_position);

        // Getting GoogleMap object from the fragment
        googleMap = fm.getMap();


        // Enabling MyLocation Layer of Google Map
        googleMap.setMyLocationEnabled(true);
        try {
            googleMap.setMyLocationEnabled(true);
           // googleMap.setTrafficEnabled(true);
           // googleMap.setIndoorEnabled(true);


           /*------ Below Code will show google maps directly
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + getIntent().getStringExtra("SourceAddress").toString() + "&daddr=" + getIntent().getStringExtra("DestinationAddress").toString()));
            startActivity(intent);

*/
            String sourceAddress=getIntent().getStringExtra("SourceAddress").toString();
            String destAddress=getIntent().getStringExtra("DestinationAddress").toString();
            sourceAddress=sourceAddress.replaceAll(" ","+");
            destAddress=destAddress.replaceAll(" ","+");

//            sourceLatitude = (getLocationFromAddress(sourceAddress,getApplicationContext()).getLatitudeE6()) / 1E6;
//            Toast.makeText(this,sourceLatitude.toString(),Toast.LENGTH_LONG).show();
//            sourceLongitude = ((getLocationFromAddress(sourceAddress,getApplicationContext()).getLongitudeE6()) / 1E6);
//            Toast.makeText(this,sourceLongitude.toString(),Toast.LENGTH_LONG).show();
//
//            final LatLng sourcePoint = new LatLng(sourceLatitude, sourceLongitude);
//            drawMarker(sourcePoint);
//            destinationLatitude = (getLocationFromAddress(destAddress,getApplicationContext()).getLatitudeE6()) / 1E6;
//            destinationLongitude = (getLocationFromAddress(destAddress,getApplicationContext()).getLongitudeE6()) / 1E6;
//
//            final LatLng destinationPoint = new LatLng(destinationLatitude, destinationLongitude);
//
//
//            drawMarker(destinationPoint);

            Navigator nav = new Navigator(googleMap, sourceAddress, destAddress);
            pro=(ProgressBar)findViewById(R.id.pro);
            pro.setVisibility(ProgressBar.VISIBLE);
            nav.findDirections(true);
            //nav.getDirections().
            pro=(ProgressBar)findViewById(R.id.pro);
            pro.setVisibility(ProgressBar.INVISIBLE);
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    //googleMap.setMapType(441);
                    //googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 30));


                                   }

            });
        }
            catch(Exception e){
            Toast.makeText(this,e.getMessage().toString(),Toast.LENGTH_LONG).show();
        }
    }
    public void displayProgressBar(){
        pro=(ProgressBar)findViewById(R.id.pro);
        pro.setVisibility(ProgressBar.VISIBLE);
    }
    private void showAllMarkers() {
        try {


            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }
            final LatLngBounds bounds = builder.build();
            int padding = 0; // offset from edges of the map in pixels
           // CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 1);
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 30));
                }
            });

        }
        catch(Exception e){
            Log.d("asa","sasa");
        }
    }
    private void drawMarker(LatLng point) {

        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.show_owner_position);

        // Getting reference to google map
        GoogleMap googleMap = fm.getMap();

          if(googleMap!=null) {
            MarkerOptions options = new MarkerOptions();
            options.position(point);

            options.title("Position");
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            markers.add(googleMap.addMarker(options));


        }
        assert googleMap != null;
        googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(point.latitude,point.longitude) , 14.0f));
        //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(point.latitude,point.longitude), 15.5f), 4000, null);




            }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

        public GeoPoint getLocationFromAddress(final String strAddress,final Context context) {
            final GeoPoint[] p1 = {null};
             Thread thread = new Thread() {
                @Override
                public void run() {
                    Geocoder coder = new Geocoder(context, Locale.getDefault());
                    List<Address> address = null;

                    try {
                        address = coder.getFromLocationName(strAddress, 3);

                        //this.stopLockTask();
                        for(int i=0;i<10;i++) {

                                Toast.makeText(context,address.size(), Toast.LENGTH_LONG);
                                address = coder.getFromLocationName(strAddress, 1);
                            }



                        Address location = address.get(0);
                        location.getLatitude();
                        location.getLongitude();

                        p1[0] = new GeoPoint((int) (location.getLatitude() * 1E6),
                                (int) (location.getLongitude() * 1E6));


                    } catch (Exception e) {
                        Log.d("Exception occured", "WTF");
                    } finally {
                    }
                }

            };
            thread.start();
            return p1[0];
            }

    public void post_ride_owner(View v){
        pro=(ProgressBar)findViewById(R.id.pro);
        pro.setVisibility(ProgressBar.VISIBLE);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            username = currentUser.getUsername();
        }

        String seatsStr=getIntent().getStringExtra("OwnerSeats");
        String from=getIntent().getStringExtra("SourceAddress");
        String to=getIntent().getStringExtra("DestinationAddress");
        //String meeting = getIntent().getStringExtra("meeting");
        String depart=getIntent().getStringExtra("departDate");
        //String returnt=getIntent().getStringExtra("return");
        String depart_time =getIntent().getStringExtra("departTime");
        ParseObject details=new ParseObject("Post");
        details.put("owner", currentUser.getObjectId());
        details.put("from", from);
        details.put("to", to);
        //details.put("meeting", meeting);
        details.put("depart", depart);
        //details.put("return", returnt);

        details.put("depart_time", depart_time);
        //details.put("return_time", return_time);
        details.put("totalseats",Integer.parseInt(seatsStr));
        List<String> users=new ArrayList<String>();
        details.put("users", users);
        details.saveInBackground(new SaveCallback(){

            @SuppressLint("ShowToast")
            @Override
            public void done(ParseException e) {
                // TODO Auto-generated method stub
                if (e == null) {
                    pro.setVisibility(ProgressBar.INVISIBLE);
                    Log.d("Posted", "posted");
                    Toast.makeText(getApplicationContext(), "Posted successfully!", Toast.LENGTH_LONG).show();
                    Intent i=new Intent(getApplicationContext(),Driveoptions.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } else {
                    // something went wrong

                    Log.d("NOT Posted", e.getMessage());
                }

            } });
    }





}

