package com.example.model;

public class BlockedContact {
	private long id;
	private String number;

	public BlockedContact() {
	}

	public BlockedContact(long id, String number) {
		this.id = id;
		this.number = number;
	}

	public BlockedContact(String number) {
		this.number = number;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
}
