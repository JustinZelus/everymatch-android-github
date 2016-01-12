package com.everymatch.saas.ui.sign;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.SectionsPagerAdapter;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.singeltones.Preferences;
import com.everymatch.saas.ui.base.BaseLoginActivity;
import com.everymatch.saas.util.AnimationAdapter;
import com.everymatch.saas.util.ShapeDrawableUtils;
import com.everymatch.saas.view.DisableableViewPager;
import com.viewpagerindicator.CirclePageIndicator;

//import com.everymatch.saas.view.BlurImageView;


public class SignActivity extends BaseLoginActivity implements BaseSignFragment.Callbacks, View.OnClickListener, RecoverPasswordFragment.Callbacks {

    private static final String TAG = SignActivity.class.getSimpleName();

    private Button btnLogin, btnRegister;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private DisableableViewPager mViewPager;
    private Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        showWelcome();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:
                onLoginButtonClick();
                break;

            case R.id.register_button:
                onRegisterButtonClick();
                break;
        }
    }

    private void showWelcome() {
        //bind views
        btnLogin = (Button) findViewById(R.id.login_button);
        btnRegister = (Button) findViewById(R.id.register_button);
        mViewPager = (DisableableViewPager) findViewById(R.id.pager);

        //set Buttons background
        btnRegister.setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton());
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),
                ds.getApplicationData().start.getModel());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.pager_indicator);
        titleIndicator.setViewPager(mViewPager);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    private void onLoginButtonClick() {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_up_animation, 0)
                .replace(R.id.activity_sign_main_layout, LoginFragment.newInstance(), LoginFragment.TAG).commit();
    }

    private void onRegisterButtonClick() {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_up_animation, 0)
                .replace(R.id.activity_sign_main_layout, RegistrationFragment.newInstance(), RegistrationFragment.TAG).commit();
    }

    @Override
    protected void onDestroy() {
        DataManager.getInstance().cancelResourcesFetching();
        super.onDestroy();
    }

    @Override
    protected void onGetUserFinished() {
        performLoginOperation();
    }

    @Override
    public void onFetchApplicationFinished() {
        fetchUser();
    }

    @Override
    protected void onGetApplicationFailure() {
    }
    @Override
    public void onActionClick(boolean isLoginClick) {

        if (isLoginClick) {
            replaceFragment(R.id.activity_sign_main_layout, LoginFragment.newInstance(), LoginFragment.TAG);
        } else {
            replaceFragment(R.id.activity_sign_main_layout, RegistrationFragment.newInstance(), RegistrationFragment.TAG);
        }
    }

    @Override
    public void onRegistrationComplete(String loginEmail) {
        replaceFragment(R.id.activity_sign_main_layout, LoginFragment.newInstance(loginEmail, true, false), LoginFragment.TAG);
    }

    @Override
    public void onBackPressed() {

        mCurrentFragment = findFragment(LoginFragment.TAG);

        if (mCurrentFragment == null){
            mCurrentFragment = findFragment(RegistrationFragment.TAG);
        }

        if (mCurrentFragment != null && mCurrentFragment.isVisible()){

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_down_animation);

            animation.setAnimationListener(new AnimationAdapter() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    getSupportFragmentManager().beginTransaction().remove(mCurrentFragment).commit();
                }
            });

            mCurrentFragment.getView().startAnimation(animation);
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onLoginCompleted() {

        // Remove timestamp in order to fetch new application object
        Preferences.getInstance().setTimestamp(null);

        fetchApplicationData();
    }

    @Override
    public void onForgotPasswordClick() {
        replaceFragment(R.id.activity_sign_main_layout, new RecoverPasswordFragment(),
                RecoverPasswordFragment.TAG, true,RecoverPasswordFragment.TAG);
    }

    @Override
    public void onPasswordSent(String email) {
        replaceFragment(R.id.activity_sign_main_layout, LoginFragment.newInstance(email, false, true), LoginFragment.TAG);
    }
}