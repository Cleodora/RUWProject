package com.example.test;

public class SpamWordInfo {
	private long id;
	private String wordId;
	private long probSpam;
	private long probHam;

	private String token;
	private long count;

	public SpamWordInfo() {
	}

	public SpamWordInfo(long id, String wordId, long probSpam, long probHam) {
		this.id = id;
		this.wordId = wordId;
		this.probSpam = probSpam;
		this.probHam = probHam;
	}

	public SpamWordInfo(long id, String wordId, long probSpam, long probHam, String token) {
		this.id = id;
		this.wordId = wordId;
		this.probSpam = probSpam;
		this.probHam = probHam;
		this.token = token;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getWordId() {
		return wordId;
	}

	public void setWordId(String wordId) {
		this.wordId = wordId;
	}

	public long getProbSpam() {
		return probSpam;
	}

	public void setProbSpam(long probSpam) {
		this.probSpam = probSpam;
	}

	public long getProbHam() {
		return probHam;
	}

	public void setProbHam(long probHam) {
		this.probHam = probHam;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
}
