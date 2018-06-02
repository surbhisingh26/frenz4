package com.social.beFriendly.tools;

import org.bson.types.ObjectId;

import com.social.beFriendly.service.UserService;

public class QueryTool {
public static void main(String args[]){
	UserService userService = new UserService();
	userService.getChat(new ObjectId("5af16a61e2e9a708c090917e"),new ObjectId("5af547cde2e9a70900b73338"));
}
}
