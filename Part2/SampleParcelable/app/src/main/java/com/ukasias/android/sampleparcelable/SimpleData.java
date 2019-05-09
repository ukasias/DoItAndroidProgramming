package com.ukasias.android.sampleparcelable;

import android.os.Parcel;
import android.os.Parcelable;

public class SimpleData implements Parcelable {
    int number;
    String message;

    public SimpleData(int num, String msg) {
        number = num;
        message = msg;
    }

    public SimpleData(Parcel src) {
        number = src.readInt();
        message = src.readString();
    }

    public int getNumber() {
        return number;
    }

    public String getMessage() {
        return message;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SimpleData createFromParcel(Parcel in) {
            return new SimpleData(in);
        }

        public SimpleData[] newArray(int size) {
            return new SimpleData[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag) {
        dest.writeInt(number);
        dest.writeString(message);
    }
}
