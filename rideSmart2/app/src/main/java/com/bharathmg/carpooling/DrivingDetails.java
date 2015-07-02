package com.bharathmg.carpooling;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.os.Bundle;

import com.bharathmg.carpooling.R;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SignUpCallback;


import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

public class DrivingDetails extends Activity {
    protected EditText mEditFirstName;
    protected Spinner mEditCar;
    protected EditText mEditEmailAddress;
    protected EditText mEditPassword;
    protected Spinner mEditAge;
    protected EditText mNotes;
    protected EditText mCarNumber;
    ProgressBar pro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving_details);
        pro=(ProgressBar)findViewById(R.id.progressBar1);
        mEditFirstName = (EditText)findViewById(R.id.editText1);
        mEditPassword = (EditText)findViewById(R.id.EditText02);
        mEditEmailAddress = (EditText)findViewById(R.id.EditText03);

        //Populating Age Category DropDown
        mEditAge = (Spinner)findViewById(R.id.age_spinner);
        String[] ageCategory = getResources().getStringArray(R.array.ageCategory);
        ArrayAdapter<String> ageDataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, ageCategory);
        ageDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEditAge.setAdapter(ageDataAdapter);
        mEditAge.setSelection(getCountForAge());
        //mEditAge.setPrompt(getResources().getString(R.string.age_CategoryPrompt));


        //Populating Car Model DropDown
        mEditCar =(Spinner)findViewById(R.id.carModel_spinner);
        String[] carCategory = getResources().getStringArray(R.array.carCategory);
        ArrayAdapter<String> carDataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, carCategory);
        carDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEditCar.setAdapter(carDataAdapter);
        mEditCar.setSelection(getCountForCar());


        mNotes = (EditText)findViewById(R.id.EditText05);

        mCarNumber=(EditText)findViewById(R.id.EditText06);
        Parse.initialize(this, "14na9mFYIWfa5jI8lBetFNtvYx0V8ZZiP9wZS3p4", "WXU6n8Ny9ThvmAtscJ14gf5VgJZuT7MM9WkDaC09");
        ParseUser.logOut();
    }


    public int getCountForAge(){
        int count = mEditAge.getCount();
        return count > 0 ? count - 1 : count;
    }

    public int getCountForCar(){
        int count = mEditCar.getCount();
        return count > 0 ? count - 1 : count;
    }


//    public void addListenerOnSpinnerItemSelection() {
//        mEditAge = (Spinner) findViewById(R.id.age_spinner);
//        mEditAge.setOnItemSelectedListener(new CustomOnItemSelectedListener());
//    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driving_details, menu);
        return true;
    }


    public void join(View v){

        pro.setVisibility(ProgressBar.VISIBLE);
       // Toast.makeText(getApplicationContext(),mEditAge.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
        String test=mEditAge.getSelectedItem().toString();
        ParseUser user = new ParseUser();
        user.setUsername(mEditFirstName.getText().toString());
        user.setPassword(mEditPassword.getText().toString());
        user.setEmail(mEditEmailAddress.getText().toString());


        // other fields can be set just like with ParseObject
        user.put("age", mEditAge.getSelectedItem().toString());
        user.put("car", mEditCar.getSelectedItem().toString());
        user.put("notes", mNotes.getText().toString());
        user.put("carNumber", mCarNumber.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                pro.setVisibility(ProgressBar.INVISIBLE);
                if (e == null) {
                    // Hooray! Let them use the app now.
                    CharSequence text = "Welcome to CarPooling. You have successfully signed up!";

                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                    Intent i=new Intent(getApplicationContext(),Driveoptions.class);
                    startActivity(i);
                } else {
                    Log.w("ERROR", "Sign up failed parse");
                    Toast.makeText(getApplicationContext(), "registration Failed because of the reason"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void terms(View v){
        Intent i=new Intent(this,Terms.class);
        startActivity(i);
    }
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000); /* milliseconds */
            conn.setConnectTimeout(15000);/* milliseconds */
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("network", "The response is: " + response);
            Toast.makeText(getApplicationContext(), response + "", Toast.LENGTH_LONG).show();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            Toast.makeText(getApplicationContext(), contentAsString + "", Toast.LENGTH_LONG).show();
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}
