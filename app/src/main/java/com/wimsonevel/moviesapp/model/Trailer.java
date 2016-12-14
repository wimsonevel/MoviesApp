package com.wimsonevel.moviesapp.model;

/**
 * Created by Wim on 12/7/16.
 */
public class Trailer extends BaseModel<TrailerData> {

    private int id;

    public Trailer() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
