package com.example.asmahansalem.booklisting;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Here is a Java code source that helped me in implement this how to write this JSON Parsing and Async Task
 * <p>
 * https://github.com/laramartin/android_book_listing/blob/master/app/src/main/java/eu/laramartin/booklisting/MainActivity.java
 */
public class MainActivity extends AppCompatActivity {

    EditText Search;
    Context context = MainActivity.this;
    ImageView searchButton;
    ProgressBar mProgressBar;
    adapterBook adapter;
    ListView bookList;
    TextView noData;
    static final String SEARCH_RESULTS = "booksSearchResults";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // define views
        Search = (EditText) findViewById(R.id.searchEditText);
        searchButton = (ImageView) findViewById(R.id.search);
        noData = (TextView) findViewById(R.id.text_noData);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        bookList = (ListView) findViewById(R.id.bookList);

        adapter = new adapterBook(this, -1);
        bookList.setAdapter(adapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetConnectionAvailable()) {
                    asyncTask task = new asyncTask();
                    task.execute();
                    mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(MainActivity.this, R.string.error_connection,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (savedInstanceState != null) {
            book[] books = (book[]) savedInstanceState.getParcelableArray(SEARCH_RESULTS);
            adapter.addAll(books);
            mProgressBar.setVisibility(View.GONE);

        }
    }

    private boolean isInternetConnectionAvailable() {
        try {
            // Errors resolved by through the solution this link
            // https://stackoverflow.com/questions/31654999/boolean-android-net-networkinfo-isconnectedorconnecting-is-not-working-in-andr
            ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected())
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void updateUi(List<book> books) {
        if (books.isEmpty()) {
            // if no Results found, will display a message
            mProgressBar.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);

        } else {
            noData.setVisibility(View.GONE);
        }
        adapter.clear();
        adapter.addAll(books);
    }

    private String getUserInput() {
        return Search.getText().toString();
    }

    private String getUrlForHttpRequest() {
        final String baseUrl = "https://www.googleapis.com/books/v1/volumes?q=1";
        String formatUserInput = getUserInput().trim().replaceAll("\\s+", "+");
        String url = baseUrl + formatUserInput;
        return url;
    }

    private class asyncTask extends AsyncTask<URL, Void, List<book>> {

        @Override
        protected List<book> doInBackground(URL... urls) {
            URL url = createURL(getUrlForHttpRequest());
            String jsonResponse = "";

            try {
                jsonResponse = makeHttpRequest(url);

            } catch (IOException e) {
                e.printStackTrace();
            }

            List<book> books = parseJson(jsonResponse);
            return books;
        }

        @Override
        protected void onPostExecute(List<book> books) {
            if (books == null) {
                return;
            }
            updateUi(books);
        }

        private URL createURL(String stringUrl) {
            try {
                return new URL(stringUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            if (url == null) {
                return jsonResponse;
            }
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e("mainActivity", "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();

                }
            }
            return jsonResponse;

        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private List<book> parseJson(String json) {

            if (json == null) {
                return null;
            }

            List<book> books = Utils.extractBooks(json);
            return books;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        book[] books = new book[adapter.getCount()];
        for (int i = 0; i < books.length; i++) {
            books[i] = adapter.getItem(i);
        }
        outState.putParcelableArray(SEARCH_RESULTS, (Parcelable[]) books);
    }
}
