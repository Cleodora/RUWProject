package com.example.model;


public class SMSLocal {
	public long id;
	public String address;
	public int person;
	public long date;
	public long date_sent;
	public int protocol;
	public int read;
	public int status;
	public int type;
	public int reply_path_present;
	public String subject;
	public String body;
	public String service_center;
	public int locked;
	public int error_code;
	public int seen;
	public long inbox_id;
	public int hide;

	public SMSLocal() {
	}

	public SMSLocal(long id, String address, int person, int date, int date_sent, int protocol, int read, int status, int type, int reply_path_present, String subject, String body, String service_center, int locked, int error_code, int seen, long inbox_id) {
		this.id = id;
		this.address = address;
		this.person = person;
		this.date = date;
		this.date_sent = date_sent;
		this.protocol = protocol;
		this.read = read;
		this.status = status;
		this.type = type;
		this.reply_path_present = reply_path_present;
		this.subject = subject;
		this.body = body;
		this.service_center = service_center;
		this.locked = locked;
		this.error_code = error_code;
		this.seen = seen;
		this.inbox_id = inbox_id;
	}
}
