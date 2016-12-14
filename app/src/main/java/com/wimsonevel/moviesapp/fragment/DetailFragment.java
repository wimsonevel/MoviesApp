package com.wimsonevel.moviesapp.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wimsonevel.moviesapp.R;
import com.wimsonevel.moviesapp.db.SQLiteDB;
import com.wimsonevel.moviesapp.listener.DetailTaskListener;
import com.wimsonevel.moviesapp.listener.TaskListener;
import com.wimsonevel.moviesapp.model.Genre;
import com.wimsonevel.moviesapp.model.MovieData;
import com.wimsonevel.moviesapp.model.MovieDetail;
import com.wimsonevel.moviesapp.model.ReviewData;
import com.wimsonevel.moviesapp.model.TrailerData;
import com.wimsonevel.moviesapp.network.Constant;
import com.wimsonevel.moviesapp.task.FetchMovieDetailTask;
import com.wimsonevel.moviesapp.task.FetchReviewTask;
import com.wimsonevel.moviesapp.task.FetchTrailerTask;
import com.wimsonevel.moviesapp.util.ConnectionUtil;

import java.util.List;

/**
 * Created by Wim on 12/7/16.
 */
public class DetailFragment extends Fragment {

    private Toolbar toolbar;
    private ImageView imgPoster;
    private TextView tvMovieTitle;
    private TextView tvMovieDate;
    private TextView tvMovieDuration;
    private TextView tvMovieGenre;
    private TextView tvMovieHomepage;
    private TextView tvMovieOverview;
    private LinearLayout viewTrailers;
    private LinearLayout viewReviews;

    private ProgressBar pgTrailers;
    private ProgressBar pgReviews;

    private SQLiteDB sqLiteDB;
    private MovieData movieData;

    public static DetailFragment newInstance(MovieData movieData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(DetailFragment.class.getSimpleName(), movieData);
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        imgPoster = (ImageView) view.findViewById(R.id.img_poster);
        tvMovieTitle = (TextView) view.findViewById(R.id.movie_title);
        tvMovieDate = (TextView) view.findViewById(R.id.movie_date);
        tvMovieDuration = (TextView) view.findViewById(R.id.movie_duration);
        tvMovieGenre = (TextView) view.findViewById(R.id.movie_genre);
        tvMovieHomepage = (TextView) view.findViewById(R.id.movie_homepage);
        tvMovieOverview = (TextView) view.findViewById(R.id.movie_overview);
        viewTrailers = (LinearLayout) view.findViewById(R.id.view_trailers);
        viewReviews = (LinearLayout) view.findViewById(R.id.view_reviews);

        pgTrailers = (ProgressBar) view.findViewById(R.id.pg_trailers);
        pgReviews = (ProgressBar) view.findViewById(R.id.pg_reviews);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.app_name);
            actionBar.setSubtitle("Movie Detail");
        }

        movieData = getArguments().getParcelable(DetailFragment.class.getSimpleName());

        sqLiteDB = new SQLiteDB(getContext());


        if(ConnectionUtil.isConnected(getContext())) {
            loadMovieDetail(movieData.getId());
            loadTrailer(movieData.getId());
            loadReviews(movieData.getId());
        }else{
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    private void loadMovieDetail(int id) {
        new FetchMovieDetailTask(new DetailTaskListener<MovieDetail>() {
            @Override
            public void loadDataFinished(MovieDetail data) {
                Picasso.with(getContext())
                        .load(Constant.IMG_URL + data.getPosterPath())
                        .into(imgPoster);

                tvMovieTitle.setText(data.getOriginalTitle());
                tvMovieDate.setText(data.getReleaseDate());
                tvMovieDuration.setText(data.getRuntime() + " Minutes");

                for (int i = 0; i < data.getGenres().size(); i++) {
                    Genre genre = data.getGenres().get(i);

                    if(i < data.getGenres().size() - 1) {
                        tvMovieGenre.append(genre.getName() + ",");
                    }else{
                        tvMovieGenre.append(genre.getName());
                    }
                }

                tvMovieHomepage.setText(data.getHomepage());
                tvMovieOverview.setText(data.getOverview());
            }

            @Override
            public void showProgress() {

            }

            @Override
            public void hideProgress() {

            }

            @Override
            public void failed(String message) {

            }
        }).execute(id);
    }

    private void loadTrailer(int id) {
        new FetchTrailerTask(new TaskListener<TrailerData>() {
            @Override
            public void loadDataFinished(List<TrailerData> trailerDatas) {
                showTrailers(trailerDatas);
            }

            @Override
            public void showProgress() {
                pgTrailers.setVisibility(View.VISIBLE);
            }

            @Override
            public void hideProgress() {
                pgTrailers.setVisibility(View.GONE);
            }

            @Override
            public void failed(String message) {

            }
        }).execute(id);
    }

    private void loadReviews(int id) {
        new FetchReviewTask(new TaskListener<ReviewData>() {
            @Override
            public void loadDataFinished(List<ReviewData> reviewDatas) {
                showReviews(reviewDatas);
            }

            @Override
            public void showProgress() {
                pgReviews.setVisibility(View.VISIBLE);
            }

            @Override
            public void hideProgress() {
                pgReviews.setVisibility(View.GONE);
            }

            @Override
            public void failed(String message) {

            }
        }).execute(id);
    }

    private void showTrailers(List<TrailerData> trailerDatas) {
        viewTrailers.removeAllViews();

        for (int i = 0; i < trailerDatas.size(); i++) {

            final TrailerData trailerData = trailerDatas.get(i);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_trailer, viewTrailers, false);

            ImageView trailerThumb = (ImageView) view.findViewById(R.id.trailer_thumb);
            TextView trailerName = (TextView) view.findViewById(R.id.trailer_name);

            if(trailerData.getSite().equalsIgnoreCase("youtube")) {
                Picasso.with(getContext())
                        .load("http://img.youtube.com/vi/" + trailerData.getKey() + "/default.jpg")
                        .into(trailerThumb);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    watchYoutubeVideo(trailerData.getKey());
                }
            });


            trailerName.setText(trailerData.getName());
            viewTrailers.addView(view);
        }
    }

    public void watchYoutubeVideo(String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    private void showReviews(List<ReviewData> reviewDatas) {
        viewReviews.removeAllViews();

        for (int i = 0; i < reviewDatas.size(); i++) {

            ReviewData reviewData = reviewDatas.get(i);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_review, viewReviews, false);

            TextView reviewers = (TextView) view.findViewById(R.id.reviewers);
            TextView content = (TextView) view.findViewById(R.id.content);

            reviewers.setText(reviewData.getAuthor());
            content.setText(reviewData.getContent().length() > 100 ?
                    reviewData.getContent().substring(0, 100) + "..." : reviewData.getContent());

            viewReviews.addView(view);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detail, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem menuFav = menu.findItem(R.id.action_add_favorite);

        if(movieData != null) {
            if (sqLiteDB.getMovieData(movieData.getId()) != null) {
                menuFav.setVisible(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_add_favorite) {
            if(movieData != null) {
                sqLiteDB.create(movieData);
                Toast.makeText(getContext(), "Added to your favorites!", Toast.LENGTH_SHORT).show();
                item.setVisible(false);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);

    }
}