package com.example.lunchamigomvp;

import android.os.AsyncTask;
import android.widget.Toast;

public class dbUpdateUserTask extends AsyncTask<User, Void, Boolean> {

	boolean isValid = false;
	@Override
	protected Boolean doInBackground(User... parameters) {
		// TODO Auto-generated method stub
		User amigoUser = parameters[0];
		
		
		
		isValid = false;
		try{
		//This can be refactored to use one single connection Object
		UserDAO userDAO = new UserDAO();
		if(userDAO.updateUser(amigoUser))
		{
			isValid = true;
		}
		}catch(Exception e)
		{
			//Toast.makeText(this, "Error in Thread", Toast.LENGTH_LONG).Show() ;
			System.err.println("Error in thread");
			
		}
		return isValid;
	}
	
	protected void onPostExecute(Long result) {
        //showDialog("Downloaded " + result + " bytes");
        //Here I need to show a Message
    }

}