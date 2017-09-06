package com.example.asmahansalem.booklisting;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Asmahan Salem on 9/5/2017.
 */

public class book implements Parcelable {
    String bookAuthor;
    String bookName;

    /**
     * Here is a Java code source that helped me in how to write this class
     * <p>
     * https://github.com/laramartin/android_book_listing/blob/master/app/src/main/java/eu/laramartin/booklisting/Book.java
     */

    public book(String author, String title) {
        this.bookAuthor = author;
        this.bookName = title;
    }

    protected book(Parcel in) {
        bookAuthor = in.readString();
        bookName = in.readString();
    }

    public static final Parcelable.Creator<book> CREATOR = new Parcelable.Creator<book>() {
        @Override
        public book createFromParcel(Parcel in) {
            return new book(in);
        }

        @Override
        public book[] newArray(int size) {
            return new book[size];
        }
    };

    public String getBookAuthor() {
        return bookAuthor;
    }

    public String getBookName() {
        return bookName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(bookAuthor);
        parcel.writeString(bookName);
    }
}
