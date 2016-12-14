package com.wimsonevel.moviesapp.model;

import java.util.List;

/**
 * Created by Wim on 12/5/16.
 */
public class BaseModel<T> {

    private int page;
    private List<T> results;

    public BaseModel() {
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
