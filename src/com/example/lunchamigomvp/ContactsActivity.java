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
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//If contact file does not exist
		
		//Match Names before displaying
		friendList = matchEmails();
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
		
		   final String[] SELECTION_ARGS = new String[friendList.size()];
		   friendList.toArray(SELECTION_ARGS);
		    
		return new CursorLoader(this, ContactsContract.Data.CONTENT_URI,
		        PROJECTION, SELECTION,SELECTION_ARGS , null);
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
		
		String amigosListFilename = "amigosListFile"
		
		
		
		
		File file = new File(amigosListFilename);
		if(file.exists()){
			FileInputStream fis = openFileInput(amigosListFilename);
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			DataInputStream in = new DataInputStream(bais);
			while (fis.available() > 0) {
			    String element = in.readUTF();
			    //System.out.println(element);
			    trueAmigos.add(element);
			}
		}      
		//Do somehting
		else{
			for(String s:amigos)
			{
				//Amigo is on the DB
				if(userDAO.alreadyExistEmail(s))
				{
					trueAmigos.add(s);
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
		
<<<<<<< HEAD
=======
		userDAO = new UserDAO();
		
		List<String> amigos = getNameEmailDetails();
		List<String> trueAmigos = new ArrayList<String>();
		
		
		for(String s:amigos)
		{
			//Amigo is on the DB
			if(userDAO.alreadyExistEmail(s))
			{
				//TODO
				// if s get user availability or timestamp
				//Time Now
				Date dNow = new Date( ); // Instantiate a Date object
				Calendar cal = Calendar.getInstance();
				cal.setTime(dNow);
				dNow = cal.getTime();	
				
				amigoUser = userDAO.getUser(s);
				//timestamp arimethic
				// if the time now is before the available until, it's valid !
				if( dNow.compareTo(amigoUser.getAvailableUntil()) < 0 ){
					trueAmigos.add(s);
				}
				//Get from database
			}
		}
>>>>>>> bb3a426da30191a554900e1db34c70cb7c6ead2e
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
}