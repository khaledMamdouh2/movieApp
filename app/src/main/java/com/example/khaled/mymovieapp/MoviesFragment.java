package com.example.khaled.mymovieapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


import com.example.khaled.mymovieapp.adapters.postersAdapter;
import com.example.khaled.mymovieapp.database.MovieDbHelper;

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


public class MoviesFragment extends android.support.v4.app.Fragment {
    boolean tabMode = false;
    ArrayList<Movie> movies = new ArrayList<>();
    GridView myGrid;
    postersAdapter myAdapter;
    String moviesType;
    String sortType;

    public MoviesFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isConnected()) {
            BringMoviesTask task = new BringMoviesTask();
            task.execute();
            CountDownTimer toastCountDown;
            toastCountDown = new CountDownTimer(2000,1000) {
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    Toast.makeText(getActivity(),moviesType+" movies",Toast.LENGTH_LONG).show();
                }
            };
            toastCountDown.start();
        } else {
            Toast.makeText(getActivity(), "no internet connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movies, container, false);
        myGrid = (GridView) view.findViewById(R.id.grid);

        if (isTablet(getActivity())) {
            tabMode = true;
        }

        myGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie movieClicked = (Movie) myAdapter.getItem(position);
                if (tabMode) {
                    Bundle b = new Bundle();
                    b.putInt("id", movieClicked.getId());
                    b.putDouble("rate", movieClicked.getRate());
                    b.putString("movieName", movieClicked.getTitle());
                    b.putString("date", movieClicked.getReleaseDate());
                    b.putString("path", movieClicked.getPath());
                    b.putString("overview", movieClicked.getOverview());
                    b.putBoolean("isFav", movieClicked.getisFav());
                    DetailFragment d = new DetailFragment();
                    d.setArguments(b);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.tablet_detail, d).commit();
                } else {

                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra("id", movieClicked.getId());
                    intent.putExtra("rate", movieClicked.getRate());
                    intent.putExtra("movieName", movieClicked.getTitle());
                    intent.putExtra("date", movieClicked.getReleaseDate());
                    intent.putExtra("path", movieClicked.getPath());
                    intent.putExtra("overview", movieClicked.getOverview());
                    intent.putExtra("isFav", movieClicked.getisFav());
                    startActivity(intent);
                }


            }
        });
        return view;
    }


    public class BringMoviesTask extends AsyncTask<Void, Void, ArrayList<Movie>> {
        @Override
        protected ArrayList<Movie> doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader;
            String jsonStr = null;
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            sortType = sharedPrefs.getString("movie-type", "popular");
            if (sortType.equals("Favourite")) {
                movies = new MovieDbHelper(getActivity()).getMovies();
                return movies;
            }
            try {
                URL url = new URL("http://api.themoviedb.org/3/movie/" + sortType + "?api_key=6747689de753c8f20f248bd73ae08fc1");

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
                return extractFromJson(jsonStr);
            } catch (JSONException e) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {

            myAdapter = new postersAdapter(movies, getActivity());
            myGrid.setAdapter(myAdapter);
            moviesType=sortType;

        }
    }

    private ArrayList<Movie> extractFromJson(String jsonStr) throws JSONException {
        movies = new ArrayList<>();
        JSONObject movieJson = new JSONObject(jsonStr);
        JSONArray objectsArr = movieJson.getJSONArray("results");
        for (int i = 0; i < objectsArr.length(); i++) {
            Movie movie = new Movie();
            String poster_path, title, overview, releaseDate;
            int id;
            double rate;
            JSONObject movieObject = objectsArr.getJSONObject(i);
            poster_path = movieObject.getString("poster_path");
            overview = movieObject.getString("overview");
            title = movieObject.getString("title");
            releaseDate = movieObject.getString("release_date");
            rate = movieObject.getDouble("vote_average");
            id = movieObject.getInt("id");
            movie.setPath("http://image.tmdb.org/t/p/w185" + poster_path);
            movie.setId(id);
            movie.setOverview(overview);
            movie.setReleaseDate(releaseDate);
            movie.setRate(rate);
            movie.setTitle(title);
            movie.setFav(isFav(id));
            movies.add(movie);
        }
        return movies;
    }

    boolean isFav(int id) {
        ArrayList<Movie> movies;
        movies = new MovieDbHelper(getActivity()).getMovies();
        for (int i = 0; i < movies.size(); i++) {
            Movie movie;
            movie = movies.get(i);
            if (id == movie.getId()) {
                return true;
            }
        }
        return false;
    }

    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected())
            return true;
        else
            return false;
    }
}




