package com.example.analysis;

public class WordProbability {
	private String word;
	private int spamProbability;
	private int hamProbability;

	public WordProbability() {
	}

	public WordProbability(String word, int spamProbability, int hamProbability) {
		this.word = word;
		this.spamProbability = spamProbability;
		this.hamProbability = hamProbability;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getSpamProbability() {
		return spamProbability;
	}

	public void setSpamProbability(int spamProbability) {
		this.spamProbability = spamProbability;
	}

	public int getHamProbability() {
		return hamProbability;
	}

	public void setHamProbability(int hamProbability) {
		this.hamProbability = hamProbability;
	}
}
