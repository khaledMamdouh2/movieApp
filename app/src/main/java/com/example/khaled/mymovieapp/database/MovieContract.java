package com.example.khaled.mymovieapp.database;

import android.provider.BaseColumns;

/**
 * Created by khaled on 11/18/2016.
 */

public class MovieContract {
    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "favourites";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_DATE = "release_date";

        public static final String COLUMN_MOVIE_NAME = "movie_name";

        public static final String COLUMN_OVERVIEW = "overview";

        public static final String COLUMN_POSTER_PATH = "path";

        public static final String COLUMN_RATE = "rate";

    }
}
