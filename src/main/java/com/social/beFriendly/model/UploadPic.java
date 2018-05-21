package com.social.beFriendly.model;

import java.util.Date;

import org.bson.types.ObjectId;

import com.social.scframework.model.BaseObject;

public class UploadPic extends BaseObject {
private ObjectId uid;
private String path;
private Date uploadTime;
private String message;
private String caption;
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}
public String getCaption() {
	return caption;
}
public void setCaption(String caption) {
	this.caption = caption;
}
public ObjectId getUid() {
	return uid;
}
public void setUid(ObjectId uid) {
	this.uid = uid;
}
public String getPath() {
	return path;
}
public void setPath(String path) {
	this.path = path;
}
public Date getUploadTime() {
	return uploadTime;
}
public void setUploadTime(Date uploadTime) {
	this.uploadTime = uploadTime;
}
}
