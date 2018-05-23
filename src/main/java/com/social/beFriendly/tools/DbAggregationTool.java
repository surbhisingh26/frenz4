package com.social.beFriendly.tools;

import org.bson.types.ObjectId;

import com.social.beFriendly.service.FriendService;
import com.social.beFriendly.service.UserService;

public class DbAggregationTool {
public static void main(String args[]){
	//FriendService friendService = new FriendService();
	UserService userService = new UserService();
	//userService.myActivity(new ObjectId("5af16a61e2e9a708c090917e"));
	//friendService.friendsActivity(new ObjectId("5af16a61e2e9a708c090917e"));
	userService.showComments(new ObjectId("5b03c1d5e2e9a71b90124a6e"),0,5);
}
}
