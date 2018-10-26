package com.social.frenz4.DAO;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.social.frenz4.app.PropertiesApp;
import com.social.frenz4.model.Comment;
import com.social.scframework.service.DBConnection;

public class CommentDAO {
	PropertiesApp prop = new PropertiesApp();
	String DBName = prop.getProperty("DbName");
	String DBPortStr = prop.getProperty("DbPort");
	int DBPort = Integer.parseInt(DBPortStr);
	String DBHost = prop.getProperty("DbHost");
	
		public JacksonDBCollection<Comment, String> commentDAO(){
			DBConnection db = new DBConnection();
			DB mongo = db.getDB(DBName,DBPort,DBHost);
			DBCollection collec = mongo.getCollection("comment");
			JacksonDBCollection<Comment, String> coll = JacksonDBCollection.wrap(collec,Comment.class, String.class);
			return coll;
		}

		public DBCollection commentCollectionDAO() {
			DBConnection db = new DBConnection();
			DB mongo = db.getDB(DBName,DBPort,DBHost);
			DBCollection collec = mongo.getCollection("comment");
			return collec;
		}
}
