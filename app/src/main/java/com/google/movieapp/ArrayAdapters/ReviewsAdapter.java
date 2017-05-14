package com.google.movieapp.ArrayAdapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.movieapp.Models.Reviews;
import com.google.movieapp.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ahmed El-Mahdi on 4/30/2017.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private final static String LOG_TAG = ReviewsAdapter.class.getSimpleName();

    private final ArrayList<Reviews> mReviews;
    private final ReviewsAdapterOnClickHandler mClickHandler;

    public interface ReviewsAdapterOnClickHandler {
        void onClick(Reviews review, int position);
    }


    public ReviewsAdapter(ArrayList<Reviews> mReviews,ReviewsAdapterOnClickHandler mClickHandler ) {
        this.mReviews = mReviews;
        this.mClickHandler=mClickHandler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Reviews review = mReviews.get(position);
        holder.mReview = review;
        holder.mContent.setText(review.getmContent());
        holder.mAuthor.setText(review.getmAuthor());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickHandler.onClick(review, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public Reviews mReview;
        public final TextView mAuthor;
        public final TextView mContent;

        public ViewHolder(View view) {
            super(view);
            this.mAuthor = (TextView) view.findViewById(R.id.author);
            this.mContent = (TextView) view.findViewById(R.id.content);
            mView = view;
        }
    }
    public void add(List<Reviews> reviews) {
        mReviews.clear();
        mReviews.addAll(reviews);
        notifyDataSetChanged();
    }

    public ArrayList<Reviews> getReviews() {
        return mReviews;
    }
}


