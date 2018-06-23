package com.social.frenz4.tools;

import org.bson.types.ObjectId;

import com.social.frenz4.DAO.FriendDAO;
import com.social.frenz4.service.FriendService;
import com.social.frenz4.service.UserService;

public class DbAggregationTool {
public static void main(String args[]){
	//FriendService friendService = new FriendService();
	//UserService userService = new UserService();
	//userService.myActivity(new ObjectId("5af16a61e2e9a708c090917e"));
	//friendService.friendsActivity(new ObjectId("5af16a61e2e9a708c090917e"));
	//friendService.heartFriend(new ObjectId("5b03c1d5e2e9a71b90124a6e"));
	//userService.showComments(new ObjectId("5b03c1d5e2e9a71b90124a6e"),0,5);
	//friendService.heartFriend(new ObjectId("5b06bbebe2e9a7666047f27e"),0,5, false);
	FriendDAO dao = new FriendDAO();
	dao.doubleAggregation("user", "userinfo", 10, new ObjectId("5af16a61e2e9a708c090917e"), "uid");
}
}
