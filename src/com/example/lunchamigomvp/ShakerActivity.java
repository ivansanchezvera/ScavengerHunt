package com.example.lunchamigomvp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.lunchamigomvp.ShakeDetector.OnShakeListener;

public class ShakerActivity extends Activity {
	
	/* initialize the class */
	private ShakeDetector mShakeDetector;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Button bananaButton;
	private Boolean firstTime; /* prevent toomuch timer */
	private LocationListener locationListener; /* gps */
	private LocationManager locationManager;
	private double mLongitude;
	private double mLatitude;
	
	//For DB Management
	private UserDAO userDAO;
	private User amigoUser;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shaker);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		bananaButton = (Button) findViewById(R.id.bananaButton);
		firstTime = true;
		
		
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		      // Called when a new location is found by the network location provider.
		      //makeUseOfNewLocation(location);
				mLatitude = location.getLatitude();
				mLongitude = location.getLongitude();

		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {

		    }

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		  };

		// Register the listener with the Location Manager to receive location updates
<<<<<<< HEAD
		  if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		  {
			  locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		  }
=======
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
		    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		}
>>>>>>> 908cee7e8ae89a7644e0c1236ea6a77a0bd9fd4f
		mShakeDetector = new ShakeDetector(new OnShakeListener() {
			
			@Override
			public void onShake() {
				// TODO Auto-generated method stub
				if (firstTime == true) {
					findFriends();
				
				}
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shaker, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
<<<<<<< HEAD
		if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		{
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, locationListener);
=======
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {	
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, locationListener);		
>>>>>>> 908cee7e8ae89a7644e0c1236ea6a77a0bd9fd4f
		}
		firstTime = true;
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(mShakeDetector);
		locationManager.removeUpdates(locationListener);

	}
	
	public void bananaButton(View view) {
		//to make sure multiple shake can only be registered as once !
		if (firstTime == true) {
			findFriends();
		}
	}
	
	/* todo server stuff */
	public void findFriends() {
		
		CountDownTimer cdt = new CountDownTimer(6000, 1000) {

			public void onTick(long millisUntilFinished) {
				bananaButton.setBackgroundResource(R.drawable.banana_half);

			}

			public void onFinish() {
				Double latitude = mLatitude;
				Double longitude = mLongitude;

				CharSequence text = latitude.toString() + "," + longitude.toString();
					
				bananaButton.setBackgroundResource(R.drawable.banana);
				//
				locationManager.removeUpdates(locationListener);
				startIntent();
			}

		}.start();
		
		//to make sure multiple shake can only be registered as once !
		firstTime = false;

	}
	
	/* start the next intent */
	public void startIntent() {
		Intent intent = new Intent(this, ContactsActivity.class);
		startActivity(intent);
	}
	
	

}
