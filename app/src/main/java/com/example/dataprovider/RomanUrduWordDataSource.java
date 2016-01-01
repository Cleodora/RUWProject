package com.example.dataprovider;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.model.RomanUrduWord;

import java.util.ArrayList;
import java.util.List;

public class RomanUrduWordDataSource {
	private SQLiteDatabase database;
	private SQLiteDataHelper dbHelper;
	private String[] allColumns = {SQLiteDataHelper.COLUMN_ID,
			SQLiteDataHelper.COLUMN_WORD};

	private static RomanUrduWordDataSource romanUrduWordDataSource = null;
	private boolean isOpened = false;

	private RomanUrduWordDataSource(Context context) {
		dbHelper = SQLiteDataHelper.getInstance(context);
	}

	public static synchronized RomanUrduWordDataSource getInstance(Context context) {
		if (romanUrduWordDataSource == null) {
			romanUrduWordDataSource = new RomanUrduWordDataSource(context);
			romanUrduWordDataSource.open();
		}
		return romanUrduWordDataSource;
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		if (!database.isReadOnly()) {
			database.execSQL("PRAGMA foreign_keys = ON;");
		}
		isOpened = true;
	}

	public void close() {
		dbHelper.close();
		isOpened = false;
		romanUrduWordDataSource = null;
	}

	public RomanUrduWord addWord(String word) {
		ContentValues values = new ContentValues();
		values.put(SQLiteDataHelper.COLUMN_WORD, word);
		long insertId = database.insert(SQLiteDataHelper.TABLE_ROMAN_URDU_WORDS, null, values);
		Cursor cursor = database.query(SQLiteDataHelper.TABLE_ROMAN_URDU_WORDS,
				allColumns, SQLiteDataHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		RomanUrduWord newWord = cursorToWord(cursor);
		cursor.close();
		return newWord;
	}

	public void deleteWord(RomanUrduWord word) {
		long id = word.id;
		database.delete(SQLiteDataHelper.TABLE_ROMAN_URDU_WORDS, SQLiteDataHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public List<RomanUrduWord> getRange(long start, long count) {
		String query = "SELECT * FROM " + SQLiteDataHelper.TABLE_ROMAN_URDU_WORDS + " LIMIT " + start + "," + count + ";";
		return applyQuery(query);
	}

	public List<RomanUrduWord> getStartWith(String sequence) {
		String query = "SELECT * FROM " + SQLiteDataHelper.TABLE_ROMAN_URDU_WORDS + " WHERE " + SQLiteDataHelper.COLUMN_WORD + " LIKE " + "'" + sequence + "%';";
		return applyQuery(query);
	}

	public List<RomanUrduWord> getEndWith(String sequence) {
		String query = "SELECT * FROM " + SQLiteDataHelper.TABLE_ROMAN_URDU_WORDS + " WHERE " + SQLiteDataHelper.COLUMN_WORD + " LIKE " + "'%" + sequence + "';";
		return applyQuery(query);
	}

	public List<RomanUrduWord> getHaving(String sequence) {
		String query = "SELECT * FROM " + SQLiteDataHelper.TABLE_ROMAN_URDU_WORDS + " WHERE " + SQLiteDataHelper.COLUMN_WORD + " LIKE " + "'%" + sequence + "%';";
		return applyQuery(query);
	}

	public List<RomanUrduWord> getAllWords() {
		List<RomanUrduWord> romanUrduWords = new ArrayList<RomanUrduWord>();

		Cursor cursor = database.query(SQLiteDataHelper.TABLE_ROMAN_URDU_WORDS,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			RomanUrduWord romanUrduWord = cursorToWord(cursor);
			romanUrduWords.add(romanUrduWord);
			cursor.moveToNext();
		}
		cursor.close();
		return romanUrduWords;
	}

	private List<RomanUrduWord> applyQuery(String query) {
		Cursor cursor = database.rawQuery(query, null);

		List<RomanUrduWord> list = new ArrayList<RomanUrduWord>();
		RomanUrduWord ruw = null;

		while (cursor.moveToNext()) {
			list.add(cursorToWord(cursor));
		}
		return list;
	}

	private RomanUrduWord cursorToWord(Cursor cursor) {
		RomanUrduWord romanUrduWord = new RomanUrduWord();
		romanUrduWord.id = cursor.getLong(0);
		romanUrduWord.word = cursor.getString(1);
		return romanUrduWord;
	}

	public List<RomanUrduWord> getTestWords(){
		////select * from RomanUrduWords where _id not in(0,1,2,3,4) order by random()
		Cursor cursor = database.query(SQLiteDataHelper.TABLE_ROMAN_URDU_WORDS,null," _id not in(0,1,2,3,4) and _id < 12",null,null,null,"random()  limit 4");

		List<RomanUrduWord> roms = new ArrayList<RomanUrduWord>();

		cursor.moveToFirst();

		while (!cursor.isAfterLast()){
			roms.add(cursorToWord(cursor));
			cursor.moveToNext();
		}

		return roms;
	}
}
