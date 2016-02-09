package com.everymatch.saas.ui.me.settings;

import android.view.View;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.responses.ResponseApplication;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.base.BaseAbstractFragment;
import com.everymatch.saas.view.EventDataRow;
import com.everymatch.saas.view.ViewSeperator;

/**
 * Created by PopApp_laptop on 12/01/2016.
 */
public class LanguageFragment extends BaseAbstractFragment {

    public static final String TAG = "LanguageFragment";


    @Override
    protected void setHeader() {
        super.setHeader();
        mEventHeader.setTitle(dm.getResourceText(R.string.Profile_Language));
    }

    @Override
    protected void addRows() {
        super.addRows();
        rowsContainer.removeAllViews();

        for (ResponseApplication.DataCulture culture : ds.getApplicationData().getCultures()) {
            EventDataRow edtLanguage = new EventDataRow(getActivity());
            edtLanguage.getLeftMediaContainer().setVisibility(View.GONE);
            edtLanguage.setTitle(culture.text_title);
            edtLanguage.setDetails(null);
            edtLanguage.setRightIconText(SettingsFragment.userSelectedCulture.culture_name.equals(culture.culture_name) ? Consts.Icons.icon_StatusPositive : "");
            edtLanguage.getRightIcon().setTextColor(ds.getIntColor(EMColor.PRIMARY));
            edtLanguage.setRightText(null);
            edtLanguage.setTag(culture);
            edtLanguage.setOnClickListener(clickListener);
            rowsContainer.addView(edtLanguage);
            rowsContainer.addView(new ViewSeperator(getActivity(), null));
        }

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ResponseApplication.DataCulture culture = (ResponseApplication.DataCulture) v.getTag();
            SettingsFragment.userSelectedCulture = culture;
            getActivity().onBackPressed();
        }
    };
}
