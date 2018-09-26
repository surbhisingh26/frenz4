package com.social.frenz4.actions;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.social.frenz4.app.PropertiesApp;
import com.social.frenz4.app.RequestResponseUtility;
import com.social.frenz4.model.User;
import com.social.frenz4.service.EmailService;
import com.social.frenz4.service.FriendService;
import com.social.frenz4.service.NotificationService;
import com.social.frenz4.service.UserService;
import com.social.scframework.App.Email;
import com.social.scframework.App.Utility;

public class FriendActions extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Utility utility = new Utility();
	ObjectId uid;
	PropertiesApp prop = new PropertiesApp();
	String templatePath = prop.getProperty("templatePath");
	//String templatePath = "D:/apps/apache-tomcat-8.5.5/webapps/ROOT/WEB-INF/templates/fancy-colorlib";
	Map<String, Object> hmap = new HashMap<String, Object>();
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FriendActions() {
		super();
		
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
		System.out.println("befriend");
		try{

			UserService userservice = new UserService();
			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			User user = (User) hmap.get("loggedInUser");

			ObjectId fid =new ObjectId( request.getParameter("fid"));
			FriendService friendservice = new FriendService();
			friendservice.beFriend(uid,fid);
			User friend = userservice.findOneById(fid.toString());
			NotificationService  notify = new NotificationService();
			Email email = new Email();
			hmap.put("reciever",friend);
			String status = "Pending";
			EmailService emailservice = new EmailService();
			String id = emailservice.email(user.getName(),"FriendRequest",friend.getEmail(),status,"Friend Request");
			Boolean subscription = emailservice.checkSubscription(friend.getEmail());
			if(subscription==false)
				status = "Failed";
			else{
				email.send(friend.getName(),"singh.surabhi.055@gmail.com", "Friend Request","requestTemplate",templatePath+"/EmailTemplates", hmap);
				status = "Sent";
			}
			System.out.println("status " + status);
			emailservice.updateEmail(id,status);
			String notification = user.getName()+ " wants to be your friend";
			notify.sendNotification(fid,user.getImagepath(),notification,"friendrequest","Friend Request");
			hmap.put("responseStatus",true);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(new Gson().toJson(hmap));
			return;
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void friends(HttpServletRequest request,HttpServletResponse response){
		try{

			
			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			if(uid!=null){
				FriendService friendService = new FriendService();
				List<Object> friendList = friendService.getFriends(uid,30);
				System.out.println("................."+hmap);

				hmap.put("friendList", friendList);
				System.out.println("................."+hmap);
				utility.getHbs(response,"friends",hmap,templatePath);
			}
			else{
				hmap.put("message","Please login First!!!");
				utility.getHbs(response,"login_page",hmap,templatePath);
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
			hmap.put("responseStatus",false);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			try {
				response.getWriter().write(new Gson().toJson(hmap));
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
		}
	}
	public void friendresponse(HttpServletRequest request,HttpServletResponse response){
		try{

			
			UserService userservice = new UserService();
			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			User user = (User) hmap.get("loggedInUser");
			String requestResponse = request.getParameter("response");
			ObjectId fid =new ObjectId(request.getParameter("fid"));
			User friend = userservice.findOneById(fid.toString());
			System.out.println(fid);
			System.out.println("Response is .............." + requestResponse);
			FriendService friendService = new FriendService();
			friendService.friendResponse(uid,fid,requestResponse);
			NotificationService notifyservice = new NotificationService();
			String notification = user.getName() + " " + requestResponse + "ed to be Your Friend";
			notifyservice.sendNotification(fid, user.getImagepath(), notification,"friendprofile?fid="+uid+"&","Friend Response");
			hmap.put("reciever",friend);
			hmap.put("response", requestResponse);

			String status = "pending";
			EmailService emailservice = new EmailService();
			String id = emailservice.email(user.getName(),"FriendResponse",friend.getEmail(),status,"Friend Request");
			Boolean subscription = emailservice.checkSubscription(friend.getEmail());
			Email email = new Email();
			if(subscription==false)
				status = "Failed";
			else{

				email.send(friend.getName(),"singh.surabhi.055@gmail.com", "Friend Request","responseTemplate",templatePath+"/EmailTemplates", hmap);
				status = "Sent";
			}
			emailservice.updateEmail(id,status);
			hmap.put("responseStatus",true);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(new Gson().toJson(hmap));
		}
		catch(Exception e){
			hmap.put("responseStatus",false);
			hmap.put("responseStatus",false);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			try {
				response.getWriter().write(new Gson().toJson(hmap));
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
		}
	}
	public void cancelfriendrequest(HttpServletRequest request,HttpServletResponse response){
		try{

			
			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			User user = (User) hmap.get("loggedInUser");
			ObjectId fid = new ObjectId(request.getParameter("fid"));
			System.out.println(fid);
			FriendService friendService = new FriendService();
			friendService.cancelRequest(uid,fid);
			NotificationService notifyservice = new NotificationService();
			String notification = user.getName()+ "wants to be your friend";
			notifyservice.deleteNotification(fid,notification);
			hmap.put("responseStatus",true);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(new Gson().toJson(hmap));

		}
		catch(Exception e){
			e.printStackTrace();
			hmap.put("responseStatus",false);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			try {
				response.getWriter().write(new Gson().toJson(hmap));
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
		}
	}
	public void removefriend(HttpServletRequest request,HttpServletResponse response){
		try{

			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			ObjectId fid = new ObjectId(request.getParameter("fid"));
			System.out.println(fid);
			FriendService friendService = new FriendService();
			friendService.removeFriend(uid,fid);

			hmap.put("responseStatus",true);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(new Gson().toJson(hmap));

		}
		catch(Exception e){
			e.printStackTrace();
			hmap.put("responseStatus",false);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			try {
				response.getWriter().write(new Gson().toJson(hmap));
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
		}
	}
	public void friendprofile(HttpServletRequest request,HttpServletResponse response){
		try{

			
			UserService userservice = new UserService();
			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			if(uid!=null){
				ObjectId fid = new ObjectId(request.getParameter("fid"));
				System.out.println(fid);
				User friend = userservice.findOneById(fid.toString());
				String read = request.getParameter("read");
				if(read==null)
					read = request.getParameter("?read");
				String id = request.getParameter("id");
				NotificationService notificationService = new NotificationService();

				if(read!=null&&read.equals("true")){
					notificationService.markRead(id);
				}
				System.out.println(friend.getName());
				hmap.put("friend", friend);
				utility.getHbs(response, "friend_profile", hmap, templatePath);

			}
			else{
				hmap.put("message","Please login First!!!");
				utility.getHbs(response,"login_page",hmap,templatePath);
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void friendrequest(HttpServletRequest request,HttpServletResponse response){
		try{

			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");

			FriendService friendService = new FriendService();
			List<User> requestList = friendService.getFriendRequests(uid);
			String read = request.getParameter("read");
			if(read==null)
				read = request.getParameter("?read");
			String id = request.getParameter("id");
			NotificationService notificationService = new NotificationService();

			if(read!=null&&read.equals("true")){
				notificationService.markRead(id);
			}
			hmap.put("requestList", requestList);
			utility.getHbs(response, "friend_request", hmap, templatePath);

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void friendactivity(HttpServletRequest request,HttpServletResponse response){
		try{
		
			//System.out.println("Friends Activity");
			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			if(uid!=null){
				FriendService friendService = new FriendService();
				hmap.putAll(friendService.friendsActivity(uid));
				System.out.println("..............." + uid);
				utility.getHbs(response, "friends_activity", hmap, templatePath);
				System.out.println("end");

			}
			else{
				hmap.put("message","Please login First!!!");
				utility.getHbs(response,"login_page",hmap,templatePath);
			}
			

		}
		catch(Exception e){

		}
	}
	public void onlinefriends(HttpServletRequest request,HttpServletResponse response){
		try{
			
			System.out.println("Friends Activity");
			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			FriendService friendService = new FriendService();
			hmap.putAll(friendService.onlineFriends(uid));
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(new Gson().toJson(hmap));
			
		}
		catch(Exception e){

		}
	}
	public void friendchatwindow(HttpServletRequest request,HttpServletResponse response){
		try{
			//System.out.println("FRIEND CHAT WINDOW.......");
			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			
			if(uid!=null){
				String fid = request.getParameter("fid");
				UserService userService = new UserService();
				User userFriend = userService.findOneById(fid);
				hmap.putAll(userService.getChat(uid,new ObjectId(fid)));
				hmap.putAll(userService.getRecentChats(uid));
				hmap.put("fid", fid);
				hmap.put("userFriend", userFriend);
				String window = request.getParameter("window");
				if(window.equals("true")){
				utility.getHbs(response, "chat_panel", hmap, templatePath);
				}
				else
				{
					System.out.println("Window false");
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					response.getWriter().write(new Gson().toJson(hmap));
				}

			}
			else{
				hmap.put("message","Please login First!!!");
				utility.getHbs(response,"login_page",hmap,templatePath);
			}
			

		}
		catch(Exception e){

		}
	}
	public void heartfriend(HttpServletRequest request,HttpServletResponse response){
		try{
			
			System.out.println("Friends Activity");
			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			ObjectId typeId = new ObjectId(request.getParameter("typeId"));
			String skipStr = request.getParameter("skip");
			String broken = request.getParameter("broken");
			int skip = 0;
			int limit = 15;
			if(skipStr!=null&&!skipStr.equals("")){
				skip=Integer.parseInt(skipStr);
			}
			
			FriendService friendService = new FriendService();
			hmap.putAll(friendService.heartFriend(typeId,skip,limit,Boolean.parseBoolean(broken)));
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(new Gson().toJson(hmap));
			

		}
		catch(Exception e){

		}
	}


}
