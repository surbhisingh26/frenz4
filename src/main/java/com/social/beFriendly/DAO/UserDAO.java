package com.social.beFriendly.DAO;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.social.beFriendly.model.User;
import com.social.scframework.service.DBConnection;

public class UserDAO {
	public JacksonDBCollection<User,String> userDAO(){
	DBConnection db = new DBConnection();
	DB mongo = db.getDB("BeFriendly");
	DBCollection collec = mongo.getCollection("user");
	JacksonDBCollection<User, String> coll = JacksonDBCollection.wrap(collec,User.class, String.class);
	return coll;
}
	public DBCollection userCollectionDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("BeFriendly");
		DBCollection collec = mongo.getCollection("user");
		
		return collec;
	}
}
