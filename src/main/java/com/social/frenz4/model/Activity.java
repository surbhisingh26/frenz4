package com.social.frenz4.model;

import java.util.Date;

import org.bson.types.ObjectId;

import com.social.scframework.model.BaseObject;

public class Activity extends BaseObject{
private ObjectId uid;
private String type;
private Date date;
private ObjectId typeId;
private long hearts;
private long heartBreaks;
private long comments;
private Boolean viewfalse;
private Boolean deleted;

public Boolean getViewfalse() {
	return viewfalse;
}
public void setViewfalse(Boolean viewfalse) {
	this.viewfalse = viewfalse;
}
public Boolean getDeleted() {
	return deleted;
}
public void setDeleted(Boolean deleted) {
	this.deleted = deleted;
}
public long getHearts() {
	return hearts;
}
public void setHearts(long hearts) {
	this.hearts = hearts;
}
public long getHeartBreaks() {
	return heartBreaks;
}
public void setHeartBreaks(long heartBreaks) {
	this.heartBreaks = heartBreaks;
}
public long getComments() {
	return comments;
}
public void setComments(long comments) {
	this.comments = comments;
}
public ObjectId getUid() {
	return uid;
}
public void setUid(ObjectId uid) {
	this.uid = uid;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public Date getDate() {
	return date;
}
public void setDate(Date date) {
	this.date = date;
}
public ObjectId getTypeId() {
	return typeId;
}
public void setTypeId(ObjectId typeId) {
	this.typeId = typeId;
}


}
