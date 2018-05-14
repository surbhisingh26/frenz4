package com.social.beFriendly.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.social.scframework.model.BaseObject;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Unsubscribe extends BaseObject{

	private String emails;
	private Date date;
	public String getEmails() {
		return emails;
	}
	public void setEmails(String emails) {
		this.emails = emails;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
