package com.wimsonevel.moviesapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wimsonevel.moviesapp.model.MovieData;

/**
 * Created by Wim on 12/8/16.
 */
public class SQLiteDB extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movie.db";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MovieField.TABLE_NAME + " (" +
                    MovieField.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MovieField.COLUMN_MOVIE_ID + INTEGER_TYPE + COMMA_SEP +
                    MovieField.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    MovieField.COLUMN_LANGUAGE + TEXT_TYPE + COMMA_SEP +
                    MovieField.COLUMN_POSTER + TEXT_TYPE + COMMA_SEP +
                    MovieField.COLUMN_ADULT + INTEGER_TYPE + " DEFAULT 0" + COMMA_SEP +
                    MovieField.COLUMN_OVERVIEW + TEXT_TYPE + COMMA_SEP +
                    MovieField.COLUMN_DATE + TEXT_TYPE + COMMA_SEP +
                    MovieField.COLUMN_BACKDROP + TEXT_TYPE + COMMA_SEP +
                    MovieField.COLUMN_POPULARITY + REAL_TYPE + COMMA_SEP +
                    MovieField.COLUMN_VOTE_COUNT + INTEGER_TYPE + COMMA_SEP +
                    MovieField.COLUMN_VIDEO + INTEGER_TYPE + " DEFAULT 0" + COMMA_SEP +
                    MovieField.COLUMN_VOTE_AVERAGE + REAL_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieField.TABLE_NAME;

    public SQLiteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void create(MovieData movieData){
        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(MovieField.COLUMN_MOVIE_ID, movieData.getId());
        values.put(MovieField.COLUMN_TITLE, movieData.getOriginalTitle());
        values.put(MovieField.COLUMN_LANGUAGE, movieData.getOriginalLanguage());
        values.put(MovieField.COLUMN_POSTER, movieData.getPosterPath());
        values.put(MovieField.COLUMN_ADULT, movieData.isAdult() ? 1 : 0);
        values.put(MovieField.COLUMN_OVERVIEW, movieData.getOverview());
        values.put(MovieField.COLUMN_DATE, movieData.getReleaseDate());
        values.put(MovieField.COLUMN_BACKDROP, movieData.getBackdropPath());
        values.put(MovieField.COLUMN_POPULARITY, movieData.getPopularity());
        values.put(MovieField.COLUMN_VOTE_COUNT, movieData.getVoteCount());
        values.put(MovieField.COLUMN_VIDEO, movieData.isVideo() ? 1 : 0);
        values.put(MovieField.COLUMN_VOTE_AVERAGE, movieData.getVoteAverage());
        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                MovieField.TABLE_NAME,
                null,
                values);
    }

    public Cursor retrieve(){
        SQLiteDatabase db = getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                MovieField.COLUMN_MOVIE_ID,
                MovieField.COLUMN_TITLE,
                MovieField.COLUMN_LANGUAGE,
                MovieField.COLUMN_POSTER,
                MovieField.COLUMN_ADULT,
                MovieField.COLUMN_OVERVIEW,
                MovieField.COLUMN_DATE,
                MovieField.COLUMN_BACKDROP,
                MovieField.COLUMN_POPULARITY,
                MovieField.COLUMN_VOTE_COUNT,
                MovieField.COLUMN_VIDEO,
                MovieField.COLUMN_VOTE_AVERAGE };
        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                MovieField.COLUMN_ID + " DESC";
        Cursor c = db.query(
                MovieField.TABLE_NAME,                    // The table to query
                projection,                                 // The columns to return
                null,                                       // The columns for the WHERE clause
                null,                                       // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                sortOrder                                   // The sort order
        );
        return c;
    }

    public void update(MovieData movieData){
        SQLiteDatabase db = getReadableDatabase();
        // New value for one column
        ContentValues values = new ContentValues();
        values.put(MovieField.COLUMN_TITLE, movieData.getOriginalTitle());
        values.put(MovieField.COLUMN_LANGUAGE, movieData.getOriginalLanguage());
        values.put(MovieField.COLUMN_POSTER, movieData.getPosterPath());
        values.put(MovieField.COLUMN_ADULT, movieData.isAdult() ? 1 : 0);
        values.put(MovieField.COLUMN_OVERVIEW, movieData.getOverview());
        values.put(MovieField.COLUMN_DATE, movieData.getReleaseDate());
        values.put(MovieField.COLUMN_BACKDROP, movieData.getBackdropPath());
        values.put(MovieField.COLUMN_POPULARITY, movieData.getPopularity());
        values.put(MovieField.COLUMN_VOTE_COUNT, movieData.getVoteCount());
        values.put(MovieField.COLUMN_VIDEO, movieData.isVideo() ? 1 : 0);
        values.put(MovieField.COLUMN_VOTE_AVERAGE, movieData.getVoteAverage());
        // Which row to update, based on the ID
        String selection = MovieField.COLUMN_MOVIE_ID + " = ? ";
        String[] selectionArgs = { String.valueOf(movieData.getId()) };
        int count = db.update(
                MovieField.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public void delete(int id){
        SQLiteDatabase db = getReadableDatabase();
        // Define 'where' part of query.
        String selection = MovieField.COLUMN_MOVIE_ID + " = ? ";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(id) };
        // Issue SQL statement.
        db.delete(MovieField.TABLE_NAME, selection, selectionArgs);
    }

    // Getting single movie
    public MovieData getMovieData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(MovieField.TABLE_NAME, new String[] {
                        MovieField.COLUMN_MOVIE_ID,
                        MovieField.COLUMN_TITLE,
                        MovieField.COLUMN_LANGUAGE,
                        MovieField.COLUMN_POSTER,
                        MovieField.COLUMN_ADULT,
                        MovieField.COLUMN_OVERVIEW,
                        MovieField.COLUMN_DATE,
                        MovieField.COLUMN_BACKDROP,
                        MovieField.COLUMN_POPULARITY,
                        MovieField.COLUMN_VOTE_COUNT,
                        MovieField.COLUMN_VIDEO,
                        MovieField.COLUMN_VOTE_AVERAGE }, MovieField.COLUMN_MOVIE_ID + "=?",
                new String[] { String.valueOf(id)}, null, null, null, null);


        if (cursor != null) {
            if(cursor.moveToFirst()) {
                MovieData movieData = new MovieData();
                movieData.setId(cursor.getInt(0));
                movieData.setOriginalTitle(cursor.getString(1));
                movieData.setOriginalLanguage(cursor.getString(2));
                movieData.setPosterPath(cursor.getString(3));
                movieData.setAdult(cursor.getInt(4) > 0 ? true : false);
                movieData.setOverview(cursor.getString(5));
                movieData.setReleaseDate(cursor.getString(6));
                movieData.setBackdropPath(cursor.getString(7));
                movieData.setPopularity(cursor.getDouble(8));
                movieData.setVoteCount(cursor.getInt(9));
                movieData.setVideo(cursor.getInt(10) > 0 ? true : false);
                movieData.setVoteAverage(cursor.getDouble(11));

                return movieData;
            }
        }

        return null;
    }
}
