package com.social.frenz4.tools;


import org.bson.types.ObjectId;

import com.social.frenz4.service.UserService;

public class getChat {
	public static void main(String args[]){
UserService userservice = new UserService();
userservice.getChat(new ObjectId("5b3789fb949e3d11643f72c1"), new ObjectId("5b3b51e0949e3d10c8ade388"));
}
}
