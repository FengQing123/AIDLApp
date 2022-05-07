package com.fengqing.aidlapp.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * @author fengqing
 * @date 2019/5/27
 */

/**
 * 创建BookProvider,继承ContentProvider，并实现其中的六个抽象方法
 * 这六个方法中除了onCreate由系统调用并运行在主线程里，其他五个方法均由外界调用并运行Binder线程池中
 */
public class BookProvider extends ContentProvider {

    private static final String TAG = "BookProvider";

    private static final String AUTHORITY = "com.fengqing.aidlapp.book.provider";

    public static final int BOOK_URI_CODE = 0;
    public static final int USER_URI_CODE = 1;

    /**
     * 使用UriMatcher将Uri和Uri_Code关联
     * 把与uri所对应的表取出
     * content://com.fengqing.aidlapp.book.provider/book--对应Book表
     * content://com.fengqing.aidlapp.book.provider/user--对应User表
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    {
        sUriMatcher.addURI(AUTHORITY, "book", BOOK_URI_CODE);
        sUriMatcher.addURI(AUTHORITY, "user", USER_URI_CODE);
    }


    private Context mContext;

    private SQLiteDatabase mDB;

    private String getTableName(Uri uri) {
        String tableName = null;
        switch (sUriMatcher.match(uri)) {
            case BOOK_URI_CODE:
                tableName = DbOpenHelper.BOOK_TABLE_NAME;
                break;
            case USER_URI_CODE:
                tableName = DbOpenHelper.USER_TABLE_NAME;
                break;
            default:
                break;
        }
        return tableName;
    }

    /**
     * ContentProvider的创建，做些初始化的操作，但不能做耗时操作
     * ContentResolver 执行增删改查时创建
     *
     * @return
     */
    @Override
    public boolean onCreate() {
        Log.e(TAG, "onCreate current thread=" + Thread.currentThread().getName());
        mContext = getContext();
        mDB = new DbOpenHelper(mContext).getWritableDatabase();

        /**
         * 这里不推荐在主线程中进行耗时的数据库操作
         */
        mDB.execSQL("delete from " + DbOpenHelper.BOOK_TABLE_NAME);
        mDB.execSQL("delete from " + DbOpenHelper.USER_TABLE_NAME);
        mDB.execSQL("insert into book values(3,'Android');");
        mDB.execSQL("insert into book values(4,'iOS');");
        mDB.execSQL("insert into book values(5,'Html5');");
        mDB.execSQL("insert into user values(1,'jake',1,2);");
        mDB.execSQL("insert into user values(2,'jas',2,1);");

        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.e(TAG, "query current thread=" + Thread.currentThread().getName());
        Log.e(TAG, "query Uri:" + uri);
        String table = getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
        return mDB.query(table, projection, selection, selectionArgs, null, null, sortOrder, null);
    }


    /**
     * 用于返回一个Uri请求对应的MIME类型(媒体类型)，比如图片、视频等
     * 如果应用不关注，可以直接返回null;
     *
     * @param uri
     * @return
     */
    @Override
    public String getType(Uri uri) {
        Log.e(TAG, "getType current thread=" + Thread.currentThread().getName());
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.e(TAG, "insert current thread=" + Thread.currentThread().getName());
        Log.e(TAG, "insert Uri:" + uri);
        String table = getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        }

        mDB.insert(table, null, values);
        mContext.getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.e(TAG, "delete current thread=" + Thread.currentThread().getName());

        String table = getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        }

        int count = mDB.delete(table, selection, selectionArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.e(TAG, "update current thread=" + Thread.currentThread().getName());

        String table = getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        }

        int row = mDB.update(table, values, selection, selectionArgs);

        if (row > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return row;
    }
}
