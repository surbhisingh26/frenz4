package com.social.frenz4.DAO;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.social.frenz4.model.Unsubscribe;
import com.social.scframework.service.DBConnection;

public class UnsubscribeDAO {
	public JacksonDBCollection<Unsubscribe,String> unsubscribeDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("frenz4");
		DBCollection collec = mongo.getCollection("unsubscribe");
		JacksonDBCollection<Unsubscribe, String> coll = JacksonDBCollection.wrap(collec,Unsubscribe.class, String.class);
		return coll;
	}
}
