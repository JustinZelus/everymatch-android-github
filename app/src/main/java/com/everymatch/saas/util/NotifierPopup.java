package com.everymatch.saas.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.view.PieProgressView;

/**
 * Created by Dor on 26/11/2015.
 */
public class NotifierPopup {

    private static final int ANIMATION_DURATION = 300;

    public static final int TYPE_INFO = 1;
    public static final int TYPE_SUCCESS = 2;
    public static final int TYPE_ERROR = 3;

    private static PopupWindow sCurrentPopup;

    public static class Builder {

        private Handler handler = new Handler();

        private Context mContext;

        private NotifierParams mParams;
        private String TAG = Builder.class.getSimpleName();

        /**
         * Creates a builder for the default notifier
         */
        public Builder(Context context) {
            this.mContext = context;
            mParams = new NotifierParams();
        }

        public Context getContext() {
            return mContext;
        }

        /**
         * Set title
         *
         * @return This Builder
         */
        public Builder setMessage(String message) {
            mParams.mMessage = message;
            return this;
        }

        /**
         * Set title using the resource key.
         *
         * @return This Builder
         */
        public Builder setMessage(int messageId) {
            mParams.mMessage = DataManager.getInstance().getResourceText(messageId);
            return this;
        }

        /**
         * Set duration
         *
         * @return This Builder
         */
        public Builder setDuration(long duration) {
            mParams.mDuration = duration;
            return this;
        }

        /**
         * Set gravity of notifier
         *
         * @return This Builder
         */
        public Builder setGravity(int gravity) {
            mParams.mGravity = gravity;
            return this;
        }

        /**
         * Set offset of y axis
         *
         * @return This Builder
         */
        public Builder setTopOffset(int topOffset) {
            mParams.mTopOffset = topOffset;
            return this;
        }

        /**
         * Set type of notifier
         *
         * @return This Builder
         */
        public Builder setType(int type) {
            mParams.mType = type;
            return this;
        }

        /**
         * Set type of notifier
         *
         * @return This Builder
         */
        public Builder setView(View view) {
            mParams.mParentView = view;
            return this;
        }


        public PopupWindow create() {

            if (sCurrentPopup != null){
                sCurrentPopup.dismiss();
                sCurrentPopup = null;
            }

            final View view = LayoutInflater.from(mContext).inflate(R.layout.view_notifier_popup, null);
            sCurrentPopup = new PopupWindow(view);
            final PieProgressView pieProgressView = (PieProgressView) view.findViewById(R.id.view_notifier_popup_icon);
            TextView textMessage = (TextView) view.findViewById(R.id.view_notifier_popup_text_message);
            sCurrentPopup.setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
            sCurrentPopup.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
            sCurrentPopup.setBackgroundDrawable(new BitmapDrawable());
            /*close in out touch*/
            sCurrentPopup.setOutsideTouchable(true);

            // Set background
            switch (mParams.mType) {
                case TYPE_ERROR:
                    view.setBackgroundColor(DataStore.getInstance().getIntColor(EMColor.NEGATIVE));
                    pieProgressView.setColor(DataStore.getInstance().getIntColor(EMColor.WHITE));
                    textMessage.setTextColor(DataStore.getInstance().getIntColor(EMColor.WHITE));
                    break;

                case TYPE_INFO:
                    view.setBackgroundColor(DataStore.getInstance().getIntColor(EMColor.INFO));
                    break;

                case TYPE_SUCCESS:
                    view.setBackgroundColor(DataStore.getInstance().getIntColor(EMColor.POSITIVE));
                    pieProgressView.setColor(DataStore.getInstance().getIntColor(EMColor.NIGHT));
                    textMessage.setTextColor(DataStore.getInstance().getIntColor(EMColor.NIGHT));
                    break;

            }

            // Set message
            textMessage.setText(Utils.setFirstLetterUpperCase(mParams.mMessage));

            view.post(new Runnable() {
                @Override
                public void run() {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(view, view.TRANSLATION_Y.getName(), -view.getMeasuredHeight(), 0);
                    animator.setDuration(ANIMATION_DURATION).start();
                    pieProgressView.showProgress(100f, 100f, mParams.mDuration);
                }
            });

            sCurrentPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    try {
                        EMLog.i(TAG, "onDismiss");
                        pieProgressView.cancelAnimation();
                        handler.removeCallbacksAndMessages(null);
                    } catch (Exception e) {
                        EMLog.i(TAG, "onDismiss::Exception");
                    }

                    sCurrentPopup = null;
                }
            });

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(view, view.TRANSLATION_Y.getName(), -view.getMeasuredHeight());
                    animator.setDuration(ANIMATION_DURATION).start();
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            try {
                                EMLog.i(TAG, "onAnimationEnd");
                                sCurrentPopup.dismiss();
                            } catch (Exception e) {
                                EMLog.i(TAG, "onAnimationEnd::Exception");
                            }

                            sCurrentPopup = null;
                        }
                    });
                }
            }, mParams.mDuration);

            return sCurrentPopup;
        }

        public PopupWindow show() {
            final PopupWindow window = create();
            window.showAtLocation(mParams.mParentView, mParams.mGravity, 0, mParams.mTopOffset);
            return window;
        }
    }

    private static class NotifierParams {

        private static final long DEFAULT_DURATION = 7000;

        String mMessage;
        long mDuration;
        int mGravity;
        int mType;
        View mParentView;
        int mTopOffset;

        private NotifierParams() {
            this.mDuration = DEFAULT_DURATION;
            this.mGravity = Gravity.TOP;
            this.mType = TYPE_INFO;
        }
    }

}
