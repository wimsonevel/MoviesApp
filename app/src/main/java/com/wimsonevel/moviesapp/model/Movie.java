package com.wimsonevel.moviesapp.model;

/**
 * Created by Wim on 12/5/16.
 */
public class Movie extends BaseModel<MovieData> {

    private int totalResult;
    private int totalPages;

    public Movie() {
    }

    public int getTotalResult() {
        return totalResult;
    }

    public void setTotalResult(int totalResult) {
        this.totalResult = totalResult;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
