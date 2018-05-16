package com.social.beFriendly.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;

import com.mongodb.BasicDBObject;
import com.social.beFriendly.DAO.NotificationDAO;
import com.social.beFriendly.model.Notification;

public class NotificationService {
	NotificationDAO notificationdao = new NotificationDAO();
	JacksonDBCollection<Notification,String> notifyCollection = notificationdao.notificationDAO();

	public void sendNotification(String userId, String image, String notifications,String link,String subject) {
		Date date = new Date();
		Notification notification = new Notification();
		notification.setImage(image);
		notification.setLink(link);
		notification.setUserId(userId);
		notification.setDate(date);
		notification.setNotification(notifications);
		notification.setPurpose(subject);
		notification.setRead(false);
		notifyCollection.insert(notification);
	}

	public void deleteNotification(String userId, String notification) {
		BasicDBObject query = new BasicDBObject();
		query.put("userId", userId);
		query.put("notification", notification);
		notifyCollection.remove(query);
	}

	public Map<String,Object> getNotification(String userId) {
		BasicDBObject query = new BasicDBObject();
		Map<String,Object> hmap = new HashMap<String, Object>();
		List<Notification> notifyList = new ArrayList<Notification>();
		query.put("userId", userId);
		BasicDBObject sortQuery = new BasicDBObject();
		sortQuery.put("date", -1);
		long count = notifyCollection.count(query);
		DBCursor<Notification> cursor = notifyCollection.find(query).sort(sortQuery);
		while(cursor.hasNext()){
			Notification notification = cursor.next();
			System.out.println("Notification ................. " + notification.getNotification());
			notifyList.add(notification);
		}
		hmap.put("count", count);
		hmap.put("notifyList", notifyList);
		return hmap;
	}

	public void markRead(String id) {
		Notification notification = notifyCollection.findOneById(id);
		notification.setRead(true);
		notifyCollection.updateById(id, notification);
	}
	public Map<String,Object> notificationRead(String uid) {
		BasicDBObject query = new BasicDBObject();
		Map<String,Object> hmap = new HashMap<String, Object>();
		query.put("userId", uid);
		query.put("read", false);
		if(notifyCollection.count(query)>0)
			hmap.put("unread", true);
		else
			hmap.put("unread", false);
		return hmap;
	}
	
}
