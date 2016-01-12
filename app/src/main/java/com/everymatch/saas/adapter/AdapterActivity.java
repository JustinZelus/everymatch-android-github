package com.everymatch.saas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.everymatch.saas.R;
import com.everymatch.saas.server.Data.DataActivity;
import com.everymatch.saas.util.IconManager;
import com.everymatch.saas.view.BaseIconTextView;
import com.everymatch.saas.view.BaseTextView;

import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 06/10/2015.
 */
public class AdapterActivity extends BaseAdapter {
    ArrayList<DataActivity> data;
    Context con;
    LayoutInflater inflater;

    public AdapterActivity(ArrayList<DataActivity> data, Context con) {
        this.data = data;
        this.con = con;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        DataActivity item = data.get(i);

        View v = view;
        if (view == null)
            v = inflater.inflate(R.layout.view_event_data_row, null);

        BaseTextView tvTitle = (BaseTextView) v.findViewById(R.id.event_data_row_title);
        BaseTextView tvDetails = (BaseTextView) v.findViewById(R.id.event_data_row_details);
        BaseIconTextView tvIcon = (BaseIconTextView) v.findViewById(R.id.event_data_row_icon_left);
        BaseIconTextView tvRight = (BaseIconTextView) v.findViewById(R.id.event_data_row_icon_right);

        tvTitle.setText(item.text_title);
        tvRight.setVisibility(View.GONE);
        tvDetails.setVisibility(View.GONE);
       // tvIcon.setText(IconManager.getInstance(con).getIconString(item.icon.getValue()));

        return v;
    }
}

