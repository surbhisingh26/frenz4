package com.social.frenz4.DAO;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.social.frenz4.app.PropertiesApp;
import com.social.frenz4.model.Status;
import com.social.scframework.service.DBConnection;

public class StatusDAO {
	PropertiesApp prop = new PropertiesApp();
	String DBName = prop.getProperty("DbName");
	String DBPortStr = prop.getProperty("DbPort");
	int DBPort = Integer.parseInt(DBPortStr);
	String DBHost = prop.getProperty("DbHost");
	
	public JacksonDBCollection<Status, String> statusDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB(DBName,DBPort,DBHost);
		DBCollection collec = mongo.getCollection("status");
		JacksonDBCollection<Status, String> coll = JacksonDBCollection.wrap(collec,Status.class, String.class);
		return coll;
	}

	public DBCollection statusCollectionDAO() {
		DBConnection db = new DBConnection();
		DB mongo = db.getDB(DBName,DBPort,DBHost);
		DBCollection collec = mongo.getCollection("status");
		return collec;
	}
}
