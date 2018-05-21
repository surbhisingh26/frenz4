package com.social.beFriendly.tools;

import org.bson.types.ObjectId;

import com.social.beFriendly.service.UserService;

public class searchUserTool {
	
	public static void main(String args[]){
	UserService user = new UserService();
	user.searchUser("singh",new ObjectId(""));
	}

}
