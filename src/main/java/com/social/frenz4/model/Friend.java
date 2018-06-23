package com.social.frenz4.model;

import java.util.Date;

import org.bson.types.ObjectId;

import com.social.scframework.model.BaseObject;

public class Friend extends BaseObject {

	
	private ObjectId uid;
	private ObjectId fid;
	private Date requestDate;
	private Date responseDate;
	private String status;
	private Boolean friends;

	public Date getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}
	public Date getResponseDate() {
		return responseDate;
	}
	public void setResponseDate(Date responseDate) {
		this.responseDate = responseDate;
	}	
	public ObjectId getUid() {
		return uid;
	}
	public void setUid(ObjectId uid) {
		this.uid = uid;
	}
	public ObjectId getFid() {
		return fid;
	}
	public void setFid(ObjectId fid) {
		this.fid = fid;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Boolean getFriends() {
		return friends;
	}
	public void setFriends(Boolean friends) {
		this.friends = friends;
	}

}


