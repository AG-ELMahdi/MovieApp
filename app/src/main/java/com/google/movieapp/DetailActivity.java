package com.google.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.movieapp.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

public class DetailActivity extends AppCompatActivity {

    private Movie mMovie;

    private ImageView mImageView;
    private TextView mTitleView;
    private TextView mOverviewView;
    private TextView mDateView;
    private TextView mVoteAverageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if (savedInstanceState == null) {
            mMovie = intent.getParcelableExtra("detail_movie");
        }

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


    }
}
