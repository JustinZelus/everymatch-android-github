package com.everymatch.saas.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.AdapterChatMessage;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataChatBlock;
import com.everymatch.saas.server.Data.DataChatMessage;
import com.everymatch.saas.server.Data.DataConversation;
import com.everymatch.saas.server.Data.DataEventActions;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestChatMessageSend;
import com.everymatch.saas.server.requests.RequestChatMessages;
import com.everymatch.saas.server.requests.RequestConversations;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseString;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.singeltones.PusherManager;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.ShapeDrawableUtils;
import com.everymatch.saas.view.BaseEditText;
import com.everymatch.saas.view.BaseIconTextView;
import com.everymatch.saas.view.EventHeader;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by PopApp_laptop on 10/08/2015.
 */
public class ChatFragment extends BaseFragment implements EventHeader.OnEventHeader, View.OnClickListener, AdapterChatMessage.ChatCallback {
    private static final String ARG_CONVERSATION = "extra.convertation";
    private static final String EXTRA_USER_ID = "extra.user.id";
    private static final String EXTRA_CHAT_TYPE = "extra.chat.type";
    private static final String EXTRA_EVENT_ACTION = "extra.event.action";

    public static final String CHAT_TYPE_USER = "chat.type.user";
    public static final String CHAT_TYPE_GROUP = "chat.type.group";
    public static final String CHAT_TYPE_UNKNOWN = "chat.type.unknown";
    public static final String TAG = ChatFragment.class.getSimpleName();
    private static final int MESSAGES_LOAD_COUNT = 10;

    /* in order to know if screen is on */
    public static boolean IS_VISIBLE = false;

    //Views
    private EventHeader mHeader;
    ListView lvMessages;
    BaseIconTextView tvSend;
    BaseEditText etMessage;
    View header;

    //Data
    public DataConversation mDataConversation = null;
    private String mConversationId;
    //private static String mOtherUserId;
    private AdapterChatMessage adapter;

    private boolean isClicked = false;
    private String mChatType;
    private boolean flag_loading = false;
    private boolean shouldLoadMore = true;
    private DataEventActions mEventAction;

    public static ChatFragment getInstance(DataConversation c, String conversationId, String chatType) {
        ChatFragment answer = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONVERSATION, c);
        args.putString(EXTRA_USER_ID, conversationId);
        args.putString(EXTRA_CHAT_TYPE, chatType);
        answer.setArguments(args);
        return answer;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataConversation = (DataConversation) getArguments().getSerializable(ARG_CONVERSATION);
        mConversationId = getArguments().getString(EXTRA_USER_ID);
        mChatType = getArguments().getString(EXTRA_CHAT_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHeader(view);
        lvMessages = (ListView) view.findViewById(R.id.listViewChat);
        tvSend = (BaseIconTextView) view.findViewById(R.id.tv_view_chat_send);
        tvSend.setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton());
        etMessage = (BaseEditText) view.findViewById(R.id.et_view_chat_send);
        etMessage.setTextColor(DataStore.getInstance().getIntColor(EMColor.NIGHT));
        etMessage.setHintTextColor(DataStore.getInstance().getIntColor(EMColor.MOON));
        tvSend.setOnClickListener(this);
        header = LayoutInflater.from(getActivity()).inflate(R.layout.view_load_more_messages, null);
        header.setOnClickListener(this);
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvSend.setVisibility(etMessage.getText().toString().trim().length() == 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        loadConversation();
        IS_VISIBLE = true;
    }

    private void setHeader(View view) {
        mHeader = (EventHeader) view.findViewById(R.id.eventHeader);
        mHeader.setListener(this);
        mHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mHeader.getIconOne().setVisibility(View.GONE);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setText(Consts.Icons.icon_Search);
        mHeader.setTitle(dm.getResourceText(R.string.Inbox_title));
        mHeader.getEditTitle().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (adapter != null)
                    adapter.getFilter().filter(s.toString());
            }
        });
    }

    void setAdapter() {
        if (adapter == null) {
            adapter = new AdapterChatMessage(mDataConversation.getMessages(), getActivity(), mDataConversation);
            lvMessages.setAdapter(adapter);
            adapter.setListiner(this);
        }

        adapter.notifyDataSetChanged();

        updateHeaderVisibility();
        /*go to the end of the list*/
        lvMessages.smoothScrollToPosition(adapter.getCount());
    }

    /*decide if to show header or not*/
    private void updateHeaderVisibility() {
        if (adapter.getCount() < 10 || !shouldLoadMore) {
            /*hide header*/
            lvMessages.removeHeaderView(header);
        } else {
            /*we have messages - just show header*/
            if (lvMessages.getHeaderViewsCount() == 0)
                lvMessages.addHeaderView(header);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IS_VISIBLE = false;
    }

    @Override
    public void onBackButtonClicked() {
        getActivity().onBackPressed();
    }

    @Override
    public void onOneIconClicked() {

    }

    @Override
    public void onTwoIconClicked() {

    }

    @Override
    public void onThreeIconClicked() {
        if (!isClicked) {
            mHeader.getTitle().setVisibility(View.GONE);
            mHeader.getEditTitle().setVisibility(View.VISIBLE);
            mHeader.getEditTitle().setFocusable(true);

            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(mHeader.getEditTitle().getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

            isClicked = true;
        } else {
            mHeader.getTitle().setVisibility(View.VISIBLE);
            mHeader.setTitle(dm.getResourceText(R.string.Inbox_title));
            mHeader.getEditTitle().setVisibility(View.GONE);
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

            isClicked = false;
            adapter.cancelSearch();
        }

    }

    public void loadMessages() {
        // we get string response not Json
        flag_loading = true;
        ServerConnector.getInstance().processRequest(new RequestChatMessages(mDataConversation._id, -1/*not really metter*/, (adapter.getCount() * -1) - MESSAGES_LOAD_COUNT), new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                try {
                    JSONObject jsonObject = new JSONObject(((ResponseString) baseResponse).responseStr);
                    DataChatBlock dataChatBlock = new DataChatBlock(jsonObject);
                    adapter.addMessages(dataChatBlock.messages);
                    /* go to start of newest messages */
                    //lvMessages.setSelection(dataChatBlock.messages.size());
                    lvMessages.smoothScrollToPosition(dataChatBlock.messages.size());

                    flag_loading = false;
                    if (dataChatBlock.messages.size() < MESSAGES_LOAD_COUNT) {
                        shouldLoadMore = false;
                    }
                } catch (Exception ex) {
                    EMLog.i("parse Error", ex.getMessage());
                }
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                //Toast.makeText(getActivity(), errorResponse.getServerRawResponse(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view == header) {
            loadMessages();
            return;
        }
        switch (view.getId()) {
            case R.id.tv_view_chat_send:
                String msg = etMessage.getText().toString().trim();
                if (msg.length() == 0) {
                    return;
                }
                sendChatMessage(msg);
                break;
        }

    }

    private void sendChatMessage(String msg) {
        //  we get string response not Json
        //String id = getPrefix() + (mChatType.equals(CHAT_TYPE_USER) ? mConversationId : mDataConversation._id);
        String id = getChannelName();
        if (mDataConversation._id != null) {
            id = mDataConversation._id;
        }
        ServerConnector.getInstance().processRequest(new RequestChatMessageSend(id, msg, mDataConversation._id == null), new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                try {
                    String responseStr = ((ResponseString) baseResponse).responseStr;
                    /** if this was the first message, there was no id to conversation */
                    if (mDataConversation._id == null) {
                        mDataConversation = new Gson().fromJson(responseStr, DataConversation.class);
                        setAdapter();
                        etMessage.setText("");
                        return;
                    }

                    /* just add message to adapter (or wait to pusher...)*/
                    DataChatMessage dataChatMessage = new Gson().fromJson(responseStr, DataChatMessage.class);
                    //adapter.addMessages(dataChatMessage);
                    lvMessages.smoothScrollToPosition(adapter.getCount());
                    etMessage.setText("");
                } catch (Exception ex) {
                    Log.i("parseError", ex.getMessage());
                }
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                //Toast.makeText(getActivity(), errorResponse.getServerRawResponse(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLoadMoreClicked() {
        loadMessages();
    }

    public void loadConversation() {
        ServerConnector.getInstance().processRequest(new RequestConversations(0, 10, mConversationId), new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                try {
                    mDataConversation = (DataConversation) baseResponse;
                    mHeader.setTitle(mDataConversation.getConversation_title());
                    mProgressBar.setVisibility(View.GONE);
                    if (mDataConversation.getMessages().size() > 0) {
                        setAdapter();
                        updateHeaderVisibility();
                    } else {
                        EMLog.d(TAG, "no messages!");
                    }
                } catch (Exception ex) {
                    EMLog.e(TAG, ex.getMessage());
                }

            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void handleBroadcast(Serializable eventObject, String eventName) {
        // super.handleBroadcast(eventObject, eventName);
        if (PusherManager.PUSHER_EVENT_EVENT_NEW_MESSAGE.equals(eventName)) {
            DataChatMessage dataChatMessage = (DataChatMessage) eventObject;
            if (dataChatMessage != null) {
                if (adapter == null)
                    setAdapter();

                adapter.addMessages(dataChatMessage);
                lvMessages.smoothScrollToPosition(adapter.getCount());
                etMessage.setText("");
            }
        }
    }

    public String getChannelName() {
        if (mChatType.equals(CHAT_TYPE_USER))
            return "users_" + mConversationId;

        if (mChatType.equals(CHAT_TYPE_GROUP))
            return "chats_" + mConversationId;
        if (mChatType.equals(CHAT_TYPE_UNKNOWN))
            return "chats_" + mConversationId;

        EMLog.e(getTag(), "prefix not found");
        return "";
    }
}
