package com.wimsonevel.moviesapp.listener;

import java.util.List;

/**
 * Created by Wim on 12/7/16.
 */
public interface TaskListener<T> {

    void loadDataFinished(List<T> lists);
    void showProgress();
    void hideProgress();
    void failed(String message);

}