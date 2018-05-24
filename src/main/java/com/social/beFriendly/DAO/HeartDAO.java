package com.social.beFriendly.DAO;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.social.beFriendly.model.Heart;
import com.social.scframework.service.DBConnection;

public class HeartDAO {
	public JacksonDBCollection<Heart, String> heartDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("BeFriendly");
		DBCollection collec = mongo.getCollection("heart");
		JacksonDBCollection<Heart, String> coll = JacksonDBCollection.wrap(collec,Heart.class, String.class);
		return coll;
	}

	public DBCollection heartCollectionDAO() {
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("BeFriendly");
		DBCollection collec = mongo.getCollection("heart");
		return collec;
	}
}
