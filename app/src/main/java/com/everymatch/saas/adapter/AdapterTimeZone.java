package com.everymatch.saas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.server.Data.DataTimeZone;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.view.BaseIconTextView;

import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 22/11/2015.
 */
public class AdapterTimeZone extends EmBaseAdapter<DataTimeZone> {
    // ArrayList<DataTimeZone> data;
    Context con;
    LayoutInflater inflater;

    public DataTimeZone getSelectedTimeZone() {
        return selectedTimeZone;
    }

    private DataTimeZone selectedTimeZone = null;

    public AdapterTimeZone(ArrayList<DataTimeZone> data, Context con) {
        this.mData = data;
        this.con = con;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        selectedTimeZone = mData.get(0);
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getFinalView(final int position, View convertView, ViewGroup parent) {
        final DataTimeZone item = getItem(position);

        View v = convertView;
        if (convertView == null)
            v = inflater.inflate(R.layout.view_time_zone, null);

        TextView title = (TextView) v.findViewById(R.id.tvTimeZoneTitle);
        TextView subTitle = (TextView) v.findViewById(R.id.tvTimeZoneSunTitle);
        final BaseIconTextView icon = (BaseIconTextView) v.findViewById(R.id.tvTimeZoneIcon);

        title.setText(item.title);
        subTitle.setText(item.country_code);


        icon.setText(selectedTimeZone.equals(item) ? Consts.Icons.icon_StatusPositive : Consts.Icons.icon_selectEmpty);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTimeZone = item;
                notifyDataSetChanged();
            }
        });
        return v;
    }

    @Override
    public boolean filterObject(DataTimeZone dataTimeZone, String constraint) {
        return dataTimeZone.title.toLowerCase().startsWith(constraint.toLowerCase());
    }
}
