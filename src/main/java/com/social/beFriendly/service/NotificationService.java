package com.social.beFriendly.service;

import java.util.Date;

import org.mongojack.JacksonDBCollection;

import com.social.beFriendly.DAO.NotificationDAO;
import com.social.beFriendly.model.Notification;

public class NotificationService {
	NotificationDAO notificationdao = new NotificationDAO();
	JacksonDBCollection<Notification,String> notifyCollection = notificationdao.notificationDAO();

	public void sendNotification(String uid, String fid, String notifications) {
		Date date = new Date();
		Notification notification = new Notification();
		notification.setFriendId(uid);
		notification.setLink("friends");
		notification.setUserId(uid);
		notification.setDate(date);
		notification.setNotification(notifications);
		notifyCollection.insert(notification);
	}

}
