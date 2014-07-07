package com.example.lunchamigomvp;

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

public class UserDAO {
	private CouchDbClientAndroid dbClient;

	public UserDAO(CouchDbClientAndroid dbClient) {
		this.dbClient = dbClient;
		connectDatabase();
	}

	public UserDAO() {
		connectDatabase();
	}
	
	protected void connectDatabase() {
		/*
		try{
		dbClient = new CouchDbClientAndroid("db-amigos", true, "http", "lunchamigo.iriscouch.com", 5984, "admin", "steveBallmer1");
		}catch(Exception e)
		{
			System.err.println("DB connection failed");
			e.printStackTrace();
		}
		String maxine = "Harder, Better, Faster, Stronger";
		String a = maxine;
		*/
		
		CouchDbProperties properties = new CouchDbProperties();
		properties.setDbName("db-amigos");
		properties.setCreateDbIfNotExist(true);
		properties.setProtocol("http");
		//for production
		//properties.setHost("lunchamigo.iriscouch.com");
		//properties.setHost("54.79.24.208");
		//http://54.79.24.208/
		//for testing & developing
		//properties.setHost("localhost");
		properties.setHost("10.9.240.209");
		//properties.setUsername("admin");
		//properties.setPassword("steveBallmer1");
		properties.setPort(5984);
		//properties.setPort(17088);
		
		try{
		dbClient = new CouchDbClientAndroid(properties);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public boolean insertUser(String email, String token) {
		Gson gson = new Gson();
		String json;
		Calendar c = Calendar.getInstance();
		Date myDate = c.getTime();
		try {
			Timestamp stamp = new Timestamp(myDate.getTime());
			User user = new User(email, "", token, "false", stamp);

			// Cast the new object to JSON file to be saved in the DB
			json = gson.toJson(user);

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
	
	//Insert a Whole User
	public boolean insertUser(User u) {
		Gson gson = new Gson();
		String json;
		try {
			User user = u;

			// Cast the new object to JSON file to be saved in the DB
			json = gson.toJson(user);

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

	public User getUser(String email)
	{
		try {
			//String token
			//View needs to be created for lunch amigo!
			List<User> list = dbClient.view("userViews/getUserByEmail")
					.includeDocs(true)
					.key(email)
					.limit(1).query(User.class);
			
			
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
	
	
	public boolean updateUser(User user) {
		
		Gson gson = new Gson();
		String json;
		try {
			// Cast the new object to JSON file to be saved in the DB
			json = gson.toJson(user);

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