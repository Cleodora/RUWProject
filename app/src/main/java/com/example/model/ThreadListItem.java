package com.example.model;

import java.util.Date;

public class ThreadListItem {
	public int imageId;
	public String address;
	public long count;
	public String snippet;
	public Date dateTime;
	public long threadId;

	public ThreadListItem() {
	}

	public ThreadListItem( int imageId, String address, long count, String snippet, Date dateTime, long threadId ) {
		this.imageId = imageId;
		this.address = address;
		this.count = count;
		this.snippet = snippet;
		this.dateTime = dateTime;
		this.threadId = threadId;
	}
}
