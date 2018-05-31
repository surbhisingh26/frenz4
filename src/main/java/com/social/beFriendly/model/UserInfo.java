package com.social.beFriendly.model;

import org.bson.types.ObjectId;

import com.social.scframework.model.BaseObject;

public class UserInfo extends BaseObject {
	
private ObjectId uid;
private String address;
private String aboutme;
public ObjectId getUid() {
	return uid;
}
public void setUid(ObjectId uid) {
	this.uid = uid;
}
public String getAddress() {
	return address;
}
public void setAddress(String address) {
	this.address = address;
}
public String getAboutme() {
	return aboutme;
}
public void setAboutme(String aboutme) {
	this.aboutme = aboutme;
}
}
