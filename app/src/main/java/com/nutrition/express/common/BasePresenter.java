package com.nutrition.express.common;

/**
 * Created by huang on 7/13/16.
 */

public interface BasePresenter<T> {
    void onAttach(T t);
    void onDetach();
}
