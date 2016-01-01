package com.example.analysis;


import android.content.Context;
import android.net.Uri;

import com.example.dataprovider.ContentProviderDataSource;
import com.example.dataprovider.SpamDictionaryDataSource;
import com.example.dataprovider.SpamSMSDataSource;
import com.example.model.HamSMS;
import com.example.model.HamSMSWord;
import com.example.model.SMSLocal;
import com.example.model.SpamSMSWord;
import com.example.model.SpamWord;
import com.example.test.EventLogger;
import com.example.test.SMSTokenInfo;
import com.example.test.SpamWordInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BayesianClassifier {

	public static synchronized float classify(Context context, SMSLocal sms) {
		String text = sms.body;
		HashMap<String, Integer> uniqueWords = getUniqueWords(text);
		Set<String> tokens = uniqueWords.keySet();
		SpamDictionaryDataSource spamDictionary = SpamDictionaryDataSource.getInstance(context);
		List<SpamWord> spamWords = new ArrayList<SpamWord>();


		//test code start
		List<SpamWordInfo> spamWordInfos = new ArrayList<SpamWordInfo>();
		//test code end

		String wordId = "";
		for (String token : tokens) {
			wordId = SoundexRefined.getCode(token);
			SpamWord spamWord = spamDictionary.getSpamWord(wordId);

			if (spamWord != null) {
				spamWords.add(spamWord);
				//test code start
				SpamWordInfo spamWordInfo = new SpamWordInfo();
				spamWordInfo.setToken(token);
				spamWordInfo.setWordId(spamWord.getWordId());
				spamWordInfo.setProbSpam(spamWord.getSpamProbability());
				spamWordInfo.setProbHam(spamWord.getHamProbability());
				spamWordInfos.add(spamWordInfo);
				//test code end
			} else {
				//test code start
				SpamWordInfo spamWordInfo = new SpamWordInfo();
				spamWordInfo.setToken(token);
				spamWordInfo.setWordId(wordId);
				spamWordInfo.setProbSpam(-1);
				spamWordInfo.setProbHam(-1);
				spamWordInfos.add(spamWordInfo);
				//test code end
			}
		}

		float overallSpamacity = calculateOverallSpamacity(context, spamWords);

		//test code start
		EventLogger.getInstance(context).logSMSReceived(sms.body, spamWordInfos, overallSpamacity);
		//test code end

		return overallSpamacity;
	}


	static float calculateOverallSpamacity(Context context, List<SpamWord> spamWords) {

		SpamDictionaryDataSource spamDictionary = SpamDictionaryDataSource.getInstance(context);

		double countInSpam = 0;
		double countInHam = 0;

		float probSpam = 0;
		float probHam = 0;
		float spamacity = 0;

		float fact1 = 1;
		float fact2 = 1;

		for (SpamWord spamWord : spamWords) {
			countInSpam = spamDictionary.getWordCountInSpamSMSs(spamWord.getId());
			countInHam = spamDictionary.getWordCountInHamSMSs(spamWord.getId());

			probSpam = (float) spamWord.getSpamProbability() / (float) 100.0;
			probHam = (float) spamWord.getHamProbability() / (float) 100.0;
			if (probSpam + probHam == 0) {
				spamacity = 0.4f;
			} else if (countInSpam + countInHam < 5) {
				spamacity = 0.4f;
			} else {
				spamacity = probSpam / (probSpam + probHam);
				if (spamacity < 0.4) {
					spamacity = 0.4f;
				}
			}

			fact1 *= spamacity;
			fact2 *= (1 - spamacity);
		}

		float overallSpamacity = fact1 / (fact1 + fact2);


		return overallSpamacity;
	}

	public static synchronized void trainSpam2(Context context, SMSLocal sms) {

		SpamDictionaryDataSource spamDictionary = SpamDictionaryDataSource.getInstance(context);

		List<HamSMS> hamSMSList = spamDictionary.getHamSMSs(sms.inbox_id);

		for (HamSMS hamSMS : hamSMSList) {
			if (sms.date == hamSMS.getDate()) {
				spamDictionary.deleteHamSMS(hamSMS.getId());
			}
		}

		String text = sms.body;

		HashMap<String, Integer> uniqueWords = getUniqueWords(text);
		Set<String> tokens = uniqueWords.keySet();


		double countInSpam = 0;

		String wordId = "";
		long spamWordId = 0;

		//test code start
		List<SMSTokenInfo> smsTokenInfoList = new ArrayList<SMSTokenInfo>();
		//test code end

		for (String token : tokens) {
			wordId = SoundexRefined.getCode(token);

			countInSpam = uniqueWords.get(token);

			if (spamDictionary.isWordInSpamDictionary(wordId)) {
				spamWordId = spamDictionary.getSpamWord(wordId).getId();

				countInSpam += spamDictionary.getWordCountInSpamSMSs(spamWordId);
			}
			SpamWord spamWord = new SpamWord();
			if (!spamDictionary.isWordInSpamDictionary(wordId)) {
				spamWord.setWordId(wordId);
				spamWordId = spamDictionary.addSpamWord(spamWord);
			}

			//test code start
			SMSTokenInfo smsTokenInfo = new SMSTokenInfo();
			smsTokenInfo.setWordId(wordId);
			smsTokenInfo.setToken(token);
			smsTokenInfo.setCount((long) countInSpam);

			smsTokenInfoList.add(smsTokenInfo);
			//test code end

			spamWord.getSpamSMSDetails().add(new SpamSMSWord(sms.id, spamWordId, uniqueWords.get(token)));
			spamDictionary.addSpamWordAssociation(spamWord.getSpamSMSDetails());
		}


		updateAllSpamWords(context);

		//test code start
		List<SpamWordInfo> spamWordInfoList = new ArrayList<SpamWordInfo>();
		List<SpamWord> spamWords = spamDictionary.getAllSpamWords();

		for (SpamWord spamWord : spamWords) {
			SpamWordInfo spamWordInfo = new SpamWordInfo();
			spamWordInfo.setId(spamWord.getId());
			spamWordInfo.setWordId(spamWord.getWordId());
			spamWordInfo.setProbSpam(spamWord.getSpamProbability());
			spamWordInfo.setProbHam(spamWord.getHamProbability());

			spamWordInfoList.add(spamWordInfo);
		}

		EventLogger.getInstance(context).logSpamSMSAdded(sms.body, smsTokenInfoList, spamWordInfoList);
		//test code end

		double totalSpamSMS = spamDictionary.getSpamSMSAssociationsCount();
		double totalHamSMS = spamDictionary.getHamSMSAssociationsCount();

		if (totalSpamSMS > totalHamSMS) {
			balanceHam(context, (long) (totalSpamSMS - totalHamSMS));
		}
	}

	private static void balanceHam(Context context, long difference) {
		SpamDictionaryDataSource spamDictionary = SpamDictionaryDataSource.getInstance(context);
		List<SMSLocal> randomHamSMSs = ContentProviderDataSource.getRandomSMSFromInbox(context, difference, spamDictionary.getHamSMSsInboxIds());

		for (SMSLocal sms : randomHamSMSs) {
			long insertId = spamDictionary.addHamSMS(new HamSMS(sms.inbox_id, sms.date));
			sms.id = insertId;
			trainHam2(context, sms);
		}
	}

	public static synchronized void trainHam2(Context context, SMSLocal sms) {
		String text = sms.body;

		HashMap<String, Integer> uniqueWords = getUniqueWords(text);
		Set<String> tokens = uniqueWords.keySet();
		SpamDictionaryDataSource spamDictionary = SpamDictionaryDataSource.getInstance(context);

		double countInHam = 0;

		String wordId = "";
		long spamWordId = 0;

		//test code start
		List<SMSTokenInfo> smsTokenInfoList = new ArrayList<SMSTokenInfo>();
		//test code end

		for (String token : tokens) {
			wordId = SoundexRefined.getCode(token);

			countInHam = uniqueWords.get(token);

			if (spamDictionary.isWordInSpamDictionary(wordId)) {
				spamWordId = spamDictionary.getSpamWord(wordId).getId();

				countInHam += spamDictionary.getWordCountInSpamSMSs(spamWordId);
			}

			SpamWord spamWord = new SpamWord();
			if (!spamDictionary.isWordInSpamDictionary(wordId)) {
				spamWord.setWordId(wordId);
				spamWordId = spamDictionary.addSpamWord(spamWord);
			}

			//test code start
			SMSTokenInfo smsTokenInfo = new SMSTokenInfo();
			smsTokenInfo.setWordId(wordId);
			smsTokenInfo.setToken(token);
			smsTokenInfo.setCount((long) countInHam);

			smsTokenInfoList.add(smsTokenInfo);
			//test code end

			spamWord.getHamSMSDetails().add(new HamSMSWord(sms.id, spamWordId, uniqueWords.get(token)));
			spamDictionary.addHamWordAssociations(spamWord.getHamSMSDetails());
		}


		updateAllSpamWords(context);

		//test code start
		List<SpamWordInfo> spamWordInfoList = new ArrayList<SpamWordInfo>();
		List<SpamWord> spamWords = spamDictionary.getAllSpamWords();

		for (SpamWord spamWord : spamWords) {
			SpamWordInfo spamWordInfo = new SpamWordInfo();
			spamWordInfo.setId(spamWord.getId());
			spamWordInfo.setWordId(spamWord.getWordId());
			spamWordInfo.setProbSpam(spamWord.getSpamProbability());
			spamWordInfo.setProbHam(spamWord.getHamProbability());

			spamWordInfoList.add(spamWordInfo);
		}

		EventLogger.getInstance(context).logHamSMSAdded(sms.body, smsTokenInfoList, spamWordInfoList);
		//test code end
	}

	private static void updateAllSpamWords(Context context) {
		SpamDictionaryDataSource spamDictionary = SpamDictionaryDataSource.getInstance(context);

		double totalSpamSMS = spamDictionary.getSpamSMSAssociationsCount();
		double totalHamSMS = spamDictionary.getHamSMSAssociationsCount();

		double countInSpam = 0;
		double countInHam = 0;

		long spamWordId = 0;

		double probSpam = 0;
		double probHam = 0;

		List<SpamWord> spamWords = spamDictionary.getAllSpamWords();

		for (SpamWord spamWord : spamWords) {
			spamWordId = spamWord.getId();

			countInSpam = spamDictionary.getWordCountInSpamSMSs(spamWordId);
			countInHam = spamDictionary.getWordCountInHamSMSs(spamWordId);

			probSpam = (probSpam = countInSpam / totalSpamSMS) > 1 ? 100 : probSpam * 100;

			if (totalHamSMS > 0) {
				probHam = (probHam = countInHam / totalHamSMS) > 1 ? 100 : probHam * 100;
			} else {
				probHam = 50;
			}

			if (countInHam == 0 && countInSpam == 0) {
				spamDictionary.deleteSpamWord(spamWordId);
			} else {
				spamDictionary.updateSpamWord(spamWordId, (long) probSpam, (long) probHam);
			}
		}
	}

	public static void markNotSpam(Context context, SMSLocal sms) {
		Uri uri = ContentProviderDataSource.restoreSMS(context, sms);

		SpamSMSDataSource.getInstance(context).deleteSMS(sms.id);

		SMSLocal hamSMS = ContentProviderDataSource.getSMSFromInbox(context, uri);

		long insertId = SpamDictionaryDataSource.getInstance(context).addHamSMS(new HamSMS(hamSMS.inbox_id, hamSMS.date));
		hamSMS.id = insertId;
		trainHam2(context, hamSMS);
	}

	private static HashMap<String, Integer> getUniqueWords(String text) {
		String spaces = "\\p{Space}";
		String splitter = "[[\\p{Space}\\p{Punct}]&&[^\']]";
		String valid = "([\'a-zA-Z]+){3,30}";

		String[] rawTokens = text.split(splitter);

		Pattern p = Pattern.compile(valid);
		Matcher m = null;

		String out = null;
		List<String> tokens = new ArrayList<String>();
		for (String str : rawTokens) {
			out = str.replaceAll(spaces, "");
			m = p.matcher(out);
			if (m.matches()) {
				tokens.add(out.toLowerCase());
			}
		}

		HashMap<String, Integer> uniqueWords = new HashMap<String, Integer>();
		int count = 0;
		for (int i = 0; i < tokens.size(); i++) {
			String str = tokens.get(i);
			count = 1;

			for (int j = 0; j < tokens.size(); j++) {
				String m1 = tokens.get(j);

				if (i != j && m1.matches(str)) {
					count++;
					tokens.remove(j);
				}
			}

			uniqueWords.put(str, count);
		}

		return uniqueWords;
	}
}

//test code start
//test code end