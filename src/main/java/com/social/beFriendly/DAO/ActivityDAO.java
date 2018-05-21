package com.social.beFriendly.DAO;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.social.beFriendly.model.Activity;
import com.social.scframework.service.DBConnection;

public class ActivityDAO {
	public JacksonDBCollection<Activity,String> activityDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("BeFriendly");
		DBCollection collec = mongo.getCollection("activity");
		JacksonDBCollection<Activity, String> coll = JacksonDBCollection.wrap(collec,Activity.class, String.class);
		return coll;
	}
	public DBCollection activityCollectionDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("BeFriendly");
		DBCollection collec = mongo.getCollection("activity");
		//JacksonDBCollection<Friend, String> coll = JacksonDBCollection.wrap(collec,Friend.class, String.class);
		return collec;
	}
}
