package com.social.frenz4.model;

import java.util.Date;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.social.scframework.model.BaseObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification extends BaseObject{
	private ObjectId userId;
	private String notification;
	private String link;
	private Date date;
	private String image;
	private String purpose;
	private Boolean read;
	public ObjectId getUserId() {
		return userId;
	}
	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}
	public String getNotification() {
		return notification;
	}
	public void setNotification(String notification) {
		this.notification = notification;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String friendId) {
		this.image = friendId;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public Boolean getRead() {
		return read;
	}
	public void setRead(Boolean read) {
		this.read = read;
	}
}


