package com.everymatch.saas.ui.inbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.everymatch.saas.R;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.chat.ConversationsFragment;
import com.everymatch.saas.ui.match.MatchFragment;

/**
 * Created by PopApp_laptop on 02/02/2016.
 */
public class InboxActivity extends BaseActivity {

    public static final String EXTRA_START_MODE = "extra.start.mode";
    public static final String START_MODE_INBOX = "inbox";
    public static final String START_MODE_CHAT = "chat";


    public static void startInbox(Context context) {
        Intent starter = new Intent(context, InboxActivity.class);
        starter.putExtra(EXTRA_START_MODE, START_MODE_INBOX);
        context.startActivity(starter);
    }

    public static void startChat(Context context) {
        Intent starter = new Intent(context, InboxActivity.class);
        starter.putExtra(EXTRA_START_MODE, START_MODE_CHAT);
        context.startActivity(starter);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abstract_activity);
        String mode = getIntent().getStringExtra(EXTRA_START_MODE);
        if (mode.equals(START_MODE_INBOX))
            replaceFragment(R.id.fragment_container, new ConversationsFragment(), "");
        else
            replaceFragment(R.id.fragment_container, new MatchFragment(), "");
    }
}
