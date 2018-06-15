package com.social.beFriendly.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.social.beFriendly.DAO.UserDAO;
import com.social.beFriendly.app.RequestResponseUtility;
import com.social.beFriendly.service.FriendService;
import com.social.beFriendly.service.UserService;

public class QueryTool {
	public static void main(String args[]){
		ObjectId uid;

//		UserService userService = new UserService();
//		userService.getChat(new ObjectId("5af16a61e2e9a708c090917e"),new ObjectId("5af547cde2e9a70900b73338"));
		RequestResponseUtility rrutility = new RequestResponseUtility();
		Map<String, Object> hmap  = new HashMap<String, Object>();
		hmap.putAll(rrutility.getUserDetails(null));
		UserService userService = new UserService();
		uid = (ObjectId) hmap.get("uid");
		List<Object> friendList = new ArrayList<Object>();
		hmap.putAll(userService.myActivity(uid));
		FriendService friendService = new FriendService();
		friendList = friendService.getFriends(uid, 6);
		hmap.put("friendList", friendList);
	}
}
