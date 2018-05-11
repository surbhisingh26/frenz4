package com.social.beFriendly.tools;

import com.social.beFriendly.service.UserService;

public class searchUserTool {
	
	public static void main(String args[]){
	UserService user = new UserService();
	user.searchUser("singh");
	}

}
