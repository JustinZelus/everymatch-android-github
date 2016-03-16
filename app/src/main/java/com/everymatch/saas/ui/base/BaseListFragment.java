package com.everymatch.saas.ui.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.EmBaseAdapter;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.ShapeDrawableUtils;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseButton;
import com.everymatch.saas.view.BaseListView;
import com.everymatch.saas.view.BaseRelativeLayout;
import com.everymatch.saas.view.EventHeader;

/**
 * Created by dors on 11/3/15.
 */
public abstract class BaseListFragment extends BaseFragment implements AbsListView.OnScrollListener, BaseListView.ListViewObserver, TextWatcher, EventHeader.OnEventHeader {

    private static final String TAG = BaseListFragment.class.getSimpleName();

    protected static final int PAGE_COUNT = 10;
    protected DataStore ds = DataStore.getInstance();
    protected static DataManager dm = DataManager.getInstance();

    // Views
    protected BaseListView mAbsListView;
    protected View mFooterWrapper, emptyFooterView;
    protected EventHeader mEventHeader;
    protected LinearLayout titleHolder;
    protected TextView tvTitle;

    protected BaseRelativeLayout mEmptyViewContainer;
    /*belongs to people fragments*/
    protected BaseButton mActionButtonPrimary;

    protected BaseButton mActionButtonSecondary;

    // A container for adding extra items above the list
    /*belongs to my events fragment*/
    protected FrameLayout mTopContainer;

    // Data
    protected boolean mLoading;
    protected boolean mIsNoMoreResults;
    protected boolean mShowEmptyFooterView;
    protected boolean mIsSearching;
    protected int mMode = DataStore.ADAPTER_MODE_TEXT;
    private boolean mIsUserScrolled;
    private boolean mIsSearchIconClicked;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTopContainer = (FrameLayout) view.findViewById(R.id.fragment_event_list_top_container);
        mEventHeader = (EventHeader) view.findViewById(R.id.fragment_list_header);
        mEmptyViewContainer = (BaseRelativeLayout) view.findViewById(R.id.emptyViewContainer);
        titleHolder = (LinearLayout) view.findViewById(R.id.BaseListTitleHolder);
        tvTitle = (TextView) view.findViewById(R.id.tvBaseListTitleText);

        /*belong to BaseListFragment*/
        mAbsListView = (BaseListView) view.findViewById(android.R.id.list);
        /*bottom buttons*/
        mActionButtonPrimary = (BaseButton) view.findViewById(R.id.fragment_list_action_button_primary);
        mActionButtonSecondary = (BaseButton) view.findViewById(R.id.fragment_list_action_button_secondary);
        mActionButtonPrimary.setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton());
        mActionButtonSecondary.setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton(ds.getIntColor(EMColor.NEGATIVE)));

        /*belong to BaseListFragment*/
        mEventHeader = (EventHeader) view.findViewById(R.id.fragment_list_header);
        setActionButtons();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHeader();
        /*now we have a listView */
        mAbsListView.setOnScrollListener(this);

        mFooterWrapper = LayoutInflater.from(getActivity()).inflate(R.layout.view_footer_progress, mAbsListView, false);
        //create empty footer View
        emptyFooterView = new LinearLayout(getActivity());
        //emptyFooterView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dpToPx(70)));
        emptyFooterView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, Utils.dpToPx(70)));

        /*from base fragment*/
        mProgressBar = mFooterWrapper.findViewById(android.R.id.progress);
        /*adding footer view and hide it */
        addFooterView();
        /*everyone set it own first data*/
        initAdapter();

        setEmptyView();
    }

    protected void setEmptyView() {

    }

    protected void initAdapter() {
    }

    /*every child will set appearance of the bottom buttons */
    protected void setActionButtons() {
    }

    protected void setHeader() {
        mEventHeader.setArrowDownVisibility(true);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(float deltaY) {

        if (deltaY != 0) {
            mIsUserScrolled = true;
            EMLog.i(TAG, "user scrolled");
        }
    }

    @Override
    public void onScroll(AbsListView listView, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {

        mAbsListView = (BaseListView) listView;

        /* Prevent fetching more data when this method isn't invoked by the user*/
        if (!mIsUserScrolled) {
            mAbsListView.setObserver(this);
            return;
        }
        /*don't  call onScroll(float deltaY)*/
        mAbsListView.setObserver(null);

        if (mIsNoMoreResults) {
            return;
        }
        /*check if chiled is in fetching data operation right now...*/
        if (mLoading) {
            EMLog.i(TAG, "Still loading...");
            return;
        }

        if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
            if (shouldFetchMoreData()) {
                /*urn on footer visibility*/
                mProgressBar.setVisibility(View.VISIBLE);
                mLoading = true;
                EMLog.i(TAG, "Scroll Bottom - going to fetch the next page");
                /*tells every chiled to fetch more data*/
                fetchNextPage();
            } else {
                setNoMoreResults();
            }
        }
    }

    /**
     * Fetch the next page, once the list arrived bottom
     */
    protected abstract void fetchNextPage();

    protected boolean shouldFetchMoreData() {
        return true;
    }

    /**
     * Adds a footer view to the bottom of the list in order to represents loading
     */
    protected void addFooterView() {

        if (mAbsListView.getFooterViewsCount() == 0) {
            mAbsListView.addFooterView(mFooterWrapper);
        }

        mProgressBar.setVisibility(View.GONE);
    }

    /*each chiled calles this method when he finish fetching his data
    and there ara no more item to load*/

    protected void setNoMoreResults() {
        mIsNoMoreResults = true;
        mProgressBar.setVisibility(View.GONE);

        if (mAbsListView.getFooterViewsCount() <= 2 && mShowEmptyFooterView) {
            mAbsListView.addFooterView(emptyFooterView);
        }
    }

    /*Methods from TextWatcher*/

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        performSearch(s.toString());
    }

    protected void setTitle(String title) {
        titleHolder.setVisibility(Utils.isEmpty(title) ? View.GONE : View.VISIBLE);
        tvTitle.setText(title);
    }
    // Unused callbacks

    @Override
    public void onOneIconClicked() {
    }

    @Override
    public void onTwoIconClicked() {
    }

    @Override
    public void onThreeIconClicked() {
    }


    /**
     * Call this method when your search icon has been clicked
     */
    public void onSearchIconClick() {

        if (!mIsSearchIconClicked) {
            mEventHeader.getTitle().setVisibility(View.GONE);
            mEventHeader.getEditTitle().setVisibility(View.VISIBLE);
            mEventHeader.getEditTitle().setFocusable(true);
            mEventHeader.getEditTitle().setText("");

            /* show keyboard on search click */
            showKeyboard();
            mIsSearchIconClicked = true;
            mIsSearching = true;

            setNoMoreResults();
        } else {

            mIsSearching = false;

            mEventHeader.getTitle().setVisibility(View.VISIBLE);
            mEventHeader.getEditTitle().setVisibility(View.GONE);

            /*hide keyboard*/
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

            mIsSearchIconClicked = false;

            getAdapter().cancelSearch();

            mIsNoMoreResults = false;
        }
    }

    private void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(mEventHeader.getEditTitle().getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    public abstract EmBaseAdapter getAdapter();

    /*every child will perform his own search */
    public void performSearch(String searchConstraint) {
    }
}
