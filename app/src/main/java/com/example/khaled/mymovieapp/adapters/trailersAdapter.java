package com.example.khaled.mymovieapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.khaled.mymovieapp.R;

import java.util.ArrayList;

/**
 * Created by khaled on 11/20/2016.
 */

public class trailersAdapter extends BaseAdapter {
    private ArrayList<String> links = new ArrayList<>();
    private Context context;
    public ArrayList<String> trailerSequence = new ArrayList<>();

    public trailersAdapter(ArrayList<String> links, Context c) {
        this.links = links;
        context = c;
        for (int i = 1; i <= links.size(); i++) {
            trailerSequence.add("Trailer " + i);
        }
    }

    @Override
    public int getCount() {
        return links.size();
    }

    @Override
    public String getItem(int position) {
        return links.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.trailers_list_item, null);
        }

        TextView trailer = (TextView) convertView.findViewById(R.id.trailer_item);
        trailer.setText(trailerSequence.get(position));
        return convertView;
    }
}
