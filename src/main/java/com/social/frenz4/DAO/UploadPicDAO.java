package com.social.frenz4.DAO;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.social.frenz4.model.UploadPic;
import com.social.scframework.service.DBConnection;

public class UploadPicDAO {
	public JacksonDBCollection<UploadPic,String> uploadPicDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("frenz4");
		DBCollection collec = mongo.getCollection("uploadpic");
		JacksonDBCollection<UploadPic, String> coll = JacksonDBCollection.wrap(collec,UploadPic.class, String.class);
		return coll;
	}
	public DBCollection uploadCollectionDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("frenz4");
		DBCollection collec = mongo.getCollection("uploadpic");
		
		return collec;
	}
}
