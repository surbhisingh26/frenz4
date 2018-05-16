package com.social.beFriendly.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;

import com.mongodb.BasicDBObject;
import com.social.beFriendly.DAO.FriendDAO;
import com.social.beFriendly.DAO.ProfilePicDAO;
import com.social.beFriendly.DAO.UserDAO;
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

	public void beFriend(String uid, String fid) {
		Date requestDate = new Date();

		Friend user = new Friend();
		user.setUid(uid);
		user.setFid(fid);
		user.setRequestDate(requestDate);
		user.setStatus("Request Sent");
		user.setFriends(false);
		friendCollection.insert(user);
		Friend friend = new Friend();
		friend.setFid(uid);
		friend.setUid(fid);
		friend.setRequestDate(requestDate);
		friend.setStatus("Pending Request");
		user.setFriends(false);
		friendCollection.insert(friend);
	}
	public String checkStatus(String uid, String fid) {

		BasicDBObject query = new BasicDBObject();
		query.put("uid",uid);
		query.put("fid",fid);
		DBCursor<Friend> cursor = friendCollection.find(query);
		if(cursor.hasNext()){
			return cursor.next().getStatus();
		}
		else{
			query.replace("uid",fid);
			query.replace("fid",uid);
			if(cursor.hasNext()){
				return cursor.next().getStatus();
			}
		}

		return "Not Friends";
	}
	public void friendResponse(String uid, String fid, String requestResponse) {
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
			}
			else if(requestResponse.equalsIgnoreCase("Reject"))
				friend.setStatus("My Request Rejected");
			friendCollection.updateById(friend.getId(),friend);
		}

	}
	public void cancelRequest(String uid, String fid) {
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
	public void removeFriend(String uid, String fid) {
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
	public List<User> getFriends(String uid) {
		BasicDBObject query = new BasicDBObject();
		UserService userservice = new UserService();
		List<User> friendList = new ArrayList<User>();
		query.put("uid", uid);
		query.put("friends", true);
		DBCursor<Friend> cursor = friendCollection.find(query);
		while(cursor.hasNext()){
			Friend friend = cursor.next();
			User user = userservice.findOneById(friend.getFid());
			friendList.add(user);
		}
		
		return friendList;
	}
	public List<User> getFriendRequsets(String uid) {
		BasicDBObject query = new BasicDBObject();
		List<User> requestList = new ArrayList<User>();
		query.put("uid", uid);
		query.put("status", "Pending Request");
		DBCursor<Friend> cursor = friendCollection.find(query);
		while(cursor.hasNext()){
			Friend friend = cursor.next();
			UserService userService = new UserService();
			User user = userService.findOneById(friend.getFid());
			requestList.add(user);
		}
		return requestList;
		
	}
	public Map<String,Object> friendsActivity(String uid) {
		BasicDBObject query = new BasicDBObject();
		Map<String,Object> hmap = new HashMap<String, Object>();
		List<Object> activityList = new ArrayList<Object>();
		query.put("uid", uid);
		query.put("friends", true);
		DBCursor<Friend> cursor = friendCollection.find(query);
		while(cursor.hasNext()){
			Friend friend = cursor.next();
			BasicDBObject activity = new BasicDBObject();
			activity.put("uid", friend.getId());
			DBCursor<ProfilePic> profilepic = dpCollection.find(activity);
			while(profilepic.hasNext()){
				ProfilePic pic = profilepic.next();
				activityList.add(pic);
			}
			
			
			
		}
		hmap.put("activityList", activityList);
		return hmap;
	}

}
