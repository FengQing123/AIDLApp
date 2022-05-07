package com.fengqing.aidlapp.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author fengqing
 * @date 2019/5/27
 * 调用 getWritableDatabase 或者 getReadableDatabase 方法时，
 * 会根据DB_NAME去本地查找是否有数据库，如果没有就调用onCreate方法创建，
 * 如果有，就使用DB_VERSION来判断是否要调用onUpgrade方法更新数据库
 */

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "book_provider.db";

    public static final String BOOK_TABLE_NAME = "book";

    public static final String USER_TABLE_NAME = "user";

    public static final int DB_VERSION = 1;

    private String CREATE_BOOK_TABLE = "create table if not exists " + BOOK_TABLE_NAME + "(_id integer primary key," + "name text)";
    private String CREATE_USER_TABLE = "create table if not exists " + USER_TABLE_NAME + "(_id integer primary key," + "name text," + "sex integer," + "higher integer)";

    public DbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK_TABLE);
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
