package com.social.beFriendly.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;

import com.mongodb.BasicDBObject;
import com.social.beFriendly.DAO.FriendDAO;
import com.social.beFriendly.DAO.UserDAO;
import com.social.beFriendly.model.Friend;
import com.social.beFriendly.model.User;

public class FriendService {
	FriendDAO frienddao = new FriendDAO();
	JacksonDBCollection<Friend, String> friendCollection = frienddao.friendDAO();

	UserDAO userdao = new UserDAO();
	JacksonDBCollection<User, String> userCollection =  userdao.userDAO();

	public void beFriend(String uid, String fid) {
		Date requestDate = new Date();

		Friend user = new Friend();
		user.setUid(uid);
		user.setFid(fid);
		user.setRequestDate(requestDate);
		user.setStatus("Request Sent");
		friendCollection.insert(user);
		Friend friend = new Friend();
		friend.setFid(uid);
		friend.setUid(fid);
		friend.setRequestDate(requestDate);
		friend.setStatus("Pending Request");
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
			if(requestResponse.equalsIgnoreCase("Accept"))
				friend.setStatus("I Accepted Request");
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
			if(requestResponse.equalsIgnoreCase("Accept"))
				friend.setStatus("My Request Accepted");
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
		query.put("status", "My Request Accepted");
		if(friendCollection.find(query).hasNext())
			friendCollection.remove(query);
		else
		{
			query.replace("status", "I Accepted Request");
			friendCollection.remove(query);
		}
		query.replace("uid", fid);
		query.replace("fid", uid);
		query.replace("status", "My Request Accepted");
		if(friendCollection.find(query).hasNext())
			friendCollection.remove(query);
		else
		{
			query.replace("status", "I Accepted Request");
			friendCollection.remove(query);
		}
	}
	public List<User> getFriends(String uid) {
		BasicDBObject query = new BasicDBObject();
		UserService userservice = new UserService();
		List<User> friendList = new ArrayList<User>();
		query.put("uid", uid);
		query.put("status", "I Accepted Request");
		DBCursor<Friend> cursor = friendCollection.find(query);
		while(cursor.hasNext()){
			Friend friend = cursor.next();
			User user = userservice.findOneById(friend.getFid());
			friendList.add(user);
		}
		query.put("status", "My Request Accepted");
		DBCursor<Friend> cursor1 = friendCollection.find(query);
		while(cursor1.hasNext()){
			Friend friend = cursor1.next();
			User user = userservice.findOneById(friend.getFid());
			friendList.add(user);
		}
		return friendList;
	}

}
