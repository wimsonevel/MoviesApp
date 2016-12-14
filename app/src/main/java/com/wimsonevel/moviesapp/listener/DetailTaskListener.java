package com.wimsonevel.moviesapp.listener;

/**
 * Created by Wim on 12/7/16.
 */
public interface DetailTaskListener<T> {

    void loadDataFinished(T data);
    void showProgress();
    void hideProgress();
    void failed(String message);

}
