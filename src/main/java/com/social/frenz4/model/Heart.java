package com.social.frenz4.model;

import org.bson.types.ObjectId;

import com.social.scframework.model.BaseObject;

public class Heart extends BaseObject{
private ObjectId activityId;
private ObjectId fid;
private Boolean broken;


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
public Boolean getBroken() {
	return broken;
}
public void setBroken(Boolean heart) {
	this.broken = heart;
}


}
