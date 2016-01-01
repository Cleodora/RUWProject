package com.example.model;


import java.util.Date;

public class SMSListItem {
	public long id;
	public int imageId;
	public String body;
	public Date dateTime;

	public SMSListItem() {
	}

	public SMSListItem( long id, int imageId, String body, Date dateTime ) {
		this.id = id;
		this.imageId = imageId;
		this.body = body;
		this.dateTime = dateTime;
	}
}
