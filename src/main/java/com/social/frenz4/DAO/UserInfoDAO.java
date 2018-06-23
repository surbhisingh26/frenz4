package com.social.frenz4.DAO;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.social.frenz4.model.UserInfo;
import com.social.scframework.service.DBConnection;

public class UserInfoDAO {

	public JacksonDBCollection<UserInfo,String> userInfoDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("frenz4");
		DBCollection collec = mongo.getCollection("userinfo");
		JacksonDBCollection<UserInfo, String> coll = JacksonDBCollection.wrap(collec,UserInfo.class, String.class);
		return coll;
	}
	public DBCollection userInfoCollectionDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("frenz4");
		DBCollection collec = mongo.getCollection("userinfo");
		//JacksonDBCollection<Friend, String> coll = JacksonDBCollection.wrap(collec,Friend.class, String.class);
		return collec;
	}
}
