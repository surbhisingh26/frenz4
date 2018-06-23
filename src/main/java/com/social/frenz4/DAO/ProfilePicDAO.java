package com.social.frenz4.DAO;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.social.frenz4.model.ProfilePic;
import com.social.scframework.service.DBConnection;

public class ProfilePicDAO {
	public JacksonDBCollection<ProfilePic,String> profilePicDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("frenz4");
		DBCollection collec = mongo.getCollection("profilepic");
		JacksonDBCollection<ProfilePic, String> coll = JacksonDBCollection.wrap(collec,ProfilePic.class, String.class);
		return coll;
	}

	public DBCollection dpCollectionDAO() {
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("frenz4");
		DBCollection collec = mongo.getCollection("profilepic");
		return collec;
	}
}
