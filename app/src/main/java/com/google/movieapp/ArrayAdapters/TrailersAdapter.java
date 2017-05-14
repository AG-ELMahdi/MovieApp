package com.google.movieapp.ArrayAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.movieapp.Models.Trailers;
import com.google.movieapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed El-Mahdi on 4/30/2017.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {

    private final ArrayList<Trailers> mTrailers;
    private final TrailersAdapterOnClickHandler mClickHandler;

    public TrailersAdapter(ArrayList<Trailers> mTrailers, TrailersAdapterOnClickHandler mClickHandler) {
        this.mTrailers = mTrailers;
        this.mClickHandler = mClickHandler;
    }


    public interface TrailersAdapterOnClickHandler {
        void onClick(Trailers trailers, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_trailer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Trailers trailer = mTrailers.get(position);
        final Context context = holder.mView.getContext();


        holder.mTrailer = trailer;
        String image_url = "http://img.youtube.com/vi/" + trailer.getmKey() + "/0.jpg";
        Picasso.with(context).load(image_url).into(holder.mTrailerImage);
        holder.mTrailerName.setText(trailer.getmName());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickHandler.onClick(trailer, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mTrailers.size();

    }
    public void clear() {

            mTrailers.clear();

        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Trailers mTrailer;
        public final View mView;
        public final ImageView mTrailerImage;
        public final TextView mTrailerName;


        public ViewHolder(View view){
            super(view);
            mTrailerImage = (ImageView) view.findViewById(R.id.trailer_image);
            mTrailerName = (TextView) view.findViewById(R.id.trailer_name);
            mView = view;

        }
    }

    public void add(List<Trailers> trailers) {
        mTrailers.clear();
        mTrailers.addAll(trailers);
        notifyDataSetChanged();
    }

    public ArrayList<Trailers> getTrailers() {
        return mTrailers;
    }
}
