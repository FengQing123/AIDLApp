package com.fengqing.aidlapp.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author fengqing
 * @date 2019/5/23
 */

public class Book implements Parcelable {

    public int mBookId;
    public String mBookName;

    public Book() {

    }

    public Book(int id, String name) {
        mBookId = id;
        mBookName = name;
    }


    public Book(Parcel in) {
        mBookId = in.readInt();
        mBookName = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mBookId);
        dest.writeString(mBookName);
    }


    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public String toString() {
        return String.format("[bookId:%s,bookName:%s]", mBookId, mBookName);
    }
}
