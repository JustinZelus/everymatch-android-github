package com.everymatch.saas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;

/**
 * Created by dors on 10/26/15.
 */
public class BaseListView extends ListView {

    private ListViewObserver mObserver;
    private View mTrackedChild;
    private int mTrackedChildPrevPosition;
    private int mTrackedChildPrevTop;

    public BaseListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(attrs);
    }

    private void initAttributes(AttributeSet attributeSet) {

        if (attributeSet == null) {
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.ResourceColor);

        if (typedArray != null) {
            int bgColor = typedArray.getInt(R.styleable.ResourceColor_bg_color, -1);

            if (bgColor != -1) {
                setBackgroundColor(Color.parseColor(DataStore.getInstance().getColor(bgColor)));
            }

            int dividerColor = typedArray.getInt(R.styleable.ResourceColor_divider_color, -1);

            if (dividerColor != -1) {
                String hexColor = DataStore.getInstance().getColor(dividerColor);
                setDivider(new ColorDrawable(Color.parseColor(hexColor)));
            }

            typedArray.recycle();
        }

        typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.BaseListView);

        if (typedArray != null) {
            float height = typedArray.getDimension(R.styleable.BaseListView_divider_height, 0);

            if (height != 0) {
                setDividerHeight((int) height);
            }

            typedArray.recycle();
        }
    }

    public interface ListViewObserver {
        void onScroll(float deltaY);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mTrackedChild == null) {
            if (getChildCount() > 0) {
                mTrackedChild = getChildInTheMiddle();
                mTrackedChildPrevTop = mTrackedChild.getTop();
                mTrackedChildPrevPosition = getPositionForView(mTrackedChild);
            }
        } else {
            boolean childIsSafeToTrack = mTrackedChild.getParent() == this && getPositionForView(mTrackedChild) == mTrackedChildPrevPosition;
            if (childIsSafeToTrack) {
                int top = mTrackedChild.getTop();
                if (mObserver != null) {
                    float deltaY = top - mTrackedChildPrevTop;
                    mObserver.onScroll(deltaY);
                }
                mTrackedChildPrevTop = top;
            } else {
                mTrackedChild = null;
            }
        }
    }

    private View getChildInTheMiddle() {
        return getChildAt(getChildCount() / 2);
    }

    public void setObserver(ListViewObserver observer) {
        mObserver = observer;
    }
}
