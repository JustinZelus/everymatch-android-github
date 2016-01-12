package com.everymatch.saas.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.AdapterConversations;
import com.everymatch.saas.adapter.EmBaseAdapter;
import com.everymatch.saas.server.Data.DataConversation;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestConversations;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseConversations;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.base.BaseListFragment;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.EmptyViewFactory;
import com.everymatch.saas.view.EventHeader;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationsFragment extends BaseListFragment implements EventHeader.OnEventHeader, AdapterConversations.inboxCallback {

    public static final String TAG = ConversationsFragment.class.getSimpleName();
    //private ArrayList<DataConversation> mDataConversation = null;
    private boolean isClicked = false;

    //Views
    //ListView lvMessages;
    AdapterConversations adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mDataConversation = new ArrayList<DataConversation>();
        getConversations();
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
                ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container, ChatFragment.getInstance(adapter.data.get(i), adapter.data.get(i)._id,ChatFragment.CHAT_TYPE_USER),
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
        mEventHeader.setTitle("Messaging");
    }

    @Override
    protected void fetchNextPage() {
        //TODO - USE STATIC METHOD IN DATACONVERSATION
        ServerConnector.getInstance().processRequest(new RequestConversations(adapter.getCount(), PAGE_COUNT), new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                ResponseConversations responseMessage = (ResponseConversations) baseResponse;
                if (responseMessage != null) {

                    if (responseMessage != null) {
                        for (DataConversation conversation : responseMessage.array)
                            adapter.data.add(conversation);
                    }
                    //adapter.data = mDataConversation;
                    adapter.notifyDataSetChanged();
                    if (responseMessage.array.length < PAGE_COUNT) {
                        mIsNoMoreResults = true;
                    }
                } else {
                    Toast.makeText(getActivity(), "response = null", Toast.LENGTH_SHORT).show();
                    EMLog.e(TAG, "RequestConversations is NULL");
                }
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                Toast.makeText(getActivity(), errorResponse.getServerRawResponse(), Toast.LENGTH_SHORT).show();
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
            mEventHeader.setTitle("People");
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
    public void onReplyClick(int pos) {
        mAbsListView.getOnItemClickListener().onItemClick(null, null, pos, pos);
    }

    @Override
    public void onArchiveClick(int pos) {

    }

    @Override
    public void onReportClick(int pos) {

    }
}
