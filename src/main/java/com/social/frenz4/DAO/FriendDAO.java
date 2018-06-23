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
import com.social.frenz4.model.Friend;
import com.social.scframework.service.DBConnection;

public class FriendDAO {
	
	public JacksonDBCollection<Friend,String> friendDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("frenz4");
		DBCollection collec = mongo.getCollection("friend");
		JacksonDBCollection<Friend, String> coll = JacksonDBCollection.wrap(collec,Friend.class, String.class);
		return coll;
	}
	public DBCollection frndCollectionDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("frenz4");
		DBCollection collec = mongo.getCollection("friend");
		//JacksonDBCollection<Friend, String> coll = JacksonDBCollection.wrap(collec,Friend.class, String.class);
		return collec;
	}
	public List<Object> doubleAggregation(String db1, String db2, int limit, ObjectId match, String matchField){
		
		List<Object> friendList = new ArrayList<Object>();
		
		List<DBObject> pipeline = new ArrayList<DBObject>();
		DBObject matchfid = new BasicDBObject("$match",
				new BasicDBObject(matchField , match)

				);
		DBObject matchfriend = new BasicDBObject("$match",
				new BasicDBObject("friends" , true)

				);
		pipeline.add(matchfid);
		pipeline.add(matchfriend);
		DBObject lookupFields = new BasicDBObject("from", db1);
		lookupFields.put("localField","fid");
		lookupFields.put("foreignField","_id");
		lookupFields.put("as", "friend");  
		pipeline.add(new BasicDBObject("$lookup",lookupFields));
		DBObject unwindFriend = new BasicDBObject("$unwind","$friend");
		pipeline.add(unwindFriend);
		
		DBObject secondFields = new BasicDBObject("from", db2);
		secondFields.put("localField","fid");
		secondFields.put("foreignField","uid");
		secondFields.put("as", "friendinfo");  
		pipeline.add(new BasicDBObject("$lookup",secondFields));
		DBObject limitCount = new BasicDBObject("$limit",limit);
		pipeline.add(limitCount);
		AggregationOutput output = frndCollectionDAO().aggregate(pipeline);

		for (DBObject result : output.results()) {
			friendList.add(result);
			System.out.println(result);

		}
		return friendList;
	}
}
