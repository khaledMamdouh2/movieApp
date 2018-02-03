package com.example.khaled.mymovieapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.khaled.mymovieapp.Movie;

import java.util.ArrayList;

/**
 * Created by khaled on 11/18/2016.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "movies.db";
    SQLiteDatabase dp;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVOURITE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY," +
                MovieContract.MovieEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RATE + " NUMERIC, " +
                MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL " +
                " );";
        db.execSQL(SQL_CREATE_FAVOURITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        onCreate(db);
    }

    public void insertMovie(Movie movie) {
        dp = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPath());
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_DATE, movie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_NAME, movie.getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RATE, movie.getRate());

        dp.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
        dp.close();
    }

    public void deleteMovie(int id) {
        dp = this.getWritableDatabase();
        dp.delete(MovieContract.MovieEntry.TABLE_NAME, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ", new String[]{String.valueOf(id)});
        dp.close();
    }

    public ArrayList<Movie> getMovies() {
        ArrayList<Movie> list = new ArrayList<>();
        dp = this.getReadableDatabase();
        String columns[] = new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID, MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                MovieContract.MovieEntry.COLUMN_OVERVIEW, MovieContract.MovieEntry.COLUMN_DATE, MovieContract.MovieEntry.COLUMN_MOVIE_NAME,
                MovieContract.MovieEntry.COLUMN_RATE};
        Cursor cursor = dp.query(MovieContract.MovieEntry.TABLE_NAME, columns, null, null, null, null, null);
        int indexId = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        int indextitle = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_NAME);
        int indexposter = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        int indexoverview = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        int indexrete = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATE);
        int indexdate = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DATE);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.setId(cursor.getInt(indexId));
                movie.setRate(cursor.getDouble(indexrete));
                movie.setPath(cursor.getString(indexposter));
                movie.setTitle(cursor.getString(indextitle));
                movie.setOverview(cursor.getString(indexoverview));
                movie.setReleaseDate(cursor.getString(indexdate));
                movie.setFav(true);
                list.add(movie);
            } while (cursor.moveToNext());
            cursor.close();
            dp.close();
            return list;
        }
        cursor.close();
        dp.close();
        return list;


    }
}
