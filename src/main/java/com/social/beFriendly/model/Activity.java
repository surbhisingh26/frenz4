package com.social.beFriendly.model;

import java.util.Date;

import org.bson.types.ObjectId;

import com.social.scframework.model.BaseObject;

public class Activity extends BaseObject{
private ObjectId uid;
private String type;
private Date date;
private ObjectId activityId;

public ObjectId getUid() {
	return uid;
}
public void setUid(ObjectId uid) {
	this.uid = uid;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public Date getDate() {
	return date;
}
public void setDate(Date date) {
	this.date = date;
}
public ObjectId getActivityId() {
	return activityId;
}
public void setActivityId(ObjectId activityId) {
	this.activityId = activityId;
}

}
