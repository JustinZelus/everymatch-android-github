package com.everymatch.saas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.everymatch.saas.R;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.view.BaseIconTextView;
import com.everymatch.saas.view.BaseTextView;

import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 15/12/2015.
 */
public abstract class AdapterDataRow<T> extends BaseAdapter {
    public enum ADAPTER_MODE {
        MODE_SELECT_ONE,
        MODE_SELECT_MULTY,
        MODE_NONE
    }

    ArrayList<T> data;
    public T selectedItem;
    Context con;
    LayoutInflater inflater;
    private ADAPTER_MODE mode;
    private int selectedPosition;

    public AdapterDataRow(ArrayList<T> data, Context con, ADAPTER_MODE mode) {
        this.data = data;
        this.con = con;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mode = mode;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setSelectedItemPosition(int selectedPosition) {
        this.selectedItem = data.get(selectedPosition);
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = (selectedPosition);
        notifyDataSetChanged();
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

        T item = data.get(i);

        holder.leftContainer.setVisibility(View.GONE);
        holder.leftIcon.setVisibility(View.GONE);
        if (mode == ADAPTER_MODE.MODE_NONE) {
            holder.rightIcon.setText(Consts.Icons.icon_Arrowright);
        } else if (mode == ADAPTER_MODE.MODE_SELECT_ONE) {
            if (selectedPosition == i)
                holder.rightIcon.setText(Consts.Icons.icon_StatusPositive);
            else
                holder.rightIcon.setText(Consts.Icons.icon_selectEmpty);
        }


        handleViewCreation(i, item, holder);
        return view;
    }

    protected abstract void handleViewCreation(int position, T item, ViewHolder holder);

    protected static class ViewHolder {
        public FrameLayout leftContainer;
        public BaseIconTextView leftIcon;
        public BaseIconTextView rightIcon;
        public BaseTextView tvTitle;
        public BaseTextView tvDetails;
        public BaseTextView tvRightText;
    }
}
