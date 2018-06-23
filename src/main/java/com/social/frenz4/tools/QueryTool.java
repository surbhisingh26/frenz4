package com.social.frenz4.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.social.frenz4.DAO.UserDAO;
import com.social.frenz4.app.RequestResponseUtility;
import com.social.frenz4.service.FriendService;
import com.social.frenz4.service.UserService;
import com.social.scframework.service.DBConnection;

public class QueryTool {
	public static void main(String args[]){
		ObjectId uid;

//		UserService userService = new UserService();
//		userService.getChat(new ObjectId("5af16a61e2e9a708c090917e"),new ObjectId("5af547cde2e9a70900b73338"));
		DBConnection conn = new DBConnection();

		for(int i = 0; i<5;i++) {
			conn.getDB("user");
			conn.getDB("friend");

		}
	}
}
