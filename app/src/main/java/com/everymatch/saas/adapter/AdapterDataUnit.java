package com.everymatch.saas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.Data.ApplicationSettings;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.view.BaseIconTextView;
import com.everymatch.saas.view.BaseTextView;

/**
 * Created by PopApp_laptop on 15/12/2015.
 */
public class AdapterDataUnit extends BaseAdapter {
    Context con;
    LayoutInflater inflater;
    ApplicationSettings.UnitsHolder units;

    public AdapterDataUnit(Context con) {
        this.con = con;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        units = DataStore.getInstance().getApplicationData().getSettings().getUnits();
    }

    @Override
    public int getCount() {
        return units.getCount();
    }

    @Override
    public Object getItem(int i) {
        return units.getUnitListNameByPosition(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            view = inflater.inflate(R.layout.view_event_data_row, null);
            holder = new ViewHolder();
            holder.leftContainer = (FrameLayout) view.findViewById(R.id.view_vent_data_row_left_media_container);
            holder.leftIcon = (BaseIconTextView) view.findViewById(R.id.event_data_row_icon_left);
            holder.rightIcon = (BaseIconTextView) view.findViewById(R.id.event_data_row_icon_right);
            holder.tvTitle = (BaseTextView) view.findViewById(R.id.event_data_row_title);
            holder.tvDetails = (BaseTextView) view.findViewById(R.id.event_data_row_details);
            holder.tvRightText = (BaseTextView) view.findViewById(R.id.event_data_row_text_right);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.leftContainer.setVisibility(View.GONE);
        holder.rightIcon.setText(Consts.Icons.icon_Arrowright);
        holder.tvDetails.setVisibility(View.GONE);
        holder.tvTitle.setText(units.getUnitListNameByPosition(i));
        holder.tvRightText.setText("" + units.getUserUnitByPosition(i));

        return view;
    }

    protected static class ViewHolder {
        FrameLayout leftContainer;
        BaseIconTextView leftIcon;
        BaseIconTextView rightIcon;
        BaseTextView tvTitle;
        BaseTextView tvDetails;
        BaseTextView tvRightText;
    }
}
