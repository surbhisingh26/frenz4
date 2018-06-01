package com.social.beFriendly.model;

import java.util.Date;

import org.bson.types.ObjectId;

import com.social.scframework.model.BaseObject;

public class Chat extends BaseObject {

	private ObjectId senderId;
	private ObjectId recieverId;
	private String text;
	private Date sentAt;
	private Date deliveredAt;
	private boolean isDelivered;
	public ObjectId getSenderId() {
		return senderId;
	}
	public void setSenderId(ObjectId senderId) {
		this.senderId = senderId;
	}
	public ObjectId getRecieverId() {
		return recieverId;
	}
	public void setRecieverId(ObjectId recieverId) {
		this.recieverId = recieverId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Date getSentAt() {
		return sentAt;
	}
	public void setSentAt(Date sentAt) {
		this.sentAt = sentAt;
	}
	public Date getDeliveredAt() {
		return deliveredAt;
	}
	public void setDeliveredAt(Date deliveredAt) {
		this.deliveredAt = deliveredAt;
	}
	public boolean isDelivered() {
		return isDelivered;
	}
	public void setDelivered(boolean isDelivered) {
		this.isDelivered = isDelivered;
	}
	
	
}
