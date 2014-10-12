package unimelb.mit.mobile.scavengerhunt;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Congratulations extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_congratulations);
		Bundle extras = getIntent().getExtras();
		TextView mMessageFrom = (TextView) findViewById(R.id.txtLabelYourMessage);
		TextView mMessage = (TextView) findViewById(R.id.txtMessage);
        if (extras != null) {
            mMessageFrom.setText("Your message from " + extras.getString("messageFrom") + ":");
            mMessage.setText(extras.getString("message"));
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.congratulations, menu);
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
}
