package com.social.frenz4.service;


import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BasicBSONObject;
import org.bson.types.ObjectId;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import com.mongodb.AggregationOptions;


import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import com.social.frenz4.DAO.ActivityDAO;
import com.social.frenz4.DAO.ChatDAO;
import com.social.frenz4.DAO.FriendDAO;
import com.social.frenz4.DAO.HeartDAO;
import com.social.frenz4.DAO.UserDAO;
import com.social.frenz4.model.Activity;
import com.social.frenz4.model.Chat;
import com.social.frenz4.model.Friend;
import com.social.frenz4.model.Heart;
import com.social.frenz4.model.User;

public class FriendService {
	

	public void beFriend(ObjectId uid, ObjectId fid) {
		System.out.println("beFriend");
		FriendDAO frienddao = new FriendDAO();
		JacksonDBCollection<Friend, String> friendCollection = frienddao.friendDAO();
		ActivityDAO activitydao = new ActivityDAO();
		JacksonDBCollection<Activity, String> activityCollection =  activitydao.activityDAO();

		Date requestDate = new Date();

		Friend user = new Friend();
		user.setUid(uid);
		user.setFid(fid);
		user.setRequestDate(requestDate);
		user.setStatus("Request Sent");
		user.setFriends(false);
		WriteResult<Friend,String> usr = friendCollection.insert(user);
		user = usr.getSavedObject();
		Friend friend = new Friend();
		friend.setFid(uid);
		friend.setUid(fid);
		friend.setRequestDate(requestDate);
		friend.setStatus("Pending Request");
		friend.setFriends(false);
		friendCollection.insert(friend);

		Activity activity = new Activity();
		activity.setUid(uid);
		activity.setTypeId(new ObjectId(user.getId()));
		activity.setDate(requestDate);
		activity.setType("befriend");
		activity.setViewfalse(false);
		activity.setDeleted(false);
		activityCollection.insert(activity);
	}
	public String checkStatus(ObjectId uid, ObjectId objectId) {
		FriendDAO frienddao = new FriendDAO();
		JacksonDBCollection<Friend, String> friendCollection = frienddao.friendDAO();
		BasicDBObject query = new BasicDBObject();
		query.put("uid",uid);
		query.put("fid",objectId);
		DBCursor<Friend> cursor = friendCollection.find(query);
		if(cursor.hasNext()){
			return cursor.next().getStatus();
		}
		else{
			query.replace("uid",objectId);
			query.replace("fid",uid);
			if(cursor.hasNext()){
				return cursor.next().getStatus();
			}
		}

		return "Not Friends";
	}
	public void friendResponse(ObjectId uid, ObjectId fid, String requestResponse) {
		FriendDAO frienddao = new FriendDAO();
		JacksonDBCollection<Friend, String> friendCollection = frienddao.friendDAO();
		Date date = new Date();
		BasicDBObject query = new BasicDBObject();
		query.put("uid", uid);
		query.put("fid", fid);
		query.put("status", "Pending Request");
		DBCursor<Friend> cursor = friendCollection.find(query);
		if(cursor.hasNext()){
			Friend friend = cursor.next();
			if(requestResponse.equalsIgnoreCase("Accept")){
				friend.setStatus("I Accepted Request");
				friend.setFriends(true);
				friend.setResponseDate(date);
			}
			else if(requestResponse.equalsIgnoreCase("Reject"))
				friend.setStatus("I Rejected Request");
			friendCollection.updateById(friend.getId(),friend);
		}
		query.replace("uid", fid);
		query.replace("fid", uid);
		query.replace("status", "Request Sent");
		DBCursor<Friend> cursor1 = friendCollection.find(query);
		if(cursor1.hasNext()){
			Friend friend = cursor1.next();
			if(requestResponse.equalsIgnoreCase("Accept")){
				friend.setStatus("My Request Accepted");
				friend.setFriends(true);
				friend.setResponseDate(date);
			}
			else if(requestResponse.equalsIgnoreCase("Reject"))
				friend.setStatus("My Request Rejected");
			friendCollection.updateById(friend.getId(),friend);
		}

	}
	public void cancelRequest(ObjectId uid, ObjectId fid) {
		FriendDAO frienddao = new FriendDAO();
		JacksonDBCollection<Friend, String> friendCollection = frienddao.friendDAO();
		BasicDBObject query = new BasicDBObject();
		query.put("uid",uid);
		query.put("fid", fid);
		query.put("status", "Request Sent");
		friendCollection.remove(query);
		query.replace("uid", fid);
		query.replace("fid", uid);
		query.replace("status", "Pending Request");
		friendCollection.remove(query);

	}
	public void removeFriend(ObjectId uid, ObjectId fid) {
		FriendDAO frienddao = new FriendDAO();
		JacksonDBCollection<Friend, String> friendCollection = frienddao.friendDAO();
		BasicDBObject query = new BasicDBObject();
		query.put("uid",uid);
		query.put("fid", fid);
		query.put("friends", true);
		if(friendCollection.find(query).hasNext())
			friendCollection.remove(query);

		query.replace("uid", fid);
		query.replace("fid", uid);
		query.replace("friends", true);
		if(friendCollection.find(query).hasNext())
			friendCollection.remove(query);

	}
	public List<Object> getFriends(ObjectId uid,int limit) {
		FriendDAO frienddao = new FriendDAO();
		
		List<Object> friendList = frienddao.doubleAggregation("user","userinfo",limit,uid,"uid");

		return friendList;
	}
	public List<User> getFriendRequests(ObjectId uid) {
		FriendDAO frienddao = new FriendDAO();
		JacksonDBCollection<Friend, String> friendCollection = frienddao.friendDAO();
		BasicDBObject query = new BasicDBObject();
		List<User> requestList = new ArrayList<User>();
		query.put("uid", uid);
		query.put("status", "Pending Request");
		DBCursor<Friend> cursor = friendCollection.find(query);
		while(cursor.hasNext()){
			Friend friend = cursor.next();
			UserService userService = new UserService();
			User user = userService.findOneById(friend.getFid().toString());
			requestList.add(user);
		}
		return requestList;

	}
	public Map<String,Object> friendsActivity(ObjectId uid) {
		UserDAO userdao = new UserDAO();
		
		Map<String,Object> hmap = new HashMap<String, Object>();
		final DBCollection coll = userdao.userCollectionDAO();
		int count = 1;
		
		List<Object>activityList = new ArrayList<Object>();

		List<DBObject> pipeline = new ArrayList<DBObject>();
		DBObject match = new BasicDBObject("$match",
				new BasicDBObject("_id" , uid));

		pipeline.add(match);

		DBObject friendFields = new BasicDBObject("from", "friend");
		friendFields.put("localField","_id");
		friendFields.put("foreignField","uid");
		friendFields.put("as", "friend");
		pipeline.add(new BasicDBObject("$lookup",friendFields));
		DBObject unwindFriend = new BasicDBObject("$unwind","$friend");
		pipeline.add(unwindFriend);
		
		DBObject matchfriend = new BasicDBObject("$match",
				new BasicDBObject("friend.friends" , true));		
		pipeline.add(matchfriend);

		DBObject friendInfoFields = new BasicDBObject("from", "user");
		friendInfoFields.put("localField","friend.fid");
		friendInfoFields.put("foreignField","_id");
		friendInfoFields.put("as", "friendInfo");
		pipeline.add(new BasicDBObject("$lookup",friendInfoFields));


		DBObject activityFields = new BasicDBObject("from", "activity");
		activityFields.put("localField","friend.fid");
		activityFields.put("foreignField","uid");
		activityFields.put("as", "activity");
		pipeline.add(new BasicDBObject("$lookup",activityFields));
		DBObject unwindActivity = new BasicDBObject("$unwind","$activity");
		pipeline.add(unwindActivity);
		DBObject matchactivity = new BasicDBObject("$match",
				new BasicDBObject("activity.deleted" , false));		
		pipeline.add(matchactivity);
		DBObject uploadpicFields = new BasicDBObject("from", "uploadpic");
		uploadpicFields.put("localField","activity.typeId");
		uploadpicFields.put("foreignField","_id");
		uploadpicFields.put("as", "uploadpic");
		pipeline.add(new BasicDBObject("$lookup",uploadpicFields));

		DBObject profilepicFields = new BasicDBObject("from", "profilepic");
		profilepicFields.put("localField","activity.typeId");
		profilepicFields.put("foreignField","_id");
		profilepicFields.put("as", "profilepic");
		pipeline.add(new BasicDBObject("$lookup",profilepicFields));
		
		DBObject statusFields = new BasicDBObject("from", "status");
		statusFields.put("localField","activity.typeId");
		statusFields.put("foreignField","_id");
		statusFields.put("as", "status");
		pipeline.add(new BasicDBObject("$lookup",statusFields));		
		
		DBObject heartFields = new BasicDBObject("from", "heart");
		heartFields.put("localField","activity.typeId");
		heartFields.put("foreignField","typeId");
		heartFields.put("as", "heart");
		pipeline.add(new BasicDBObject("$lookup",heartFields));
		
		 List<Object> eqOperands = new ArrayList<Object>();
	        eqOperands.add("$heart.fid");
	        eqOperands.add(uid);
		
		DBObject project = new BasicDBObject("$project",
			new BasicDBObject("name", 1).append("email", 1).append("imagepath", 1).append("friend.uid", 1).append("friend._id", 1)
			.append("friend.fid", 1).append("friend.friends", 1).append("friendInfo.name", 1).append("friendInfo._id", 1)
			.append("friendInfo.email", 1).append("friendInfo.imagepath", 1).append("activity._id", 1).append("activity.uid", 1)
			.append("activity.type", 1).append("activity.date", 1).append("activity.typeId", 1).append("activity.hearts", 1)
			.append("activity.comments", 1).append("activity.heartBreaks", 1).append("uploadpic._id", 1).append("uploadpic.uid", 1)
			.append("uploadpic.path", 1).append("profilepic._id", 1).append("profilepic.uid", 1).append("profilepic.path", 1)
			.append("profilepic.current", 1).append("heart._id", 1).append("heart.fid",1).append("heart.typeId", 1)
			.append("heart.broken", 1).append("status.statusText", 1).append("status._id", 1)
			
			
				);
		
		pipeline.add(project);
		DBObject sort = new BasicDBObject("$sort",
				new BasicDBObject("activity.date",-1));
		
		pipeline.add(sort);
	//	System.out.println(pipeline);
	//  DBObject explain = new BasicDBObject("$explain",true);
	//	pipeline.add(explain);
		//AggregateOperation options = null;
	//	Aggregation output = coll.aggregate(pipeline);
		
		AggregationOptions options = AggregationOptions.builder().outputMode(AggregationOptions.OutputMode.CURSOR).allowDiskUse(Boolean.TRUE).batchSize(1000).build();
		final Cursor dbCursor = coll.aggregate(pipeline, options);
		System.out.println(dbCursor);

		//AggregationOptions ao =  new AggregationOptions();
		//Cursor output = coll.aggregate(pipeline,ao, ReadPreference.primary());
		while(dbCursor.hasNext()) {
			Object result = dbCursor.next();
			System.out.println("test "+result);	
			List<Object> res = (List<Object>) ((BasicBSONObject) result).get("heart");
			if(!res.isEmpty()){
				
				((HashMap<String, Object>) result).put("noAction",true);
				for(Object db:res){
					//System.out.println(db);
					BasicDBObject object = (BasicDBObject) db;
					object.get("fid");
					
				if(object.get("fid").toString().equals(uid.toString())){
					
					((HashMap<String, Object>) result).put("broken",object.get("broken"));
					((HashMap<String, Object>) result).put("noAction",false);
					break;
				}
					
				}									
			}
			else{
				((HashMap<String, Object>) result).put("noAction",true);
				
			}			
			Object friend = (Object) ((BasicBSONObject) result).get("friend");
			BasicDBObject fr = (BasicDBObject)friend;
			if(fr.get("uid").equals(fr.get("fid"))){			
				((HashMap<String, Object>) result).put("myactivity", true);
			}
			
			
			if(count%2==0)
				((HashMap<String, Object>) result).put("left", false);
			else
				((HashMap<String, Object>) result).put("left", true);
			activityList.add(result);
			System.out.println(result);
			count++;
		}
		
		
		
		
		
		/*	for (DBObject result : output.results()) {
			//System.out.println(result.get("heart"));
			@SuppressWarnings("unchecked")
			List<Object> res = (List<Object>) result.get("heart");
			
			if(!res.isEmpty()){
				
				result.put("noAction",true);
				for(Object db:res){
					//System.out.println(db);
					BasicDBObject object = (BasicDBObject) db;
					object.get("fid");
					
				if(object.get("fid").toString().equals(uid.toString())){
					
					result.put("broken",object.get("broken"));
					result.put("noAction",false);
					break;
				}
					
				}									
			}
			else{
				result.put("noAction",true);
				
			}			
			Object friend = (Object) result.get("friend");
			BasicDBObject fr = (BasicDBObject)friend;
			if(fr.get("uid").equals(fr.get("fid"))){			
				result.put("myactivity", true);
			}
			
			
			if(count%2==0)
				result.put("left", false);
			else
				result.put("left", true);
			activityList.add(result);
			System.out.println(result);
			count++;
		}*/
		//System.out.println("SIZE IS ......................" +activityList.size());
		
		
		hmap.put("activityList", activityList);
		return hmap;
	}
	public Map<String,Object> heartFriend(ObjectId typeId,int skip,int limit,Boolean broken) {
		Map<String,Object> hmap = new HashMap<String, Object>();
		List<Object>friendList = new ArrayList<Object>();
		
		HeartDAO heartdao = new HeartDAO();
		DBCollection heartCollection = heartdao.heartCollectionDAO();
		JacksonDBCollection<Heart, String> heartColl = heartdao.heartDAO();
		List<DBObject> pipeline = new ArrayList<DBObject>();
		DBObject match = new BasicDBObject("$match",
				new BasicDBObject("typeId" , typeId));
		
		pipeline.add(match);
		DBObject matchHeart = new BasicDBObject("$match",
				new BasicDBObject("broken" , broken));
		
		pipeline.add(matchHeart);
		
		DBObject friendFields = new BasicDBObject("from", "user");
		friendFields.put("localField","fid");
		friendFields.put("foreignField","_id");
		friendFields.put("as", "friend");
		pipeline.add(new BasicDBObject("$lookup",friendFields));
		DBObject unwindFriend = new BasicDBObject("$unwind","$friend");
		pipeline.add(unwindFriend);
		DBObject skipTo = new BasicDBObject("$skip",skip);
		pipeline.add(skipTo);
		DBObject limitCount = new BasicDBObject("$limit",limit);
		pipeline.add(limitCount);
		//AggregationOutput output = heartCollection.aggregate(pipeline);
		AggregationOptions options = AggregationOptions.builder().outputMode(AggregationOptions.OutputMode.CURSOR).allowDiskUse(Boolean.TRUE).batchSize(1000).build();
		final Cursor output = heartCollection.aggregate(pipeline, options);
		while(output.hasNext()){
		Object result = output.next();
		friendList.add(result);
		System.out.println(result);		
	}
		BasicDBObject query = new BasicDBObject();
		query.put("typeId", typeId);
		query.put("broken", broken);
		
		if(skip+limit<heartColl.count(query)){
			hmap.put("showMore", true);
		}
		else
		{
			hmap.put("showMore", false);
		}
		
		hmap.put("skip", skip+limit);
		hmap.put("friendList", friendList);
		return hmap;
	}
	public Map<String,Object> onlineFriends(ObjectId uid) {
		UserDAO userdao = new UserDAO();
		JacksonDBCollection<User, String> userCollection =  userdao.userDAO();
		FriendDAO frienddao = new FriendDAO();
		JacksonDBCollection<Friend, String> friendCollection = frienddao.friendDAO();
		ChatDAO chatdao = new ChatDAO();
		JacksonDBCollection<Chat, String> chatCollection = chatdao.chatDAO();
		Map<String,Object> hmap = new HashMap<String, Object>();
		List<User> friendList = new ArrayList<User>();
		BasicDBObject query = new BasicDBObject();
		query.put("uid", uid);
		query.put("friends", true);
		DBCursor<Friend> cursor = friendCollection.find(query);
		System.out.println("Friend finding");
		while(cursor.hasNext()){
			Friend friend = cursor.next();
			System.out.println("Friend found " + friend.getFid());
			User user = userCollection.findOneById(friend.getFid().toString());
			System.out.println("User " + user.getName() + user.getLoggedIn());
			if(user.getLoggedIn()){
				
				BasicDBObject chatquery = new BasicDBObject();
				chatquery.put("recieverId", uid);
				chatquery.put("senderId", new ObjectId(user.getId()));
				System.out.println("uid "+uid);
				System.out.println("fid "+user.getId());
				//db.chat.find({ "$or" : [ { "$and" : [ { "recieverId" : ObjectId("5af547cde2e9a70900b73338") , "senderId" : ObjectId("5af16a61e2e9a708c090917e")}]},{ "$and" : [ { "senderId" : ObjectId("5af547cde2e9a70900b73338") , "recieverId" : ObjectId("5af16a61e2e9a708c090917e")}]}] , "delivered" : true}).pretty()
				
				chatquery.put("delivered", true);
				chatquery.put("read", false);
				long chats = chatCollection.find(chatquery).count();
				System.out.println("Counts are " + chats);
				user.setChat(chats);
				friendList.add(user);
				
			}
		}
		
		hmap.put("friendList", friendList);
		return hmap;
	}	


}

