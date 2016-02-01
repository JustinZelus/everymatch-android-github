package com.everymatch.saas.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.everymatch.saas.util.EMLog;

/**
 * Created by dors on 12/2/15.
 */
public class BaseRecyclerView extends RecyclerView {
     public final String TAG = getClass().getName();

    public BaseRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean fling(int velocityX, int velocityY) {
        if(getAdapter().getItemCount()<=1){
            EMLog.d(TAG,"has on event in recycler");
            return super.fling(velocityX, velocityY);
        }

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();


        int lastVisibleView = linearLayoutManager.findLastVisibleItemPosition();
        int firstVisibleView = linearLayoutManager.findFirstVisibleItemPosition();
        View firstView = linearLayoutManager.findViewByPosition(firstVisibleView);
        View lastView = linearLayoutManager.findViewByPosition(lastVisibleView);
        int leftMargin = (getWidth() - lastView.getWidth()) / 2;
        int rightMargin = (getWidth() - firstView.getWidth()) / 2 + firstView.getWidth();
        int leftEdge = lastView.getLeft();
        int rightEdge = firstView.getRight();
        int scrollDistanceLeft = leftEdge - leftMargin;
        int scrollDistanceRight = rightMargin - rightEdge;

        if (velocityX > 0) {
            smoothScrollBy(scrollDistanceLeft, 0);
        } else {
            smoothScrollBy(-scrollDistanceRight, 0);
        }

        return true;
    }
}
