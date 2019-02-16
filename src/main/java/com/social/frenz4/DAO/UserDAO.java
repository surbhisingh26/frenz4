package com.social.frenz4.DAO;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.social.frenz4.app.PropertiesApp;
import com.social.frenz4.model.User;
import com.social.scframework.service.DBConnection;

public class UserDAO {
	PropertiesApp prop = new PropertiesApp();
	String DBName = prop.getProperty("DbName");
	String DBPortStr = prop.getProperty("DbPort");
	int DBPort = Integer.parseInt(DBPortStr);
	String DBHost = prop.getProperty("DbHost");
	
	public JacksonDBCollection<User,String> userDAO(){
	DBConnection db = new DBConnection();
	DB mongo = db.getDB(DBName,DBPort,DBHost);
	DBCollection collec = mongo.getCollection("user");
	JacksonDBCollection<User, String> coll = JacksonDBCollection.wrap(collec,User.class, String.class);
	return coll;
}
	public DBCollection userCollectionDAO(){
		DBConnection db = new DBConnection();
		
		DB mongo = db.getDB(DBName,DBPort,DBHost);
		DBCollection collec = mongo.getCollection("user");
	//	collec.addOption(1);
		
		return collec;
	}
}
