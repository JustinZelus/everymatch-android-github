package com.everymatch.saas.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.everymatch.saas.R;
import com.everymatch.saas.server.Data.DataAnswer;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataEventHolder;
import com.everymatch.saas.server.Data.DataPeople;
import com.everymatch.saas.server.Data.DataQuestion;
import com.everymatch.saas.singeltones.EventListener;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.PeopleViewPagerFragment;
import com.everymatch.saas.ui.chat.ChatFragment;
import com.everymatch.saas.ui.discover.PeopleFragment;
import com.everymatch.saas.ui.event.EventActivity;
import com.everymatch.saas.ui.inbox.InboxActivity;
import com.everymatch.saas.ui.me.MeFragment;
import com.everymatch.saas.ui.me.MyProfileFragment;
import com.everymatch.saas.ui.me.settings.SettingsFragment;
import com.everymatch.saas.ui.questionnaire.QuestionnaireActivity;

/**
 * Created by dors on 10/26/15.
 */
public class UserActivity extends BaseActivity implements EventListener, PeopleFragment.Callbacks, MeFragment.MeCallback, MyProfileFragment.MyProfileCallbacks {

    private static final int REQUEST_ANSWER_QUESTION = 123;
    private static final String TAG = UserActivity.class.getSimpleName();
    private static final String EXTRA_USER_ID = "extra.user.id";
    private static final String EXTRA_SHOW_MY_PROFILE_FRAGMENT = "extra.show.my.profile.fragment";
    private static final String EXTRA_SHOW_SETTINGS_FRAGMENT = "extra.show.settings.fragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abstract_activity);
        showNextFragment();
    }

    public static void openOtherUserFragment(Activity activity, DataPeople user) {
        Intent intent = new Intent(activity, UserActivity.class);
        intent.putExtra(EXTRA_USER_ID, user);
        activity.startActivity(intent);
    }

    public static void openMeFragment(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, UserActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void openMyProfileFragment(Activity activity) {
        Intent intent = new Intent(activity, UserActivity.class);
        intent.putExtra(EXTRA_SHOW_MY_PROFILE_FRAGMENT, true);
        activity.startActivity(intent);
    }

    public static void openSettingsFragment(Activity activity) {
        Intent intent = new Intent(activity, UserActivity.class);
        intent.putExtra(EXTRA_SHOW_SETTINGS_FRAGMENT, true);
        activity.startActivity(intent);
    }

    private void showNextFragment() {
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(EXTRA_USER_ID)) {
            replaceFragment(R.id.fragment_container, PeopleFragment.getInstance
                            ((DataPeople) intent.getSerializableExtra(EXTRA_USER_ID)),
                    PeopleViewPagerFragment.TAG, false, null);
        } else {
            if (intent.hasExtra(EXTRA_SHOW_SETTINGS_FRAGMENT)) {
                replaceFragment(R.id.fragment_container, new SettingsFragment(), SettingsFragment.TAG, false, null);
            } else if (intent.hasExtra(EXTRA_SHOW_MY_PROFILE_FRAGMENT)) {
                showMyProfile(false);
            } else {
                replaceFragment(R.id.fragment_container, new MeFragment(), MeFragment.TAG);
            }
        }
    }

    @Override
    public void onEventClick(DataEvent event) {
        event._id = event.entity._id;
        EventActivity.startActivity(this, event);
    }

    @Override
    public void onViewAllEventsClick(DataEventHolder eventHolder) {
    }

    @Override
    public void onChatButtonClick(String userId) {
        //replaceFragment(R.id.fragment_container, ChatFragment.getInstance(null, userId, ChatFragment.CHAT_TYPE_USER), ChatFragment.TAG, true, null);
        InboxActivity.startChat(UserActivity.this, null, userId, ChatFragment.CHAT_TYPE_USER);
    }

    @Override
    public void onProfileClick() {
        showMyProfile(true);
    }

    private void showMyProfile(boolean addToBackStack) {

        if (addToBackStack) {
            replaceFragment(R.id.fragment_container, MyProfileFragment.getInstance(),
                    MyProfileFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right);
        } else {
            replaceFragment(R.id.fragment_container, MyProfileFragment.getInstance(),
                    MyProfileFragment.TAG);
        }
    }

    @Override
    public void onSettingsClick() {
        replaceFragment(R.id.fragment_container, new SettingsFragment(),
                SettingsFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    public void onAddProfileClick() {
        // set the question flag to activity creation
        QuestionnaireActivity.create_mode = QuestionnaireActivity.CREATE_MODE.CREATE_ACTIVITY;
        final Intent intent = new Intent(this, QuestionnaireActivity.class);
        intent.putExtra(QuestionnaireActivity.EXTRA_ACTIVITY_ID, 0);
        //getActivity().finish();
        startActivity(intent);
    }

    @Override
    public void onQuestionClick(DataQuestion dataQuestion, DataAnswer answer, String answerStr) {
        QuestionnaireActivity.answerSingleQuestion(this, dataQuestion, answer, answerStr, REQUEST_ANSWER_QUESTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ANSWER_QUESTION && resultCode == Activity.RESULT_OK) {
            MyProfileFragment fragment = (MyProfileFragment) findFragment(MyProfileFragment.TAG);

            if (fragment != null) {
                fragment.refreshData();
            }
        }
    }
}
