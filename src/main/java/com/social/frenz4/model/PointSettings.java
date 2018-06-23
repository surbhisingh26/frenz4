package com.social.frenz4.model;

import java.util.Date;

import com.social.scframework.model.BaseObject;

public class PointSettings extends BaseObject {

	private String type;
	private int points;
	private Date date;
	private String adminName;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getAdminName() {
		return adminName;
	}
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
	
}
