package com.example.nitheeshkpai.nytimes;

/**
 * Created by nitheeshkpai on 3/6/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitheeshkpai on 2/21/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "NewsItemsManager";

    // Contacts table name
    private static final String TABLE_ITEMS = "newsItems";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_URL = "url";
    private static final String KEY_BODY = "body";
    private static final String KEY_DATE = "date";
    private static final String KEY_IMAGE_URL = "imageURL";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NEWS_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
                + KEY_URL + " TEXT," + KEY_BODY + " TEXT," + KEY_DATE + " TEXT,"
                + KEY_IMAGE_URL + " TEXT" + ")";
        db.execSQL(CREATE_NEWS_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    public void addItem(NewsItemInfo itemInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, itemInfo.getTitle());
        values.put(KEY_URL, itemInfo.getLink());
        values.put(KEY_BODY, itemInfo.getBody());
        values.put(KEY_DATE, itemInfo.getDate());
        values.put(KEY_IMAGE_URL, itemInfo.getImageURL());

        db.insert(TABLE_ITEMS, null, values);
        db.close();
    }

    public List<NewsItemInfo> getAllItems() {
        List<NewsItemInfo> itemInfoList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_ITEMS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                NewsItemInfo itemInfo = new NewsItemInfo();
                itemInfo.setTitle(cursor.getString(1));
                itemInfo.setLink(cursor.getString(2));
                itemInfo.setBody(cursor.getString(3));
                itemInfo.setDate(cursor.getString(4));
                itemInfo.setImageURL(cursor.getString(5));

                itemInfoList.add(itemInfo);
            } while (cursor.moveToNext());
        }

        return itemInfoList;
    }

    public void deleteItem(NewsItemInfo itemInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, KEY_URL + " = ?",
                new String[]{String.valueOf(itemInfo.getLink())});
        db.close();
    }

    public void deleteAllItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, null, null);
        db.close();
    }
}
