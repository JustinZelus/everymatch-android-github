package com.everymatch.saas.ui.dialog;

import android.view.View;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.responses.ResponseApplication;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.base.BaseAbstractFragment;
import com.everymatch.saas.view.EventDataRow;
import com.everymatch.saas.view.ViewSeperator;

/**
 * Created by PopApp_laptop on 22/11/2015.
 */
public class FragmentCurrencies extends BaseAbstractFragment {


    @Override
    protected void setHeader() {
        super.setHeader();
        mEventHeader.setTitle(dm.getResourceText(R.string.Currency));
    }

    @Override
    protected void addRows() {
        rowsContainer.removeAllViews();
        for (ResponseApplication.DataCurrency currency : ds.getApplicationData().getCurrencies()) {
            EventDataRow edrCurrency = new EventDataRow(getActivity());
            edrCurrency.getLeftMediaContainer().setVisibility(View.GONE);
            edrCurrency.setTitle(currency.code);
            edrCurrency.setDetails(currency.symbol);
            edrCurrency.setRightIconText(ds.getUser().user_settings.currency.equals(currency.code) ? Consts.Icons.icon_StatusPositive : "");
            edrCurrency.getRightIcon().setTextColor(ds.getIntColor(EMColor.PRIMARY));
            edrCurrency.setRightText(null);
            edrCurrency.setTag(currency);
            edrCurrency.setOnClickListener(clickListener);
            rowsContainer.addView(edrCurrency);
            rowsContainer.addView(new ViewSeperator(getActivity(), null));
        }
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ResponseApplication.DataCurrency currency = (ResponseApplication.DataCurrency) v.getTag();
            ds.getUser().user_settings.currency = currency.code;
            addRows();
            getActivity().onBackPressed();
        }
    };
}
