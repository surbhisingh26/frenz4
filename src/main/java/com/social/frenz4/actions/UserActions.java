package com.social.frenz4.actions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.social.frenz4.app.AsyncRunnable;
import com.social.frenz4.app.RequestResponseUtility;
import com.social.frenz4.model.Points;
import com.social.frenz4.model.ProfilePic;
import com.social.frenz4.model.UploadPic;
import com.social.frenz4.model.User;
import com.social.frenz4.model.UserInfo;
import com.social.frenz4.service.EmailService;
import com.social.frenz4.service.FriendService;
import com.social.frenz4.service.NotificationService;
import com.social.frenz4.service.UserService;
import com.social.scframework.App.Email;
import com.social.scframework.App.Utility;
import com.social.scframework.highchart.HighChart;

/**
 * Servlet implementation class UserActions
 * 
 */

public class UserActions extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Utility utility = new Utility();
	ObjectId uid;
	String templatePath = "D:/apps/apache-tomcat-8.5.5/webapps/ROOT/WEB-INF/templates/fancy-colorlib";
	public static final String SALT = "my-salt-text";
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserActions() {
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
		UserActions useraction = new UserActions();


		String path = request.getPathInfo();
		System.out.println("path "+ path);


		if(path==null||path.equals("/")){
			Map<String, Object> hmap  = new HashMap<String, Object>();
			//hmap = utility.checkSession(request);
			utility.getHbs(response,"friends_activity",hmap,templatePath);
		}

		else{
			try {
				String ur = path.replace("/", "");
				Method method = UserActions.class.getDeclaredMethod(ur,HttpServletRequest.class,HttpServletResponse.class);

				method.invoke(useraction,request,response);
			} catch (Exception e) {

				e.printStackTrace();
			} 
		}
	}

	public void login(HttpServletRequest request,HttpServletResponse response){

		try {
			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String,Object> hmap = new HashMap<String,Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			if(uid==null){
				utility.getHbs(response,"login_page",null,templatePath);
			}
			else{
				response.sendRedirect("friendactivity");
			}
		} catch (ServletException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void register(HttpServletRequest request,HttpServletResponse response){

		try {
			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String,Object> hmap = new HashMap<String,Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			if(uid==null){
				utility.getHbs(response,"register_page",null,templatePath);
			}
			else{
				response.sendRedirect("friendactivity");
			}
		} catch (ServletException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	public void adduser(HttpServletRequest request,HttpServletResponse response){

		try {
			String fname = request.getParameter("fname");
			String mname = request.getParameter("mname");
			String lname = request.getParameter("lname");
			String email = request.getParameter("email");
			String country = request.getParameter("country");
			String city = request.getParameter("city");
			String password = request.getParameter("pass");
			String dob = request.getParameter("dob");
			String mobile =request.getParameter("mobile");

			String saltedPassword = SALT + password;
			String hashedPassword = utility.generateHash(saltedPassword);



			String reference = request.getParameter("reference");
			if(reference==null)
				reference = "No reference";
			String referenceId = request.getParameter("referenceId");
			if(referenceId==null)
				referenceId="null";
			String gender = request.getParameter("gender");
			String bgcolor = "#000000";
			String rootPath = System.getProperty("catalina.home");
			String savePath = rootPath + File.separator + "webapps/images/frenz4images";
			File fileSaveDir=new File(savePath);
			//File file = new File(rootPath + File.separator + "images");
			//File fileSaveDir=new File(file);
			if(!fileSaveDir.exists()){
				fileSaveDir.mkdir();
			}
			//String imagePath= fileSaveDir + File.separator + "default.jpg";
			String filePath=null;
			if(reference.equals("fb")){
				filePath = request.getParameter("picUrl");
			}
			else
				filePath = File.separator +"images/frenz4images" + File.separator + "default.jpg";

			UserService rs = new UserService();
			String result = rs.registerUser(fname, lname, mname,country,city,mobile,hashedPassword,gender,dob,bgcolor,filePath,email,reference,referenceId);
			Map<String, Object> hmap  = new HashMap<String, Object>();
			if(result == null){

				String msg = "You are already registered with this email";
				hmap.put("message", msg);
				//utility.getHbs(response,"message",hmap);
				utility.getHbs(response,"register_page",hmap,templatePath);
			}
			else{
				//sending welcome email
				Email welcomeEmail = new Email();
				EmailService emailservice = new EmailService();
				String subject = "Welcome to frenz4" ;
				String purpose = "welcome mail";
				String status = "Pending...";				
				String id = emailservice.email("frenz4", purpose, email, status, subject);
				hmap.put("name", fname + lname);
				hmap.put("id", result);
				welcomeEmail.send(fname + lname, email, subject, "welcomeEmailTemplate" , templatePath+"/EmailTemplates", hmap);
				status = "Sent";
				emailservice.updateEmail(id,status);
				String registeredMsg = "You are successfully registered!!! Login to Continue";
				hmap.put("message", registeredMsg);


				if(!referenceId.equals("null")){
					response.sendRedirect("login");
				}
				else
				{					
					utility.getHbs(response,"login_page",hmap,templatePath);
				}

			}
		} catch (ServletException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void signin(HttpServletRequest request,HttpServletResponse response){

		try {
			String email = request.getParameter("email");
			System.out.println("username "+email);
			if(email==null)
				email="";
			String password = request.getParameter("pass");
			System.out.println("password "+password);
			if(password==null)
				password="";
			String reference = request.getParameter("reference");
			if(reference==null)
				reference = "No reference";
			String referenceId = request.getParameter("referenceId");
			if(referenceId==null)
				referenceId="null";
			System.out.println("reference is "+reference);
			System.out.println("ref Id is "+referenceId);

			String saltedPassword = SALT + password;
			String hashedPassword = utility.generateHash(saltedPassword);

			System.out.println("en pass " +hashedPassword);
			UserService userservice = new UserService();
			String result = userservice.checkValid(email,hashedPassword,reference,referenceId);
			Map<String, Object> hmap  = new HashMap<String, Object>();
			//System.out.println(request.getParameter("firstname"));
			System.out.println(result);
			String msg = null;
			if(result.equals(email)){
				msg = "No such username exists!!!"
						+ " Register or login with another username";

				hmap.put("message", msg);

				utility.getHbs(response,"login_page",hmap,templatePath);

			}
			else if(result.equals(hashedPassword)){
				msg = "Wrong password entered";
				hmap.put("message", msg);
				utility.getHbs(response,"login_page",hmap,templatePath);
			}

			else if(result.equals("Register First")){
				hmap.put("register", true);

				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(new Gson().toJson(hmap));


			}
			else {

				Cookie loginCookie = new Cookie("uid",result);

				System.out.println("uid is "+result);
				response.addCookie(loginCookie);
				loginCookie.setMaxAge(30*60); 			
				//UserService userservice = new UserService();
				Boolean loggedIn = userservice.login(result);
				hmap.put("LoggedIn", loggedIn);

				//hmap.put("register", false);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(new Gson().toJson(hmap));
				if(referenceId.equals("null")){

					response.sendRedirect("friendactivity");
				}

			}


		} catch (ServletException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void dashboard(HttpServletRequest request,HttpServletResponse response){

		try {
			Map<String, Object> hmap  = new HashMap<String, Object>();


			RequestResponseUtility rrutility = new RequestResponseUtility();

			hmap.putAll(rrutility.getUserDetails(request));
			UserService userService = new UserService();
			uid = (ObjectId) hmap.get("uid");
			if(uid!=null){
				List<Object> friendList = new ArrayList<Object>();
				hmap.putAll(userService.myActivity(uid));
				FriendService friendService = new FriendService();
				friendList = friendService.getFriends(uid, 6);
				hmap.put("friendList", friendList);

				utility.getHbs(response,"dashboard",hmap,templatePath);
				return;
			}
			else{
				System.out.println("ELSE");
				hmap.put("message","Please login First!!!");
				utility.getHbs(response,"login_page",hmap,templatePath);
			}

		} catch (ServletException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	public void profile(HttpServletRequest request,HttpServletResponse response){

		try {
			Map<String, Object> hmap  = new HashMap<String, Object>();

			RequestResponseUtility rrutility = new RequestResponseUtility();

			hmap.putAll(rrutility.getUserDetails(request));
			UserService userService = new UserService();
			uid = (ObjectId) hmap.get("uid");
			if(uid!=null){
				UserInfo myinfo = userService.getmyInfo(uid);
				hmap.put("myinfo", myinfo);
				utility.getHbs(response, "profile", hmap, templatePath);
			}
			else{
				hmap.put("message","Please login First!!!");
				utility.getHbs(response,"login_page",hmap,templatePath);
			}

		} catch (IOException e) {

			e.printStackTrace();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void logout(HttpServletRequest request,HttpServletResponse response){
		try{
			System.out.println(utility.getSession(request));
			uid = new ObjectId(utility.getSession(request));
			String reference = request.getParameter("reference");

			Cookie loginCookie=new Cookie("uid","");  
			loginCookie.setMaxAge(0);  
			response.addCookie(loginCookie); 

			UserService userservice = new UserService();
			userservice.logout(uid);

			//System.out.println(request.getContextPath());
			//request.getRequestDispatcher("").forward(request, response);
			if(reference==null)
				response.sendRedirect("login");
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void gallery(HttpServletRequest request,HttpServletResponse response){
		try{
			Map<String, Object> hmap  = new HashMap<String, Object>();

			RequestResponseUtility rrutility = new RequestResponseUtility();

			hmap.putAll(rrutility.getUserDetails(request));
			UserService userservice = new UserService();

			uid = (ObjectId) hmap.get("uid");
			System.out.println("uid id ............................." + uid);
			if(uid!=null){
				System.out.println("TRUE..............");
				List<ProfilePic> profilePicList = userservice.getProfilePic(uid);
				hmap.put("profilePicList", profilePicList);

				List<UploadPic> uploadPicList = userservice.getUploadPic(uid);
				hmap.put("uploadPicList", uploadPicList);
				System.out.println("................."+hmap);
				utility.getHbs(response,"picture_gallery",hmap,templatePath);
			}
			else{
				System.out.println("LOGIN FIRST.................");
				hmap.put("message","Please login First!!!");
				utility.getHbs(response,"login_page",hmap,templatePath);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}


	public void search(HttpServletRequest request,HttpServletResponse response){
		try{
			Map<String, Object> hmap  = new HashMap<String, Object>();

			RequestResponseUtility rrutility = new RequestResponseUtility();

			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			if(uid!=null){
				UserService userservice = new UserService();
				String search = request.getParameter("search");

				hmap.putAll(userservice.searchUser(search,uid));			
				hmap.put("search",search);
				utility.getHbs(response,"search",hmap,templatePath);
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

	public void profilepic(HttpServletRequest request, HttpServletResponse response){
		try {

			Map<String, Object> hmap  = new HashMap<String, Object>();
			RequestResponseUtility rrutility = new RequestResponseUtility();

			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			if(uid!=null){

				System.out.println("profile pic "+uid);
				String rootPath = System.getProperty("catalina.home");
				String savePath = rootPath + File.separator + "webapps/images/frenz4images/"+uid+"/dp";
				File fileSaveDir=new File(savePath);
				if(!fileSaveDir.exists()){
					fileSaveDir.mkdirs();
				}
				String fileName = request.getParameter("filename");
				//for(Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
				//	System.out.println("key " + entry.getKey() +  " --- "  + entry.getValue());
				//	}
				System.out.println("File is ................ " + fileName);
				System.out.println("File is ................ " + request.getParameter("file"));
				Part file = request.getPart("file");
				//				System.out.println("File is ................ " + file.getName());

				hmap.put("file", file);
				hmap.put("filename",fileName);
				file.write(fileSaveDir + File.separator + fileName);
				String filePath= File.separator +"images/frenz4images/"+uid+"/dp" + "/" + fileName;
				UserService userService = new UserService();
				userService.updatePic(filePath,uid);


				//hmap.put("loggedInUser",user);
				System.out.println("filepath  ...  "+filePath);
				//String path = request.getPathInfo();
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
	public void addPictures(HttpServletRequest request,HttpServletResponse response){
		try{

			Map<String, Object> hmap  = new HashMap<String, Object>();

			RequestResponseUtility rrutility = new RequestResponseUtility();

			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			if(uid!=null){
				String rootPath = System.getProperty("catalina.home");
				String savePath = rootPath + File.separator + "webapps/images/frenz4images/"+uid+"/uploads";
				File fileSaveDir=new File(savePath);
				if(!fileSaveDir.exists()){
					fileSaveDir.mkdirs();
				}
				String fileName = request.getParameter("filename");
				System.out.println("File is ................ " + fileName);
				Part file = request.getPart("file");
				System.out.println("File is ................ " + file);

				hmap.put("file", file);
				hmap.put("filename",fileName);
				file.write(fileSaveDir + File.separator + fileName);
				String filePath= File.separator +"images/frenz4images/"+uid+"/uploads" + File.separator + fileName;
				UserService userService = new UserService();
				userService.uploadPic(filePath,uid);


				//hmap.put("loggedInUser",user);
				System.out.println("filepath  ...  "+filePath);
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
	public void unsubscribe(HttpServletRequest request,HttpServletResponse response){
		try{
			String email = request.getParameter("email");
			EmailService emailservice = new EmailService();
			emailservice.unsubscribe(email);

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void notifications(HttpServletRequest request,HttpServletResponse response){
		try{
			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			NotificationService notifyservice = new NotificationService();
			hmap.putAll(notifyservice.getNotification(uid));

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(new Gson().toJson(hmap));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void allnotifications(HttpServletRequest request,HttpServletResponse response){
		try{

			Map<String, Object> hmap  = new HashMap<String, Object>();

			RequestResponseUtility rrutility = new RequestResponseUtility();

			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			if(uid!=null){
				NotificationService notifyservice = new NotificationService();
				hmap.putAll(notifyservice.getNotification(uid));
				utility.getHbs(response, "notification", hmap, templatePath);
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
	public void addcomment(HttpServletRequest request,HttpServletResponse response){
		try{

			Map<String, Object> hmap  = new HashMap<String, Object>();

			RequestResponseUtility rrutility = new RequestResponseUtility();

			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			if(uid!=null){
				User user = (User) hmap.get("loggedInUser");
				String comment = request.getParameter("comment");
				ObjectId fid = new ObjectId(request.getParameter("fid"));
				ObjectId typeId =  new ObjectId(request.getParameter("typeId"));
				UserService userService = new UserService();
				//Activity activity = userService.findActivityLink(typeId.toString());
				//String type = activity.getType();
				userService.addComment(comment,uid,typeId);
				
				if(!fid.equals(uid)){
					System.out.println("if true");
					NotificationService notiService = new NotificationService();
					notiService.sendNotification(fid, user.getImagepath(), user.getName() + " commented on your post","post?typeId="+typeId+"&" , "New comment");
				}
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
	public void showcomments(HttpServletRequest request,HttpServletResponse response){
		try{
			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			String skipStr = request.getParameter("skip");
			String limitStr = request.getParameter("limit");
			System.out.println("Skip " + skipStr);
			System.out.println("Limit " + limitStr);
			int skip = 0;
			int limit = 5;
			if(skipStr!=null&&!skipStr.equals("")){
				skip = Integer.parseInt(skipStr);
			}
			if(limitStr!=null&&!limitStr.equals("")){
				limit = Integer.parseInt(limitStr);
			}
			ObjectId typeId = new ObjectId(request.getParameter("typeId"));
			UserService userService = new UserService();
			hmap.putAll(userService.showComments(typeId,skip,limit));
			System.out.println(hmap);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(new Gson().toJson(hmap));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void post(HttpServletRequest request,HttpServletResponse response){
		try{

			Map<String, Object> hmap  = new HashMap<String, Object>();

			RequestResponseUtility rrutility = new RequestResponseUtility();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			if(uid!=null){
				UserService userservice = new UserService();
				ObjectId typeId = new ObjectId(request.getParameter("typeId"));
				String read = request.getParameter("read");
				if(read==null)
					read = request.getParameter("?read");
				String id = request.getParameter("id");
				NotificationService notificationService = new NotificationService();

				if(read!=null&&read.equals("true")){
					notificationService.markRead(id);
				}
				hmap.putAll(userservice.post(typeId,uid));

				utility.getHbs(response, "post", hmap, templatePath);
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
	public void heartincrease(HttpServletRequest request,HttpServletResponse response){
		try{

			Map<String, Object> hmap  = new HashMap<String, Object>();

			RequestResponseUtility rrutility = new RequestResponseUtility();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			if(uid!=null){
				User user = (User) hmap.get("loggedInUser");
				UserService userservice = new UserService();
				ObjectId typeId = new ObjectId(request.getParameter("typeId"));
				String fidStr = request.getParameter("fid");
				String brokenStr = request.getParameter("broken");
				boolean broken = Boolean.parseBoolean(brokenStr);
				System.out.println("Broken..........." + broken);
				hmap.putAll(userservice.heartIncrease(typeId,uid,broken));
				//System.out.println("fid.......... " + fid);
				if(fidStr!=null&&!fidStr.equals(uid.toString())){
					ObjectId fid = new ObjectId(fidStr);
					NotificationService notiservice = new NotificationService();
					String notification = null;
					if(broken){
						notification = "You have a HeartBreak from " + user.getName();
					}
					else{
						notification = "You have a Heart from " + user.getName();
					}

					notiservice.sendNotification(fid, user.getImagepath(), notification, "post?typeId="+typeId+"&", "New Reaction");
				}
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(new Gson().toJson(hmap));
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
	public void addstatus(HttpServletRequest request,HttpServletResponse response){
		try{

			Map<String, Object> hmap  = new HashMap<String, Object>();

			RequestResponseUtility rrutility = new RequestResponseUtility();

			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			if(uid!=null){
				UserService userservice = new UserService();
				String status = request.getParameter("status");
				userservice.addStatus(uid, status);
				response.sendRedirect("friendactivity");
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

	public void gethighcharts(HttpServletRequest request, HttpServletResponse response){
		try {

			Map<String, Object> hmap  = new HashMap<String, Object>();

			RequestResponseUtility rrutility = new RequestResponseUtility();
			List<HighChart> highcharts = new ArrayList<HighChart>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");

			String fidStr = request.getParameter("fid");
			
			if(fidStr!=null){
				uid = new ObjectId(fidStr);
				
			}
			UserService userservice = new UserService();
			highcharts.add(userservice.requestpiechart(uid));
			highcharts.add(userservice.stackedgraph(uid));

			hmap.put("highcharts",highcharts);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(new Gson().toJson(hmap));


		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void updateprofile(HttpServletRequest request, HttpServletResponse response){
		try {


			Map<String, Object> hmap  = new HashMap<String, Object>();

			RequestResponseUtility rrutility = new RequestResponseUtility();

			hmap.putAll(rrutility.getUserDetails(request));

			uid = (ObjectId) hmap.get("uid");
			if(uid!=null){
				String name = request.getParameter("name");
				String email = request.getParameter("email");
				String address = request.getParameter("address");
				String mobile = request.getParameter("mobile");
				String aboutme = request.getParameter("aboutme");
				String country = request.getParameter("country");
				String city = request.getParameter("city");

				UserService userService = new UserService();
				userService.updateProfile(uid,name,email,address,mobile,aboutme,country,city);
				response.sendRedirect("profile");
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
	public void earnpoints(HttpServletRequest request, HttpServletResponse response){
		try {
			Map<String, Object> hmap  = new HashMap<String, Object>();

			RequestResponseUtility rrutility = new RequestResponseUtility();

			hmap.putAll(rrutility.getUserDetails(request));
			String read = request.getParameter("read");
			if(read==null)
				read = request.getParameter("?read");
			String id = request.getParameter("id");
			NotificationService notificationService = new NotificationService();

			if(read!=null&&read.equals("true")){
				notificationService.markRead(id);
			}
			uid = (ObjectId) hmap.get("uid");
			if(uid!=null){
				UserService userService = new UserService();
				List<Points> pointsList = userService.getPoints(uid);
				hmap.put("pointsList", pointsList);
				utility.getHbs(response,"points",hmap,templatePath);
			}
			else{
				hmap.put("message","Please login First!!!");
				utility.getHbs(response,"login",hmap,templatePath);
			}	

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void invite(HttpServletRequest request, HttpServletResponse response){
		try {
			Map<String, Object> hmap  = new HashMap<String, Object>();

			RequestResponseUtility rrutility = new RequestResponseUtility();
			hmap.putAll(rrutility.getUserDetails(request));

			uid = (ObjectId) hmap.get("uid");
			if(uid!=null){
				String inviteEmail = request.getParameter("inviteEmail");
				System.out.println(inviteEmail);
				UserService userservice = new UserService();
				String from = userservice.invite(uid,inviteEmail);
				if(from==null){
					String msg = "User with this email is already our member!!! Try another";
					System.out.println(msg);
					hmap.put("mailMessage", msg);
					utility.getHbs(response,"points",hmap,templatePath);
					//response.sendRedirect("points");
					System.out.println("Message is .... "+hmap.get("mailMessage"));
				}
				else if (from.equalsIgnoreCase("Already Invited")){
					String msg = "You already invited this user";
					hmap.put("mailMessage", msg);
					utility.getHbs(response,"points",hmap,templatePath);
				}
				else{
					Email email = new Email();
					EmailService emailservice = new EmailService();
					Boolean subscription = emailservice.checkSubscription(inviteEmail);
					String subject = "Get more connected with your friends..";
					String purpose = "inviteToJoin";
					String status = "Pending...";
					System.out.println("Subscription for "+ inviteEmail +"...."+subscription);

					String id = emailservice.email(from, purpose, inviteEmail, status, subject);
					if(subscription==true){

						email.send(inviteEmail, inviteEmail, subject, "invitationTemplate" , id, hmap);
						status = "Sent";
					}
					else{
						status="Failed";
					}
					emailservice.updateEmail(id,status);
					String msg = "Mail Sent...";
					hmap.put("mailMessage", msg);
					utility.getHbs(response,"points",hmap,templatePath);
					//response.sendRedirect("points");
				}
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
	public void settings(HttpServletRequest request, HttpServletResponse response){
		try {
			Map<String, Object> hmap  = new HashMap<String, Object>();


			RequestResponseUtility rrutility = new RequestResponseUtility();
			hmap.putAll(rrutility.getUserDetails(request));

			uid = (ObjectId) hmap.get("uid");
			if(uid!=null){
				utility.getHbs(response,"settings",hmap,templatePath);
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
	public void changepassword(HttpServletRequest request, HttpServletResponse response){
		try {
			Map<String, Object> hmap  = new HashMap<String, Object>();



			RequestResponseUtility rrutility = new RequestResponseUtility();

			hmap.putAll(rrutility.getUserDetails(request));

			uid = (ObjectId) hmap.get("uid");
			if(uid!=null){
				String currentPass = request.getParameter("currentPass");
				String newPass = request.getParameter("newPass");
				String confirmPass = request.getParameter("confirmPass");

				String result = null;
				if(!newPass.equals(confirmPass)){
					result = "Not Matched";
				}			
				else{
					String saltedPassword = SALT + currentPass;
					String hashedCurrentPassword = utility.generateHash(saltedPassword);

					String saltedPassword1 = SALT + newPass;
					String hashedNewPassword = utility.generateHash(saltedPassword1);

					UserService userService = new UserService();
					result = userService.changePassword(hashedCurrentPassword,hashedNewPassword,uid);
					hmap.put("result", result);
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
			e.printStackTrace();
		}
	}

	public void googlemap(HttpServletRequest request, HttpServletResponse response){
		try {
			Map<String, Object> hmap  = new HashMap<String, Object>();

			RequestResponseUtility rrutility = new RequestResponseUtility();

			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			if(uid!=null){

				FriendService friendService = new FriendService();
				List<Object> friendList = friendService.getFriends(uid,30);
				hmap.put("friendList", friendList);
				UserService userService = new UserService();
				UserInfo myInfo  = userService.getmyInfo(uid);
				hmap.put("myInfo", myInfo);
				utility.getHbs(response,"google_map",hmap,templatePath);
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

	public void sendmessage(HttpServletRequest request,HttpServletResponse response){
		try{
			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			String text = request.getParameter("text");
			ObjectId fid = new ObjectId(request.getParameter("fid"));
			UserService userservice = new UserService();

			userservice.storeChat(uid,fid,text);

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void checkmessage(HttpServletRequest request,HttpServletResponse response){
		try{
			Map<String, Object> hmap  = new HashMap<String, Object>();

			RequestResponseUtility rrutility = new RequestResponseUtility();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");
			if(uid!=null){
				boolean asyncSupported = request.isAsyncSupported();
				System.out.println("asyncSupported 1: " + asyncSupported);
				// Tomcat only
				request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
				asyncSupported = request.isAsyncSupported();
				System.out.println("asyncSupported 2: " + asyncSupported);
				if (asyncSupported) {
					System.out.println("async");
					AsyncContext asyncCtx = request.startAsync(request, response);  // req.startAsync();
					asyncCtx.setTimeout(0); // => disable timeout
					AsyncRunnable thread = new AsyncRunnable(asyncCtx,uid);
					asyncCtx.start(thread);
				}
			}
		}	
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void resetpassword(HttpServletRequest request, HttpServletResponse response){
		try {
			Map<String, Object> hmap  = new HashMap<String, Object>();
			String recoveryEmail = request.getParameter("remail");
			System.out.println("Email is  " + recoveryEmail);
			String generatedString = RandomStringUtils.randomAlphanumeric(7);

			UserService userService = new UserService();
			String saltedPassword = SALT + generatedString;
			String hashedPassword = utility.generateHash(saltedPassword);
			userService.forgotPass(hashedPassword,recoveryEmail);
			hmap.put("pass", generatedString);

			Email email = new Email();
			email.send("", recoveryEmail, "Reset Password", "reset_passTemplate", templatePath+"/EmailTemplates", hmap);
			
			hmap.put("message", "Please check your email for new password");
			utility.getHbs(response,"login_page",hmap,templatePath);

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void confirmMail(HttpServletRequest request, HttpServletResponse response){
		try {
			String id = request.getParameter("id");
			System.out.println(id);
			UserService userService = new UserService();
			userService.confirmMail(id);
			Map<String, Object> hmap = new HashMap<String,Object>();
			hmap.put("message", "Your mail is verified!!! Login to continue..");
			utility.getHbs(response,"login_page",hmap,templatePath);

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void viewfalse(HttpServletRequest request, HttpServletResponse response){
		try {
			UserService userService = new UserService();
			ObjectId typeId = new ObjectId(request.getParameter("typeId"));
			userService.setviewfalse(typeId);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void removepost(HttpServletRequest request, HttpServletResponse response){
		try {
			UserService userService = new UserService();
			ObjectId typeId = new ObjectId(request.getParameter("typeId"));
			userService.setDeleted(typeId);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void removepic(HttpServletRequest request, HttpServletResponse response){
		try {
			UserService userService = new UserService();
			ObjectId typeId = new ObjectId(request.getParameter("typeId"));
			userService.deletePic(typeId);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void hidepost(HttpServletRequest request, HttpServletResponse response){
		try {
			UserService userService = new UserService();
			ObjectId typeId = new ObjectId(request.getParameter("typeId"));
			userService.hidePost(typeId);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void msgread(HttpServletRequest request, HttpServletResponse response){
		try {
			UserService userService = new UserService();
			RequestResponseUtility rrutility = new RequestResponseUtility();
			Map<String, Object> hmap  = new HashMap<String, Object>();
			hmap.putAll(rrutility.getUserDetails(request));
			uid = (ObjectId) hmap.get("uid");			
			ObjectId fid = new ObjectId(request.getParameter("fid"));			
			userService.markMsgRead(uid,fid);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}


