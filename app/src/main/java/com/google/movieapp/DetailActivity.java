package com.google.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.movieapp.ArrayAdapters.ReviewsAdapter;
import com.google.movieapp.ArrayAdapters.TrailersAdapter;
import com.google.movieapp.Data.GetReviewsFromJson;
import com.google.movieapp.Data.GetTrailersFromJson;
import com.google.movieapp.Helper.MoviesContract;
import com.google.movieapp.Models.Movies;
import com.google.movieapp.Models.Reviews;
import com.google.movieapp.Models.Trailers;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class DetailActivity extends AppCompatActivity implements TrailersAdapter.TrailersAdapterOnClickHandler,
ReviewsAdapter.ReviewsAdapterOnClickHandler{

    private Movies mMovie;

    private ImageView mImageView;
    private TextView mTitleView;
    private TextView mOverviewView;
    private TextView mDateView;
    private TextView mVoteAverageView;

    private Toast mToast;



    public static final String DETAIL_MOVIE = "detail_movie";
    public static final String TRAILERS = "trailers";
    public static final String REVIEWS = "reviews";



    private TrailersAdapter mTrailerAdapter;
    private ReviewsAdapter mReviewAdapter;

   private RecyclerView mRecyclerViewTrailers;

   private RecyclerView mRecyclerViewReviews;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (savedInstanceState == null) {
            mMovie = intent.getParcelableExtra(DETAIL_MOVIE);
        }
        setContentView(R.layout.activity_detail);

        mRecyclerViewTrailers = (RecyclerView)findViewById(R.id.trailer_list);
        mRecyclerViewReviews = (RecyclerView)findViewById(R.id.review_list);

        mImageView = (ImageView) findViewById(R.id.detail_image);
        mTitleView = (TextView) findViewById(R.id.detail_title);
        mOverviewView = (TextView) findViewById(R.id.detail_overview);
        mDateView = (TextView) findViewById(R.id.detail_date);
        mVoteAverageView = (TextView) findViewById(R.id.detail_vote_average);

        String image_url = "http://image.tmdb.org/t/p/w342" + mMovie.getBackdrop_path();
        Picasso.with(this).load(image_url).into(mImageView);

        mTitleView.setText(mMovie.getTitle());
        mOverviewView.setText(mMovie.getOverview());

        String movie_date = mMovie.getRelease_date();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String date = DateUtils.formatDateTime(getApplicationContext(),
                    formatter.parse(movie_date).getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
            mDateView.setText(date);
        } catch (Exception e) {
            e.printStackTrace();
        }


        mVoteAverageView.setText(Integer.toString(mMovie.getVote_average()));


        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mRecyclerViewTrailers.setLayoutManager(layoutManager);

        mRecyclerViewTrailers.setHasFixedSize(true);

        mTrailerAdapter = new TrailersAdapter(new ArrayList<Trailers>(), this);

        mRecyclerViewTrailers.setAdapter(mTrailerAdapter);

        mReviewAdapter = new ReviewsAdapter(new ArrayList<Reviews>(), this);
        mRecyclerViewReviews.setAdapter(mReviewAdapter);



    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Trailers> trailers = mTrailerAdapter.getTrailers();
        if (trailers != null && !trailers.isEmpty()) {
            outState.putParcelableArrayList(TRAILERS, trailers);
        }

        ArrayList<Reviews> reviews = mReviewAdapter.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            outState.putParcelableArrayList(REVIEWS, reviews);
        }
    }
   /* public void markFavorite() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (!isFavorite()) {
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID,
                            mMovie.getId());
                    movieValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE,
                            mMovie.getTitle());
                    movieValues.put(MoviesContract.MoviesEntry.COLUMN_POSTER_IMAGE,
                            mMovie.getPoster_path());
                    movieValues.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW,
                            mMovie.getOverview());
                    movieValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE,
                            mMovie.getVote_average());
                    movieValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE,
                            mMovie.getRelease_date());
                    movieValues.put(MoviesContract.MoviesEntry.COLUMN_BACKDROP_IMAGE,
                            mMovie.getBackdrop_path());
                    getContext().getContentResolver().insert(
                            MoviesContract.MoviesEntry.CONTENT_URI,
                            movieValues
                    );
                }
                return null;



    }*/
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_favorite:
                if (mMovie != null) {
                    // check if movie is in favorites or not
                    new AsyncTask<Void, Void, Integer>() {

                        @Override
                        protected Integer doInBackground(Void... params) {
                            return isFavorited(getApplicationContext(), mMovie.getId());
                        }

                        @Override
                        protected void onPostExecute(Integer integer) {
                            // if it is in favorites
                            if (integer == 1) {
                                // delete from favorites
                                new AsyncTask<Void, Void, Integer>() {
                                    @Override
                                    protected Integer doInBackground(Void... params) {
                                        return getApplicationContext().getContentResolver().delete(
                                                MoviesContract.MoviesEntry.CONTENT_URI,
                                                MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " = ?",
                                                new String[]{Integer.toString(mMovie.getId())}
                                        );
                                    }

                                    @Override
                                    protected void onPostExecute(Integer integer) {
                                        item.setChecked(false);
                                        if (mToast != null) {
                                            mToast.cancel();
                                        }
                                        mToast = Toast.makeText(getApplicationContext(), getString(R.string.movie_unfavorite), Toast.LENGTH_SHORT);
                                        mToast.show();
                                    }
                                }.execute();
                            }
                            else {
                                new AsyncTask<Void, Void, Uri>() {
                                    @Override
                                    protected Uri doInBackground(Void... params) {
                                        ContentValues values = new ContentValues();

                                        values.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, mMovie.getId());
                                        values.put(MoviesContract.MoviesEntry.COLUMN_TITLE, mMovie.getTitle());
                                        values.put(MoviesContract.MoviesEntry.COLUMN_POSTER_IMAGE, mMovie.getPoster_path());
                                        values.put(MoviesContract.MoviesEntry.COLUMN_BACKDROP_IMAGE, mMovie.getBackdrop_path());
                                        values.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, mMovie.getOverview());
                                        values.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, mMovie.getVote_average());
                                        values.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, mMovie.getRelease_date());

                                        return getApplicationContext().getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI,
                                                values);
                                    }

                                    @Override
                                    protected void onPostExecute(Uri returnUri) {
                                        item.setChecked(true);
                                        if (mToast != null) {
                                            mToast.cancel();
                                        }
                                        mToast = Toast.makeText(getApplicationContext(), getString(R.string.favorites), Toast.LENGTH_SHORT);
                                        mToast.show();
                                    }
                                }.execute();
                            }
                        }
                    }.execute();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);

        inflater.inflate(R.menu.movie_detail_menu, menu);

        return true;
    }


    @Override
    public void onClick(Reviews review, int position) {

        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(review.getmUrl())));
    }

    @Override
    public void onClick(Trailers trailers, int position) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailers.getmKey()));
        startActivity(intent);

    }

    public class FetchTrailersTask extends AsyncTask<String, Void, List<Trailers>> {

        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        @Override
        protected List<Trailers> doInBackground(String... params) {
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
                final String VIDEO_PATH = "videos";
                final String API_PARAM = "api_key";
                final String QUERY_PARAM = "q";

                Uri.Builder builder = new Uri.Builder();
                builder.scheme(SCHEME)
                        .authority(AUTHORITY)
                        .appendPath(FIRST_PATH)
                        .appendPath(SECOND_PATH)
                        .appendPath(params[0])
                        .appendPath(VIDEO_PATH)
                        .appendQueryParameter(API_PARAM, getString(R.string.movie_api_key))
                        .appendQueryParameter(QUERY_PARAM, params[0]);

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
                return GetTrailersFromJson.getTrailersDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(List<Trailers> trailers) {

            if (trailers != null) {
                if (mTrailerAdapter != null) {
                    mTrailerAdapter.clear();
                        mTrailerAdapter.add(trailers);
                }

            }
        }
    }

    public class FetchReviewsTask extends AsyncTask<String, Void, List<Reviews>> {
        private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();
        @Override
        protected List<Reviews> doInBackground(String... params) {
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
                final String REVIEWS_PATH = "reviews";
                final String API_PARAM = "api_key";
                final String QUERY_PARAM = "q";

                Uri.Builder builder = new Uri.Builder();
                builder.scheme(SCHEME)
                        .authority(AUTHORITY)
                        .appendPath(FIRST_PATH)
                        .appendPath(SECOND_PATH)
                        .appendPath(params[0])
                        .appendPath(REVIEWS_PATH)
                        .appendQueryParameter(API_PARAM, getString(R.string.movie_api_key))
                        .appendQueryParameter(QUERY_PARAM, params[0]);

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
                return GetReviewsFromJson.getReviewsDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(List<Reviews> reviews) {
            if (reviews != null) {
                if (mReviewAdapter != null) {
                        mReviewAdapter.add(reviews);

                }
            }

        }
    }

    public int isFavorited(Context context, int id) {
        String mSelection = MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " = ?";
        String []mSelectionArg = new String[] { Integer.toString(id)};
        Cursor cursor = context.getContentResolver().query(
                MoviesContract.MoviesEntry.CONTENT_URI,
                null,
                mSelection,
                mSelectionArg,
                null
        );
        int numRows = cursor.getCount();
        cursor.close();
        return numRows;

    }


    @Override
    protected void onStart() {
        super.onStart();

        if(mMovie !=null) {
            new FetchTrailersTask().execute(Integer.toString(mMovie.getId()));
            new FetchReviewsTask().execute(Integer.toString(mMovie.getId()));
        }
    }

}
