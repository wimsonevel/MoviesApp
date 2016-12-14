package com.wimsonevel.moviesapp.model;

/**
 * Created by Wim on 12/7/16.
 */
public class Review extends BaseModel<ReviewData> {

    private int id;
    private int totalPages;
    private int totalResults;

    public Review() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}
