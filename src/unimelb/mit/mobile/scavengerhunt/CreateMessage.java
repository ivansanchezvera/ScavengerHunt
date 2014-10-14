package unimelb.mit.mobile.scavengerhunt;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.LatLng;

public class CreateMessage extends ActionBarActivity implements 
		ConnectionCallbacks,
		OnConnectionFailedListener, 
		LocationListener, 
		OnMyLocationButtonClickListener {

	private EditText messageText;
	private EditText messageHint;	
	private EditText recipientNameText;
	public static final String AUTHPREFS = "authPrefs" ;
	private String sender;
	private String receiver;
	private List<String> hints;
	
	//For location From Chavi
	private LatLng currentLocation;
	private LocationClient mLocationClient;
	
    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(3000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_message);
		
		messageText = (EditText) findViewById(R.id.messageText);
		messageHint = (EditText) findViewById(R.id.messageHintText);
		recipientNameText = (EditText) findViewById(R.id.recipientUsernameText);
		
		//Get Receiver information from Contacts Activity
		receiver = getIntent().getStringExtra("recipientEmail");
		recipientNameText.setText(receiver);
		recipientNameText.setEnabled(false);
		
		
		//GetSender and Receiver from previous activity
		// Restore preferences to get username global variable
	       SharedPreferences settings = getSharedPreferences(AUTHPREFS, 0);
	       boolean silent = settings.getBoolean("silentMode", false);
		
	    //Manage this error, what happens when there is not preference
	    //for this key and the default value is taken. This should not happen
		sender = settings.getString("userEmail", "abc@error.com");
		
		//Now the hints
		hints = new ArrayList<String>();
		
		mLocationClient = new LocationClient(this, this, this);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_message, menu);
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
	
	//Now persist message
	public void sendMessage(View v)
	{
		//Asych Task to persist Message		
		AsyncTask<Message, Void, Boolean> messageDB = new dbCreateMessage();
		boolean messageSavedOK = false;
		
		Location currentLocation;
		
		//Insert Message
		try {
			//Code for generating timestamps Refactor and put it in a general class
			Calendar c = Calendar.getInstance();
			c.setTime(new Date()); // sets calendar time/date
			c.add(Calendar.HOUR_OF_DAY, 1);
			Date myDate = c.getTime();  // adds one hour
			Timestamp stamp = new Timestamp(myDate.getTime());	
			
			//Get hints form screen
			hints.add(messageHint.getText().toString());
			
			currentLocation = mLocationClient.getLastLocation();
			
			String messageLocation = null;
			if(currentLocation!= null)
			{
			messageLocation = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
			}
			
			
			Message m = new Message(messageText.getText().toString(), sender, receiver, MessageState.UNREAD, MessageNotificationState.UNNOTIFIED, messageLocation, stamp, hints);
			
			messageSavedOK = messageDB.execute(m).get();
				
				if(messageSavedOK)
				{
					Toast.makeText(this, "Message was Saved!!!", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(this, "Message Failed, Sudden Death!!!", Toast.LENGTH_LONG).show();
				}	
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
		
		
	}
	
    /**
     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates(
                REQUEST,
                this);  // LocationListener
    }

    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(
                    getApplicationContext(),
                    this,  // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
        setUpLocationClientIfNeeded();
        mLocationClient.connect();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
    }

	@Override
	public boolean onMyLocationButtonClick() {
		// TODO Auto-generated method stub
        Toast.makeText(this, "Showing current location.", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
}
