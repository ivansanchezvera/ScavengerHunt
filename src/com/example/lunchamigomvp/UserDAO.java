package com.example.lunchamigomvp;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.Response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class UserDAO {
	private CouchDbClient dbClient;

	public UserDAO(CouchDbClient dbClient) {
		this.dbClient = dbClient;
		connectDatabase();
	}

	public UserDAO() {
		connectDatabase();
	}
	
	protected void connectDatabase() {
		CouchDbProperties properties = new CouchDbProperties();
		properties.setDbName("db-amigos");
		properties.setCreateDbIfNotExist(true);
		properties.setProtocol("http");
		//for production
		//properties.setHost("lunchamigo.iriscouch.com");
		//for testing & developing
		properties.setHost("localhost");
		properties.setPort(5984);
		dbClient = new CouchDbClient(properties);
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

	public boolean alreadyExistEmail(String email) {
		try {
			//View needs to be created for lunch amigo!
			List<User> list = dbClient.view("userByEmail/userByEmail")
					.key(email)
					.includeDocs(true)
					.limit(1).query(User.class);

			if (list.get(0) != null) {
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
			List<User> list = dbClient.view("userByEmail/userByEmail")
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
