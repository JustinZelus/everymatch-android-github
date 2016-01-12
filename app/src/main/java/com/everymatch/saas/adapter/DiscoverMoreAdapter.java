package com.everymatch.saas.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.PopupMenuItem;
import com.everymatch.saas.util.IconManager;
import com.everymatch.saas.view.BaseTextView;

import java.util.List;

/**
 * Created by dors on 7/26/15.
 */
public class DiscoverMoreAdapter extends BaseAdapter {

    private List<PopupMenuItem> mItems;

    public DiscoverMoreAdapter(List<PopupMenuItem> items) {
        this.mItems = items;
    }

    public void setItems(List<PopupMenuItem> items) {
        this.mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PopupMenuItem item = mItems.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_discover_more_menu_item, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.view_discover_more_menu_item_title)).setText(item.title);
        if (item.icon == null)
            ((TextView) convertView.findViewById(R.id.view_discover_more_menu_item_icon)).setVisibility(View.GONE);
        else {
            ((TextView) convertView.findViewById(R.id.view_discover_more_menu_item_icon)).setVisibility(View.VISIBLE);
            ((TextView) convertView.findViewById(R.id.view_discover_more_menu_item_icon)).setText(IconManager.getInstance(parent.getContext()).getIconString(item.icon));
        }
        BaseTextView tvBadge = (BaseTextView) convertView.findViewById(R.id.badge);
        if (item.badge == null)
            tvBadge.setVisibility(View.GONE);
        else {
            tvBadge.setVisibility(View.VISIBLE);
            tvBadge.setText(item.badge);
        }
        return convertView;
    }
}
