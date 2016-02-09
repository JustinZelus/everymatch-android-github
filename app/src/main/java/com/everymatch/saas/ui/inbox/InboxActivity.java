package com.everymatch.saas.ui.inbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.everymatch.saas.R;
import com.everymatch.saas.server.Data.DataConversation;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.chat.ChatFragment;
import com.everymatch.saas.ui.chat.ConversationsFragment;

/**
 * Created by PopApp_laptop on 02/02/2016.
 */
public class InboxActivity extends BaseActivity {

    public static final String EXTRA_START_MODE = "extra.start.mode";
    public static final String EXTRA_CONVERSATION = "extra.converation";
    public static final String EXTRA_CONVERSATION_ID = "extra.conversation.id";
    public static final String EXTRA_CHAT_TYPE = "extra.chat.type";
    public static final String START_MODE_INBOX = "inbox";
    public static final String START_MODE_CHAT = "chat";


    public static void startInbox(Context context) {
        Intent starter = new Intent(context, InboxActivity.class);
        starter.putExtra(EXTRA_START_MODE, START_MODE_INBOX);
        context.startActivity(starter);
    }

    public static void startChat(Context context, DataConversation c, String conversationId, String chatType) {
        Intent starter = new Intent(context, InboxActivity.class);
        starter.putExtra(EXTRA_CONVERSATION, c);
        starter.putExtra(EXTRA_CONVERSATION_ID, conversationId);
        starter.putExtra(EXTRA_CHAT_TYPE, chatType);
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
        else {
            DataConversation conversation = (DataConversation) getIntent().getSerializableExtra(EXTRA_CONVERSATION);
            String id = getIntent().getStringExtra(EXTRA_CONVERSATION_ID);
            String chatType = getIntent().getStringExtra(EXTRA_CHAT_TYPE);
            ChatFragment chatFragment = ChatFragment.getInstance(conversation, id, chatType);
            replaceFragment(R.id.fragment_container, chatFragment, "");
        }
    }
}
