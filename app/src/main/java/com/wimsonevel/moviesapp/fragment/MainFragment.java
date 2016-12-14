package com.wimsonevel.moviesapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wimsonevel.moviesapp.AboutActivity;
import com.wimsonevel.moviesapp.DetailActivity;
import com.wimsonevel.moviesapp.FavoritesActivity;
import com.wimsonevel.moviesapp.R;
import com.wimsonevel.moviesapp.adapter.MovieListAdapter;
import com.wimsonevel.moviesapp.listener.TaskListener;
import com.wimsonevel.moviesapp.model.MovieData;
import com.wimsonevel.moviesapp.network.Constant;
import com.wimsonevel.moviesapp.task.FetchMovieTask;
import com.wimsonevel.moviesapp.util.ConnectionUtil;
import com.wimsonevel.moviesapp.util.EndlessRecyclerOnScrollListener;
import com.wimsonevel.moviesapp.util.GridMarginDecoration;

import java.util.List;

/**
 * Created by Wim on 12/5/16.
 */
public class MainFragment extends Fragment implements MovieListAdapter.OnMovieItemSelectedListener, TaskListener<MovieData> {

    private Toolbar toolbar;
    private RecyclerView rvMovies;
    private GridLayoutManager gridLayoutManager;
    private MovieListAdapter movieListAdapter;
    private SwipeRefreshLayout refreshLayout;

    private String sorting = Constant.MOVIE_POPULAR;

    private ActionBar actionBar;
    private int page = 1;
    private int limit = 20;
    private int type = 1;

    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        rvMovies = (RecyclerView) view.findViewById(R.id.rv_movies);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(savedInstanceState == null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

            actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(R.string.app_name);
                actionBar.setSubtitle(R.string.most_popular);
            }

            movieListAdapter = new MovieListAdapter(getContext());
            movieListAdapter.setOnMovieItemSelectedListener(this);

            gridLayoutManager = new GridLayoutManager(getContext(), 2);
            rvMovies.setLayoutManager(gridLayoutManager);

            rvMovies.addItemDecoration(new GridMarginDecoration(getContext(), 1, 1, 1, 1));
            rvMovies.setHasFixedSize(true);
            rvMovies.setAdapter(movieListAdapter);

            addScroll();

            refreshLayout.setColorSchemeResources(R.color.colorPrimary);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshData(type);
                }
            });

            loadData(sorting, page);
        }
    }

    private void addScroll() {
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(gridLayoutManager, page, limit) {
            @Override
            public void onLoadMore(int next) {
                page = next;
                loadData(sorting, page);
            }
        };

        rvMovies.addOnScrollListener(endlessRecyclerOnScrollListener);
    }

    private void removeScroll() {
        rvMovies.removeOnScrollListener(endlessRecyclerOnScrollListener);
    }

    private void loadData(String sort, int page) {
        if(ConnectionUtil.isConnected(getContext())) {
            new FetchMovieTask(this).execute(new String[]{sort, String.valueOf(page)});
        }else{
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    private void refreshData(int type) {
        if(movieListAdapter != null) {
            movieListAdapter.clear();
        }
        page = 1;

        limit = type == 1 ? 20 : 30;

        removeScroll();
        addScroll();
        loadData(sorting, page);
    }

    @Override
    public void onItemClick(View v, int position) {
        DetailActivity.start(getContext(), movieListAdapter.getItem(position));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_most_popular) {
            if(actionBar != null) {
                actionBar.setSubtitle(R.string.most_popular);
            }

            sorting = Constant.MOVIE_POPULAR;
            type = 1;
            refreshData(type);
            return true;
        }else if(id == R.id.action_top_rated) {
            if(actionBar != null) {
                actionBar.setSubtitle(R.string.top_rated);
            }

            sorting = Constant.MOVIE_TOP_RATED;
            type = 2;
            refreshData(type);
            return true;
        }else if(id == R.id.action_favorites){
            FavoritesActivity.start(getContext());
            return true;
        }else if(id == R.id.action_about) {
            AboutActivity.start(getContext());
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void loadDataFinished(List<MovieData> movieDatas) {
        if(movieListAdapter != null) {
            movieListAdapter.addAll(movieDatas);
        }
    }

    @Override
    public void showProgress() {
        if (refreshLayout != null) {
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                }
            });
        }
    }

    @Override
    public void hideProgress() {
        if (refreshLayout != null)
            refreshLayout.setRefreshing(false);
    }

    @Override
    public void failed(String message) {
        if (refreshLayout != null)
            refreshLayout.setRefreshing(false);

        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
