package com.everymatch.saas.ui.me.settings;

import android.view.View;
import android.widget.AdapterView;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.AdapterLanguages;
import com.everymatch.saas.adapter.EmBaseAdapter;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.base.BaseListFragment;

/**
 * Created by PopApp_laptop on 12/01/2016.
 */
public class LanguageFragment extends BaseListFragment implements AdapterView.OnItemClickListener {

    public static final String TAG = "LanguageFragment";

    @Override
    protected void fetchNextPage() {

    }

    @Override
    protected void setHeader() {
        super.setHeader();
        mEventHeader.setListener(this);
        mEventHeader.setTitle(dm.getResourceText(R.string.Profile_Language));
        mEventHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mEventHeader.getIconOne().setVisibility(View.GONE);
        mEventHeader.getIconTwo().setVisibility(View.GONE);
        mEventHeader.getIconThree().setVisibility(View.GONE);
    }

    @Override
    protected void initAdapter() {
        super.initAdapter();
        mAbsListView.setAdapter(new AdapterLanguages(getActivity()));
        mAbsListView.setOnItemClickListener(this);
    }

    @Override
    public EmBaseAdapter getAdapter() {
        return null;
    }

    @Override
    public void onBackButtonClicked() {
        getActivity().onBackPressed();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
