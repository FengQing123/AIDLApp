package com.fengqing.aidlapp.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author fengqing
 * @date 2019/5/27
 */

public class User implements Parcelable {

    public int mUserId;
    public String mUserName;
    public boolean isMale;
    public boolean isHigher;

    public User() {

    }

    public User(Parcel in) {
        mUserId = in.readInt();
        mUserName = in.readString();
        isMale = in.readByte() != 0;
        isHigher = in.readByte() != 0;//这里boolean值的读取用byte来实现
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mUserId);
        dest.writeString(mUserName);
        dest.writeByte((byte) (isMale ? 1 : 0));
        dest.writeByte((byte) (isHigher ? 1 : 0));//这里boolean的写入用byte来实现
    }

    @Override
    public String toString() {
        return String.format("[userId:%s,userName:%s,userSex:%s,isHigher:%s]", mUserId, mUserName, isMale, isHigher);
    }
}
