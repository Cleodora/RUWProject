package com.example.dataprovider;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.model.RomanUrduRepresentation;
import com.example.model.RomanUrduWord;

import java.util.ArrayList;
import java.util.List;

public class RomanUrduRepresentationDataSource {
	private SQLiteDatabase database;
	private SQLiteDataHelper dbHelper;
	private String[] allColumns = {SQLiteDataHelper.COLUMN_ID
			, SQLiteDataHelper.COLUMN_WORD, SQLiteDataHelper.COLUMN_WORD_ID};

	private static RomanUrduRepresentationDataSource romanUrduRepresentationDataSource = null;
	private boolean isOpened = false;

	private RomanUrduRepresentationDataSource(Context context) {
		dbHelper = SQLiteDataHelper.getInstance(context);
	}

	public static synchronized RomanUrduRepresentationDataSource getInstance(Context context) {
		if (romanUrduRepresentationDataSource == null) {
			romanUrduRepresentationDataSource = new RomanUrduRepresentationDataSource(context);
			romanUrduRepresentationDataSource.open();
		}

		return romanUrduRepresentationDataSource;
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		if (!database.isReadOnly()) {
			database.execSQL("PRAGMA foreign_keys = ON;");
		}
	}

	public void close() {
		dbHelper.close();
		romanUrduRepresentationDataSource = null;
		isOpened = false;
	}

	public RomanUrduRepresentation addWord(String word, long foreignKey) {
		ContentValues values = new ContentValues();
		values.put(SQLiteDataHelper.COLUMN_WORD, word);
		values.put(SQLiteDataHelper.COLUMN_WORD_ID, foreignKey);
		long insertId = database.insert(SQLiteDataHelper.TABLE_ROMAN_URDU_REPRESENTATIONS, null, values);
		Cursor cursor = database.query(SQLiteDataHelper.TABLE_ROMAN_URDU_REPRESENTATIONS,
				allColumns, SQLiteDataHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		RomanUrduRepresentation newWord = cursorToWord(cursor);
		cursor.close();
		return newWord;
	}

	public void deleteWord(RomanUrduWord word) {
		long id = word.id;
		database.delete(SQLiteDataHelper.TABLE_ROMAN_URDU_REPRESENTATIONS, SQLiteDataHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public List<RomanUrduRepresentation> getAllWords() {
		List<RomanUrduRepresentation> romanUrduRepresentations = new ArrayList<RomanUrduRepresentation>();

		Cursor cursor = database.query(SQLiteDataHelper.TABLE_ROMAN_URDU_REPRESENTATIONS,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			RomanUrduRepresentation romanUrduRepresentation = cursorToWord(cursor);
			romanUrduRepresentations.add(romanUrduRepresentation);
			cursor.moveToNext();
		}
		cursor.close();
		return romanUrduRepresentations;
	}

	public RomanUrduWord getStandardWord(String word) {
		Cursor cursor = database.rawQuery(
				"select * from " + SQLiteDataHelper.TABLE_ROMAN_URDU_WORDS +
						" where " + SQLiteDataHelper.COLUMN_ID + "= " +
						"(select distinct " + SQLiteDataHelper.COLUMN_WORD_ID + " from " + SQLiteDataHelper.TABLE_ROMAN_URDU_REPRESENTATIONS +
						" where " + SQLiteDataHelper.COLUMN_WORD + " = ?" + ");", new String[]{word}
		);

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			RomanUrduWord romanUrduWord = new RomanUrduWord();
			romanUrduWord.id= cursor.getLong(0);
			romanUrduWord.word = cursor.getString(1);
			cursor.close();

			return romanUrduWord;
		}

		return null;
	}

	private RomanUrduRepresentation cursorToWord(Cursor cursor) {
		RomanUrduRepresentation romanUrduRepresentation = new RomanUrduRepresentation();
		romanUrduRepresentation.id = cursor.getLong(0);
		romanUrduRepresentation.word = cursor.getString(1);
		romanUrduRepresentation.romanUrduWordId = cursor.getLong(2);
		return romanUrduRepresentation;
	}

}
