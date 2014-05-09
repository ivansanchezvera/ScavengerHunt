package com.example.lunchamigomvp;

import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.json.simple.JSONValue;

public class User {
	private String _id;
	private String _rev;
	private String email;
	private String password;
	private String token;
	private String confirmed;
	private Timestamp timestamp;
	

	public User(String email, String password, String token, String confirmed,
			Timestamp timestamp) {
		// super();
		this.email = email;
		this.password = password;
		this.token = token;
		this.confirmed = confirmed;
		this.timestamp = timestamp;
		_rev = null;
		_id = null;
		
	}
	
	public String get_rev() {
		return _rev;
	}

	public void set_rev(String _revision) {
		this._rev = _revision;
	}

	public User(String id, String revision, String email, String password, String token, String confirmed,
			Timestamp timestamp) {
		// super();
		this._rev = revision;
		this._id = id;
		this.email = email;
		this.password = password;
		this.token = token;
		this.confirmed = confirmed;
		this.timestamp = timestamp;
	}
	
	public void writeJSONString(Writer out) throws IOException {
		LinkedHashMap obj = new LinkedHashMap();
		obj.put("email", email);
		obj.put("_id", _id);
		obj.put("_revision", _rev);
		obj.put("password", password);
		obj.put("token", token);
		obj.put("confirmed", confirmed);
		obj.put("timestamp", timestamp.toString());
		JSONValue.writeJSONString(obj, out);
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(String confirmed) {
		this.confirmed = confirmed;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

}



/*import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.json.simple.JSONValue;

public class User {
	private String email;
	private String password;
	private String token;
	private String confirmed;
	private Timestamp timestamp;

	public User(String email, String password, String token, String confirmed,
			Timestamp timestamp) {
		// super();
		this.email = email;
		this.password = password;
		this.token = token;
		this.confirmed = confirmed;
		this.timestamp = timestamp;
	}

	public void writeJSONString(Writer out) throws IOException {
		LinkedHashMap obj = new LinkedHashMap();
		obj.put("email", email);
		obj.put("password", password);
		obj.put("token", token);
		obj.put("confirmed", confirmed);
		obj.put("timestamp", timestamp.toString());
		JSONValue.writeJSONString(obj, out);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(String confirmed) {
		this.confirmed = confirmed;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

}
*/