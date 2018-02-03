package com.example.khaled.mymovieapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.khaled.mymovieapp.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by khaled on 11/25/2016.
 */

public class reviewsAdapter extends BaseAdapter {
    private ArrayList<Map<String, String>> reviews = new ArrayList<>();
    private Context context;

    public reviewsAdapter(ArrayList<Map<String, String>> reviews, Context c) {
        this.reviews = reviews;
        context = c;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Map<String, String> getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.reviews_list_item, null);
        }

        TextView author = (TextView) convertView.findViewById(R.id.author);
        TextView content = (TextView) convertView.findViewById(R.id.review_content);
        author.setText((reviews.get(position)).get("author"));
        content.setText((reviews.get(position)).get("content"));
        return convertView;
    }
}
