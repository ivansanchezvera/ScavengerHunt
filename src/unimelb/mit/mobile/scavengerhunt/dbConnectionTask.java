package unimelb.mit.mobile.scavengerhunt;

import android.os.AsyncTask;

public class dbConnectionTask extends AsyncTask<String, Void, Boolean> {

	@Override
	protected Boolean doInBackground(String... email) {
		// TODO Auto-generated method stub
		
		boolean temp = false;
		try{
		UserDAO userDAO = new UserDAO();
		if(email.length>0)
		{
		temp = userDAO.alreadyExistEmail(email[0]);
		}
		}catch(Exception e)
		{
			//Toast.makeText(this, "Error in Thread", Toast.LENGTH_LONG).Show() ;
			System.err.println("Error in thread");
		}
		return temp;
	}

}
