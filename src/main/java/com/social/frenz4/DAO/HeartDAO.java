package com.social.frenz4.DAO;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.social.frenz4.app.PropertiesApp;
import com.social.frenz4.model.Heart;
import com.social.scframework.service.DBConnection;

public class HeartDAO {
	PropertiesApp prop = new PropertiesApp();
	String DBName = prop.getProperty("DbName");
	String DBPortStr = prop.getProperty("DbPort");
	int DBPort = Integer.parseInt(DBPortStr);
	String DBHost = prop.getProperty("DbHost");
	
	public JacksonDBCollection<Heart, String> heartDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB(DBName,DBPort,DBHost);
		DBCollection collec = mongo.getCollection("heart");
		JacksonDBCollection<Heart, String> coll = JacksonDBCollection.wrap(collec,Heart.class, String.class);
		return coll;
	}

	public DBCollection heartCollectionDAO() {
		DBConnection db = new DBConnection();
		DB mongo = db.getDB(DBName,DBPort,DBHost);
		DBCollection collec = mongo.getCollection("heart");
		return collec;
	}
}
