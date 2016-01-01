package com.example.model;


import java.util.ArrayList;
import java.util.List;

public class SpamWord {
	private long id;
	private String wordId;
	private long spamProbability;
	private long hamProbability;

	private List<SpamSMSWord> spamSMSDetails = new ArrayList<SpamSMSWord>();
	private List<HamSMSWord> hamSMSDetails = new ArrayList<HamSMSWord>();

	public SpamWord() {
	}

    public SpamWord(String wordId, long spamProbability, long hamProbability) {
        this.wordId = wordId;
        this.spamProbability = spamProbability;
        this.hamProbability = hamProbability;
    }

    public SpamWord(long id, String wordId, long spamProbability, long hamProbability) {
		this.id = id;
		this.wordId = wordId;
		this.spamProbability = spamProbability;
		this.hamProbability = hamProbability;
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

	public long getSpamProbability() {
		return spamProbability;
	}

	public void setSpamProbability(long spamProbability) {
		this.spamProbability = spamProbability;
	}

	public long getHamProbability() {
		return hamProbability;
	}

	public void setHamProbability(long hamProbability) {
		this.hamProbability = hamProbability;
	}

	public List<SpamSMSWord> getSpamSMSDetails() {
		return spamSMSDetails;
	}

	public void setSpamSMSDetails(List<SpamSMSWord> spamSMSDetails) {
		this.spamSMSDetails = spamSMSDetails;
	}

	public List<HamSMSWord> getHamSMSDetails() {
		return hamSMSDetails;
	}

	public void setHamSMSDetails(List<HamSMSWord> hamSMSDetails) {
		this.hamSMSDetails = hamSMSDetails;
	}
}

