package com.example.model;


public class HamSMSWord {
	private long id;
	private long hamSMSId;
	private long spamWordId;
	private long count;

	public HamSMSWord() {
	}

    public HamSMSWord(long hamSMSId, long spamWordId, long count) {
        this.hamSMSId = hamSMSId;
        this.spamWordId = spamWordId;
        this.count = count;
    }

    public HamSMSWord(long id,long hamSMSId, long spamWordId, long count) {
		this.id = id;
		this.hamSMSId = hamSMSId;
		this.spamWordId = spamWordId;
		this.count = count;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getHamSMSId() {
		return hamSMSId;
	}

	public void setHamSMSId(long hamSMSId) {
		this.hamSMSId = hamSMSId;
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

