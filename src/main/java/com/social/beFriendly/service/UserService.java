package com.social.beFriendly.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.social.beFriendly.model.ProfilePic;
import com.social.beFriendly.model.UploadPic;
import com.social.beFriendly.model.User;
import com.social.scframework.service.DBConnection;


public class UserService {
	DBConnection db = new DBConnection();
	DB mongo = db.getDB("BeFriendly");
	DBCollection collec = mongo.getCollection("user");
	JacksonDBCollection<User, String> coll = JacksonDBCollection.wrap(collec,User.class, String.class);
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
		DBCursor<User> cursor = coll.find(query);

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

		WriteResult<User, String> reg = coll.insert(registration);
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
			DBCursor<User> cursor = coll.find(query);
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
			DBCursor<User> cursor = coll.find(query);
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

		User user = coll.findOneById(uid);
		System.out.println(uid);
		System.out.println(user.getName());
		user.setLoggedIn(true);
		coll.updateById(uid, user);
		return user.getLoggedIn();

	}
	public User findOneById(String uid) {

		User user = coll.findOneById(uid);
		return user;
	}

	public void logout(String uid){

		Date date = new Date();		

		//System.out.println("Date is "+now);
		User user = coll.findOneById(uid);
		user.setLoggedIn(false);
		user.setLastLoggedInAt(date);
		coll.updateById(uid, user);
	}
	public List<User> searchUser(String search) {
		
		BasicDBObject query = new BasicDBObject();
		query.put("name", new BasicDBObject("$regex" , "(?i).*"+search+".*"));
		List<User> searchedUser = new ArrayList<User>();
		DBCursor<User> cursor = coll.find(query);
		User user = new User();
		while(cursor.hasNext()){
			user = cursor.next();
			searchedUser.add(user);
			System.out.println("name " + user.getName());
		}
		query.clear();
		query.put("email", new BasicDBObject("$regex" , "(?i).*"+search+".*"));
		DBCursor<User> EmailCursor = coll.find(query);
		while(EmailCursor.hasNext()){
			user = EmailCursor.next();
			Boolean flag = true;
				for(User u:searchedUser){
					
					if(u.getEmail().equals(user.getEmail())){
						flag = false;
						break;
					}
				}
				if(flag==true)
				searchedUser.add(user);		
				
			
			System.out.println("email " + user.getName());
		}
		
		return searchedUser;
	}
	public User updatePic(String filePath, String uid) {
		User user = coll.findOneById(uid);
		System.out.println("uid is ..."+uid);
		user.setImagepath(filePath);
		System.out.println("path is ..."+user.getImagepath());
		coll.updateById(uid, user);

		DBCollection collection = mongo.getCollection("profilepic");
		JacksonDBCollection<ProfilePic, String> coll1 = JacksonDBCollection.wrap(collection,ProfilePic.class, String.class);
		
		BasicDBObject query = new BasicDBObject();
		query.put("current", true);
		DBCursor<ProfilePic> cursor = coll1.find(query);
		ProfilePic profilepic = new ProfilePic();
		if(cursor.hasNext()){
			profilepic = cursor.next();
			profilepic.setCurrent(false);			
		}
		coll1.updateById(profilepic.getId(), profilepic);
		profilepic = new ProfilePic();
		Date date = new Date();
		profilepic.setUid(uid);
		profilepic.setPath(filePath);
		profilepic.setCurrent(true);
		profilepic.setUploadTime(date);
		coll1.insert(profilepic);
		
		
		return user;
	}
	public List<ProfilePic> getProfilePic(String uid) {
		
		List<ProfilePic> profilePicList = new ArrayList<ProfilePic>();
		DBCollection collection = mongo.getCollection("profilepic");
		JacksonDBCollection<ProfilePic, String> coll1 = JacksonDBCollection.wrap(collection,ProfilePic.class, String.class);
		BasicDBObject query = new BasicDBObject();
		query.put("uid", uid);
		DBCursor<ProfilePic> cursor = coll1.find(query);
		while(cursor.hasNext()){
			ProfilePic profilepic = cursor.next();
			profilePicList.add(profilepic);
			
		}
		return profilePicList;
	}
	public User uploadPic(String filePath, String uid) {
		List<UploadPic> profilePicList = new ArrayList<UploadPic>();
		DBCollection collection = mongo.getCollection("uploadPic");
		JacksonDBCollection<UploadPic, String> coll1 = JacksonDBCollection.wrap(collection,UploadPic.class, String.class);
		return null;
	}	
}


