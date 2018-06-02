package com.social.beFriendly.DAO;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongojack.JacksonDBCollection;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
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
		
		return collec;
	}
public List<Object> singleAggregation(String db1, int limit, ObjectId match, String matchField){
		
List<Object> chatList = new ArrayList<Object>();
	
	
	List<DBObject> pipeline = new ArrayList<DBObject>();
	DBObject matchRecieverId = new BasicDBObject("$match",
			new BasicDBObject(matchField , match)

			);
	DBObject matchDelivered = new BasicDBObject("$match",
			new BasicDBObject("delivered" , false)

			);
	pipeline.add(matchRecieverId);
	pipeline.add(matchDelivered);
	DBObject lookupFields = new BasicDBObject("from", "user");
	lookupFields.put("localField","senderId");
	lookupFields.put("foreignField","_id");
	lookupFields.put("as", "friend");  
	pipeline.add(new BasicDBObject("$lookup",lookupFields));
	//DBObject unwindFriend = new BasicDBObject("$unwind","$friend");
	//pipeline.add(unwindFriend);
	AggregationOutput output = chatCollectionDAO().aggregate(pipeline);
	for (DBObject result : output.results()) {
		
		chatList.add(result);
		System.out.println(result);

	}
		return chatList;
	}
}
