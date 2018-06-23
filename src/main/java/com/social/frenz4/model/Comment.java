package com.social.frenz4.model;

import java.util.Date;

import org.bson.types.ObjectId;

import com.social.scframework.model.BaseObject;

public class Comment extends BaseObject{
	
private String comment;
private Date time;
private ObjectId activityId;
private ObjectId fid;
public String getComment() {
	return comment;
}
public void setComment(String comment) {
	this.comment = comment;
}
public Date getTime() {
	return time;
}
public void setTime(Date time) {
	this.time = time;
}
public ObjectId getActivityId() {
	return activityId;
}
public void setActivityId(ObjectId activityId) {
	this.activityId = activityId;
}
public ObjectId getFid() {
	return fid;
}
public void setFid(ObjectId fid) {
	this.fid = fid;
}
}
