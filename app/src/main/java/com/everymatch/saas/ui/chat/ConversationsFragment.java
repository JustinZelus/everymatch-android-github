package com.everymatch.saas.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.AdapterConversations;
import com.everymatch.saas.adapter.EmBaseAdapter;
import com.everymatch.saas.server.Data.DataConversation;
import com.everymatch.saas.server.request_manager.ConversationManager;
import com.everymatch.saas.server.responses.ResponseConversations;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.singeltones.GenericCallback;
import com.everymatch.saas.singeltones.PusherManager;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.base.BaseListFragment;
import com.everymatch.saas.ui.dialog.menus.MenuConversations;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.EmptyViewFactory;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.EventHeader;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationsFragment extends BaseListFragment implements EventHeader.OnEventHeader, AdapterConversations.inboxCallback {
    public static final String TAG = ConversationsFragment.class.getSimpleName();
    private boolean isClicked = false;

    public static final String CONVERSATION_TYPE_ACTIVE = dm.getResourceText(R.string.Inbox_title);
    public static final String CONVERSATION_TYPE_ARCHIVE = dm.getResourceText(R.string.Archive);

    //Data
    private String mCurrentConversationType = CONVERSATION_TYPE_ACTIVE;
    private ArrayList<DataConversation> chat = new ArrayList<>();
    private ArrayList<DataConversation> archive = new ArrayList<>();

    //Views
    AdapterConversations adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View emptyView = EmptyViewFactory.createEmptyView(EmptyViewFactory.TYPE_MESSAGES);
        ((ViewGroup) mAbsListView.getParent()).addView(emptyView);
        mAbsListView.setEmptyView(emptyView);
        //mAbsListView.setAdapter(adapter);
        mAbsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container, ChatFragment.getInstance(adapter.getItem(i), adapter.getItem(i)._id, ChatFragment.CHAT_TYPE_USER),
                        ConversationsFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right);
            }
        });

        mAbsListView.setPadding(0, 0, 0, 0);
        mAbsListView.setDividerHeight(0);
    }

    @Override
    protected void initAdapter() {
        /*we have to load conversations eny time  ):  */
        adapter = new AdapterConversations(new ArrayList<DataConversation>(), getActivity(), this);
        mAbsListView.setAdapter(adapter);
        chat.clear();
        archive.clear();
        fetchNextPage();
    }

    public void updateData() {
        adapter.cancelSearch();
        adapter.refreshData(mCurrentConversationType.equals(CONVERSATION_TYPE_ACTIVE) ? chat : archive);
    }

    @Override
    protected void setHeader() {
        //super.setHeader();
        mEventHeader.setListener(this);
        mEventHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mEventHeader.getIconOne().setVisibility(View.GONE);
        mEventHeader.getIconTwo().setVisibility(View.GONE);
        mEventHeader.getIconThree().setText(Consts.Icons.icon_Search);
        updateHeaderTitle();

        mEventHeader.getTitle().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open menu
                ArrayList<String> titles = new ArrayList<>();
                titles.add(CONVERSATION_TYPE_ACTIVE);
                titles.add(CONVERSATION_TYPE_ARCHIVE);
                MenuConversations menuConversations = MenuConversations.getInstance(titles);
                menuConversations.setTargetFragment(ConversationsFragment.this, 0);
                menuConversations.show(getActivity().getSupportFragmentManager(), "");
            }
        });

        mEventHeader.getEditTitle().addTextChangedListener(new TextWatcher() {
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

    public void handleConversationFilterChange(String title) {
        mCurrentConversationType = title;
        updateData();
        updateHeaderTitle();
    }

    private void updateHeaderTitle() {
        mEventHeader.setArrowDownVisibility(true);
        mEventHeader.getTvArrowDown().setPadding(Utils.dpToPx(10), Utils.dpToPx(3), Utils.dpToPx(3), Utils.dpToPx(3));
        mEventHeader.getTvArrowDown().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEventHeader.getTitle().performClick();
            }
        });
        mEventHeader.setTitle(mCurrentConversationType);
    }

    @Override
    protected void fetchNextPage() {
        ConversationManager.getAllConversations(adapter.getCount(), PAGE_COUNT, new GenericCallback() {
            @Override
            public void onDone(boolean success, Object data) {
                if (!success)
                    return;

                ResponseConversations responseMessage = (ResponseConversations) data;
                for (DataConversation conversation : responseMessage.array) {
                    if (conversation.status.equals("active"))
                        chat.add(conversation);
                    else
                        archive.add(conversation);
                }

                updateData();
                if (responseMessage.array.length < PAGE_COUNT)
                    mIsNoMoreResults = true;
            }
        });
    }

    @Override
    protected boolean shouldFetchMoreData() {
        return super.shouldFetchMoreData();
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
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
            mEventHeader.getTitle().setVisibility(View.GONE);
            mEventHeader.getEditTitle().setVisibility(View.VISIBLE);
            mEventHeader.getEditTitle().setFocusable(true);

            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(mEventHeader.getEditTitle().getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

            isClicked = true;
        } else {
            mEventHeader.getTitle().setVisibility(View.VISIBLE);
            mEventHeader.setTitle(dm.getResourceText(R.string.Inbox_title));
            mEventHeader.getEditTitle().setVisibility(View.GONE);
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            isClicked = false;
            adapter.cancelSearch();
        }
    }

    @Override
    public EmBaseAdapter getAdapter() {
        return null;
    }

    @Override
    public void onReplyClick(DataConversation dataConversation) {
        ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container, ChatFragment.getInstance(dataConversation, dataConversation._id, ChatFragment.CHAT_TYPE_USER),
                ConversationsFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    public void onArchiveClick(DataConversation dataConversation) {
        try {
            dataConversation.status = "archive";
            archive.add(chat.remove(chat.indexOf(dataConversation)));
            updateData();

            ConversationManager.setConversationStatus(dataConversation._id, "archive", new GenericCallback() {
                @Override
                public void onDone(boolean success, Object data) {

                }
            });
        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void onDeleteClick(DataConversation dataConversation) {
        try {
            boolean b = chat.remove(dataConversation);
            b = archive.remove(dataConversation);
        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }
        ConversationManager.setConversationStatus(dataConversation._id, "deleted", new GenericCallback() {
            @Override
            public void onDone(boolean success, Object data) {
            }
        });
        updateData();
    }

    @Override
    public void onUnArchiveClick(DataConversation dataConversation) {
        dataConversation.status = "active";
        chat.add(archive.remove(archive.indexOf(dataConversation)));
        updateData();
        ConversationManager.setConversationStatus(dataConversation._id, "active", new GenericCallback() {
            @Override
            public void onDone(boolean success, Object data) {
            }
        });
    }

    @Override
    protected void handleBroadcast(Serializable eventObject, String eventName) {
        if (PusherManager.PUSHER_EVENT_EVENT_NEW_MESSAGE.equals(eventName)) {
            initAdapter();
        }
    }
}
