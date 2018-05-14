package com.social.beFriendly.service;

import java.util.Date;

import javax.swing.plaf.basic.BasicScrollBarUI;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import com.mongodb.BasicDBObject;
import com.social.beFriendly.DAO.EmailDAO;
import com.social.beFriendly.DAO.UnsubscribeDAO;
import com.social.beFriendly.model.Email;
import com.social.beFriendly.model.Unsubscribe;

public class EmailService {
	EmailDAO emaildao = new EmailDAO();
	JacksonDBCollection<Email, String> emailCollection = emaildao.emailDAO();
	UnsubscribeDAO unsubscribedao = new UnsubscribeDAO();
	JacksonDBCollection<Unsubscribe, String> unsubscribeCollection = unsubscribedao.unsubscribeDAO();
	public String email(String from, String purpose, String recieverEmail, String status, String subject) {

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
		Email email = emailCollection.findOneById(id);
		email.setStatus(status);
		emailCollection.updateById(id, email);

	}
	public Boolean checkSubscription(String email){
		BasicDBObject query = new BasicDBObject();
		query.put("email", email);
		DBCursor<Unsubscribe> cursor = unsubscribeCollection.find(query);
		if(cursor.hasNext()){
			return true;
		}
		return false;
	}

}
