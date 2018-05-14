package com.social.beFriendly.actions;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.social.beFriendly.model.User;
import com.social.beFriendly.service.EmailService;
import com.social.beFriendly.service.FriendService;
import com.social.beFriendly.service.NotificationService;
import com.social.beFriendly.service.UserService;
import com.social.scframework.App.Email;
import com.social.scframework.App.Utility;

public class FriendActions extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Utility utility = new Utility();
	String uid;
	String templatePath = "C:/soft/apache-tomcat-8.5.23/webapps/beFriendly/WEB-INF/templates/fancy-colorlib";
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FriendActions() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		FriendActions friendaction = new FriendActions();


		String path = request.getPathInfo();
		System.out.println("path "+ path);


		if(path==null||path.equals("/")){
			Map<String, Object> hmap  = new HashMap<String, Object>();
			//hmap = utility.checkSession(request);
			utility.getHbs(response,"home",hmap,templatePath);
		}

		else{
			try {
				String pathname = path.replace("/", "");
				Method method = FriendActions.class.getDeclaredMethod(pathname,HttpServletRequest.class,HttpServletResponse.class);

				method.invoke(friendaction,request,response);
			} catch (Exception e) {

				e.printStackTrace();
			} 
		}
	}
	public void befriend(HttpServletRequest request, HttpServletResponse response){
		try{
		Map<String, Object> hmap = new HashMap<String, Object>();
		uid = utility.getSession(request);
		UserService userservice = new UserService();
		User user = userservice.findOneById(uid);
		hmap.put("loggedInUser", user);
		String fid = request.getParameter("fid");
		FriendService friendservice = new FriendService();
		friendservice.beFriend(uid,fid);
		User friend = userservice.findOneById(fid);
		NotificationService  notify = new NotificationService();
		Email email = new Email();
		hmap.put("name",friend.getName());
		String status = "Pending";
		EmailService emailservice = new EmailService();
		String id = emailservice.email(user.getName(),"FriendRequest",friend.getEmail(),status,"Friend Request");
		Boolean unsubscription = emailservice.checkSubscription(friend.getEmail());
		if(unsubscription==true)
			status = "Failed";
		else{
		email.send(friend.getName(),"singh.surabhi.055@gmail.com", "Friend Request","requestTemplate",templatePath+"/EmailTemplates", hmap);
		status = "Sent";
		}
		emailservice.updateEmail(id,status);
		String notification = "You have a friend request from "+ user.getName();
		notify.sendNotification(uid,fid,notification);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void friends(HttpServletRequest request,HttpServletResponse response){
		try{
			Map<String, Object> hmap = new HashMap<String, Object>();
			uid = utility.getSession(request);
			UserService userservice = new UserService();
			User user = userservice.findOneById(uid);
			hmap.put("loggedInUser", user);
			utility.getHbs(response,"friends",hmap,templatePath);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
