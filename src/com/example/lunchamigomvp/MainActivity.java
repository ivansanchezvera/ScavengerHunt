package com.example.lunchamigomvp;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
// abstract this out to a different package
// todo
// what to do on Pause or back button ?
// todo
// todo
// use regex
public class MainActivity extends Activity {
	/* login */
	
	private String mEmail;
	private String mPassword;
	
	private EditText PasswordView;
	private EditText EmailView;	
	private Boolean isLoggedIn;
	private Boolean isValid;
	
	//For DB Management
	private UserDAO userDAO;
	private User amigoUser;
	
	SharedPreferences sharedpreferences;
	
	public static final String AUTHPREFS = "authPrefs" ;
	public static final String EMAIL = "emailKey"; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		isLoggedIn = false;
		isValid = false;
		EmailView = (EditText) findViewById(R.id.editEmailAddress);		
		PasswordView = (EditText) findViewById(R.id.editPassword);
	    
	    //For network permissions
	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
		
	    sharedpreferences = getSharedPreferences(AUTHPREFS, Context.MODE_PRIVATE);
	   // userDAO = new UserDAO();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	// Login Button
	public void tryLogin(View view) {

		mEmail = EmailView.getText().toString();
		mPassword = PasswordView.getText().toString();
				
		attemptLogin();
		
		// start another activity if the person succeed to login
		if (isLoggedIn == true) {
			storeCredentials();
			Intent intent = new Intent(this, ShakerActivity.class);
			startActivity(intent);
		}
	}
	

	
	public void attemptLogin() {
		// Please abstract the string to R.strings
		// todo

		//Match Email with regular Expressions
		
		//shows error in a toast
		// todo improve this to be similar to LoginActivity android template
		// or just use toast?
		if (TextUtils.isEmpty(mEmail)) {
			Context context = getApplicationContext();
			CharSequence text = "Please Insert Email";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		} else if (!mEmail.contains("@")) {
			Context context = getApplicationContext();
			CharSequence text = "Error Invalid Email!";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		} else {
			
			
			//Here we call the db and check if the user exists
			AsyncTask<String, Void, Boolean> connectDB = new dbConnectionTask();
			AsyncTask<User, Void, Boolean> loginDB = new dbLogin();
			
			Boolean emailExists;
			Boolean userLoginOK;
			try {
				emailExists = connectDB.execute(mEmail).get();
		
				if(emailExists)
				{
					Calendar c = Calendar.getInstance();
					c.setTime(new Date()); // sets calendar time/date
					c.add(Calendar.HOUR_OF_DAY, 1);
					Date myDate = c.getTime();  // adds one hour
					Timestamp stamp = new Timestamp(myDate.getTime());				
					
					
					amigoUser = new User(mEmail, mPassword, "1234", "yes", stamp, "0.0,0.0", true, myDate);
					
					/*
					userDAO.insertUser(amigoUser);
					isValid = true;
					*/
					userLoginOK = loginDB.execute(amigoUser).get();
					
					if(userLoginOK)
					{
					Toast.makeText(this, "LunchTime, you are in!!!", Toast.LENGTH_SHORT).show();
					
					// todo use shared preference as a bool to say you are logged in instead of a instance variable
					isLoggedIn = true;
					}else{
						Toast.makeText(this, "Login Failed, Wrong Credentials!!!", Toast.LENGTH_LONG).show();
					isLoggedIn = false;
					}
				}else{
					isValid = false;
					Toast.makeText(this, "User doesn't Exist!!!", Toast.LENGTH_LONG).show();
				}	
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// todo what if user is not registered ? add another elif	

		
	}
	
	public void tryRegister(View view) {

		mEmail = EmailView.getText().toString();
		mPassword = PasswordView.getText().toString();
				
		attemptRegister();
		/* todo register the person here */
		if (isValid == true) {
			Context context = getApplicationContext();
			CharSequence text = "You are now registered";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			
		}
		
		// start another activity if the person succeed to login

	}
	
	public void attemptRegister() {
		if (TextUtils.isEmpty(mEmail)) {
			Context context = getApplicationContext();
			CharSequence text = "Please Insert Email";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		} else if (!mEmail.contains("@")) //check agains regex 
		{
			Context context = getApplicationContext();
			CharSequence text = "Error Invalid Email!";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();		

			
		} else {
			
			//Our User
			//amigoUser = new User
			
			//Here we call the db and check if the user exists
			AsyncTask<String, Void, Boolean> connectDB = new dbConnectionTask();
			AsyncTask<User, Void, Boolean> saveUserToDB = new dbCreateUserTask();
			
			Boolean emailExists;
			Boolean userRegistrationOK;
			try {
				emailExists = connectDB.execute(mEmail).get();
		
				if(!emailExists)
				{
					Calendar c = Calendar.getInstance();
					c.setTime(new Date()); // sets calendar time/date
					c.add(Calendar.HOUR_OF_DAY, 1);
					Date myDate = c.getTime();  // adds one hour
					Timestamp stamp = new Timestamp(myDate.getTime());				
					
					
					amigoUser = new User(mEmail, mPassword, "1234", "yes", stamp, "0.0,0.0", true, myDate);
					
					/*
					userDAO.insertUser(amigoUser);
					isValid = true;
					*/
					userRegistrationOK = saveUserToDB.execute(amigoUser).get();
					
					if(userRegistrationOK)
					{
					Toast.makeText(this, "LunchTime, you are in!!!", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(this, "Registration Failed, try later please!!!", Toast.LENGTH_SHORT).show();
					}
				}else{
				isValid = false;
				Toast.makeText(this, "User Already Exists... Dat shit cray!", Toast.LENGTH_LONG).show();
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
	
	public void storeCredentials() {
		Editor editor = sharedpreferences.edit();
	    editor.putString(EMAIL, mEmail);
	    editor.commit();
	}
	
}
