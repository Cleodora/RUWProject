package com.example.model;

public class ContactListItem {

	private long id;
	private String displayName;
	private String number;

	public ContactListItem() {
	}

	public ContactListItem(String displayName, String number) {
		this.displayName = displayName;
		this.number = number;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
}
