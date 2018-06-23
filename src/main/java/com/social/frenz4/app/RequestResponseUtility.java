package com.social.frenz4.app;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;

import com.social.frenz4.model.User;
import com.social.frenz4.service.NotificationService;
import com.social.frenz4.service.UserService;
import com.social.scframework.App.Utility;

public class RequestResponseUtility {
	ObjectId uid;
	public Map<String, Object> getUserDetails(HttpServletRequest request){
		System.out.println("getUserDetails");
		Utility utility = new Utility();
		Map<String, Object> hmap = new HashMap<String, Object>();
		uid = new ObjectId(utility.getSession(request));
		UserService userservice = new UserService();
		User user = userservice.findOneById(uid.toString());
		if(user.getuType().equals("Admin")){
			hmap.put("admin", true);
		}
		hmap.put("loggedInUser", user);
		hmap.put("uid", uid);
		if(uid!=null)
			hmap.put("login", true);
		NotificationService notificationService = new NotificationService();
		hmap.putAll(notificationService.notificationRead(uid));

		return hmap;
	}
}
