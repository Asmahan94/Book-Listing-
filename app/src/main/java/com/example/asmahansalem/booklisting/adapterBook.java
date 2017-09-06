package com.example.asmahansalem.booklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Asmahan Salem on 9/5/2017.
 */

public class adapterBook extends ArrayAdapter<book> {

    /**
     * Here is a simple adapter for listView
     */
    public adapterBook(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        book book = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_item, parent, false);
        }

        TextView bookName = (TextView) view.findViewById(R.id.bookName);
        TextView bookAuthor = (TextView) view.findViewById(R.id.bookAuthor);
        bookName.setText(book.getBookName());
        bookAuthor.setText(book.getBookAuthor());

        return view;
    }
}
