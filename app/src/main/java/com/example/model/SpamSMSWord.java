package com.example.model;


public class SpamSMSWord {
	private long id;
	private long spamSMSId;
	private long spamWordId;
	private long count;

	public SpamSMSWord() {
	}

    public SpamSMSWord(long spamSMSId, long spamWordId, long count) {
        this.spamSMSId = spamSMSId;
        this.spamWordId = spamWordId;
        this.count = count;
    }

    public SpamSMSWord(long id, long spamSMSId, long spamWordId, long count) {
		this.id = id;
		this.spamSMSId = spamSMSId;
		this.spamWordId = spamWordId;
		this.count = count;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSpamSMSId() {
		return spamSMSId;
	}

	public void setSpamSMSId(long spamSMSId) {
		this.spamSMSId = spamSMSId;
	}

	public long getSpamWordId() {
		return spamWordId;
	}

	public void setSpamWordId(long spamWordId) {
		this.spamWordId = spamWordId;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
}
