package com.social.frenz4.DAO;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.social.frenz4.model.Status;
import com.social.scframework.service.DBConnection;

public class StatusDAO {
	public JacksonDBCollection<Status, String> statusDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("frenz4");
		DBCollection collec = mongo.getCollection("status");
		JacksonDBCollection<Status, String> coll = JacksonDBCollection.wrap(collec,Status.class, String.class);
		return coll;
	}

	public DBCollection statusCollectionDAO() {
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("frenz4");
		DBCollection collec = mongo.getCollection("status");
		return collec;
	}
}
