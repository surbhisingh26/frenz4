package com.social.beFriendly.service;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import com.mongodb.BasicDBObject;

import com.social.beFriendly.DAO.EmailDAO;
import com.social.beFriendly.DAO.UnsubscribeDAO;
import com.social.beFriendly.model.Email;
import com.social.beFriendly.model.Unsubscribe;
import com.social.scframework.App.Utility;

public class EmailService {
	
	public String email(String from, String purpose, String recieverEmail, String status, String subject) {
		EmailDAO emaildao = new EmailDAO();
		JacksonDBCollection<Email, String> emailCollection = emaildao.emailDAO();
		Date date = new Date();
		Email email = new Email();
		email.setDate(date);
		email.setFrom(from);
		email.setPurpose(purpose);
		email.setRecieverEmail(recieverEmail);
		email.setStatus(status);
		email.setSubject(subject);
		email.setViewCount(0);
		WriteResult<Email, String> em = emailCollection.insert(email);
		email  = em.getSavedObject();
		return email.getId();

	}
	public void updateEmail(String id, String status) {
		EmailDAO emaildao = new EmailDAO();
		JacksonDBCollection<Email, String> emailCollection = emaildao.emailDAO();
		Email email = emailCollection.findOneById(id);
		email.setStatus(status);
		emailCollection.updateById(id, email);

	}
	public Boolean checkSubscription(String email){
		UnsubscribeDAO unsubscribedao = new UnsubscribeDAO();
		JacksonDBCollection<Unsubscribe, String> unsubscribeCollection = unsubscribedao.unsubscribeDAO();
		BasicDBObject query = new BasicDBObject();
		query.put("email", email);
		DBCursor<Unsubscribe> cursor = unsubscribeCollection.find(query);
		if(cursor.hasNext()){
			return true;
		}
		return false;
	}
	public void unsubscribe(String email) {
		UnsubscribeDAO unsubscribedao = new UnsubscribeDAO();
		JacksonDBCollection<Unsubscribe, String> unsubscribeCollection = unsubscribedao.unsubscribeDAO();
		Unsubscribe unsubscribe = new Unsubscribe();
		Date date = new Date();
		unsubscribe.setEmails(email);
		unsubscribe.setDate(date);
		unsubscribeCollection.insert(unsubscribe);		
	}
	public String checkStatus(String recieverEmail, String username,String purpose ){
		
		EmailDAO emaildao = new EmailDAO();
		JacksonDBCollection<Email, String> emailCollection = emaildao.emailDAO();
		BasicDBObject query = new BasicDBObject();
		query.put("recieverEmails", recieverEmail);
		query.put("from", username);
		query.put("purpose",purpose);
		DBCursor<Email> cursor = emailCollection.find(query);
		Email email = cursor.next();
		return email.getStatus();


	}
	public Map<String,Object> emailtable(int limit, int skip,String ascending,String sortBy) {
		EmailDAO emaildao = new EmailDAO();
		JacksonDBCollection<Email, String> emailCollection = emaildao.emailDAO();
		List<Email> emailList = new ArrayList<Email>();
		Map<String,Object> hmap = new HashMap<String, Object>();
		
		long totalCount = emailCollection.getCount();
		System.out.println("total..................." + totalCount);
		BasicDBObject query = new BasicDBObject();
		if(ascending.equalsIgnoreCase("true")){
			query.put(sortBy, -1);
		}
		else
			query.put(sortBy, 1);
		DBCursor<Email> cursor = emailCollection.find().skip(skip).limit(limit).sort(query);

		System.out.println("Cursor..........");
		while(cursor.hasNext()){
			Email email = cursor.next();
			emailList.add(email);
			System.out.println("FRom ............. " + email.getFrom());
		}
		hmap.put("total", totalCount);
		hmap.put("rows", emailList);
		return hmap;
	}
	public void updateEmail(String id,String recieverEmail, String subject, String purpose, String from, String date, String status,String view) {
		try {	
			EmailDAO emaildao = new EmailDAO();
			JacksonDBCollection<Email, String> emailCollection = emaildao.emailDAO();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			Date datetime = format.parse(date);
			System.out.println("Date time is ............. " + datetime);
			System.out.println(id);
			
			Email email = emailCollection.findOneById(id);
			System.out.println(email);
			email.setDate(datetime);
			email.setFrom(from);
			email.setPurpose(purpose);
			email.setRecieverEmail(recieverEmail);
			email.setStatus(status);
			email.setViewCount(Integer.parseInt(view));
			email.setSubject(subject);
			emailCollection.updateById(id, email);


		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	public void deleteeEmail(String id) {
		EmailDAO emaildao = new EmailDAO();
		JacksonDBCollection<Email, String> emailCollection = emaildao.emailDAO();
		System.out.println(id);
		emailCollection.removeById(id);


	}
	public void editemail(String id, String field, String change) {
		try {
			EmailDAO emaildao = new EmailDAO();
			JacksonDBCollection<Email, String> emailCollection = emaildao.emailDAO();
			Email email = emailCollection.findOneById(id);
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

			method.invoke(email,changes);

			emailCollection.updateById(id, email);
		} 
		catch (Exception e) {

			e.printStackTrace();
		} 
	}

}
