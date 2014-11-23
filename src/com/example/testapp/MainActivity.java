package com.example.testapp;


import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;



@SuppressLint("NewApi")
public class MainActivity extends  FragmentActivity implements
LocationListener
{
    private GoogleMap mMap;
    private static HashMap<Marker, CurrentLocationOverlay> mMarkersHashMap;
    
    private LatLng My_LOCATION;
    protected LocationManager locationManager;
    Context mContect ;
    Timer timer;
    MapView mapview;

    Location location;
 

	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContect = this;
        // Initialize the HashMap for Markers and CurrentLocationOverlay object
        locationManager = (LocationManager) mContect
        		.getSystemService(LOCATION_SERVICE);


        	
        trackPosition(false);
        
        timer = new Timer();

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        new GetContent().execute();
                    }
                });

            }
        }, 30000, 30000);
        
    }
            
    private void trackPosition(boolean reCall)
    {
    	
    	
		
    	setUpMap();
		// getting GPS status
		locationManagerSetup(reCall);
		mMap.clear();
    	/*My_LOCATION = 
                new LatLng(22.5232054,88.4490952);*/
		mMap.addMarker(new MarkerOptions()
        .position(My_LOCATION)
        .title("I AM HERE")
        .snippet("waiting")
        .icon(BitmapDescriptorFactory
        .fromResource(R.drawable.ic_launcher)));
    	CameraPosition cameraPosition = new CameraPosition.Builder()
        .target(My_LOCATION) // Sets the center of the map to
                                    // My location
        .zoom(17)                   // Sets the zoom
        .bearing(90) // Sets the orientation of the camera to east
        .tilt(30)    // Sets the tilt of the camera to 30 degrees
        .build();    // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
        cameraPosition));
        String sbr= "http://smartcommbluemixdemo.mybluemix.net/getlocation?pLngtd="+location.getLatitude()+"&pLttd="+location.getLongitude()+"&pUnt=100";
        new Connection().execute(new String[]{sbr});
        
        
    }

	private void locationManagerSetup(boolean pLatestRequired) {
		boolean isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		//Toast.makeText(getApplicationContext(), "Location 0"+isGPSEnabled, Toast.LENGTH_LONG).show();
		// if GPS Enabled get lat/long using GPS Services
		if (isGPSEnabled) {
			if (location == null || pLatestRequired) {
				//Toast.makeText(getApplicationContext(), "Location 1", Toast.LENGTH_LONG).show();
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0,
						0, this);
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
				Log.d("GPS Enabled", "GPS Enabled");
				
				if (locationManager != null) {
					//Toast.makeText(getApplicationContext(), "Location 2", Toast.LENGTH_LONG).show();
					location = locationManager
							.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if (location != null) {
						My_LOCATION = 
				                new LatLng(location.getLatitude(),location.getLongitude());
						
					}
				}
			}
		}
	}
    /* Request updates at startup */
    @Override
    protected void onResume() {
      super.onResume();
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
      super.onPause();
      locationManager.removeUpdates(this);
    }
    private class Connection extends AsyncTask<String, Void, String> {
    	HttpResponse mResponse = null;
    	//HttpRequest mRequest = null;
        @Override
        protected String doInBackground(String... arg0) {
        	try {
        		DefaultHttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(arg0[0]);
                mResponse = client.execute(request);
                
                
                
                
            } catch (ClientProtocolException e) {
                Log.d("HTTPCLIENT", e.getLocalizedMessage());
            } catch (IOException e) {
                Log.d("HTTPCLIENT", e.getLocalizedMessage());
            }
            return "1";
        }
  
        @Override
        protected void onPostExecute(String result) {
        	
        	ArrayList<CurrentLocationOverlay> mCurrentLocationOverlaysArray = new ArrayList<CurrentLocationOverlay>();
        	//Toast.makeText(getApplicationContext(), "onPostExecute", Toast.LENGTH_LONG).show();
        	int lReturnCode = mResponse.getStatusLine().getStatusCode();
            ArrayList<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
        	Header[] headers = mResponse.getAllHeaders();
        	for (Header header : headers) {
        		if((header.getName()).startsWith("Resp")) {
            		System.out.println("Key : " + header.getName() 
              		      + " ,Value : " + header.getValue());
            		
            		try {
						jsonObjectList.add(new JSONObject(header.getValue()));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        	}
        	
        	Iterator<JSONObject> iterator = jsonObjectList.iterator();
        	
        	JSONObject jsonObj ;
        	
    		while (iterator.hasNext()) {
    			jsonObj=iterator.next();
    			
    			 try {
					mCurrentLocationOverlaysArray.add(new CurrentLocationOverlay((String) jsonObj.get("vhcl_nm"), String.valueOf(jsonObj.get("distance")), Double.parseDouble((String) jsonObj.get("curr_lngtd")), Double.parseDouble((String) jsonObj.get("curr_lattd"))));
    				
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
 			//System.out.println(iterator.next());
    		}
        	
        	
        	//Toast.makeText(getApplicationContext(), "ReturnCode:["+lReturnCode+"]", Toast.LENGTH_SHORT).show();
        	

        	/*mCurrentLocationOverlaysArray.add(new CurrentLocationOverlay("201 UP", "icon1", Double.parseDouble("22.5832054"), Double.parseDouble("88.4490952")));
            mCurrentLocationOverlaysArray.add(new CurrentLocationOverlay("30B UP", "icon2", Double.parseDouble("22.5632054"), Double.parseDouble("88.4490962")));
            mCurrentLocationOverlaysArray.add(new CurrentLocationOverlay("215 DWN", "icon3", Double.parseDouble("22.5932054"), Double.parseDouble("88.4490992")));
            mCurrentLocationOverlaysArray.add(new CurrentLocationOverlay("201 DWN", "icon4", Double.parseDouble("22.5632054"), Double.parseDouble("88.4490062")));
            mCurrentLocationOverlaysArray.add(new CurrentLocationOverlay("AC32 UP", "icon5", Double.parseDouble("22.5732054"), Double.parseDouble("88.4490962")));
            mCurrentLocationOverlaysArray.add(new CurrentLocationOverlay("AC39 UP", "icon6", Double.parseDouble("22.5232054"), Double.parseDouble("88.4490162")));
            mCurrentLocationOverlaysArray.add(new CurrentLocationOverlay("234 DWN", "icon7", Double.parseDouble("22.5332054"), Double.parseDouble("88.4490965")));
            mCurrentLocationOverlaysArray.add(new CurrentLocationOverlay("215A UP", "icondefault", Double.parseDouble("22.5132054"), Double.parseDouble("88.4490972")));*/
            plotMarkers(mCurrentLocationOverlaysArray);
            
        }
    }
    private void plotMarkers(ArrayList<CurrentLocationOverlay> markers)
    {
    	
    	mMarkersHashMap = new HashMap<Marker, CurrentLocationOverlay>();
        if(markers.size() > 0)
        {
        	
            for (CurrentLocationOverlay CurrentLocationOverlay : markers)
            {

                // Create user marker with custom icon and other options
                MarkerOptions markerOption = new MarkerOptions().position(new LatLng(CurrentLocationOverlay.getmLatitude(), CurrentLocationOverlay.getmLongitude()));
              //  markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.currentlocation_icon));

                Marker currentMarker = mMap.addMarker(markerOption);
                mMarkersHashMap.put(currentMarker, CurrentLocationOverlay);

                mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
                
                //HttpGet get = new HttpGet(url);
             // Add a thin red line from London to New York.
              /*  Polyline line = mMap.addPolyline(new PolylineOptions()
                    .add(My_LOCATION, new LatLng(CurrentLocationOverlay.getmLatitude(), CurrentLocationOverlay.getmLongitude()))
                    .width(5)
                    .color(Color.RED));
                    */
            }
            /*double lat1 = 40.74560;
            double lon1 = -73.94622000000001;
            double lat2 = 46.59122000000001;
            double lon2 = -112.004230;

            String url = "http://maps.googleapis.com/maps/api/directions/json?";

            List<NameValuePair> params = new LinkedList<NameValuePair>();
            params.add(new BasicNameValuePair("origin", lat1 + "," + lon1));
            params.add(new BasicNameValuePair("destination", lat2 + "," + lon2));
            params.add(new BasicNameValuePair("sensor", "false"));

            String paramString = URLEncodedUtils.format(params, "utf-8");
            url += paramString;
            
            new PlotM().execute(new String[]{url});*/
         
        }
    }

      
    @SuppressLint("NewApi")
	private void setUpMap()
    {
        // Do a null check to confirm that we have not already instantiated the map.
//    	synchronized (mMap) {
    		 if (mMap == null)
    	        {
    	            // Try to obtain the map from the SupportMapFragment.
    	            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

    	            // Check if we were successful in obtaining the map.

    	            if (mMap != null)
    	            {
    	                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
    	                {
    	                    @Override
    	                    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker)
    	                    {
    	                    	
    	                        marker.showInfoWindow();
    	                        return true;
    	                    }
    	                });
    	            }
    	            else
    	                Toast.makeText(getApplicationContext(), "Unable to create Maps", Toast.LENGTH_SHORT).show();
    	        }
    	        
//		}
       
        
    }

    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
    {
        public MarkerInfoWindowAdapter()
        {
        }

        @Override
        public View getInfoWindow(Marker marker)
        {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker)
        {
            View v  = getLayoutInflater().inflate(R.layout.infowindow_layout, null);

            CurrentLocationOverlay CurrentLocationOverlay = mMarkersHashMap.get(marker);

           // ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);

            TextView markerLabel = (TextView)v.findViewById(R.id.marker_label);

          //  markerIcon.setImageResource(manageMarkerIcon(CurrentLocationOverlay.getmIcon()));
			if(CurrentLocationOverlay!=null)
			{
			            markerLabel.setText(CurrentLocationOverlay.getmLabel()+" "+CurrentLocationOverlay.getmIcon()+"km");
			}
			else{
				markerLabel.setText("user");
			}
			            return v;
			        }
			    }

	

		/*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
//        mLocationClient.connect();
    }
    
    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
//        mLocationClient.disconnect();
        super.onStop();
    }



	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
    public class GetContent extends AsyncTask<Void, Void, Void> {


      @Override
      protected Void doInBackground(Void... params) {
          return null;

      }

      @Override
      protected void onPostExecute(Void pResult) {
    	  
    	  trackPosition(true);
    	 // mapview = (MapView) findViewById(R.id.map);  
         // mapview.invalidate();
    	  

      }
  }
    public class getPath extends AsyncTask<Void, Void, Void> {


      @Override
      protected Void doInBackground(Void... params) {
          return null;

      }

      @Override
      protected void onPostExecute(Void pResult) {
    	  locationManagerSetup(true);

      }
  }



	
}