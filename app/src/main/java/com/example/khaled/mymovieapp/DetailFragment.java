package com.example.khaled.mymovieapp;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.khaled.mymovieapp.adapters.reviewsAdapter;
import com.example.khaled.mymovieapp.adapters.trailersAdapter;
import com.example.khaled.mymovieapp.database.MovieDbHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by khaled on 11/5/2016.
 */

public class DetailFragment extends android.support.v4.app.Fragment {
    public DetailFragment() {
    }

    com.example.khaled.mymovieapp.adapters.trailersAdapter trailersAdapter;
    com.example.khaled.mymovieapp.adapters.reviewsAdapter reviewsAdapter;
    ImageView fBtn;
    ListView trailerList;
    ListView reviewList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        final Bundle bundle;
        bundle = getArguments();
        ((TextView) rootView.findViewById(R.id.movie_name)).setText(bundle.getString("movieName"));
        ((TextView) rootView.findViewById(R.id.date)).setText(bundle.getString("date"));
        ((TextView) rootView.findViewById(R.id.overview)).setText(bundle.getString("overview"));
        ((TextView) rootView.findViewById(R.id.rate)).setText(Double.toString(bundle.getDouble("rate")));
        ImageView poster = (ImageView) rootView.findViewById(R.id.detail_poster);
        Picasso.with(getActivity()).load(bundle.getString("path")).into(poster);
        fBtn = (ImageView) rootView.findViewById(R.id.favoutiteBtn);
        if (bundle.getBoolean("isFav")) {
            fBtn.setColorFilter(Color.YELLOW);
        } else {
            fBtn.clearColorFilter();
        }
        fBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bundle.getBoolean("isFav")) {
                    fBtn.clearColorFilter();
                    bundle.putBoolean("isFav", false);
                    new MovieDbHelper(getActivity()).deleteMovie(bundle.getInt("id"));
                } else {
                    fBtn.setColorFilter(Color.YELLOW);
                    bundle.putBoolean("isFav", true);
                    Movie m = new Movie();
                    m.setFav(bundle.getBoolean("isFav"));
                    m.setReleaseDate(bundle.getString("date"));
                    m.setOverview(bundle.getString("overview"));
                    m.setTitle(bundle.getString("movieName"));
                    m.setId(bundle.getInt("id"));
                    m.setRate(bundle.getDouble("rate"));
                    m.setPath(bundle.getString("path"));
                    new MovieDbHelper(getActivity()).insertMovie(m);
                }
            }
        });
        trailerList = (ListView) rootView.findViewById(R.id.trailers_list);
        reviewList = (ListView) rootView.findViewById(R.id.reviews_list);
        new BringTrailersTask().execute(bundle.getInt("id"));
        new BringReviewsTask().execute(bundle.getInt("id"));

        trailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String link = trailersAdapter.getItem(position);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            }
        });
        return rootView;
    }

    public class BringTrailersTask extends AsyncTask<Integer, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Integer... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader;
            String jsonStr = null;
            try {
                URL url = new URL("http://api.themoviedb.org/3/movie/" + params[0] + "/videos?api_key=6747689de753c8f20f248bd73ae08fc1");

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
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();

            } catch (IOException e) {

                Log.v("api connection :", "Error");
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            try {
                return trailersFromJson(jsonStr);
            } catch (JSONException e) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList<String> trailerLinks) {
            trailersAdapter = new trailersAdapter(trailerLinks, getActivity());
            trailerList.setAdapter(trailersAdapter);
            ViewGroup.LayoutParams lp = trailerList.getLayoutParams();
            lp.height = (trailersAdapter.getCount()) * 90;
            trailerList.setLayoutParams(lp);
            reviewList.requestLayout();
        }

    }

    private ArrayList<String> trailersFromJson(String jsonStr) throws JSONException {
        JSONObject trailersJson = new JSONObject(jsonStr);
        JSONArray objectsArr = trailersJson.getJSONArray("results");
        ArrayList<String> youtubeLinks = new ArrayList<>();
        for (int i = 0; i < objectsArr.length(); i++) {
            String key;
            JSONObject trailerObject = objectsArr.getJSONObject(i);
            key = trailerObject.getString("key");
            youtubeLinks.add("https://www.youtube.com/watch?v=" + key);
        }
        return youtubeLinks;
    }

    public class BringReviewsTask extends AsyncTask<Integer, Void, ArrayList<Map<String, String>>> {

        @Override
        protected ArrayList<Map<String, String>> doInBackground(Integer... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader;
            String jsonStr = null;
            try {
                URL url = new URL("http://api.themoviedb.org/3/movie/" + params[0] + "/reviews?api_key=6747689de753c8f20f248bd73ae08fc1");

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
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();

            } catch (IOException e) {

                Log.v("Response :", "Error");
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            try {
                return reviewsFromJson(jsonStr);
            } catch (JSONException e) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList<Map<String, String>> reviews) {
            reviewsAdapter = new reviewsAdapter(reviews, getActivity());
            reviewList.setAdapter(reviewsAdapter);
        }

    }

    private ArrayList<Map<String, String>> reviewsFromJson(String jsonStr) throws JSONException {
        JSONObject trailersJson = new JSONObject(jsonStr);
        JSONArray objectsArr = trailersJson.getJSONArray("results");
        ArrayList<Map<String, String>> author_content_list = new ArrayList<>();
        for (int i = 0; i < objectsArr.length(); i++) {
            Map<String, String> review = new HashMap<>();
            JSONObject trailerObject = objectsArr.getJSONObject(i);
            review.put("author", trailerObject.getString("author"));
            review.put("content", trailerObject.getString("content"));
            author_content_list.add(review);
        }
        return author_content_list;
    }
}


