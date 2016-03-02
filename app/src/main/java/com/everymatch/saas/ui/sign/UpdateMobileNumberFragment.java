package com.everymatch.saas.ui.sign;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.everymatch.saas.R;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.ui.me.settings.SettingsFragment;
import com.everymatch.saas.util.NotifierPopup;
import com.everymatch.saas.view.EventHeader;

/**
 * Created by PopApp_laptop on 29/02/2016.
 */
public class UpdateMobileNumberFragment extends BaseFragment implements EventHeader.OnEventHeader, View.OnClickListener {

    public static int REQUEST_CODE_UPDATE_NUMBER = 108;

    //VIEWS
    Button btnContinue;
    EventHeader mHeader;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_mobile_number, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHeader = (EventHeader) view.findViewById(R.id.eventHeader);
        btnContinue = (Button) view.findViewById(R.id.btnContinue);
        setHeader();
        btnContinue.setOnClickListener(this);
    }

    private void setHeader() {
        mHeader.getBackButton().setText(Consts.Icons.icon_New_Close);
        mHeader.setListener(this);
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnContinue:
                BaseFragment fragment = new SMSFragment();
                fragment.setTargetFragment(UpdateMobileNumberFragment.this, REQUEST_CODE_UPDATE_NUMBER);
                ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container, fragment,
                        SettingsFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE_NUMBER) {
            //update number success
            NotifierPopup.Builder builder = new NotifierPopup.Builder(UpdateMobileNumberFragment.this.getActivity());
            builder.setDuration(6000);
            builder.setMessage(dm.getResourceText(R.string.ChangesSavedSuccessfully));
            builder.setGravity(Gravity.TOP);
            builder.setType(NotifierPopup.TYPE_SUCCESS);
            builder.setView(UpdateMobileNumberFragment.this.getView());
            builder.show();
        }
    }
}
