package com.social.frenz4.tools;

import org.bson.Document;
import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

import com.social.scframework.service.DBConnection;

public class CampainTool {
	public JacksonDBCollection<Document,String> campaignDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("abc",27017,"localhost");
		DBCollection collec = mongo.getCollection("campaign");
		JacksonDBCollection<Document, String> coll = JacksonDBCollection.wrap(collec,Document.class, String.class);
		return coll;
	}
	public JacksonDBCollection<Document,String> campaignStatusDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB("abc",27017,"localhost");
		DBCollection collec = mongo.getCollection("campaignStatus");
		JacksonDBCollection<Document, String> coll = JacksonDBCollection.wrap(collec,Document.class, String.class);
		return coll;
	}
	public void getTypeCount(){
		
		JacksonDBCollection<Document, String> coll = campaignDAO();
		JacksonDBCollection<Document, String> coll1 = campaignStatusDAO();
		DBCursor<Document> cursor = coll.find();
		while (cursor.hasNext()){
			Document camp = cursor.next();
			System.out.println(camp.get("cId"));
			BasicDBObject query = new BasicDBObject();
			query.put("campainId", camp.get("cId"));
		//	query.put("type", v)
			DBCursor<Document> cursor1 = coll.find(query);
			
			
		}
		
	}
	public static void main(String args[]){
		CampainTool campaintool =  new CampainTool();
		campaintool.getTypeCount();
	}
}
