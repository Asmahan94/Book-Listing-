package com.example.asmahansalem.booklisting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asmahan Salem on 9/6/2017.
 */

public class utils {

    /**
     * Here is a Java code source that helped me in how to write this class utils
     *
     * https://github.com/laramartin/android_book_listing/blob/master/app/src/main/java/eu/laramartin/booklisting/QueryUtils.java
     */
    private utils() {
    }

    public static String formatListOfAuthors(JSONArray authorsList) throws JSONException {

        String authorsListInString = null;

        if (authorsList.length() == 0) {
            return null;
        }

        for (int i = 0; i < authorsList.length(); i++) {
            if (i == 0) {
                authorsListInString = authorsList.getString(0);
            } else {
                authorsListInString += ", " + authorsList.getString(i);
            }
        }

        return authorsListInString;
    }


    public static List<book> extractBooks(String json) {

        List<book> books = new ArrayList<>();

        try {
            JSONObject jsonResponse = new JSONObject(json);

            if (jsonResponse.getInt("totalItems") == 0) {
                return books;
            }
            JSONArray jsonArray = jsonResponse.getJSONArray("items");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject bookObject = jsonArray.getJSONObject(i);

                JSONObject bookInfo = bookObject.getJSONObject("volumeInfo");

                String title = bookInfo.getString("title");
                JSONArray authorsArray = bookInfo.getJSONArray("authors");
                String authors = formatListOfAuthors(authorsArray);

                book Book = new book(authors, title);
                books.add(Book);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return books;
    }
}
