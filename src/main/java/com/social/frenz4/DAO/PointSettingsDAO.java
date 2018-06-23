package com.social.frenz4.DAO;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.social.frenz4.model.PointSettings;
import com.social.scframework.service.DBConnection;

public class PointSettingsDAO {

	public JacksonDBCollection<PointSettings,String> pointSettingsDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("frenz4");
		DBCollection collec = mongo.getCollection("pointsettings");
		JacksonDBCollection<PointSettings, String> coll = JacksonDBCollection.wrap(collec,PointSettings.class, String.class);
		return coll;
	}
	public DBCollection pointSettingsCollectionDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("frenz4");
		DBCollection collec = mongo.getCollection("points");
		//JacksonDBCollection<Friend, String> coll = JacksonDBCollection.wrap(collec,Friend.class, String.class);
		return collec;
	}
}
