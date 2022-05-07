package com.fengqing.aidlapp.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fengqing.aidlapp.aidl.Book;
import com.fengqing.aidlapp.aidl.User;

/**
 * @author fengqing
 * @date 2019/5/27
 * <p>
 * 使用ContentProvider实现数据库的增删改查
 */
public class ProviderActivity extends AppCompatActivity {

    private static final String TAG = "ProviderActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri bookUri = Uri.parse("content://com.fengqing.aidlapp.book.provider/book");
        ContentValues values = new ContentValues();
        values.put("_id", 6);
        values.put("name", "程序设计的艺术");
        getContentResolver().insert(bookUri, values);
        Cursor bookCursor = getContentResolver().query(bookUri, new String[]{"_id", "name"}, null, null, null);
        while (bookCursor.moveToNext()) {
            Book book = new Book();
            book.mBookId = bookCursor.getInt(0);
            book.mBookName = bookCursor.getString(1);
            Log.e(TAG, "query book:" + book.toString());
        }
        bookCursor.close();


        Uri userUri = Uri.parse("content://com.fengqing.aidlapp.book.provider/user");
        Cursor userCursor = getContentResolver().query(userUri, new String[]{"_id", "name", "sex", "higher"}, null, null, null);
        while (userCursor.moveToNext()) {
            User user = new User();
            user.mUserId = userCursor.getInt(0);
            user.mUserName = userCursor.getString(1);
            user.isMale = userCursor.getInt(2) == 1;
            user.isHigher = userCursor.getInt(3) == 1;
            Log.e(TAG, "query user:" + user.toString());
        }
        userCursor.close();
    }
}
