package com.social.beFriendly.DAO;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.social.beFriendly.model.Chat;
import com.social.scframework.service.DBConnection;

public class ChatDAO {

	public JacksonDBCollection<Chat,String> chatDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("BeFriendly");
		DBCollection collec = mongo.getCollection("chat");
		JacksonDBCollection<Chat, String> coll = JacksonDBCollection.wrap(collec,Chat.class, String.class);
		return coll;
	}
	public DBCollection chatCollectionDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("BeFriendly");
		DBCollection collec = mongo.getCollection("chat");
		//JacksonDBCollection<Friend, String> coll = JacksonDBCollection.wrap(collec,Friend.class, String.class);
		return collec;
	}
}
