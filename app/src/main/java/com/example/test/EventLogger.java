package com.example.test;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EventLogger {


	private static EventLogger eventLogger = null;

	public static synchronized EventLogger getInstance(Context context) {
		if (eventLogger == null) {
			eventLogger = new EventLogger(context);
		}

		return eventLogger;
	}

	private EventLogger(Context context) {
		this.context = context;
	}

	private Context context = null;

	public static final String fileName = "RUWLog.csv";

	public void logSMSReceived(String message, List<SpamWordInfo> words, float overallSpamacity) {


		try {
			if (isExternalStorageWritable()) {

				File externalDirectory = context.getExternalFilesDir(null);

				if (externalDirectory.isDirectory()) {

					File file = new File(externalDirectory, fileName);

					if (!file.exists()) {
						file.createNewFile();
					}


					FileWriter fileWriter = new FileWriter(file, true);
					BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
					Date date = new Date();
					DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					bufferedWriter.newLine();
					bufferedWriter.write(",event:,SMS Received");
					bufferedWriter.newLine();
					bufferedWriter.write(",time:," + dateFormat.format(date));
					bufferedWriter.newLine();
					bufferedWriter.write(",message:," + message);
					bufferedWriter.newLine();
					bufferedWriter.write(",tokens:");
					bufferedWriter.newLine();
					bufferedWriter.write(",,,tokens,word_ID,prob_spam,prob_ham");
					bufferedWriter.newLine();

					for (SpamWordInfo p : words) {
						bufferedWriter.write(",,," + p.getToken());
						bufferedWriter.write("," + p.getWordId());
						bufferedWriter.write("," + p.getProbSpam());
						bufferedWriter.write("," + p.getProbHam());
						bufferedWriter.newLine();
					}
					bufferedWriter.write(",Overall Spamacity:," + overallSpamacity);
					bufferedWriter.newLine();
					bufferedWriter.write(",-----------------------------------------------------------------------------------------------------------------");
					bufferedWriter.close();
				}
			}
		} catch (IOException ex) {
			Log.d("RUWProject", "Error writing to file '" + fileName + "'");
		}

	}

	public void logSpamSMSAdded(String message, List<SMSTokenInfo> tokens, List<SpamWordInfo> words) {
		try {

			if (isExternalStorageWritable()) {

				File externalDirectory = context.getExternalFilesDir(null);

				if (externalDirectory.isDirectory()) {

					File file = new File(externalDirectory, fileName);

					if (!file.exists()) {
						file.createNewFile();
					}


					FileWriter fileWritter = new FileWriter(file, true);
					BufferedWriter bufferedWriter = new BufferedWriter(fileWritter);
					Date date = new Date();
					DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					bufferedWriter.newLine();
					bufferedWriter.write(",event:,Spam SMS added ");
					bufferedWriter.newLine();
					bufferedWriter.write(",time:," + dateFormat.format(date));
					bufferedWriter.newLine();
					bufferedWriter.write(",message:," + message);
					bufferedWriter.newLine();
					bufferedWriter.write(",tokens:");
					bufferedWriter.newLine();
					bufferedWriter.write(",,,tokens,word_ID,count");
					bufferedWriter.newLine();
					for (int g = 0; g < tokens.size(); g++) {
						bufferedWriter.write(",,," + tokens.get(g).getToken());
						bufferedWriter.write("," + tokens.get(g).getWordId());
						bufferedWriter.write("," + tokens.get(g).getCount());
						bufferedWriter.newLine();
					}
					bufferedWriter.write(",database:");
					bufferedWriter.newLine();
					bufferedWriter.write(",,,_id,word_id,prob_spam,prob_ham");
					bufferedWriter.newLine();
					for (SpamWordInfo p : words) {
						bufferedWriter.write(",,," + p.getId());
						bufferedWriter.write("," + p.getWordId());
						bufferedWriter.write("," + p.getProbSpam());
						bufferedWriter.write("," + p.getProbHam());
						bufferedWriter.newLine();
					}

					bufferedWriter.write(",-----------------------------------------------------------------------------------------------------------------");
					bufferedWriter.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void logHamSMSAdded(String message, List<SMSTokenInfo> tokens, List<SpamWordInfo> words) {
		try {

			if (isExternalStorageWritable()) {

				File externalDirectory = context.getExternalFilesDir(null);

				if (externalDirectory.isDirectory()) {

					File file = new File(externalDirectory, fileName);

					if (!file.exists()) {
						file.createNewFile();
					}


					//true = append file
					FileWriter fileWritter = new FileWriter(file, true);
					BufferedWriter bufferedWriter = new BufferedWriter(fileWritter);
					Date date = new Date();
					DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					bufferedWriter.newLine();
					bufferedWriter.write(",event:,Random Ham SMS added ");
					bufferedWriter.newLine();
					bufferedWriter.write(",time:," + dateFormat.format(date));
					bufferedWriter.newLine();
					bufferedWriter.write(",message:," + message);
					bufferedWriter.newLine();
					bufferedWriter.write(",tokens:");
					bufferedWriter.newLine();
					bufferedWriter.write(",,,tokens,word_ID,count");
					bufferedWriter.newLine();

					for (int g = 0; g < tokens.size(); g++) {
						bufferedWriter.write(",,," + tokens.get(g).getToken());
						bufferedWriter.write("," + tokens.get(g).getWordId());
						bufferedWriter.write("," + tokens.get(g).getCount());
						bufferedWriter.newLine();
					}
					bufferedWriter.write(",database:");
					bufferedWriter.newLine();
					bufferedWriter.write(",,,_id,word_id,prob_spam,prob_ham");
					bufferedWriter.newLine();
					for (SpamWordInfo p : words) {
						bufferedWriter.write(",,," + p.getId());
						bufferedWriter.write("," + p.getWordId());
						bufferedWriter.write("," + p.getProbSpam());
						bufferedWriter.write("," + p.getProbHam());
						bufferedWriter.newLine();
					}

					bufferedWriter.write(",-----------------------------------------------------------------------------------------------------------------");
					bufferedWriter.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}


}
