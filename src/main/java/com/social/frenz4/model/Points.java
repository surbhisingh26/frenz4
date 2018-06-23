package com.social.frenz4.model;

import java.util.Date;

import org.bson.types.ObjectId;

import com.social.scframework.model.BaseObject;

public class Points extends BaseObject {

	private ObjectId uid;
	private int pointsEarn;
	private String pointReason;
	private Date date;
	public ObjectId getUid() {
		return uid;
	}
	public void setUid(ObjectId uid) {
		this.uid = uid;
	}
	public String getPointReason() {
		return pointReason;
	}
	public void setPointReason(String pointReason) {
		this.pointReason = pointReason;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getPointsEarn() {
		return pointsEarn;
	}
	public void setPointsEarn(int pointsEarn) {
		this.pointsEarn = pointsEarn;
	}
}
