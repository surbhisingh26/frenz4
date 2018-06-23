package com.social.frenz4.DAO;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.social.frenz4.model.Email;
import com.social.scframework.service.DBConnection;

public class EmailDAO {
	public JacksonDBCollection<Email,String> emailDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("frenz4");
		DBCollection collec = mongo.getCollection("email");
		JacksonDBCollection<Email, String> coll = JacksonDBCollection.wrap(collec,Email.class, String.class);
		return coll;
	}
}
