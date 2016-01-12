package com.everymatch.saas.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.everymatch.saas.util.Utils;

import java.util.ArrayList;

/**
 * Created by Dor on 19/11/2015.
 */
public abstract class EmBaseAdapter<T> extends BaseAdapter implements Filterable {

    private static final int ANIMATION_START_OFFSET = Utils.dpToPx(30);
    private static final int TRANSLATE_ANIMATION_DURATION = 500;

    protected Context mContext;
    protected int mAnimatedPosition = -1;
    protected ArrayList<T> mFilteredResults;
    protected ArrayList<T> mData;
    protected Filter mFilter;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = getFinalView(position, convertView, parent);

        if (view != null && mAnimatedPosition == position) {
            mAnimatedPosition = -1;
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y.getName(), ANIMATION_START_OFFSET, 0);
            objectAnimator.setDuration(TRANSLATE_ANIMATION_DURATION);
            objectAnimator.setInterpolator(new DecelerateInterpolator());
            objectAnimator.start();
        }

        return view;
    }

    @Override
    public T getItem(int position) {
        if (mFilteredResults != null) {
            return mFilteredResults.get(position);
        } else {
            return mData.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected abstract View getFinalView(int position, View convertView, ViewGroup parent);

    public void setAnimatedPosition(int position) {
        mAnimatedPosition = position;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<T> filteredList = new ArrayList<>();

            for (int i = 0; i < mData.size(); i++) {
                T t = mData.get(i);

                if (filterObject(t, filterString)) {
                    filteredList.add(t);
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredResults = (ArrayList<T>) results.values;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        if (mFilteredResults != null) {
            return mFilteredResults.size();
        }

        return mData.size();
    }

    /**
     * Examine a specific object of type T and decides if it passed the filtering
     */
    public boolean filterObject(T t, String constraint) {
        return false;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ItemFilter();
        }

        return mFilter;
    }

    public void cancelSearch() {
        mFilter = null;
        mFilteredResults = null;
        notifyDataSetChanged();
    }

    public void refreshData(ArrayList<T> data) {
        mData = data;
        notifyDataSetChanged();
    }
}
