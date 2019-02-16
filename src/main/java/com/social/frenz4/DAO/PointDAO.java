package com.social.frenz4.DAO;

import java.util.ArrayList;
import java.util.List;

import org.mongojack.JacksonDBCollection;

import com.mongodb.AggregationOptions;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.social.frenz4.app.PropertiesApp;
import com.social.frenz4.model.Points;
import com.social.scframework.service.DBConnection;

public class PointDAO {
	PropertiesApp prop = new PropertiesApp();
	String DBName = prop.getProperty("DbName");
	String DBPortStr = prop.getProperty("DbPort");
	int DBPort = Integer.parseInt(DBPortStr);
	String DBHost = prop.getProperty("DbHost");
	
	public JacksonDBCollection<Points,String> pointDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB(DBName,DBPort,DBHost);
		DBCollection collec = mongo.getCollection("points");
		JacksonDBCollection<Points, String> coll = JacksonDBCollection.wrap(collec,Points.class, String.class);
		return coll;
	}
	public DBCollection pointCollectionDAO(){
		DBConnection db = new DBConnection();
		DB mongo = db.getDB(DBName,DBPort,DBHost);
		DBCollection collec = mongo.getCollection("points");
		//JacksonDBCollection<Friend, String> coll = JacksonDBCollection.wrap(collec,Friend.class, String.class);
		return collec;
	}
	
	public List<Object> singleAggregation(String dbName,int sortOrder, int limit, int skip, String sortBy){
		List<Object> referralList = new ArrayList<Object>();
		List<DBObject> pipeline = new ArrayList<DBObject>();
		
		DBObject lookupFields = new BasicDBObject("from", dbName);
		lookupFields.put("localField","uid");
		lookupFields.put("foreignField","_id");
		lookupFields.put("as", "user");  
		pipeline.add(new BasicDBObject("$lookup",lookupFields));
		
		DBObject project = new BasicDBObject("$project",
				new BasicDBObject("uid", 1).append("_id", 1).append("date", 1).append("pointReason", 1).append("pointsEarn", 1)
				.append("user.name", 1).append("user.email", 1)
					);
		pipeline.add(project);
		DBObject sort = new BasicDBObject("$sort",
				new BasicDBObject(sortBy,sortOrder));
		
		pipeline.add(sort);
		
		DBObject skipTo = new BasicDBObject("$skip",skip);
		pipeline.add(skipTo);
		DBObject limitCount = new BasicDBObject("$limit",limit);
		pipeline.add(limitCount);
		
	//	AggregationOutput output = pointCollectionDAO().aggregate(pipeline);
		AggregationOptions options = AggregationOptions.builder().outputMode(AggregationOptions.OutputMode.CURSOR).allowDiskUse(Boolean.TRUE).batchSize(1000).build();
		final Cursor output = pointCollectionDAO().aggregate(pipeline, options);
		while (output.hasNext()){
		Object result = output.next();
		
			referralList.add(result);
		}
		return referralList;
	}

}
