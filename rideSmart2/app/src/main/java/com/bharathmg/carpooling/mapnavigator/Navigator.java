package com.bharathmg.carpooling.mapnavigator;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bharathmg.carpooling.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

public class Navigator {
	@SuppressWarnings("unused")
	private Context context;
	private LatLng startPosition, endPosition;
    private String startLocation,endLocation;
	private String mode;
	private GoogleMap map;
	private Directions directions;
	private int pathColor = Color.BLUE;
	private int pathBorderColor = Color.BLACK;
	private int secondPath = Color.CYAN;
	private int thirdPath = Color.RED;
	private float pathWidth = 14;
	private OnPathSetListener listener;
	private boolean alternatives = false;
	private long arrivalTime;
	private String avoid;
	private ArrayList<Polyline> lines = new ArrayList<Polyline>();
	
	public Navigator(GoogleMap map, LatLng startLocation, LatLng endLocation){
		this.startPosition = startLocation;
		this.endPosition = endLocation;
		this.map = map;
	}

    public Navigator(GoogleMap map, String startLocation, String endLocation){
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.map = map;
    }


    public interface OnPathSetListener{
		void onPathSetListener(Directions directions);
	}

	public void setOnPathSetListener(OnPathSetListener listener){
		this.listener = listener;
	}

	/**
	 * Gets the starting location for the directions
	 *
	 */
	public LatLng getStartPoint(){
		return startPosition;
	}

	/**
	 * Gets the end location for the directions
	 *
	 */
	public LatLng getEndPoint(){
		return endPosition;
	}
	
	/**
	 * Get's driving directions from the starting location to the ending location
	 * 
	 * @param showDialog 
	 *  Set to true if you want to show a ProgressDialog while retrieving directions
	 *  @param findAlternatives
	 *  give alternative routes to the destination
	 *  
	 */
	public void findDirections(boolean findAlternatives){
		this.alternatives = findAlternatives;
        new FindDirectionsAsyncClass().execute();
		new PathCreator().execute();
	}

    public void findDirectionsfromPlaceName(boolean findAlternatives){
        this.alternatives=findAlternatives;
        new PathCreatorFromStringLocation().execute();
    }
	
	/**
	 * Sets the type of direction you want (driving,walking,biking or mass transit). This MUST be called before getting the directions
	 * If using "transit" mode you must provide an arrival time
	 * 
	 * @param mode
	 * The type of directions you want (driving,walking,biking or mass transit)
	 * @param arrivalTime
	 * If selected mode it "transit" you must provide and arrival time (milliseconds since January 1, 1970 UTC). If arrival time is not given
	 * the current time is given and may return unexpected results.
	 */
	public void setMode(int mode, long arrivalTime,int avoid){
		switch(mode){
		
		case 0:
			this.mode = "driving";
			break;
		case 1:
			this.mode = "transit";
			this.arrivalTime = arrivalTime;
			break;
		case 2:
			this.mode = "bicycling";
			break;
		case 3:
			this.mode = "walking";
			break;
		default:
			this.mode = "driving";
			break;
		}
		
		switch(avoid){
		case 0:
			this.avoid = "tolls";
			break;
		case 1:
			this.avoid = "highways";
			break;
		default:
			break;
		}
	}
	
	/**
	 * Get all direction information
	 * @return
	 */
	public Directions getDirections(){
		return directions;
	}
	
	/**
	 * Change the color of the path line, must be called before calling findDirections().
	 * @param firstPath
	 * Color of the first line, default color is blue.
	 * @param secondPath
	 * Color of the second line, default color is cyan
	 * @param thirdPath
	 * Color of the third line, default color is red
	 * 
	 */
	public void setPathColor(int firstPath,int secondPath, int thirdPath){
		pathColor = firstPath;
	}
	
	public void setPathBorderColor(int firstPath,int secondPath, int thirdPath){
		pathBorderColor = firstPath;
	}
	
	/**
	 * Change the width of the path line
	 * @param width
	 * Width of the line, default width is 3
	 */
	public void setPathLineWidth(float width){
		pathWidth = width;
	}
	
	private Polyline showPath(Route route,int color){
		return map.addPolyline(new PolylineOptions().addAll(route.getPath()).color(color).width(pathWidth));
	}
	
	private Polyline showBorderPath(Route route, int color){
		return map.addPolyline(new PolylineOptions().addAll(route.getPath()).color(color).width(pathWidth + 12));
	}
	
	public ArrayList<Polyline> getPathLines(){
		return lines;
	}

	public class PathCreator extends AsyncTask<Void,Void,Directions>{
        @Override
        public void onPreExecute(){
            if(mode == null){
                mode = "driving";
            }

            //doInBackground(params);
        }

		@Override
		protected Directions doInBackground(Void... params) {

//		        String url = "http://maps.googleapis.com/maps/api/directions/json?"
//		                + "origin=" + startPosition.latitude + "," + startPosition.longitude
//		                + "&destination=" + endPosition.latitude + "," + endPosition.longitude
//		                + "&sensor=false&units=metric&mode="+mode+"&alternatives="+String.valueOf(alternatives);

            String url = "http://maps.googleapis.com/maps/api/directions/json?"
                    + "origin=" + startLocation
                    + "&destination=" + endLocation
                    + "&sensor=false&units=metric&mode="+mode+"&alternatives="+String.valueOf(alternatives);

            if(mode.equals("transit")){
		        	if(arrivalTime > 0){
		        		url += url + "&arrival_time="+arrivalTime;
		        	}else{
		        		url += url + "&departure_time="+System.currentTimeMillis();
		        	}
		        }

		        if(avoid != null){
		        	url += url+"&avoid="+avoid;
		        }

		        try {
		            HttpClient httpClient = new DefaultHttpClient();
		            HttpContext localContext = new BasicHttpContext();
		            HttpPost httpPost = new HttpPost(url);
		            HttpResponse response = httpClient.execute(httpPost, localContext);

		            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){

		            	String s = EntityUtils.toString(response.getEntity());
			            onPostExecute(new Directions(s));
		            }


		            return null;
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
			return null;
		}

		@Override
		protected void onPostExecute(Directions directions){

			if(directions != null){
				Navigator.this.directions = directions;
				for(int i=0; i<directions.getRoutes().size(); i++){
					Route r = directions.getRoutes().get(i);
					if(i == 0){
						lines.add(showBorderPath(r,pathBorderColor));
						lines.add(showPath(r,pathColor));
					}else if(i == 1){
						lines.add(showBorderPath(r,pathBorderColor));
						lines.add(showPath(r,secondPath));
					}else if(i == 3){
						lines.add(showBorderPath(r,pathBorderColor));
						lines.add(showPath(r,thirdPath));
					}
				}

				if(listener != null){
					listener.onPathSetListener(directions);
				}


			}
		}

	}



    private class PathCreatorFromStringLocation extends AsyncTask<Void,Void,Directions>{



        public void onPreExecute(){
            super.onPreExecute();

        }
        @Override
        protected Directions doInBackground(Void... params) {
            if(mode == null){
                mode = "driving";
            }

            String url = "http://maps.googleapis.com/maps/api/directions/json?"
                    + "origin=" + startLocation
                    + "&destination=" + endLocation
                    + "&sensor=false&units=metric&mode="+mode+"&alternatives="+String.valueOf(alternatives);

            if(mode.equals("transit")){
                if(arrivalTime > 0){
                    url += url + "&arrival_time="+arrivalTime;
                }else{
                    url += url + "&departure_time="+System.currentTimeMillis();
                }
            }

            if(avoid != null){
                url += url+"&avoid="+avoid;
            }

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(url);
                HttpResponse response = httpClient.execute(httpPost, localContext);

                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){

                    String s = EntityUtils.toString(response.getEntity());
                    return new Directions(s);
                }


                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Directions directions){

            if(directions != null){
                Navigator.this.directions = directions;
                for(int i=0; i<directions.getRoutes().size(); i++){
                    Route r = directions.getRoutes().get(i);
                    if(i == 0){
                        lines.add(showBorderPath(r,pathBorderColor));
                        lines.add(showPath(r,pathColor));
                    }else if(i == 1){
                        lines.add(showBorderPath(r,pathBorderColor));
                        lines.add(showPath(r,secondPath));
                    }else if(i == 3){
                        lines.add(showBorderPath(r,pathBorderColor));
                        lines.add(showPath(r,thirdPath));
                    }
                }

                if(listener != null){
                    listener.onPathSetListener(directions);
                }

            }
        }

    }

public void PostExecute(Directions direction) {
    if (directions != null) {
        Navigator.this.directions = directions;
        for (int i = 0; i < directions.getRoutes().size(); i++) {
            Route r = directions.getRoutes().get(i);
            if (i == 0) {
                lines.add(showBorderPath(r, pathBorderColor));
                lines.add(showPath(r, pathColor));
            } else if (i == 1) {
                lines.add(showBorderPath(r, pathBorderColor));
                lines.add(showPath(r, secondPath));
            } else if (i == 3) {
                lines.add(showBorderPath(r, pathBorderColor));
                lines.add(showPath(r, thirdPath));
            }
        }

        if (listener != null) {
            listener.onPathSetListener(directions);
        }

    }
}
    public class FindDirectionsAsyncClass extends AsyncTask<Void,Void,Void> {
        @Override
        public Void doInBackground(Void... params) {
            try {
                ArrayList resultList = null;
                HttpURLConnection conn = null;
                StringBuilder jsonResults = new StringBuilder();
                try {
                    StringBuilder sb = new StringBuilder("http://maps.googleapis.com/maps/api/directions/json?"
                            + "origin=" + startLocation
                            + "&destination=" + endLocation
                            + "&sensor=false&units=metric&mode=" + mode + "&alternatives=" + String.valueOf(alternatives));

                    //sb.append("?key=" + API_KEY);
                    //sb.append("&components=country:in");
                   // sb.append("&input=" + URLEncoder.encode("", "utf8"));

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
                    Log.e("Error", "Error processing Places API URL", e);
                    //return resultList;
                } catch (IOException e) {
                    Log.e("Error", "Error connecting to Places API", e);
                    //return resultList;
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }

                try {
                    // Create a JSON object hierarchy from the results
                    JSONObject jsonObj = new JSONObject(jsonResults.toString());
                    JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");


                    String s = predsJsonArray.getJSONObject(0).toString();
                    if (s != null) {
                        PostExecute(new Directions(s));
                    }
                    //return new Directions(s);


                } catch (Exception e) {
                    Log.e("Error", "Error processing Places API URL", e);
                    //return null;
                }
            }


            //Your code goes here
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }


}
