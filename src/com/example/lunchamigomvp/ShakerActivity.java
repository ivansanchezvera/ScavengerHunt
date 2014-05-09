package com.example.lunchamigomvp;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
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
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shaker);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		bananaButton = (Button) findViewById(R.id.bananaButton);
		firstTime = true;
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
		firstTime = true;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(mShakeDetector);

	}
	
	public void bananaButton(View view) {
		if (firstTime == true) {
			findFriends();
		}
	}
	
	/* todo server stuff */
	public void findFriends() {
		
		CountDownTimer cdt = new CountDownTimer(3000, 1000) {

			public void onTick(long millisUntilFinished) {
				bananaButton.setBackgroundResource(R.drawable.banana_half);
			}

			public void onFinish() {
				bananaButton.setBackgroundResource(R.drawable.banana);
				startIntent();
			}

		}.start();
		
		firstTime = false;

	}
	
	/* start the next intent */
	public void startIntent() {
		Intent intent = new Intent(this, ContactsActivity.class);
		startActivity(intent);
	}
	
	

}
