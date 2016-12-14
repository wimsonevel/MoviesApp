package com.wimsonevel.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.wimsonevel.moviesapp.fragment.DetailFragment;
import com.wimsonevel.moviesapp.model.MovieData;

/**
 * Created by Wim on 12/7/16.
 */
public class DetailActivity extends AppCompatActivity {

    public static void start(Context context, MovieData movieData) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(DetailActivity.class.getSimpleName(), movieData);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        MovieData movieData = getIntent().getParcelableExtra(DetailActivity.class.getSimpleName());

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, DetailFragment.newInstance(movieData))
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
