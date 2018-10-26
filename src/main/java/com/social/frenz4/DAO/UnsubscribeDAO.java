package com.social.frenz4.DAO;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.social.frenz4.app.PropertiesApp;
import com.social.frenz4.model.Unsubscribe;
import com.social.scframework.service.DBConnection;

public class UnsubscribeDAO {
	PropertiesApp prop = new PropertiesApp();
	String DBName = prop.getProperty("DbName");
	String DBPortStr = prop.getProperty("DbPort");
	int DBPort = Integer.parseInt(DBPortStr);
	String DBHost = prop.getProperty("DbHost");
	
	public JacksonDBCollection<Unsubscribe,String> unsubscribeDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB(DBName,DBPort,DBHost);
		DBCollection collec = mongo.getCollection("unsubscribe");
		JacksonDBCollection<Unsubscribe, String> coll = JacksonDBCollection.wrap(collec,Unsubscribe.class, String.class);
		return coll;
	}
}
