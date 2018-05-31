package com.social.beFriendly.model;


import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.social.scframework.model.BaseObject;
@JsonIgnoreProperties(ignoreUnknown = true)

public class Invite extends BaseObject {
	
	private ObjectId senderId;
	private String recieverEmail;
	public String getRecieverEmail() {
		return recieverEmail;
	}
	public void setRecieverEmail(String recieverEmail) {
		this.recieverEmail = recieverEmail;
	}
	public ObjectId getSenderId() {
		return senderId;
	}
	public void setSenderId(ObjectId senderId) {
		this.senderId = senderId;
	}
	
}
