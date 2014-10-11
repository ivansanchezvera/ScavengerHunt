package unimelb.mit.mobile.scavengerhunt;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreateMessage extends ActionBarActivity {

	private EditText messageText;
	private EditText messageHint;	
	private String sender;
	private String receiver;
	private List<String> hints;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_message);
		
		messageText = (EditText) findViewById(R.id.messageText);
		messageHint = (EditText) findViewById(R.id.messageHintText);
		
		//GetSender and Receiver from previous activity
		sender = "fulanito@guasmo.com";
		receiver = "julio@address.com";
		
		//Now the hints
		hints = new ArrayList<String>();
		hints.add(messageHint.getText().toString());
		
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
		
		//Insert Message
		try {
			//Code for generating timestamps Refactor and put it in a general class
			Calendar c = Calendar.getInstance();
			c.setTime(new Date()); // sets calendar time/date
			c.add(Calendar.HOUR_OF_DAY, 1);
			Date myDate = c.getTime();  // adds one hour
			Timestamp stamp = new Timestamp(myDate.getTime());	
			
			Message m = new Message(messageText.getText().toString(), sender, receiver, MessageState.Unread, "0001,0002", stamp, hints);
			
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
}
