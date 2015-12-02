package com.example.nikolai.shoppinglist.dataSourceLayer;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.util.Log;
import  static com.example.nikolai.shoppinglist.dataSourceLayer.ShoppingListDbHelper.*;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class ShoppingListDb implements AutoCloseable {
	private static final String LOG_TAG = "HighScoreDb";
	private SQLiteDatabase db;
	private final Context context;
	private final ShoppingListDbHelper helper;

	public ShoppingListDb(Context context) {
		this.context = context;
		helper = new ShoppingListDbHelper(context);
		}
	
	public void close() { db.close(); }
	
	public void open() {
		try {
			db = helper.getWritableDatabase();
			}
		catch (SQLiteException sqle) {
			Log.w(LOG_TAG, "Could not open database for writing "+sqle.getMessage());
			// Log.println(Log.WARN, LOG_TAG, "message... ");
			db = helper.getReadableDatabase();
			}
		}


	public long createShoppingList(String name, String dato, String user_fk)
	{
		try
		{


		ContentValues values = new ContentValues();
		values.put(list_NAME_COLUMN, name);
		values.put(list_DATO_COLUMN, dato);
		values.put(list_user_fk_COLUMN, user_fk);
		return db.insert(TABLE_List, null, values);
		}
		catch (SQLiteException sqle)
		{
			Log.w(LOG_TAG, "Could not create Shopping List "+sqle.getMessage());
			return -1;
		}
	}

	public Cursor getShoppingLists()
	{
		return db.rawQuery("select * from "+TABLE_List, null);
	}
	/*

	
	public Cursor getHighScores() {
		return db.rawQuery("select * from "+TABLE, null);
		}
		*/
	
	}