package com.social.beFriendly.service;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.social.beFriendly.DAO.ActivityDAO;
import com.social.beFriendly.DAO.ChatDAO;
import com.social.beFriendly.DAO.CommentDAO;
import com.social.beFriendly.DAO.FriendDAO;
import com.social.beFriendly.DAO.HeartDAO;
import com.social.beFriendly.DAO.InvitationDAO;
import com.social.beFriendly.DAO.PointDAO;
import com.social.beFriendly.DAO.PointSettingsDAO;
import com.social.beFriendly.DAO.ProfilePicDAO;
import com.social.beFriendly.DAO.StatusDAO;
import com.social.beFriendly.DAO.UploadPicDAO;
import com.social.beFriendly.DAO.UserDAO;
import com.social.beFriendly.DAO.UserInfoDAO;
import com.social.beFriendly.model.Activity;
import com.social.beFriendly.model.Chat;
import com.social.beFriendly.model.Comment;
import com.social.beFriendly.model.Email;
import com.social.beFriendly.model.Friend;
import com.social.beFriendly.model.Heart;
import com.social.beFriendly.model.Invite;
import com.social.beFriendly.model.PointSettings;
import com.social.beFriendly.model.Points;
import com.social.beFriendly.model.ProfilePic;
import com.social.beFriendly.model.Status;
import com.social.beFriendly.model.UploadPic;
import com.social.beFriendly.model.User;
import com.social.beFriendly.model.UserInfo;
import com.social.scframework.App.Utility;
import com.social.scframework.highchart.Chart;
import com.social.scframework.highchart.Column;
import com.social.scframework.highchart.Data;
import com.social.scframework.highchart.DataLabels;
import com.social.scframework.highchart.HighChart;
import com.social.scframework.highchart.Pie;
import com.social.scframework.highchart.PlotOptions;
import com.social.scframework.highchart.Series;
import com.social.scframework.highchart.Title;
import com.social.scframework.highchart.ToolTip;
import com.social.scframework.highchart.XAxis;
import com.social.scframework.highchart.YAxis;




public class UserService {
	UserDAO userdao = new UserDAO();
	JacksonDBCollection<User, String> userCollection =  userdao.userDAO();
	ProfilePicDAO profilepicdao = new ProfilePicDAO();
	JacksonDBCollection<ProfilePic, String> dpCollection =  profilepicdao.profilePicDAO();
	UploadPicDAO uploadpicdao = new UploadPicDAO();
	JacksonDBCollection<UploadPic, String> uploadCollection =  uploadpicdao.uploadPicDAO();
	ActivityDAO activitydao = new ActivityDAO();
	JacksonDBCollection<Activity, String> activityCollection =  activitydao.activityDAO();
	public Boolean registerUser(String fname, String lname, String mname, String country, String city, String mobile,
			String password, String gender, String dob, String bgcolor, String filePath, String email, String reference,
			String referenceId) {

		if(!mname.equals(""))
			mname = mname + " ";
		User registration = new User();
		Date date = null;
		try {	
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			date = format.parse(dob);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//---------- Creating Collection ------------
		BasicDBObject query = new BasicDBObject();

		query.put("email", email);
		DBCursor<User> cursor = userCollection.find(query);

		if (cursor.hasNext()) {
			return false;
		}
		registration.setuType("User");
		registration.setName(fname,mname,lname);
		registration.setEmail(email);
		registration.setGender(gender);
		registration.setCountry(country);
		registration.setCity(city);
		registration.setMobile(mobile);
		registration.setPassword(password);
		registration.setDob(date);
		registration.setBgcolor(bgcolor);
		registration.setLastLoggedInAt(null);
		registration.setImagepath(filePath);
		registration.setLoggedIn(false);
		registration.setReference(reference);
		registration.setReferenceId(referenceId);
		registration.setPoints(50);

		WriteResult<User, String> reg = userCollection.insert(registration);
		registration = reg.getSavedObject();
		InvitationDAO invitationdao = new InvitationDAO();
		JacksonDBCollection<Invite, String> invitationCollection = invitationdao.invitationDAO();

		NotificationService notificationservice = new NotificationService();
		String link = "points";
		
		BasicDBObject query1 = new BasicDBObject();
		query1.put("recieverEmail", email);
		DBCursor<Invite> cursor1 = invitationCollection.find(query1);
		
		PointDAO pointdao = new PointDAO();
		JacksonDBCollection<Points, String> pointCollection = pointdao.pointDAO();

		while(cursor1.hasNext()){
			Invite invite = cursor1.next();
			ObjectId userId = invite.getSenderId();
		
			User user = userCollection.findOneById(userId.toString());
			EmailService emailservice = new EmailService();
			String mailStatus = emailservice.checkStatus(email, user.getEmail(),"inviteToJoin");
			if(mailStatus.equalsIgnoreCase("Sent")){
				user.setPoints(user.getPoints()+50);
				String userEmail = user.getEmail();
				userCollection.updateById(userId.toString(), user);
				
				BasicDBObject query2 = new BasicDBObject();

				query2.put("recieverEmail", userEmail);
				DBCursor<Invite> cursor2 = invitationCollection.find(query2);
				while(cursor2.hasNext()){
					Invite invite1 = cursor2.next();
					ObjectId secondaryUserId = invite1.getSenderId();
					User user1 = userCollection.findOneById(secondaryUserId.toString());
					user1.setPoints(user1.getPoints()+10);
					userCollection.updateById(secondaryUserId.toString(), user1);
					invitationCollection.remove(query2);
					notificationservice.sendNotification(secondaryUserId, "img/Referral.jpg", "Congratulation!!! You have earned 10 points reward on joining of "+registration.getName()+" invited by your friend ", link, "Referral points");
					
					Points points = new Points();
					points.setDate(date);
					points.setPointReason("For joining of " + fname + " " + lname);
					points.setUid(secondaryUserId);
					points.setPointsEarn(10);
					pointCollection.insert(points);
				}
				Points points = new Points();
				points.setDate(date);
				points.setPointReason("For joining of " + fname + " " + lname);
				points.setUid(userId);
				points.setPointsEarn(50);
				pointCollection.insert(points);

				System.out.println("REGISTRATION ID....................."+ registration.getId());

				notificationservice.sendNotification(userId, "img/Referral.jpg", "Congratulation!!! You have earned 50 points reward on joining of "+registration.getName(), link, "Referral points");
			}
		}
		Points points = new Points();
		points.setDate(date);
		points.setPointReason("To join our website");
		points.setUid(new ObjectId(registration.getId()));
		points.setPointsEarn(50);
		pointCollection.insert(points);
		notificationservice.sendNotification(new ObjectId(registration.getId()), "img/logo-icon.png", "Welcome "+fname+" "+lname+"\n You have been rewarded by 50 points in your account", link, "Welcome");

		 

		return true;
	}
	public String checkValid(String email, String password, String reference, String referenceId) {

		if(email!=""){
			BasicDBObject query = new BasicDBObject();
			query.put("email", email);
			DBCursor<User> cursor = userCollection.find(query);
			String result = null;
			if (cursor.hasNext()) {
				User user = cursor.next();
				String pass = user.getPassword();
				System.out.println("pass "+user.getPassword());
				if(pass.equals(password)){

					result = user.getId();
					System.out.println("uid is ... "+result);

					return result;
				}				
				else{

					return password;
				}

			}}
		else{
			BasicDBObject query = new BasicDBObject();
			query.put("referenceId", referenceId);
			DBCursor<User> cursor = userCollection.find(query);
			if(cursor.hasNext()){
				User registration = cursor.next();
				String id = registration.getId();
				System.out.println("Id is "+id);
				return id;	
			}
			else{
				return "Register First";
			}
		}

		return email;

	}
	public Boolean login(String uid){

		User user = userCollection.findOneById(uid);
		System.out.println(uid);
		System.out.println(user.getName());
		user.setLoggedIn(true);
		userCollection.updateById(uid, user);
		return user.getLoggedIn();

	}
	public User findOneById(String uid) {

		User user = userCollection.findOneById(uid);
		return user;
	}

	public void logout(ObjectId uid){

		Date date = new Date();		

		//System.out.println("Date is "+now);
		User user = userCollection.findOneById(uid.toString());
		user.setLoggedIn(false);
		user.setLastLoggedInAt(date);
		userCollection.updateById(uid.toString(), user);
	}
	public Map<String,Object> searchUser(String search,ObjectId uid) {
		Map<String,Object> hmap = new HashMap<String, Object>();
		BasicDBObject query = new BasicDBObject();
		query.put("name", new BasicDBObject("$regex" , "(?i).*"+search+".*"));
		List<User> requestedUser = new ArrayList<User>();
		List<User> searchedFriend = new ArrayList<User>();
		List<User> pendingFriend = new ArrayList<User>();
		List<User> searchedUser = new ArrayList<User>();
		DBCursor<User> cursor = userCollection.find(query);
		FriendService friendservice = new FriendService();
		User user = new User();
		while(cursor.hasNext()){
			user = cursor.next();

			String status = friendservice.checkStatus(uid, new ObjectId(user.getId()));
			if(user.getId().equals(uid))
				hmap.put("user", true);
			else if(status.equalsIgnoreCase("My Request Accepted")||status.equals("I Accepted Request"))
				searchedFriend.add(user);
			else if(status.equalsIgnoreCase("Request Sent"))
				requestedUser.add(user);
			else if(status.equalsIgnoreCase("Pending Request"))
				pendingFriend.add(user);
			else
				searchedUser.add(user);
			System.out.println("name " + user.getName());
		}
		query.clear();
		query.put("email", new BasicDBObject("$regex" , "(?i).*"+search+".*"));
		DBCursor<User> EmailCursor = userCollection.find(query);
		while(EmailCursor.hasNext()){
			user = EmailCursor.next();
			Boolean flag = true;
			for(User u:searchedUser){

				if(u.getEmail().equals(user.getEmail())){
					flag = false;
					break;
				}
			}
			for(User u:searchedFriend){

				if(u.getEmail().equals(user.getEmail())){
					flag = false;
					break;
				}
			}
			for(User u:requestedUser){

				if(u.getEmail().equals(user.getEmail())){
					flag = false;
					break;
				}
			}
			for(User u:pendingFriend){

				if(u.getEmail().equals(user.getEmail())){
					flag = false;
					break;
				}
			}
			if(flag==true){
				String status = friendservice.checkStatus(uid,new ObjectId(user.getId()));
				if(user.getId().equals(uid))
					hmap.put("user", true);
				else if(status.equalsIgnoreCase("My Request Accepted")||status.equals("I Accepted Request"))
					searchedFriend.add(user);
				else if(status.equalsIgnoreCase("Request Sent"))
					requestedUser.add(user);
				else if(status.equalsIgnoreCase("Pending Request"))
					pendingFriend.add(user);
				else
					searchedUser.add(user);
			}


			System.out.println("email " + user.getName());
		}
		hmap.put("searchedFriend", searchedFriend);
		hmap.put("requestedUser", requestedUser);
		hmap.put("pendingFriend", pendingFriend);
		hmap.put("searchedUser", searchedUser);
		return hmap;
	}
	public User updatePic(String filePath, ObjectId uid) {
		User user = userCollection.findOneById(uid.toString());
		System.out.println("uid is ..."+uid);
		user.setImagepath(filePath);
		System.out.println("path is ..."+user.getImagepath());
		userCollection.updateById(uid.toString(), user);
		ProfilePic profilepic = new ProfilePic();
		BasicDBObject query = new BasicDBObject();
		query.put("current", true);
		DBCursor<ProfilePic> cursor = dpCollection.find(query);
		
		if(cursor.hasNext()){
			profilepic = cursor.next();
			profilepic.setCurrent(false);			
		}
		dpCollection.updateById(profilepic.getId(), profilepic);
		profilepic = new ProfilePic();
		Date date = new Date();
		profilepic.setUid(uid);
		profilepic.setPath(filePath);
		profilepic.setCurrent(true);
		profilepic.setUploadTime(date);
		
		WriteResult<ProfilePic, String> pic = dpCollection.insert(profilepic);
		profilepic = pic.getSavedObject();
		System.out.println("Profile pic id is ...... " + profilepic.getId());
		Activity activity = new Activity();
		activity.setUid(uid);
		activity.setActivityId(new ObjectId(profilepic.getId()));
		activity.setDate(date);
		activity.setType("profilepic");
		activity.setHeartBreaks(0);
		activity.setHearts(0);
		activity.setComments(0);
		activityCollection.insert(activity);



		return user;
	}
	public List<ProfilePic> getProfilePic(ObjectId uid) {

		List<ProfilePic> profilePicList = new ArrayList<ProfilePic>();
		BasicDBObject query = new BasicDBObject();
		query.put("uid", uid);
		DBCursor<ProfilePic> cursor = dpCollection.find(query);

		while(cursor.hasNext()){
			ProfilePic profilepic = cursor.next();
			profilePicList.add(profilepic);

		}
		return profilePicList;
	}
	public void uploadPic(String filePath, ObjectId uid) {

		Date time = new Date();
		UploadPic upload = new UploadPic();
		upload.setPath(filePath);
		upload.setUid(uid);
		upload.setUploadTime(time);
		
		WriteResult<UploadPic,String> pic = uploadCollection.insert(upload);
		upload = pic.getSavedObject();
		System.out.println("Upload id is ...... " + upload.getId());
		Activity activity = new Activity();
		activity.setUid(uid);
		activity.setActivityId(new ObjectId(upload.getId()));
		activity.setDate(time);
		activity.setType("uploadpic");
		activity.setHeartBreaks(0);
		activity.setHearts(0);
		activity.setComments(0);
		activityCollection.insert(activity);

	}
	public List<UploadPic> getUploadPic(ObjectId uid) {

		List<UploadPic> uploadPicList = new ArrayList<UploadPic>();
		BasicDBObject query = new BasicDBObject();
		BasicDBObject sort = new BasicDBObject();
		query.put("uid", uid);
		sort.put("time", -1);
		DBCursor<UploadPic> cursor = uploadCollection.find(query).sort(sort);

		while(cursor.hasNext()){

			UploadPic uploadpic = cursor.next();
			uploadPicList.add(uploadpic);

		}

		return uploadPicList;
	}

	public Map<String,Object> myActivity(ObjectId uid) {
		Map<String,Object> hmap = new HashMap<String, Object>();
		DBCollection coll = activitydao.activityCollectionDAO();
		List<Object> myActivityList = new ArrayList<Object>();
		List<DBObject> pipeline = new ArrayList<DBObject>();

		DBObject match = new BasicDBObject("$match",
				new BasicDBObject("uid" , uid)

				);
		pipeline.add(match);

		DBObject lookupFields = new BasicDBObject("from", "uploadpic");
		lookupFields.put("localField","activityId");
		lookupFields.put("foreignField","_id");
		lookupFields.put("as", "uploadpic");  
		pipeline.add(new BasicDBObject("$lookup",lookupFields));

		DBObject profilePicFields = new BasicDBObject("from", "profilepic");
		profilePicFields.put("localField","activityId");
		profilePicFields.put("foreignField","_id");
		profilePicFields.put("as", "profilepic");
		pipeline.add(new BasicDBObject("$lookup",profilePicFields));
		
		DBObject statusFields = new BasicDBObject("from", "status");
		statusFields.put("localField","activityId");
		statusFields.put("foreignField","_id");
		statusFields.put("as", "status");
		pipeline.add(new BasicDBObject("$lookup",statusFields));

		DBObject friendFields = new BasicDBObject("from", "friend");
		friendFields.put("localField","activityId");
		friendFields.put("foreignField","_id");
		friendFields.put("as", "friend");
		pipeline.add(new BasicDBObject("$lookup",friendFields));

		DBObject userfields = new BasicDBObject("from", "user");
		userfields.put("localField","friend.fid");
		userfields.put("foreignField","_id");
		userfields.put("as", "userFriend");
		pipeline.add(new BasicDBObject("$lookup",userfields));
		
		DBObject heartFields = new BasicDBObject("from", "heart");
		heartFields.put("localField","activityId");
		heartFields.put("foreignField","activityId");
		heartFields.put("as", "heart");
		pipeline.add(new BasicDBObject("$lookup",heartFields));

		DBObject sort = new BasicDBObject("$sort",
				new BasicDBObject("date",-1));

		pipeline.add(sort);

		System.out.println(pipeline);

		AggregationOutput output = coll.aggregate(pipeline);

		for (DBObject result : output.results()) {
			@SuppressWarnings("unchecked")
			List<Object> res = (List<Object>) result.get("heart");
			if(!res.isEmpty()){
				
				result.put("noAction",true);
				for(Object db:res){
					//System.out.println(db);
					BasicDBObject object = (BasicDBObject) db;
					object.get("fid");
					
				if(object.get("fid").toString().equals(uid.toString())){
					
					result.put("broken",object.get("broken"));
					result.put("noAction",false);
					break;
				}
					
				}									
			}
			else{
				result.put("noAction",true);
				
			}
			
			
			
			myActivityList.add(result);
			System.out.println(result);
		}

		hmap.put("myActivityList",myActivityList);
		return hmap;
	}
	public void addComment(String commentMessage, ObjectId fid, ObjectId activityId) {
		System.out.println("ADD COmment..................");
		Date date = new Date();
		CommentDAO commentdao = new CommentDAO();		
		JacksonDBCollection<Comment, String> commentCollection = commentdao.commentDAO();
		Comment comment = new Comment();
		comment.setActivityId(activityId);
		comment.setComment(commentMessage);
		comment.setFid(fid);
		comment.setTime(date);
		commentCollection.insert(comment);
		
		BasicDBObject query = new BasicDBObject();
		query.put("activityId", activityId);
		DBCursor<Activity> cursor = activityCollection.find(query);
		if(cursor.hasNext()){
			Activity activity = cursor.next();
			activity.setComments(activity.getComments()+1);
			activityCollection.updateById(activity.getId(), activity);
		}
		

	}
	public Map<String,Object> showComments(ObjectId activityId,int skip,int limit) {
		Map<String, Object> hmap = new HashMap<String, Object>();
		DBCollection coll = activitydao.activityCollectionDAO();
		CommentDAO commentdao = new CommentDAO();
		JacksonDBCollection<Comment, String> commentCollection = commentdao.commentDAO();
		List<DBObject> pipeline = new ArrayList<DBObject>();
		List<Object> commentList = new ArrayList<Object>();
		DBObject match = new BasicDBObject("$match",
				new BasicDBObject("activityId" , activityId)

				);
		pipeline.add(match);

		DBObject lookupFields = new BasicDBObject("from", "comment");
		lookupFields.put("localField","activityId");
		lookupFields.put("foreignField","activityId");
		lookupFields.put("as", "comments");  
		pipeline.add(new BasicDBObject("$lookup",lookupFields));
		DBObject unwindActivity = new BasicDBObject("$unwind","$comments");
		pipeline.add(unwindActivity);
		DBObject friendsFields = new BasicDBObject("from", "user");
		friendsFields.put("localField","comments.fid");
		friendsFields.put("foreignField","_id");
		friendsFields.put("as", "friend"); 

		pipeline.add(new BasicDBObject("$lookup",friendsFields));
		DBObject unwindFriend = new BasicDBObject("$unwind","$friend");
		pipeline.add(unwindFriend);
		DBObject sort = new BasicDBObject("$sort",
				new BasicDBObject("comments.time",-1));
		pipeline.add(sort);

		DBObject skipTo = new BasicDBObject("$skip",skip);
		pipeline.add(skipTo);
		DBObject limitCount = new BasicDBObject("$limit",limit);
		pipeline.add(limitCount);

		// pipeline.add(commentCount);

		System.out.println(pipeline);

		AggregationOutput output = coll.aggregate(pipeline);

		for (DBObject result : output.results()) {
			commentList.add(result);
			System.out.println(result);

		}
		BasicDBObject query = new BasicDBObject();
		query.put("activityId", activityId);
		long commentCount =  commentCollection.count(query);

		hmap.put("commentList", commentList);
		hmap.put("count", commentCount);
		hmap.put("skip", skip+limit);
		hmap.put("limit", 10);
		if(skip+limit<commentCount){
			hmap.put("showMore", true);
		}
		else
		{
			hmap.put("showMore", false);
		}
		return hmap;
	}

	public Activity findActivityLink(String id) {
		ActivityDAO activitydao = new ActivityDAO();
		JacksonDBCollection<Activity, String> activityCollection = activitydao.activityDAO();
		Activity activity = activityCollection.findOneById(id);

		return activity;
	}
	public Map<String, Object> post(ObjectId activityId, ObjectId uid) {
		Map<String, Object> hmap = new HashMap<String, Object>();
		ActivityDAO activitydao = new ActivityDAO();
		DBCollection activityCollection = activitydao.activityCollectionDAO();
		List<DBObject> pipeline = new ArrayList<DBObject>();

		DBObject match = new BasicDBObject("$match",
				new BasicDBObject("activityId" , activityId)

				);
		pipeline.add(match);
		DBObject lookupFields = new BasicDBObject("from", "profilepic");
		lookupFields.put("localField","activityId");
		lookupFields.put("foreignField","_id");
		lookupFields.put("as", "profilepic");  
		pipeline.add(new BasicDBObject("$lookup",lookupFields));
		DBObject uploadFields = new BasicDBObject("from", "uploadpic");
		uploadFields.put("localField","activityId");
		uploadFields.put("foreignField","_id");
		uploadFields.put("as", "uploadpic");  
		pipeline.add(new BasicDBObject("$lookup",uploadFields));
		DBObject heartFields = new BasicDBObject("from", "heart");
		heartFields.put("localField","activityId");
		heartFields.put("foreignField","activityId");
		heartFields.put("as", "heart");
		pipeline.add(new BasicDBObject("$lookup",heartFields));

		AggregationOutput output = activityCollection.aggregate(pipeline);
		
		for (DBObject result : output.results()) {
			//System.out.println(result.get("heart"));
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>) result.get("heart");
			List<Object> res = list;
			if(!res.isEmpty()){
				
				result.put("noAction",true);
				for(Object db:res){
					//System.out.println(db);
					BasicDBObject object = (BasicDBObject) db;
					object.get("fid");
					
				if(object.get("fid").toString().equals(uid.toString())){
					
					result.put("broken",object.get("broken"));
					result.put("noAction",false);
					break;
				}
					
				}									
			}
			else{
				result.put("noAction",true);
				
			}
			
		}
		
		
		hmap.put("activity",output.results());

		return hmap;
	}
	public Map<String, Object> heartIncrease(ObjectId activityId, ObjectId uid, boolean broken) {
		Map<String, Object> hmap = new HashMap<String, Object>();
		HeartDAO heartdao = new HeartDAO();
		JacksonDBCollection<Heart, String> heartCollection = heartdao.heartDAO();
		BasicDBObject activityQuery = new BasicDBObject();
		activityQuery.put("activityId", activityId);
		DBCursor<Activity> activityCursor = activityCollection.find(activityQuery);
		
		Heart heart = new Heart();
		BasicDBObject query = new BasicDBObject();
		query.put("activityId", activityId);
		query.put("broken", broken);
		query.put("fid", uid);
		DBCursor<Heart> cursor = heartCollection.find(query);
		if(cursor.hasNext()){
			heart = cursor.next();
			heartCollection.remove(query);
			hmap.put("remove", true);
		}
		else{
		query.replace("broken",!broken);
		System.out.println("Broken revert is " + !broken);
		DBCursor<Heart> cursor1 = heartCollection.find(query);
		if(cursor1.hasNext()){
			heart = cursor1.next();
			heart.setBroken(broken);
			heartCollection.updateById(heart.getId(),heart);
			hmap.put("revert", true);
			hmap.put("remove", false);
		}
		else{
			heart.setActivityId(activityId);
			heart.setFid(uid);
			heart.setBroken(broken);
			heartCollection.insert(heart);
			hmap.put("revert", false);
			hmap.put("remove", false);
		}
		}
		query.remove("fid");
		query.replace("broken", false);
		long countHeart = heartCollection.count(query);
		query.replace("broken", true);
		long countBroken = heartCollection.count(query);
		hmap.put("heartCount", countHeart);
		hmap.put("brokenCount", countBroken);
		if(activityCursor.hasNext()){
			Activity activity = activityCursor.next();			
			activity.setHearts(countHeart);			
			activity.setHeartBreaks(countBroken);
			
			activityCollection.updateById(activity.getId(), activity);
		}
		return hmap;
	}
	public void addStatus(ObjectId uid, String statusText) {
		StatusDAO statusdao = new StatusDAO();
		JacksonDBCollection<Status, String> statusCollection = statusdao.statusDAO();
		Date date = new Date();
		Status status = new Status();
		status.setDate(date);
		status.setStatusText(statusText);
		status.setUid(uid);
		WriteResult<Status, String> stat = statusCollection.insert(status);
		status = stat.getSavedObject();
		Activity activity = new Activity();
		activity.setActivityId(new ObjectId(status.getId()));
		activity.setComments(0);
		activity.setDate(date);
		activity.setHeartBreaks(0);
		activity.setHearts(0);
		activity.setType("status");
		activity.setUid(uid);
		activityCollection.insert(activity);
		
	}
	public HighChart stackedgraph(ObjectId uid) {
		
		List<Data> Oaccept = new ArrayList<Data>();
		List<Data> Ounresponded =new  ArrayList<Data>();
		List<Data> Oreject =new  ArrayList<Data>();
		List<Data> Iaccept =new  ArrayList<Data>();
		List<Data> Iunresponded =new  ArrayList<Data>();
		List<Data> Ireject =new  ArrayList<Data>();
		
		BasicDBObject request = new BasicDBObject();
		request.put("uid", uid);
		
		FriendDAO frienddao = new FriendDAO();
		JacksonDBCollection<Friend, String> friendCollection = frienddao.friendDAO();
	
		for(int i = 0;i<12;i++){
			DBCursor<Friend> cursor1 = friendCollection.find(request);
		
		int oucount = 0;
		int oacount = 0;
		int orcount = 0;
		int iucount = 0;
		int iacount = 0;
		int ircount = 0;
		
			while(cursor1.hasNext()){
				Friend friend = cursor1.next();
				Calendar cal = Calendar.getInstance();
				cal.setTime(friend.getRequestDate());
				int requestMonth = cal.get(Calendar.MONTH);
				String status = friend.getStatus();
				if(requestMonth==i){
					if(status.equalsIgnoreCase("Request Sent")){
						oucount += 1;
						//graph.add(1);
					}
					else if(status.equalsIgnoreCase("Pending Request")){
						iucount += 1;
						//graph.add(1);
					}
					else if(friend.getResponseDate()!=null){
						cal.setTime(friend.getResponseDate());
						int responseMonth = cal.get(Calendar.MONTH);
						System.out.println("Month..........." + requestMonth);
						
						if(status.equalsIgnoreCase("My Request Accepted")){
							if(requestMonth!=responseMonth){
								oucount += 1;
								
							}
							oacount += 1;

						}
						else if(status.equalsIgnoreCase("My Request Rejected")){
							orcount += 1;
						}
						
						if(status.equalsIgnoreCase("I Accepted Request")){
							if(requestMonth!=responseMonth){
								iucount += 1;
							}
							iacount += 1;

						}
						else if(status.equalsIgnoreCase("I Rejected Request")){
							ircount += 1;
						}
					}
				}
				
			}
			Data d = new Data();
			d.setY(oacount);
			Oaccept.add(d);
			
			d = new Data();
			d.setY(oucount);
			Ounresponded.add(d);			

			d = new Data();
			d.setY(orcount);
			Oreject.add(d);
			

			d = new Data();
			d.setY(iacount);
			Iaccept.add(d);
			
			d = new Data();
			d.setY(iucount);
			Iunresponded.add(d);
			
			d = new Data();
			d.setY(ircount);
			Ireject.add(d);		
		}

		
		Chart chart = new Chart();
		chart.setType("column");
		chart.setRenderTo("friendRequestsByMonth");
		
		Title title = new Title();
		title.setText("Monthly record of making new friends");
		
		String[] list = {"jan","Feb","Mar", "Apr", "May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		List<String> categories = new ArrayList<String>();
		categories.addAll(Arrays.asList(list));
		
		XAxis xaxis = new XAxis();
		xaxis.setCategories(categories);
		
		Title yTitle = new Title();
		yTitle.setText("Total Requests");
		YAxis yaxis = new YAxis();
		yaxis.setAllowDecimals(false);
		yaxis.setMin(0);
		yaxis.setTitle(yTitle);
		
		ToolTip tooltip = new ToolTip();
		tooltip.setFormatter("function(){return '<b>' + this.x + '</b><br/>' +this.series.name + ': ' + this.y + '<br/>' + 'Total: ' + this.point.stackTotal;}");
		
		Column column = new Column();
		column.setStacking("normal");
		PlotOptions plotoptions = new PlotOptions();
		plotoptions.setColumn(column);
		
		Series iAccept = new Series();
		iAccept.setName("I Accepted");
		iAccept.setStack("Incoming requests");
		iAccept.setData(Iaccept);
		Series iReject = new Series();
		iReject.setName("I Rejected");
		iReject.setStack("Incoming requests");
		iReject.setData(Ireject);
		Series iUnresponded = new Series();
		iUnresponded.setName("I did not respond to");
		iUnresponded.setStack("Incoming requests");
		iUnresponded.setData(Iunresponded);
		Series oAccept = new Series();
		oAccept.setName("My Accepted Requests");
		oAccept.setStack("Outgoing requests");
		oAccept.setData(Oaccept);
		Series oReject = new Series();
		oReject.setName("My Rejected Requests");
		oReject.setStack("Outgoing requests");
		oReject.setData(Oreject);
		Series oUnresponded = new Series();
		oUnresponded.setName("My unresponded requests");
		oUnresponded.setStack("Outgoing requests");
		oUnresponded.setData(Ounresponded);
		
		List<Series> series = new ArrayList<Series>();
		series.add(iAccept);
		series.add(iReject);
		series.add(iUnresponded);
		series.add(oAccept);
		series.add(oReject);
		series.add(oUnresponded);
		
		HighChart stackedChart = new HighChart();
		stackedChart.setChart(chart);
		stackedChart.setPlotOptions(plotoptions);
		stackedChart.setSeries(series);
		stackedChart.setTitle(title);
		//stackedChart.setTooltip(tooltip);
		stackedChart.setxAxis(xaxis);
		stackedChart.setyAxis(yaxis);
		
		
		return stackedChart;
	}
	public HighChart requestpiechart(ObjectId uid) {
		
		FriendDAO friendDAO = new FriendDAO();
		
		JacksonDBCollection<Friend, String> friendCollection = friendDAO.friendDAO();
		BasicDBObject request = new BasicDBObject();
		request.put("uid", uid);
		request.put("status","I Accepted Request");
		long iacount = friendCollection.getCount(request);
		request.replace("status", "I Rejected Request");
		long ircount = friendCollection.getCount(request);
		System.out.println("I accepted..............." + iacount);
		System.out.println("I rejected..............." + ircount);
		request.replace("status", "Pending Request");
		long iucount = friendCollection.getCount(request);
		request.replace("status", "My Request Accepted");
		long oacount = friendCollection.getCount(request);
		request.replace("status", "My Request Rejected");
		long orcount = friendCollection.getCount(request);
		request.replace("status", "Request Sent");
		long oucount = friendCollection.getCount(request);
		
		Data iAccept = new Data();
		iAccept.setY(iacount);
		iAccept.setName("I Accept");
		iAccept.setSelected(true);
		iAccept.setSliced(true);
		
		Data iReject = new Data();
		iReject.setY(ircount);
		iReject.setName("I Reject");
		
		Data iUnresponded = new Data();
		iUnresponded.setY(iucount);
		iUnresponded.setName("I did not respond");
		
		Data oAccept = new Data();
		oAccept.setY(oacount);
		oAccept.setName("Accepted");
		
		Data oReject = new Data();
		oReject.setY(orcount);
		oReject.setName("Rejected");
		
		Data oUnresponded = new Data();
		oUnresponded.setY(oucount);
		oUnresponded.setName("Unresponded");
		List<Data> data = new ArrayList<Data>();
		
		data.add(iAccept);
		data.add(iReject);
		data.add(iUnresponded);
		data.add(oAccept);
		data.add(oReject);
		data.add(oUnresponded);
		
		Series serie = new Series();
		serie.setName("Friends");
		serie.setData(data);
		serie.setColorByPoint(true);
		List<Series> series = new ArrayList<Series>();
		series.add(serie);
		
		DataLabels datalabels = new DataLabels();
		datalabels.setEnabled(false);
		
		Pie pie = new Pie();
		pie.setAllowPointSelect(true);
		pie.setCursor("pointer");
		pie.setDataLabels(datalabels);
		pie.setShowInLegend(true);
		
		
		PlotOptions plotOptions = new PlotOptions();
		plotOptions.setPie(pie);
		
		ToolTip tooltip = new ToolTip();
		tooltip.setPointFormat("{series.name}: <b>{point.y}</b>");
		
		Title title = new Title();
		title.setText("Total Friend Requests");
		
		Chart chart = new Chart();
		chart.setRenderTo("requestStatusPiechart");
		chart.setPlotBackgroundColor(null);
		chart.setPlotBorderWidth(null);
		chart.setPlotShadow(false);
		chart.setType("pie");
		
		HighChart highcharts = new HighChart();
		
		highcharts.setChart(chart);
		highcharts.setPlotOptions(plotOptions);
		highcharts.setSeries(series);
		highcharts.setTitle(title);
		highcharts.setTooltip(tooltip);
		
		return highcharts;
		
	}
	public void updateProfile(ObjectId uid, String name, String email, String address, String mobile, String aboutme, String country, String city) {
		User user = userCollection.findOneById(uid.toString());
		user.setName(name, "", "");
		user.setCity(city);
		user.setCountry(country);
		user.setEmail(email);
		user.setMobile(mobile);
		userCollection.updateById(uid.toString(), user);
		
		UserInfo userinfo = new UserInfo();
		UserInfoDAO userinfodao = new UserInfoDAO();
		JacksonDBCollection<UserInfo, String> userinfoCollection = userinfodao.userInfoDAO();
		BasicDBObject query = new BasicDBObject();
		query.put("uid", uid);
		DBCursor<UserInfo> cursor = userinfoCollection.find(query);
		boolean nextCursor = cursor.hasNext();
		if(nextCursor){
			userinfo = cursor.next();
		}
		userinfo.setAboutme(aboutme);
		userinfo.setUid(uid);
		userinfo.setAddress(address);
		if(nextCursor){
			userinfoCollection.updateById(userinfo.getId(), userinfo);
		}
		else{
			userinfoCollection.insert(userinfo);
		}
		
	}
	public String invite(ObjectId uid,String recieverEmail){

		User user = userCollection.findOneById(uid.toString());
		BasicDBObject query = new BasicDBObject();
		query.put("email", recieverEmail);
		DBCursor<User> cursor = userCollection.find(query);
		if(cursor.hasNext())
			return null;


		InvitationDAO invitationDAO = new InvitationDAO();
	
		JacksonDBCollection<Invite, String> invitationCollection = invitationDAO.invitationDAO();
		query.put("senderId", uid);
		DBCursor<Invite> cursor1 = invitationCollection.find(query);
		if(cursor1.hasNext()){
			return "Already invited";
		}
		Invite invite = new Invite();
		invite.setSenderId(uid);
		invite.setRecieverEmail(recieverEmail);
		invitationCollection.insert(invite);



		return user.getName();

	}
public Map<String, Object> usertable(int limit, int skip,String ascending,String sortBy) {
		
		List<User> userList = new ArrayList<User>();
		Map<String,Object> hmap = new HashMap<String, Object>();
		
		long totalCount = userCollection.getCount();
		BasicDBObject query = new BasicDBObject();
		if(ascending.equalsIgnoreCase("true")){
			query.put(sortBy, -1);
		}
		else
			query.put(sortBy, 1);
		DBCursor<User> cursor = userCollection.find().skip(skip).limit(limit).sort(query);

		
		while(cursor.hasNext()){
			User user = cursor.next();
			userList.add(user);
			System.out.println("Name ............. " + user.getName());
		}
		hmap.put("total", totalCount);
		hmap.put("rows", userList);
		return hmap;
	}
public void updateUser(String id,String name, String email, String lastLoggedIn, String country) {
	try {	

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		Date datetime = format.parse(lastLoggedIn);
		System.out.println("Date time is ............. " + datetime);
		System.out.println(id);
		
		User user = userCollection.findOneById(id);
		System.out.println(email);
		user.setName(name,"","");;
		user.setEmail(email);;
		user.setLastLoggedInAt(datetime);;
		
		user.setCountry(country);;
		
		userCollection.updateById(id, user);


	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}


}
public void deleteUser(String id) {
	
	System.out.println(id);
	userCollection.removeById(id);


}
public void editUser(String id, String field, String change) {
	try {
		
		User user = userCollection.findOneById(id);
		String fieldName = field.substring(0,1).toUpperCase() + field.substring(1);
		System.out.println(fieldName);
		
		Class<?> classType = Email.class.getDeclaredField(field).getType();
		
		String	fieldType = classType.getSimpleName();
		System.out.println(classType);
		System.out.println(fieldType);

		Object changes = change;
		if(!fieldType.equalsIgnoreCase("String")){
			Utility utility = new Utility();
			changes = utility.changeType(fieldType.toString(), change);
		}
		System.out.println("Change is " + changes);
		String callMethod = "set"+fieldName;

		Method method = Email.class.getDeclaredMethod(callMethod,classType);

		method.invoke(user,changes);

		userCollection.updateById(id, user);
	} 
	catch (Exception e) {

		e.printStackTrace();
	} 
}
public List<Points> getPoints(ObjectId uid) {
	PointDAO pointdao = new PointDAO();
	JacksonDBCollection<Points, String> pointCollection = pointdao.pointDAO();
	List<Points> pointList = new ArrayList<Points>();
	BasicDBObject query = new BasicDBObject();
	query.put("uid", uid);
	DBCursor<Points> cursor = pointCollection.find(query);
	while(cursor.hasNext()){
		Points points = cursor.next();
		pointList.add(points);
	}
	return pointList;
}
public Map<String,Object> referraltable(int limit, int skip, String ascending, String sortBy) {
	List<Object> referralList = new ArrayList<Object>();
	Map<String,Object> hmap = new HashMap<String, Object>();
	PointDAO pointdao = new PointDAO();
	//JacksonDBCollection<Points, String> pointCollection = pointdao.pointDAO();
	//long totalCount = pointCollection.getCount();
	int sortOrder;
	if(ascending.equalsIgnoreCase("true")){
		sortOrder = -1;
	}
	else
		sortOrder = 1;
	referralList = pointdao.singleAggregation("user",sortOrder,limit,skip,sortBy);
	
	//hmap.put("total", totalCount);
	hmap.put("rows", referralList);
	return hmap;

}
public void saveSiteSettings(String type, String adminName, int points, Date date) {
	
	PointSettingsDAO settingsdao= new PointSettingsDAO();
	JacksonDBCollection<PointSettings, String> pointsettingCollection = settingsdao.pointSettingsDAO();
	PointSettings pointSettings = new PointSettings();
	pointSettings.setAdminName(adminName);
	pointSettings.setDate(date);
	pointSettings.setPoints(points);
	pointSettings.setType(type);
	pointsettingCollection.insert(pointSettings);
	
}
public Map<String,Object> getLatestPoints() {
	Map<String,Object> hmap = new HashMap<String, Object>();
	PointSettingsDAO settingsdao= new PointSettingsDAO();
	int friendPoint = 0;
	int fofPoint = 0;
	int memberPoint = 0;
	JacksonDBCollection<PointSettings, String> pointsettingCollection = settingsdao.pointSettingsDAO();
	BasicDBObject query = new BasicDBObject();
	query.put("type", "friendPoint");
	BasicDBObject sort = new BasicDBObject();
	sort.put("date", -1);
	DBCursor<PointSettings> cursor = pointsettingCollection.find(query).sort(sort).limit(1);
	if(cursor.hasNext()){
		PointSettings pointSettings = cursor.next();
		friendPoint = pointSettings.getPoints();
	}
	query.replace("type", "fofPoint");
	DBCursor<PointSettings> cursor1 = pointsettingCollection.find(query).sort(sort).limit(1);
	if(cursor1.hasNext()){
		PointSettings pointSettings = cursor1.next();
		fofPoint = pointSettings.getPoints();
	}
	query.put("type", "friendPoint");
	DBCursor<PointSettings> cursor2 = pointsettingCollection.find(query).sort(sort).limit(1);
	if(cursor2.hasNext()){
		PointSettings pointSettings = cursor2.next();
		memberPoint = pointSettings.getPoints();
	}
	hmap.put("friendPoint", friendPoint);
	hmap.put("fofPoint", fofPoint);
	hmap.put("memberPoint", memberPoint);
	return hmap;
}
public String changePassword(String currentPass, String newPass, ObjectId uid) {
	
	User user = userCollection.findOneById(uid.toString());
	String oldPass = user.getPassword();
	if(!oldPass.equals(currentPass)){
		return "wrong";
	}
	
	user.setPassword(newPass);
	userCollection.updateById(uid.toString(),user);
	return "changed";
}
public UserInfo getmyInfo(ObjectId uid) {
	UserInfoDAO userInfoDAO = new UserInfoDAO();
	JacksonDBCollection<UserInfo, String> userinfoCollection = userInfoDAO.userInfoDAO();
	BasicDBObject query = new BasicDBObject();
	query.put("uid", uid);
	UserInfo userInfo = userinfoCollection.findOne(query);
	return userInfo;
	
}
public void storeChat(ObjectId uid, ObjectId fid, String text) {
	ChatDAO chatdao = new ChatDAO();
	Date sentAt = new Date();
	JacksonDBCollection<Chat, String> chatCollection = chatdao.chatDAO();
	Chat chat = new Chat();
	chat.setDelivered(false);
	chat.setSenderId(uid);
	chat.setRecieverId(fid);
	chat.setSentAt(sentAt);
	chat.setDeliveredAt(null);
	chat.setText(text);
	chatCollection.insert(chat);
	
}
public List<Object> getmessage(ObjectId recieverId) {
	
	ChatDAO chatdao = new ChatDAO();
	Date deliveredAt = new Date();
	JacksonDBCollection<Chat, String> chatCollection = chatdao.chatDAO();
	List<Object> chatList = new ArrayList<Object>();
	
	chatList = chatdao.singleAggregation("user", 30, recieverId, "recieverId");
	
	BasicDBObject query = new BasicDBObject();
	query.put("recieverId", recieverId);
	query.put("delivered", false);
	DBCursor<Chat> cursor = chatCollection.find(query);
	while(cursor.hasNext()){
		Chat chat = cursor.next();
		
		chat.setDeliveredAt(deliveredAt);
		chat.setDelivered(true);
		chatCollection.updateById(chat.getId(), chat);
	}
	return chatList;
}
public Map<String,Object> getChat(ObjectId uid, ObjectId fid) {
	
	List<Chat> mychat = new ArrayList<Chat>();
	Map<String,Object> hmap = new HashMap<String, Object>();
	ChatDAO chatdao = new ChatDAO();
	JacksonDBCollection<Chat, String> chatCollection = chatdao.chatDAO();
	
	
	
	BasicDBObject query = new BasicDBObject("$or",Arrays.asList(new BasicDBObject("$and",Arrays.asList(new BasicDBObject("recieverId",uid)
			.append("senderId", fid))),new BasicDBObject("$and",Arrays.asList(new BasicDBObject("recieverId",fid)
			.append("senderId", uid)))));
		//db.chat.find({ "$or" : [ { "$and" : [ { "recieverId" : ObjectId("5af547cde2e9a70900b73338") , "senderId" : ObjectId("5af16a61e2e9a708c090917e")}]},{ "$and" : [ { "senderId" : ObjectId("5af547cde2e9a70900b73338") , "recieverId" : ObjectId("5af16a61e2e9a708c090917e")}]}] , "delivered" : true}).pretty()

	
	query.put("delivered", true);
	
	DBCursor<Chat> cursor = chatCollection.find(query).limit(30);
	while(cursor.hasNext()){
		Chat chat = cursor.next();
		
		if(chat.getRecieverId().equals(uid)) {
			chat.setMe(true);
		}
		
		mychat.add(chat);
	}
	
	
	
	hmap.put("mychat", mychat);
	//hmap.put("friendchat", friendchat);
	return hmap;
}


}
