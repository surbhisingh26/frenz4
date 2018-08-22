package com.social.frenz4.tools;

import org.bson.types.ObjectId;

import com.social.frenz4.service.FriendService;
import com.social.frenz4.service.UserService;

public class DbAggregationTool {
public static void main(String args[]){
	String uid = "5b3789fb949e3d11643f72c1";
	FriendService friendService = new FriendService();
	UserService userService = new UserService();
	//userService.myActivity(new ObjectId(uid));
//	friendService.friendsActivity(new ObjectId(uid));
	//userService.getRecentChats(new ObjectId(uid));
	userService.getChat(new ObjectId(uid),new ObjectId("5b7c07a85854610ac8ec4cf6"));
	//friendService.heartFriend(new ObjectId(uid));
	//userService.showComments(new ObjectId(uid),0,5);
	//friendService.heartFriend(new ObjectId(uid),0,5, false);
	//FriendDAO dao = new FriendDAO();
	//dao.doubleAggregation("user", "userinfo", 10, new ObjectId(uid), "uid");
}
}
