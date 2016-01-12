package com.everymatch.saas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.server.responses.ResponseApplication;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.view.BaseIconTextView;

import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 22/11/2015.
 */
public class AdapterCurrency extends EmBaseAdapter<ResponseApplication.DataCurrency> {
    Context con;
    LayoutInflater inflater;
    private ResponseApplication.DataCurrency selectedDataCurrency = null;

    public ResponseApplication.DataCurrency getSelectedDataCurrency() {
        return selectedDataCurrency;
    }


    public AdapterCurrency(ArrayList<ResponseApplication.DataCurrency> data, Context con) {
        this.mData = data;
        this.con = con;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        selectedDataCurrency = mData.get(0);
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getFinalView(final int position, View convertView, ViewGroup parent) {
        final ResponseApplication.DataCurrency item = getItem(position);

        View v = convertView;
        if (convertView == null)
            v = inflater.inflate(R.layout.view_time_zone, null);

        TextView title = (TextView) v.findViewById(R.id.tvTimeZoneTitle);
        TextView subTitle = (TextView) v.findViewById(R.id.tvTimeZoneSunTitle);
        final BaseIconTextView icon = (BaseIconTextView) v.findViewById(R.id.tvTimeZoneIcon);

        title.setText(item.code);
        subTitle.setText(item.symbol);

        icon.setText(selectedDataCurrency.equals(item) ? Consts.Icons.icon_StatusPositive : Consts.Icons.icon_selectEmpty);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDataCurrency = item;
                notifyDataSetChanged();
            }
        });
        return v;
    }

    @Override
    public boolean filterObject(ResponseApplication.DataCurrency dataCurrency, String constraint) {
        //return dataCurrency.title.toLowerCase().startsWith(constraint.toLowerCase());
        return true;
    }
}
