package com.social.beFriendly.service;


import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.bson.types.ObjectId;
import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.social.beFriendly.DAO.ActivityDAO;

import com.social.beFriendly.DAO.FriendDAO;
import com.social.beFriendly.DAO.HeartDAO;
import com.social.beFriendly.DAO.ProfilePicDAO;
import com.social.beFriendly.DAO.UserDAO;
import com.social.beFriendly.model.Activity;

import com.social.beFriendly.model.Friend;
import com.social.beFriendly.model.ProfilePic;
import com.social.beFriendly.model.User;

public class FriendService {
	FriendDAO frienddao = new FriendDAO();
	JacksonDBCollection<Friend, String> friendCollection = frienddao.friendDAO();

	UserDAO userdao = new UserDAO();
	JacksonDBCollection<User, String> userCollection =  userdao.userDAO();

	ProfilePicDAO profilepicdao = new ProfilePicDAO();
	JacksonDBCollection<ProfilePic, String> dpCollection =  profilepicdao.profilePicDAO();

	ActivityDAO activitydao = new ActivityDAO();
	JacksonDBCollection<Activity, String> activityCollection =  activitydao.activityDAO();

	public void beFriend(ObjectId uid, ObjectId fid) {
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
		activity.setActivityId(new ObjectId(user.getId()));
		activity.setDate(requestDate);
		activity.setType("befriend");
		activityCollection.insert(activity);
	}
	public String checkStatus(ObjectId uid, ObjectId objectId) {

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
	public List<User> getFriends(ObjectId uid) {
		BasicDBObject query = new BasicDBObject();
		UserService userservice = new UserService();
		List<User> friendList = new ArrayList<User>();
		query.put("uid", uid);
		query.put("friends", true);
		DBCursor<Friend> cursor = friendCollection.find(query);
		while(cursor.hasNext()){
			Friend friend = cursor.next();
			System.out.println("Friends id in getFriends is " + friend.getFid());
			System.out.println("Friends id in getFriends in string form " + friend.getFid().toString());
			User user = userservice.findOneById(friend.getFid().toString());
			friendList.add(user);
		}

		return friendList;
	}
	public List<User> getFriendRequests(ObjectId uid) {
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
		
		Map<String,Object> hmap = new HashMap<String, Object>();
		DBCollection coll = userdao.userCollectionDAO();
		int count = 1;
	//	HeartDAO heartdao = new HeartDAO();
	//	JacksonDBCollection<Heart, String> heartCollection = heartdao.heartDAO();
		
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


		DBObject uploadpicFields = new BasicDBObject("from", "uploadpic");
		uploadpicFields.put("localField","activity.activityId");
		uploadpicFields.put("foreignField","_id");
		uploadpicFields.put("as", "uploadpic");
		pipeline.add(new BasicDBObject("$lookup",uploadpicFields));

		DBObject profilepicFields = new BasicDBObject("from", "profilepic");
		profilepicFields.put("localField","activity.activityId");
		profilepicFields.put("foreignField","_id");
		profilepicFields.put("as", "profilepic");
		pipeline.add(new BasicDBObject("$lookup",profilepicFields));
		
		
		
		DBObject heartFields = new BasicDBObject("from", "heart");
		heartFields.put("localField","activity.activityId");
		heartFields.put("foreignField","activityId");
		heartFields.put("as", "heart");
		pipeline.add(new BasicDBObject("$lookup",heartFields));
		//DBObject unwind = new BasicDBObject("$unwind","$heart");
		//pipeline.add(unwind);
		/*DBObject matchheart = new BasicDBObject("$match",
				new BasicDBObject("heart.fid" , uid));		
		pipeline.add(matchheart);*/
		/*DBObject heartsFields = new BasicDBObject("from", "heart");
		heartsFields.put("localField","heart.fid");
		heartsFields.put("foreignField","fid");
		heartsFields.put("as", "hearts");
		pipeline.add(new BasicDBObject("$lookup",heartsFields));*/
		
		DBObject project = new BasicDBObject("$project",
			new BasicDBObject("name", 1).append("email", 1).append("imagepath", 1).append("friend.uid", 1).append("friend._id", 1)
			.append("friend.fid", 1).append("friend.friends", 1).append("friendInfo.name", 1).append("friendInfo._id", 1)
			.append("friendInfo.email", 1).append("friendInfo.imagepath", 1).append("activity._id", 1).append("activity.uid", 1)
			.append("activity.type", 1).append("activity.date", 1).append("activity.activityId", 1).append("activity.hearts", 1)
			.append("activity.comments", 1).append("activity.heartBreaks", 1).append("uploadpic._id", 1).append("uploadpic.uid", 1)
			.append("uploadpic.path", 1).append("profilepic._id", 1).append("profilepic.uid", 1).append("profilepic.path", 1)
			.append("profilepic.current", 1).append("heart._id", 1).append("heart.fid", 1).append("heart.activityId", 1).
			append("heart.broken", 1)
				);
		
		/*DBObject project = new BasicDBObject("$project",
				new BasicDBObject("heart.fid", uid));*/
		pipeline.add(project);
		DBObject sort = new BasicDBObject("$sort",
				new BasicDBObject("activity.date",-1));
		
		pipeline.add(sort);

		System.out.println(pipeline);

		AggregationOutput output = coll.aggregate(pipeline);
		
		for (DBObject result : output.results()) {
			
				
			activityList.add(result);
			if(count%2==0)
				result.put("left", false);
			else
				result.put("left", true);
			
			System.out.println(result);
			count++;
		}
		System.out.println("SIZE IS ......................" +activityList.size());
		
		
		hmap.put("activityList", activityList);
		return hmap;
	}
	public Map<String,Object> heartFriend(ObjectId activityId) {
		Map<String,Object> hmap = new HashMap<String, Object>();
		List<Object>friendList = new ArrayList<Object>();

		HeartDAO heartdao = new HeartDAO();
		DBCollection heartCollection = heartdao.heartCollectionDAO();
		List<DBObject> pipeline = new ArrayList<DBObject>();
		DBObject match = new BasicDBObject("$match",
				new BasicDBObject("activityId" , activityId));
		
		pipeline.add(match);
		
		DBObject friendFields = new BasicDBObject("from", "user");
		friendFields.put("localField","fid");
		friendFields.put("foreignField","_id");
		friendFields.put("as", "friend");
		pipeline.add(new BasicDBObject("$lookup",friendFields));
		DBObject unwindFriend = new BasicDBObject("$unwind","$friend");
		pipeline.add(unwindFriend);
		AggregationOutput output = heartCollection.aggregate(pipeline);
		for (DBObject result : output.results()) {
		
		friendList.add(result);
		System.out.println(result);
		
	}
		return hmap;
	}	


}

