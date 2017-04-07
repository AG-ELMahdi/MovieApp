package com.google.movieapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String POPULAR = "popular";
    private static final String Top_RATED = "top_rated";


    private String sortBy = POPULAR;


    private GridView mGridView;
    private com.google.movieapp.MovieAdapter mMovieAdapter;
    private ProgressBar mProgressBar;
    private TextView errorTextView;



    private List<Movie> mMovies = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        errorTextView = (TextView) findViewById(R.id.error_message);


        mGridView = (GridView) findViewById(R.id.gridview_movies);

        mMovieAdapter = new MovieAdapter(getApplicationContext());

        mGridView.setAdapter(mMovieAdapter);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = mMovieAdapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class)
                        .putExtra("detail_movie", movie);
                startActivity(intent);
            }
        });

        if (savedInstanceState != null) {

            if (savedInstanceState.containsKey("movies")) {
                mMovies = savedInstanceState.getParcelableArrayList("movies");
                for (Movie movie : mMovies) {
                    mMovieAdapter.add(movie);
                }
            } else {
                updateMovies(sortBy);
            }
        } else {
            updateMovies(sortBy);
        }
    }

    private void updateMovies(String param) {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute(param);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.movie_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.sort_by_popular_movie:
                if (item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
                sortBy = POPULAR;
                updateMovies(sortBy);
                return true;
            case R.id.sort_by_top_rating:
                if (item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
                sortBy = Top_RATED;
                updateMovies(sortBy);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();


        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);

            super.onPreExecute();
        }
        @Override
        protected List<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String SCHEME = "http";
                final String AUTHORITY = "api.themoviedb.org";
                final String FIRST_PATH = "3";
                final String SECOND_PATH = "movie";
                final String API_PARAM = "api_key";
                final String QUERY_PARAM = "q";

                Uri.Builder builder = new Uri.Builder();
                builder.scheme(SCHEME)
                        .authority(AUTHORITY)
                        .appendPath(FIRST_PATH)
                        .appendPath(SECOND_PATH)
                        .appendPath(sortBy)
                        .appendQueryParameter(API_PARAM, getString(R.string.movie_api_key))
                        .appendQueryParameter(QUERY_PARAM, sortBy);

                URL url = new URL(builder.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return GetMoviesDataFromJson.getMoviesDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;

        }

        private void showDataView(List<Movie> movies) {

            mMovieAdapter.clear();
            for (Movie movie : movies) {
                mMovieAdapter.add(movie);
            }
            errorTextView.setVisibility(View.INVISIBLE);
        }
        private void showErrorMessage() {
            mMovieAdapter.clear();
            errorTextView.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (movies != null) {
                if (mMovieAdapter != null) {
                    showDataView(movies);
                }
                mMovies = new ArrayList<>();
                mMovies.addAll(movies);
            }
            else {
                showErrorMessage();
            }
        }
    }

}