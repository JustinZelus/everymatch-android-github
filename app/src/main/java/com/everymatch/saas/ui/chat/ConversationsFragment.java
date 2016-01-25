package com.everymatch.saas.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.base.BaseListFragment;
import com.everymatch.saas.ui.dialog.menus.MenuConversations;
import com.everymatch.saas.util.EmptyViewFactory;
import com.everymatch.saas.view.EventHeader;

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

    //Views
    AdapterConversations adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getConversations();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View emptyView = EmptyViewFactory.createEmptyView(EmptyViewFactory.TYPE_MESSAGES);
        ((ViewGroup) mAbsListView.getParent()).addView(emptyView);
        mAbsListView.setEmptyView(emptyView);
        mTopContainer.setVisibility(View.VISIBLE);
        //mAbsListView.setAdapter(adapter);
        mAbsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container, ChatFragment.getInstance(adapter.data.get(i), adapter.data.get(i)._id, ChatFragment.CHAT_TYPE_USER),
                        ConversationsFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right);
            }
        });
    }

    @Override
    protected void initAdapter() {
        /*we have to load conversations eny time  ):  */
        adapter = new AdapterConversations(new ArrayList<DataConversation>(), getActivity(), this);
        mAbsListView.setAdapter(adapter);
        fetchNextPage();
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
    }

    public void handleConversationFilterChange(String title) {
        mCurrentConversationType = title;
        adapter.refresh(title);
        updateHeaderTitle();
    }

    private void updateHeaderTitle() {
        mEventHeader.setTitle(dm.getResourceText(R.string.Inbox) + " (" + mCurrentConversationType + ") ▼");
    }

    @Override
    protected void fetchNextPage() {
        ConversationManager.getAllConversations(adapter.getCount(), PAGE_COUNT, new GenericCallback() {
            @Override
            public void onDone(boolean success, Object data) {
                if (!success)
                    return;

                ResponseConversations responseMessage = (ResponseConversations) data;
                for (DataConversation conversation : responseMessage.array)
                    adapter.add(conversation);

                adapter.refresh(mCurrentConversationType);
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
            mEventHeader.setTitle(dm.getResourceText(R.string.Inbox));
            mEventHeader.getEditTitle().setVisibility(View.GONE);
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            isClicked = false;
        }
    }

    @Override
    public EmBaseAdapter getAdapter() {
        return null;
    }

    public void getConversations() {

    }

    @Override
    public void onReplyClick(DataConversation dataConversation) {
        ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container, ChatFragment.getInstance(dataConversation, dataConversation._id, ChatFragment.CHAT_TYPE_USER),
                ConversationsFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    public void onArchiveClick(DataConversation dataConversation) {
        dataConversation.status = "archive";
        adapter.refresh(mCurrentConversationType);
        ConversationManager.setConversationStatus(dataConversation._id, "archive", new GenericCallback() {
            @Override
            public void onDone(boolean success, Object data) {

            }
        });
    }

    @Override
    public void onDeleteClick(DataConversation dataConversation) {
        adapter.data.remove(dataConversation);
        ConversationManager.setConversationStatus(dataConversation._id, "deleted", new GenericCallback() {
            @Override
            public void onDone(boolean success, Object data) {
            }
        });
        adapter.refresh(mCurrentConversationType);
    }

    @Override
    public void onUnArchiveClick(DataConversation dataConversation) {
        dataConversation.status = "active";
        ConversationManager.setConversationStatus(dataConversation._id, "active", new GenericCallback() {
            @Override
            public void onDone(boolean success, Object data) {
            }
        });
        adapter.refresh(mCurrentConversationType);
    }
}
