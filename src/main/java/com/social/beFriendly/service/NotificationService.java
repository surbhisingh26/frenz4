package com.social.beFriendly.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;

import com.mongodb.BasicDBObject;
import com.social.beFriendly.DAO.NotificationDAO;
import com.social.beFriendly.model.Notification;

public class NotificationService {
	

	public void sendNotification(ObjectId fid, String image, String notifications,String link,String subject) {
		NotificationDAO notificationdao = new NotificationDAO();
		JacksonDBCollection<Notification,String> notifyCollection = notificationdao.notificationDAO();
		Date date = new Date();
		Notification notification = new Notification();
		notification.setImage(image);
		notification.setLink(link);
		notification.setUserId(fid);
		notification.setDate(date);
		notification.setNotification(notifications);
		notification.setPurpose(subject);
		notification.setRead(false);
		notifyCollection.insert(notification);
	}

	public void deleteNotification(ObjectId fid, String notification) {
		NotificationDAO notificationdao = new NotificationDAO();
		JacksonDBCollection<Notification,String> notifyCollection = notificationdao.notificationDAO();
		BasicDBObject query = new BasicDBObject();
		query.put("userId", fid);
		query.put("notification", notification);
		notifyCollection.remove(query);
	}

	public Map<String,Object> getNotification(ObjectId uid) {
		NotificationDAO notificationdao = new NotificationDAO();
		JacksonDBCollection<Notification,String> notifyCollection = notificationdao.notificationDAO();
		BasicDBObject query = new BasicDBObject();
		Map<String,Object> hmap = new HashMap<String, Object>();
		List<Notification> notifyList = new ArrayList<Notification>();
		query.put("userId",uid);
		BasicDBObject sortQuery = new BasicDBObject();
		sortQuery.put("date", -1);
		long count = notifyCollection.count(query);
		DBCursor<Notification> cursor = notifyCollection.find(query).sort(sortQuery);
		while(cursor.hasNext()){
			Notification notification = cursor.next();
			
			notifyList.add(notification);
		}
		hmap.put("count", count);
		hmap.put("notifyList", notifyList);
		return hmap;
	}

	public void markRead(String id) {
		NotificationDAO notificationdao = new NotificationDAO();
		JacksonDBCollection<Notification,String> notifyCollection = notificationdao.notificationDAO();
		Notification notification = notifyCollection.findOneById(id);
		notification.setRead(true);
		notifyCollection.updateById(id, notification);
	}
	public Map<String,Object> notificationRead(ObjectId uid) {
		NotificationDAO notificationdao = new NotificationDAO();
		JacksonDBCollection<Notification,String> notifyCollection = notificationdao.notificationDAO();
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
