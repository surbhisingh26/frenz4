package com.social.frenz4.tools;

import org.bson.types.ObjectId;

import com.social.frenz4.service.UserService;

public class searchUserTool {
	
	public static void main(String args[]){
	UserService user = new UserService();
	user.searchUser("singh",new ObjectId(""));
	}

}
