package com.everymatch.saas.util;

/**
 * Created by dors on 8/2/15.
 */
public interface FetchCallback<T> {
    void postFetch(T t);
}