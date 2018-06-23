package com.social.frenz4.DAO;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.social.frenz4.model.Notification;
import com.social.scframework.service.DBConnection;

public class NotificationDAO {
	public JacksonDBCollection<Notification,String> notificationDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("frenz4");
		DBCollection collec = mongo.getCollection("notification");
		JacksonDBCollection<Notification, String> coll = JacksonDBCollection.wrap(collec,Notification.class, String.class);
		return coll;
	}
}
