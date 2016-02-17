package com.everymatch.ucpa;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestLoadProviders;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseApplication;
import com.everymatch.saas.server.responses.ResponseLoadProviders;
import com.everymatch.saas.singeltones.Preferences;
import com.everymatch.saas.ui.base.BaseLoginActivity;
import com.everymatch.saas.ui.sign.SignActivity;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.FetchCallback;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class SplashActivity extends BaseLoginActivity {
    public static final String TAG = "SplashActivity";

    private int mTotalApiCallCount = 3;
    private int mCurrentApiCall = 0;

    private static final long MIN_SPLASH_TIME = 3000;
    private long startTime;
    private boolean isCanceled = false;

    //screen dimens
    int screenHeight;
    int screenWidth;

    //views
    private ImageView imgSplashLogo;
    private BaseTextView tvPoweredBy;

    private ImageView[] imageViews;
    private Callback[] callbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startTime = System.currentTimeMillis();
        init();
        startSplashAnimations();
        beginFetchingProcess();
        DataManager.getInstance().fetchEventDefaultImage();
    }

    private void init() {
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;

        imgSplashLogo = (ImageView) findViewById(R.id.imgSplashLogo);
        tvPoweredBy = (BaseTextView) findViewById(R.id.tvSplashPoweredBy);
    }

    private void startSplashAnimations() {
        findViewById(R.id.rlSplashBg).post(new Runnable() {
            @Override
            public void run() {
                 /* background animation */
                ObjectAnimator.ofObject(findViewById(R.id.rlSplashBg), "backgroundColor", new ArgbEvaluator(), Color.WHITE, getResources().getColor(R.color.splashBackgroundColor))
                        .setDuration(600)
                        .start();
            }
        });


        imgSplashLogo.post(new Runnable() {
            @Override
            public void run() {
                 /* bring logo from top */

                final AnimatorSet mAnimatorSet = new AnimatorSet();
                mAnimatorSet.playTogether(
                        ObjectAnimator.ofFloat(imgSplashLogo, "y", -(imgSplashLogo.getMeasuredHeight() + 300), screenHeight / 5 - (imgSplashLogo.getMeasuredHeight() / 2)).setDuration(700),
                        ObjectAnimator.ofFloat(imgSplashLogo, "alpha", 0.0f, 0.7f, 1).setDuration(400),

                        ObjectAnimator.ofFloat(tvPoweredBy, "y", screenHeight + (tvPoweredBy.getMeasuredHeight() + 300), screenHeight - (screenHeight / 5) + (tvPoweredBy.getMeasuredHeight() / 2)).setDuration(700),
                        ObjectAnimator.ofFloat(tvPoweredBy, "alpha", 0.0f, 0.7f, 1).setDuration(400)
                );

                mAnimatorSet.setInterpolator(new DecelerateInterpolator());
                mAnimatorSet.start();
            }
        });
    }

    private void beginFetchingProcess() {
        fetchApplicationData();
        fetchResources();
        fetchProviders();
    }

    private void fetchProviders() {
        ServerConnector.getInstance().processRequest(new RequestLoadProviders(), new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                EMLog.i(TAG, "fetchProviders - onSuccess");
                ResponseLoadProviders responseLoadProviders = (ResponseLoadProviders) baseResponse;
                ds.responseLoadProviders = responseLoadProviders;
                checkIfCanContinue();
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                Log.i(TAG, "RequestLoadProviders onFailure");
            }
        });
    }

    /**
     * Fetch resources so the application could use it
     */
    private void fetchResources() {
        /*if (DataManager.getInstance().hasResources()) {
            EMLog.i(TAG, "fetched Resources ");
            checkIfCanContinue();
            return;
        }*/

        EMLog.i(TAG, "fetchResources - fetching resources");
        DataManager.getInstance().fetchResources(new FetchCallback<Boolean>() {
            @Override
            public void postFetch(Boolean success) {
                if (success) {
                    EMLog.i(TAG, "fetched Resources ");
                    //resources already saved in prefs!
                    checkIfCanContinue();
                } else {
                    // TODO show error dialog
                    EMLog.i(TAG, "fetched Resources failed ");

                }
            }
        });
    }

    private void checkIfCanContinue() {
        mCurrentApiCall++;

        EMLog.d(TAG, "TOTAL API CALLS-> " + mTotalApiCallCount);
        EMLog.d(TAG, "CURRENT API CALLS-> " + mCurrentApiCall);
        if (mCurrentApiCall >= mTotalApiCallCount) {
            // TODO go to DISCOVER/LOGIN/ETC...
            //if we have a token
            if (Preferences.getInstance().isRegistered()) {
                fetchUser();
                return;
            }
            PreGoToNextScreen();
        }
    }

    private void loadCenterAnimation() {
          /* animate logo to the center */
        ObjectAnimator animateToCenter = new ObjectAnimator()
                .ofFloat(imgSplashLogo, "y", screenHeight / 2 - (imgSplashLogo.getMeasuredHeight() / 2))
                .setDuration(1200);
        animateToCenter.setInterpolator(new DecelerateInterpolator());
        animateToCenter.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // PreGoToNextScreen();
                goToNextScreen();
            }
        });
        animateToCenter.start();

        ObjectAnimator.ofFloat(findViewById(R.id.activity_splash_view_loader), View.ALPHA.getName(), 0).setDuration(500).start();
    }

    private void PreGoToNextScreen() {
        long timePassed = System.currentTimeMillis() - startTime;
        long dif = MIN_SPLASH_TIME - timePassed;
        if (dif > 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadCenterAnimation();
                }
            }, dif);
        } else {
            loadCenterAnimation();
        }
    }

    private void goToNextScreen() {
        if (isCanceled) return;
        //go to login screen
        Intent intent = new Intent(SplashActivity.this, SignActivity.class);
        SplashActivity.this.finish();
        SplashActivity.this.startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        // Nullify
        imageViews = null;
        callbacks = null;
    }

    @Override
    protected void onGetUserFinished() {
        EMLog.i(TAG, "onGetUserFinished");

        /*if (!Utils.isEmpty(ds.getUser().user_settings.default_culture) &&
                (!ds.getUser().user_settings.default_culture.equals(ds.getCulture()))) {

            //user has changed culture from another device
            String userCulture = ds.getUser().user_settings.default_culture;
            Preferences.getInstance().setLanguage(userCulture);
        }*/

        performLoginOperation();
    }

    @Override
    public void onFetchApplicationFinished() {
        EMLog.i(TAG, "onFetchApplicationFinished");
        if (Preferences.getInstance().getTokenType() == null) {
           /* here we gonna load walkThrough images so we don't get blank screen on next activity */
            loadWalkTroughImages();
            //save the culture we've got from server
            String deviceCulture = ds.getCulture();
            boolean saasContainsLan = false;
            for (ResponseApplication.DataCulture culture : ds.getApplicationData().getCultures()) {
                if (culture.culture_name.equals(deviceCulture)) {
                    // saas supports device culture
                    Preferences.getInstance().setLanguage(culture.culture_name);
                    saasContainsLan = true;
                }
            }
            if (!saasContainsLan) {
                String saasDefCulture = ds.getApplicationData().getSettings().default_culture.culture_name;
                Preferences.getInstance().setLanguage(saasDefCulture);
            }
        }
        checkIfCanContinue();
    }

    private void loadWalkTroughImages() {
        int size = ds.getApplicationData().getStart().getModel().size();
        /*for the logo*/
        size++;
        EMLog.d(TAG, "" + size + "Images to load");

        imageViews = new ImageView[size];
        callbacks = new Callback[size];

        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        mTotalApiCallCount += size;
        for (int i = 0; i < size; i++) {

            String url;
            if (i == size - 1) {
                /*this is the logo imageview*/
                url = ds.getApplicationData().getStart().getModel().get(0).icon_image_url + "?&width=" + Utils.dpToPx(120) + "&height=" + Utils.dpToPx(120) + "&mode=max";
            } else {
                url = ds.getApplicationData().getStart().getModel().get(i).background_image + "?width=" + screenWidth + "&height=" + screenHeight + "&mode=max";

            }
            ImageView imageView = new ImageView(SplashActivity.this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(screenWidth, screenHeight));
            imageViews[i] = imageView;

            final int finalI = i;
            Callback callback = new Callback() {
                @Override
                public void onSuccess() {
                    EMLog.d(TAG, "image " + finalI + " was loaded");
                    checkIfCanContinue();
                }

                @Override
                public void onError() {
                    EMLog.d(TAG, "image was not lodaed");
                }
            };

            callbacks[i] = callback;

            Picasso.with(SplashActivity.this).load(url).into(imageView, callback);
        }
    }

    @Override
    public void onBackPressed() {
        isCanceled = true;
        super.onBackPressed();
    }

    /*we override this method in order to check if MIN_SPLASH_TIME HAS PASSED*/
    @Override
    protected void goToDiscover(final String last_activity_id) {
        if (isCanceled)
            return;

        long timePassed = System.currentTimeMillis() - startTime;
        long dif = MIN_SPLASH_TIME - timePassed;
        if (dif > 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    /*next time we call this function we will get into the 'else' below */
                    goToDiscover(last_activity_id);
                }
            }, dif);
        } else {
            super.goToDiscover(last_activity_id);
        }
    }

    @Override
    protected void onGetApplicationFailure() {
        EMLog.i(TAG, "onGetApplicationFailure");
        mCurrentApiCall--;
        Preferences.getInstance().setAccessToken(null);
        fetchApplicationData();
    }
}
