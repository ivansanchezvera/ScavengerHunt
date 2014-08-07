package com.example.lunchamigomvp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


import android.app.ActionBar.LayoutParams;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class ContactsActivity extends ListActivity
	implements LoaderManager.LoaderCallbacks<Cursor> {

	// This is the Adapter being used to display the list's data
	SimpleCursorAdapter mAdapter;
	
	// These are the Contacts rows that we will retrieve
	static final String[] PROJECTION = new String[] {ContactsContract.Data._ID,
	    ContactsContract.Data.DISPLAY_NAME};
	
	// This is the select criteria
	/*
	 *Previous selection
	static final String SELECTION = "((" + 
	    ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
	    ContactsContract.Data.DISPLAY_NAME + " != '' ))";
	*/
	
	// This is the select criteria
	static final String SELECTION = "((" + 
	    ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
	    ContactsContract.Data.DISPLAY_NAME + " != '' ) AND (" + 
	    ContactsContract.Data.DATA1 + "= ?))";
	
	
	//For access to CouchDB
	private UserDAO userDAO;
	private List<String> friendList;
	private User amigoUser;
	
	//for geolocation
	private static final String AUTHPREFS = "authPrefs" ;
	private static final String GEOLOCATIONKEY = "geoKey"; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//If contact file does not exist
		
		//Match Names before displaying
		//This does the same several times, always on create!!! FIX THIS!
		
		//Instead of the normal function I will try to use an AsyncLoaderTask
		//friendList = matchEmails();
		friendList = null; 
		AsyncTaskLoader<List<String>> loadEmailsAndMatchThem = new MatchEmailsAsyncTask(this);
		friendList = loadEmailsAndMatchThem.loadInBackground();
		
		
		if(friendList==null || friendList.size()<1)
		{
			Toast.makeText(this, "Fuck no amigos to show", Toast.LENGTH_LONG).show();
			finish();
		}
		else{
		
				// Create a progress bar to display while the list loads
			ProgressBar progressBar = new ProgressBar(this);
			progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
			        LayoutParams.WRAP_CONTENT, Gravity.CENTER));
			progressBar.setIndeterminate(true);
			getListView().setEmptyView(progressBar);
			
			// Must add the progress bar to the root of the layout
			ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
			root.addView(progressBar);
			
			// For the cursor adapter, specify which columns go into which views
			String[] fromColumns = {ContactsContract.Data.DISPLAY_NAME};
			int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1
			
			// Create an empty adapter we will use to display the loaded data.
			// We pass null for the cursor, then update it in onLoadFinished()
			mAdapter = new SimpleCursorAdapter(this, 
			        android.R.layout.simple_list_item_1, null,
			        fromColumns, toViews, 0);
			setListAdapter(mAdapter);
			
			// Prepare the loader.  Either re-connect with an existing one,
			// or start a new one.
			getLoaderManager().initLoader(0, null, this);
		}
	}
	
	// Called when a new Loader needs to be created
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		//Previous cursor
		/*return new CursorLoader(this, ContactsContract.Data.CONTENT_URI,
		        PROJECTION, SELECTION, null, null);*/
		
		
		//The selection should be an expression and selectionArgs should have as many elements as there are ? literal placeholders in selection.
		
		   //final String[] SELECTION_ARGS = new String[friendList.size()];
		final String[] SELECTION_ARGS = new String[friendList.size()];
		  friendList.toArray(SELECTION_ARGS);
		
		   
		  // Loader<Cursor> cursor = new CursorLoader(this, ContactsContract.Data.CONTENT_URI,
		//	        PROJECTION, SELECTION,SELECTION_ARGS , null);
		
		// This is the select criteria
		String SELECTION2 = "((" + 
		    ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
		    ContactsContract.Data.DISPLAY_NAME + " != '' ) AND (" + 
		    ContactsContract.Data.DATA1 + " IN ("+ makePlaceholders(friendList.size()) +") ))";

		   return new CursorLoader(this, ContactsContract.Data.CONTENT_URI,
		        PROJECTION, SELECTION2,SELECTION_ARGS , null);
		        
	}
	
	private String makePlaceholders(int len) {
	    if (len < 1) {
	        // It will lead to an invalid query anyway ..
	        throw new RuntimeException("No placeholders");
	    } else {
	        StringBuilder sb = new StringBuilder(len * 2 - 1);
	        sb.append("?");
	        for (int i = 1; i < len; i++) {
	            sb.append(",?");
	        }
	        return sb.toString();
	    }
	}
	
	// Called when a previously created loader has finished loading
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in.  (The framework will take care of closing the
		// old cursor once we return.)
		mAdapter.swapCursor(data);
	}
	
	// Called when a previously created loader is reset, making the data unavailable
	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
		// above is about to be closed.  We need to make sure we are no
		// longer using it.
		mAdapter.swapCursor(null);
	}
	
	@Override 
	public void onListItemClick(ListView l, View v, int position, long id) {
	// Do something when a list item is clicked
	}
	
	//Function to call view and match
	//Function should be on the foreground...
	public List<String> matchEmails()
	{
		boolean amigosRetrieved = false;
		boolean amigosOnFile = false;
		
		userDAO = new UserDAO();
		List<String> amigos = getNameEmailDetails();
		List<String> trueAmigos = new ArrayList<String>();
		
		String amigosListFilename = "amigosListFile";
		
		try{
		File file = new File(amigosListFilename);
		//If file exists
		if(file.exists()){
			FileInputStream fis = openFileInput(amigosListFilename);
			byte[] bytes = null;
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			DataInputStream in = new DataInputStream(bais);
			while (fis.available() > 0) {
			    String element = in.readUTF();
			    //System.out.println(element);
			    trueAmigos.add(element);
			}
		}      
		//Do something if file does not exists
		else{
			
			userDAO = new UserDAO();
			List<User> contactsAlreadyInPlatform = userDAO.getMultipleUsers(amigos);
			float[] results; // initialized for calculating distance
			
			// if s get user availability or timestamp
			//Time Now
			Date dNow = new Date( ); // Instantiate a Date object
			Calendar cal = Calendar.getInstance();
			cal.setTime(dNow);
			dNow = cal.getTime();
			
			String userLocation = getSharedPreferences(AUTHPREFS, MODE_PRIVATE).getString(GEOLOCATIONKEY,null);
			String[] userLatLong = userLocation.split(",");
			double distance;
			//TODO
			//access the sharedpreference
			
			//timestamp arimethic
			// use shared prefrence of previous one to get user location don't call location manager twice
			// if the time now is before the available until, it's valid !

			for(User u: contactsAlreadyInPlatform)
			{
				String amigoLocation = u.getGeolocationLatLong();
				String[] amigoLatLong = amigoLocation.split(",");
				
				distance = haversine( Double.parseDouble(userLatLong[0]), Double.
						parseDouble(userLatLong[1]), Double.parseDouble(amigoLatLong[0]), 
						Double.parseDouble(amigoLatLong[1]));
				//add another if statement
				if( dNow.compareTo(u.getAvailableUntil()) < 0 && distance < 1){
					trueAmigos.add(u.getEmail());
				}
			}

			//to write in file
			// write to byte array
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(baos);
			for (String element : trueAmigos) {
			    out.writeUTF(element);
			}
			byte[] bytes = baos.toByteArray();
			
			FileOutputStream fos = openFileOutput(amigosListFilename, Context.MODE_PRIVATE);
			fos.write(bytes);
			fos.close();
		}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		// Do something else.
			
		//If file does not exist
		
		//If file is not there create file with email list Otherwise do nothing or update it
		/*
		if(!amigosOnFile)
		{
		File file = new File(this.getFilesDir(), "lunchAmigoList");
		
		FileOutputStream outputStream;

		try {
		  outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
		  outputStream.write(string.getBytes());
		  outputStream.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
		}else{
			
		}*/
		return trueAmigos;
	}
	
	//For retrieving data from DB
	public ArrayList<String> getNameEmailDetails() {
	    ArrayList<String> emlRecs = new ArrayList<String>();
	    HashSet<String> emlRecsHS = new HashSet<String>();
	    Context context = this;
	    ContentResolver cr = context.getContentResolver();
	    String[] PROJECTION = new String[] { ContactsContract.RawContacts._ID, 
	            ContactsContract.Contacts.DISPLAY_NAME,
	            ContactsContract.Contacts.PHOTO_ID,
	            ContactsContract.CommonDataKinds.Email.DATA, 
	            ContactsContract.CommonDataKinds.Photo.CONTACT_ID };
	    String order = "CASE WHEN " 
	            + ContactsContract.Contacts.DISPLAY_NAME 
	            + " NOT LIKE '%@%' THEN 1 ELSE 2 END, " 
	            + ContactsContract.Contacts.DISPLAY_NAME 
	            + ", " 
	            + ContactsContract.CommonDataKinds.Email.DATA
	            + " COLLATE NOCASE";
	    String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
	    Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
	    if (cur.moveToFirst()) {
	        do {
	            // names comes in hand sometimes
	            String name = cur.getString(1);
	            String emlAddr = cur.getString(3);

	            // keep unique only
	            if (emlRecsHS.add(emlAddr.toLowerCase())) {
	                emlRecs.add(emlAddr);
	            }
	        } while (cur.moveToNext());
	    }

	    cur.close();
	    return emlRecs;
	}
	
	private double haversine(
	        double lat1, double lng1, double lat2, double lng2) {
	    int r = 6371; // average radius of the earth in km
	    double dLat = Math.toRadians(lat2 - lat1);
	    double dLon = Math.toRadians(lng2 - lng1);
	    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
	       Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) 
	      * Math.sin(dLon / 2) * Math.sin(dLon / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double d = r * c;
	    return d;
	}
}