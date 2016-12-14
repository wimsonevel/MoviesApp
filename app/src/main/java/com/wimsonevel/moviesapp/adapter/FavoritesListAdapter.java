package com.wimsonevel.moviesapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wimsonevel.moviesapp.R;
import com.wimsonevel.moviesapp.model.MovieData;
import com.wimsonevel.moviesapp.network.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wim on 12/8/16.
 */
public class FavoritesListAdapter extends RecyclerView.Adapter<FavoritesListAdapter.MovieViewHolder>{

    private List<MovieData> movieDatas;
    private Context context;

    private OnMovieItemSelectedListener onMovieItemSelectedListener;
    private OnFavoritesDeletedListener onFavoritesDeletedListener;

    public FavoritesListAdapter(Context context) {
        this.context = context;
        movieDatas = new ArrayList<>();
    }

    private void add(MovieData item) {
        movieDatas.add(item);
        notifyItemInserted(movieDatas.size() - 1);
    }

    public void addAll(List<MovieData> movieDatas) {
        for (MovieData movieData : movieDatas) {
            add(movieData);
        }
    }

    public void remove(MovieData item) {
        int position = movieDatas.indexOf(item);
        if (position > -1) {
            movieDatas.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public MovieData getItem(int position) {
        return movieDatas.get(position);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_favorites, parent, false);
        final MovieViewHolder movieViewHolder = new MovieViewHolder(view);
        movieViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPos = movieViewHolder.getAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION) {
                    if (onMovieItemSelectedListener != null) {
                        onMovieItemSelectedListener.onItemClick(movieViewHolder.itemView, adapterPos);
                    }
                }
            }
        });

        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        final MovieData movieData = movieDatas.get(position);
        holder.bind(movieData);
    }

    @Override
    public int getItemCount() {
        return movieDatas.size();
    }

    public void setOnMovieItemSelectedListener(OnMovieItemSelectedListener onMovieItemSelectedListener) {
        this.onMovieItemSelectedListener = onMovieItemSelectedListener;
    }

    public void setOnFavoritesDeletedListener(OnFavoritesDeletedListener onFavoritesDeletedListener) {
        this.onFavoritesDeletedListener = onFavoritesDeletedListener;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title;
        TextView date;
        TextView delete;

        public MovieViewHolder(View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.poster);
            title = (TextView) itemView.findViewById(R.id.title);
            date = (TextView) itemView.findViewById(R.id.date);
            delete = (TextView) itemView.findViewById(R.id.delete);
        }

        public void bind(MovieData movieData) {
            Picasso.with(context)
                    .load(Constant.IMG_URL + movieData.getPosterPath())
                    .into(img);

            title.setText(movieData.getOriginalTitle());
            date.setText(movieData.getReleaseDate());

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onFavoritesDeletedListener.onItem(getAdapterPosition());
                }
            });
        }
    }

    public interface OnMovieItemSelectedListener {
        void onItemClick(View v, int position);
    }

    public interface OnFavoritesDeletedListener {
        void onItem(int position);
    }

}
