package com.social.beFriendly.model;

import java.util.Date;

public class UploadPic {
private String uid;
private String path;
public String getUid() {
	return uid;
}
public void setUid(String uid) {
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
private Date uploadTime;
}