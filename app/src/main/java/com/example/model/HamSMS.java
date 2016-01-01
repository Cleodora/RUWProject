package com.example.model;


public class HamSMS {
	private long id;
	private long inboxId;
	private long date;

	public HamSMS() {
	}

    public HamSMS(long inboxId, long date) {
        this.inboxId = inboxId;
		this.date =  date;
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getInboxId() {
		return inboxId;
	}

	public void setInboxId(long inboxId) {
		this.inboxId = inboxId;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}
}
