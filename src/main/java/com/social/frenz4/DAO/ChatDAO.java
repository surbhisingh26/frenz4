package com.social.frenz4.DAO;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongojack.JacksonDBCollection;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.social.frenz4.model.Chat;
import com.social.scframework.service.DBConnection;

public class ChatDAO {

	public JacksonDBCollection<Chat,String> chatDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("frenz4");
		DBCollection collec = mongo.getCollection("chat");
		JacksonDBCollection<Chat, String> coll = JacksonDBCollection.wrap(collec,Chat.class, String.class);
		return coll;
	}
	public DBCollection chatCollectionDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("frenz4");
		DBCollection collec = mongo.getCollection("chat");
		
		return collec;
	}
public List<Object> singleAggregation(String db1, int limit, ObjectId match, String matchField){
		
List<Object> chatList = new ArrayList<Object>();
	
System.out.println("Step1 service");
	List<DBObject> pipeline = new ArrayList<DBObject>();
	DBObject matchRecieverId = new BasicDBObject("$match",
			new BasicDBObject(matchField , match)

			);
	DBObject matchDelivered = new BasicDBObject("$match",
			new BasicDBObject("delivered" , false)

			);
	System.out.println("Step2 service");
	pipeline.add(matchRecieverId);
	pipeline.add(matchDelivered);
	System.out.println("Step3 service");
	DBObject lookupFields = new BasicDBObject("from", "user");
	lookupFields.put("localField","senderId");
	lookupFields.put("foreignField","_id");
	lookupFields.put("as", "friend");  
	pipeline.add(new BasicDBObject("$lookup",lookupFields));
	System.out.println("Step4 service");
	//DBObject unwindFriend = new BasicDBObject("$unwind","$friend");
	//pipeline.add(unwindFriend);
	AggregationOutput output = chatCollectionDAO().aggregate(pipeline);
	System.out.println("Step5 service");
	for (DBObject result : output.results()) {
		
		chatList.add(result);
		System.out.println(result);

	}
		return chatList;
	}
}
