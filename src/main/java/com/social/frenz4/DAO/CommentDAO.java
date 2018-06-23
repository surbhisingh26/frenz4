package com.social.frenz4.DAO;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.social.frenz4.model.Comment;
import com.social.scframework.service.DBConnection;

public class CommentDAO {
		public JacksonDBCollection<Comment, String> commentDAO(){
			DBConnection db = new DBConnection();
			DB mongo = db.getDB("frenz4");
			DBCollection collec = mongo.getCollection("comment");
			JacksonDBCollection<Comment, String> coll = JacksonDBCollection.wrap(collec,Comment.class, String.class);
			return coll;
		}

		public DBCollection commentCollectionDAO() {
			DBConnection db = new DBConnection();
			DB mongo = db.getDB("frenz4");
			DBCollection collec = mongo.getCollection("comment");
			return collec;
		}
}
