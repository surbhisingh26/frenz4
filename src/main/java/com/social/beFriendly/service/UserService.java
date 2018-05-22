package com.social.beFriendly.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.social.beFriendly.DAO.CommentDAO;
import com.social.beFriendly.DAO.ProfilePicDAO;
import com.social.beFriendly.DAO.UploadPicDAO;
import com.social.beFriendly.DAO.UserDAO;
import com.social.beFriendly.model.Activity;
import com.social.beFriendly.model.Comment;
import com.social.beFriendly.model.ProfilePic;
import com.social.beFriendly.model.UploadPic;
import com.social.beFriendly.model.User;


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
		//DBCollection collection = mongo.getCollection("invitation");
		/*JacksonDBCollection<Invite, String> coll1 = JacksonDBCollection.wrap(collection,Invite.class, String.class);

		NotificationService notificationservice = new NotificationService();
		String link = "points";
		Date Ndate = new Date();
		BasicDBObject query1 = new BasicDBObject();
		query1.put("recieverEmail", email);
		DBCursor<Invite> cursor1 = coll1.find(query1);

		while(cursor1.hasNext()){
			Invite invite = cursor1.next();
			String userId = invite.getSenderId();

			User user = coll.findOneById(userId);
			EmailService emailservice = new EmailService();
			String mailStatus = emailservice.checkStatus(email, user.getUsername(),"inviteToJoin");
			if(mailStatus.equalsIgnoreCase("Sent")){
				user.setPoints(user.getPoints()+50);
				String userEmail = user.getEmail();
				coll.updateById(userId, user);
				String username = user.getName();
				BasicDBObject query2 = new BasicDBObject();

				query2.put("recieverEmail", userEmail);
				DBCursor<Invite> cursor2 = coll1.find(query2);
				while(cursor2.hasNext()){
					Invite invite1 = cursor2.next();
					String secondaryUserId = invite1.getSenderId();
					User user1 = coll.findOneById(secondaryUserId);
					user1.setPoints(user1.getPoints()+10);
					coll.updateById(secondaryUserId, user1);
					coll1.remove(query2);
					notificationservice.send(secondaryUserId,"Congratulation!!! You have earned 10 points reward on joining of "+registration.getName()+" invited by your friend "+username,link,Ndate);
				}

				System.out.println("REGISTRATION ID....................."+ registration.getId());

				notificationservice.send(userId,"Congratulation!!! You have earned 50 points reward on joining of "+registration.getName(),link,Ndate);
			}
		}

		notificationservice.send(registration.getId(),"Welcome "+fname+" "+lname+"\n Congratulation!!! You have been rewarded by 50 points in your account",link,Ndate);

		 */

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

		BasicDBObject query = new BasicDBObject();
		query.put("current", true);
		DBCursor<ProfilePic> cursor = dpCollection.find(query);
		ProfilePic profilepic = new ProfilePic();
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
		
	
		DBObject sort = new BasicDBObject("$sort",
	            new BasicDBObject("date",-1));
		
	     pipeline.add(sort);

		System.out.println(pipeline);

		AggregationOutput output = coll.aggregate(pipeline);
		
		for (DBObject result : output.results()) {
			myActivityList.add(result);
			System.out.println(result);
		}

		hmap.put("myActivityList",myActivityList);
		return hmap;
	}
	public void addComment(String commentMessage, ObjectId fid, ObjectId activityId) {
		Date date = new Date();
		CommentDAO commentdao = new CommentDAO();		
		JacksonDBCollection<Comment, String> commentCollection = commentdao.commentDAO();
		Comment comment = new Comment();
		comment.setActivityId(activityId);
		comment.setComment(commentMessage);
		comment.setFid(fid);
		comment.setTime(date);
		commentCollection.insert(comment);
		
	}
	public Map<String,Object> showComments(ObjectId activityId) {
		Map<String, Object> hmap = new HashMap<String, Object>();
		DBCollection coll = activitydao.activityCollectionDAO();
		
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
		
		DBObject sort = new BasicDBObject("$sort",
	            new BasicDBObject("date",-1));
		
	     pipeline.add(sort);

		System.out.println(pipeline);

		AggregationOutput output = coll.aggregate(pipeline);
		
		for (DBObject result : output.results()) {
			commentList.add(result);
			System.out.println(result);
		}
		hmap.put("commentList", commentList);
		return hmap;
	}	
}


