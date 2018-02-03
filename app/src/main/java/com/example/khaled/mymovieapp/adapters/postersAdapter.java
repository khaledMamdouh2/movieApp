package com.example.khaled.mymovieapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.khaled.mymovieapp.Movie;
import com.example.khaled.mymovieapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

/**
 * Created by khaled on 10/21/2016.
 */

public class postersAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Movie> movies;


    public postersAdapter(ArrayList<Movie> movies, Context c) {
        this.movies = movies;
        this.context = c;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return movies.get(position).getId();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.one_movie, null);
        }

        ImageView poster = (ImageView) convertView.findViewById(R.id.imageView);

        Picasso.with(context).load(movies.get(i).getPath()).into(poster);

        return convertView;

    }
}
