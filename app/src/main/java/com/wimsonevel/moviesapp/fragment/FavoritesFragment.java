package com.wimsonevel.moviesapp.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wimsonevel.moviesapp.DetailActivity;
import com.wimsonevel.moviesapp.R;
import com.wimsonevel.moviesapp.adapter.FavoritesListAdapter;
import com.wimsonevel.moviesapp.db.SQLiteDB;
import com.wimsonevel.moviesapp.model.MovieData;
import com.wimsonevel.moviesapp.util.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wim on 12/8/16.
 */
public class FavoritesFragment extends Fragment implements FavoritesListAdapter.OnMovieItemSelectedListener,
        FavoritesListAdapter.OnFavoritesDeletedListener{

    private Toolbar toolbar;
    private RecyclerView rvFavorites;
    private FavoritesListAdapter favoritesListAdapter;
    private LinearLayoutManager linearLayoutManager;

    private ActionBar actionBar;

    private SQLiteDB sqLiteDB;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        rvFavorites = (RecyclerView) view.findViewById(R.id.rv_favorites);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.favorites);
        }

        favoritesListAdapter = new FavoritesListAdapter(getContext());
        favoritesListAdapter.setOnMovieItemSelectedListener(this);
        favoritesListAdapter.setOnFavoritesDeletedListener(this);

        linearLayoutManager = new LinearLayoutManager(getContext());
        rvFavorites.setLayoutManager(linearLayoutManager);
        rvFavorites.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        rvFavorites.setHasFixedSize(true);
        rvFavorites.setAdapter(favoritesListAdapter);

        loadData();
    }

    private void loadData() {
        sqLiteDB = new SQLiteDB(getContext());

        List<MovieData> movieDatas = new ArrayList<>();
        Cursor cursor = sqLiteDB.retrieve();
        MovieData movieData;

        if (cursor.moveToFirst()) {
            do {
                movieData = new MovieData();
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

                movieDatas.add(movieData);
            }while (cursor.moveToNext());
        }

        favoritesListAdapter.clear();
        favoritesListAdapter.addAll(movieDatas);
    }

    @Override
    public void onItemClick(View v, int position) {
        DetailActivity.start(getContext(), favoritesListAdapter.getItem(position));
    }

    @Override
    public void onItem(int position) {
        if(sqLiteDB != null) {
            sqLiteDB.delete(favoritesListAdapter.getItem(position).getId());
            favoritesListAdapter.remove(favoritesListAdapter.getItem(position));
            Toast.makeText(getContext(), "Deleted from favorites!", Toast.LENGTH_SHORT).show();

        }
    }
}
