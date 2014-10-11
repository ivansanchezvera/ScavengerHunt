
package unimelb.mit.mobile.scavengerhunt;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.lightcouch.CouchDbClientAndroid;
import org.lightcouch.CouchDbProperties;

import org.lightcouch.Response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/*
import com.google.gson.Gson;
import com.google.gson.JsonObject;
*/

public class MessageDAO {
	private CouchDbClientAndroid dbClient;

	public MessageDAO(CouchDbClientAndroid dbClient) {
		this.dbClient = dbClient;
		connectDatabase();
	}

	public MessageDAO() {
		connectDatabase();
	}
	
	protected void connectDatabase() {
		try{
		dbClient = new CouchDbClientAndroid("raw/db/localx.properties");
		//dbClient = new CouchDbClientAndroid("localx.properties");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	//Insert a very simple message
	public boolean insertMessage(String message, String sender, String receiver, String location) {
		Gson gson = new Gson();
		String json;
		Calendar c = Calendar.getInstance();
		Date myDate = c.getTime();
		try {
			Timestamp stamp = new Timestamp(myDate.getTime());
			String status = "unread";
			
			Message m = new Message(message, sender, receiver, status, location, stamp); 

			// Cast the new object to JSON file to be saved in the DB
			json = gson.toJson(m);

			JsonObject jsonObj = dbClient.getGson().fromJson(json,
					JsonObject.class);

			// Saving in DB
			Response responseCouch = dbClient.save(jsonObj);
			
			if (!responseCouch.getId().equals("")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return false;
		}
	}
	
	//Insert a Whole Message
	public boolean insertMessage(Message m) {
		Gson gson = new Gson();
		String json;
		try {
			Message mess = m;

			// Cast the new object to JSON file to be saved in the DB
			json = gson.toJson(mess);

			JsonObject jsonObj = dbClient.getGson().fromJson(json,
					JsonObject.class);

			// Saving in DB
			Response responseCouch = dbClient.save(jsonObj);
			
			if (!responseCouch.getId().equals("")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return false;
		}
	}

	/*
	//modify this --Probably dont need it for messages
	public boolean alreadyExistEmail(String email) {
		try {
			//View needs to be created for lunch amigo!
			List<User> list = dbClient.view("userViews/getUserByEmail")
					.key(email)
					.includeDocs(true)
					.limit(1).query(User.class);

			if (list!= null && list.size()>0 && list.get(0) != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return false;
		}
	}

*/
	
	//--Need to tweak to get messages from db by receiver Email
	public Message getMessages(String email)
	{
		try {
			//String token
			//View needs to be created for lunch amigo!
			List<Message> list = dbClient.view("userViews/getUserByEmail")
					.includeDocs(true)
					.key(email)
					.limit(1).query(Message.class);
			
			
			if (list.get(0) != null) {
				
				if(list.size()>1)
				{throw new Exception("Must Return just one Object");}
				else{
				return list.get(0);
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return null;
		}
		//return null;
	}

	//--Get sent messages??? --Do we need this
	/*
	public List<User> getMultipleUsers(List<String> emailList)
	{	
		try {
			//String token
			//View needs to be created for lunch amigo!
			List<User> list = dbClient.view("userViews/getUserByEmail")
					.includeDocs(true)
					.keys(emailList) //This is for multiple Keys
					.query(User.class);
			
			return list;
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return null;
		}
		//return null;
	}
	*/
	
	public boolean updateMessage(Message m) {
		
		Gson gson = new Gson();
		String json;
		try {
			// Cast the new object to JSON file to be saved in the DB
			json = gson.toJson(m);

			JsonObject jsonObj = dbClient.getGson().fromJson(json,
					JsonObject.class);

			// Saving in DB
			Response responseCouch = dbClient.update(jsonObj);
			
			if (!responseCouch.getId().equals("")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return false;
		}
	}
}

//What about deleting messages??? --Build here when needed
