package com.google.movieapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
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

import com.google.movieapp.ArrayAdapters.MovieAdapter;
import com.google.movieapp.Data.GetMoviesDataFromJson;
import com.google.movieapp.Helper.MoviesContract;
import com.google.movieapp.Models.Movies;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.media.tv.TvContract.Programs.Genres.MOVIES;
import static com.google.movieapp.Data.GetMoviesDataFromCursor.getFavoriteMoviesDataFromCursor;

public class MainActivity extends AppCompatActivity {
    private static final String POPULAR = "popular";
    private static final String Top_RATED = "top_rated";
    private static final String FAVORITE = "favorite";
    private static final String MOVIE = "movie";
    private static final String SORT_BY = "SORT_BY";
    private static final String SETTING = "setting";



    private String sortBy = POPULAR;


    private GridView mGridView;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mProgressBar;
    private TextView errorTextView;





    private List<Movies> mMovies ;

    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_ID,
            MoviesContract.MoviesEntry.COLUMN_TITLE,
            MoviesContract.MoviesEntry.COLUMN_POSTER_IMAGE,
            MoviesContract.MoviesEntry.COLUMN_BACKDROP_IMAGE,
            MoviesContract.MoviesEntry.COLUMN_OVERVIEW,
            MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE,
            MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE
    };

    public static final int COL_MOVIE_ID = 1;
    public static final int COL_TITLE = 2;
    public static final int COL_POSTER_IMAGE= 3;
    public static final int COL_BACKDROP_IMAGE = 4;
    public static final int COL_OVERVIEW = 5;
    public static final int COL_VOTE_AVERAGE = 6;
    public static final int COL_RELEASE_DATE= 7;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        errorTextView = (TextView) findViewById(R.id.error_message);


        mGridView = (GridView) findViewById(R.id.gridview_movies);

        mMovieAdapter = new MovieAdapter(this);

        mGridView.setAdapter(mMovieAdapter);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movies movie = mMovieAdapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class)
                        .putExtra("detail_movie", movie);
                startActivity(intent);
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SETTING)) {
                sortBy = savedInstanceState.getString(SETTING);
            }

            if (savedInstanceState.containsKey(MOVIES)) {
                mMovies = savedInstanceState.getParcelableArrayList(MOVIES);
                showDataView(mMovies);

            }  else {
                updateMovies(sortBy);
            }
        } else {
            updateMovies(sortBy);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMovies != null && !mMovies.isEmpty()) {
            outState.putParcelableArrayList(MOVIE, (ArrayList<? extends Parcelable>) mMovies);
        }
        outState.putString(SORT_BY, sortBy);

        if (!sortBy.contentEquals(POPULAR)) {
            outState.putString(SORT_BY, sortBy);
        }
    }

    private void updateMovies(String param) {

        if (!param.contentEquals(FAVORITE)) {
            new FetchMoviesTask().execute(param);
        } else {
            new FetchFavoriteMoviesTask(this).execute();
        }
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
            case R.id.sort_by_favorite:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                sortBy = FAVORITE;
                updateMovies(sortBy);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDataView(List<Movies> movies) {

        mMovieAdapter.clear();
        for (Movies movie : movies) {
            mMovieAdapter.add(movie);
        }
        errorTextView.setVisibility(View.INVISIBLE);
    }
    public void showErrorMessage() {
        mMovieAdapter.clear();
        errorTextView.setVisibility(View.VISIBLE);

    }

    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movies>> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();



        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);

            super.onPreExecute();
        }
        @Override
        protected List<Movies> doInBackground(String... params) {
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

        @Override
        protected void onPostExecute(List<Movies> movies) {
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
    public class FetchFavoriteMoviesTask extends AsyncTask<Void, Void, List<Movies>> {

        Context mContext;

        public FetchFavoriteMoviesTask(Context context) {
            mContext = context;
        }

        @Override
        protected List<Movies> doInBackground(Void... params) {
            Cursor cursor = mContext.getContentResolver().query(
                    MoviesContract.MoviesEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
            return getFavoriteMoviesDataFromCursor(cursor);
        }

        @Override
        protected void onPostExecute(List<Movies> movies) {
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