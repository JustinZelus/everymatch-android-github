package com.everymatch.saas.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.everymatch.saas.R;

/**
 * View for showing when there is connectivity error
 */
public class NoConnectionView extends LinearLayout {

    private Callbacks mListener;

    public NoConnectionView(Context context) {
        this(context, null);
    }

    public NoConnectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_no_connection, this, true);
        initComponents();
    }

    private void initComponents() {
        setBackgroundColor(getResources().getColor(R.color.background));
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setClickable(true);

        findViewById(R.id.view_no_connection_button_try_again).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onTryAgainClick();
                }
            }
        });
    }

    public void setListener(Callbacks callbacks) {
        mListener = callbacks;
    }

    public interface Callbacks {
        void onTryAgainClick();
    }
}
