package com.everymatch.saas.ui.me.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.AdapterDataUnit;
import com.everymatch.saas.adapter.EmBaseAdapter;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.base.BaseListFragment;

/**
 * Created by PopApp_laptop on 15/12/2015.
 */
public class UnitsMenuFragment extends BaseListFragment implements AdapterView.OnItemClickListener {
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void initAdapter() {
        super.initAdapter();
        //mAbsListView.setDividerHeight(Utils.dpToPx(1));
        //mAbsListView.setPadding(0, 0, 0, 0);
        mAbsListView.setAdapter(new AdapterDataUnit(getActivity()));
        mAbsListView.setOnItemClickListener(this);
    }

    @Override
    protected void setActionButtons() {
        super.setActionButtons();
        mActionButtonPrimary.setVisibility(View.VISIBLE);
        mActionButtonPrimary.setText(dm.getResourceText(getString(R.string.Done)));

        mActionButtonPrimary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    protected void setHeader() {
        super.setHeader();
        mEventHeader.setListener(this);
        mEventHeader.setTitle(dm.getResourceText(getString(R.string.Units)));
        mEventHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mEventHeader.getIconOne().setVisibility(View.GONE);
        mEventHeader.getIconTwo().setVisibility(View.GONE);
        mEventHeader.getIconThree().setVisibility(View.GONE);
    }

    @Override
    protected void fetchNextPage() {

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
        //Fragment fragment = ds.getUser().user_settings.getUnitFragmentByPosition(position);
        UnitFragment fragment = UnitFragment.getInstance(position);
        //fragment.setTargetFragment(this, SettingsFragment.REQUEST_CODE_DIALOG_WEIGHT);
        ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container, fragment,
                SettingsFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                R.anim.enter_from_left, R.anim.exit_to_right);
    }
}
