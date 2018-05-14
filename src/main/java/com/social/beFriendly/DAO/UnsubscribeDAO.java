package com.social.beFriendly.DAO;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.social.beFriendly.model.Unsubscribe;
import com.social.scframework.service.DBConnection;

public class UnsubscribeDAO {
	public JacksonDBCollection<Unsubscribe,String> unsubscribeDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("BeFriendly");
		DBCollection collec = mongo.getCollection("unsubscribe");
		JacksonDBCollection<Unsubscribe, String> coll = JacksonDBCollection.wrap(collec,Unsubscribe.class, String.class);
		return coll;
	}
}
