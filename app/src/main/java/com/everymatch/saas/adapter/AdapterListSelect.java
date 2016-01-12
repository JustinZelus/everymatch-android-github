package com.everymatch.saas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.everymatch.saas.R;
import com.everymatch.saas.view.BaseIconTextView;
import com.everymatch.saas.view.BaseTextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by PopApp_laptop on 13/10/2015.
 */
public class AdapterListSelect extends BaseAdapter {
    ArrayList<String> data;
    Context con;
    LayoutInflater inflater;
    AdapterListSelectCallback callback;

    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public void setIsMultiSelect(boolean isMultiSelect) {
        this.isMultiSelect = isMultiSelect;
    }

    private boolean[] selected;
    private boolean isMultiSelect = false;
    private boolean isLocked = true;


    public AdapterListSelect(ArrayList<String> data, Context con) {
        this(data, con, null);
    }

    public AdapterListSelect(ArrayList<String> data, Context con, AdapterListSelectCallback callback) {
        this.data = data;
        this.con = con;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.callback = callback;
        selected = new boolean[data.size()];
        Arrays.fill(selected, false);
    }

    public void updateData(ArrayList<String> newData) {
        this.data = newData;
        selected = new boolean[data.size()];
        Arrays.fill(selected, false);
        notifyDataSetChanged();
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final String item = data.get(i);

        View vi = view;
        if (view == null)
            vi = inflater.inflate(R.layout.view_list_select_item, null);
        final BaseIconTextView tvIcon;
        BaseTextView tvText;

        tvIcon = (BaseIconTextView) vi.findViewById(R.id.tvViewListSelectItemIcon);
        tvText = (BaseTextView) vi.findViewById(R.id.tvViewListSelectItemText);

        tvText.setText(item);
        tvIcon.setVisibility(selected[i] ? View.VISIBLE : View.INVISIBLE);

        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedCount = getSelectedCount();

                if (selectedCount == 1) {
                    if (selected[i] == true) {
                        // we pressed on the single selected one
                        if (isLocked) return;
                        selected[i] = !selected[i];
                        tvIcon.setVisibility(selected[i] ? View.VISIBLE : View.INVISIBLE);
                        return;
                    }
                    if (isMultiSelect) {
                        // multiple select enabled
                        selected[i] = !selected[i];
                        tvIcon.setVisibility(selected[i] ? View.VISIBLE : View.INVISIBLE);
                    } else {

                        // switch to another item
                        int currSelected = 0;
                        for (int j = 0; j < selected.length; ++j)
                            if (selected[j] == true) {
                                currSelected = j;
                                break;
                            }
                        selected[currSelected] = false;
                        selected[i] = true;
                        notifyDataSetChanged();
                    }
                } else if (selectedCount > 1) {
                    selected[i] = !selected[i];
                    tvIcon.setVisibility(selected[i] ? View.VISIBLE : View.INVISIBLE);
                } else {
                    // this is the first selection
                    selected[i] = !selected[i];
                    tvIcon.setVisibility(selected[i] ? View.VISIBLE : View.INVISIBLE);
                }

                if (callback != null)
                    callback.onSelect(item, i, selected[i]);
            }
        });

        return vi;
    }

    public int getSelectedCount() {
        int selectedCount = 0;
        for (boolean b : selected) if (b) selectedCount++;
        return selectedCount;
    }

    public interface AdapterListSelectCallback {
        void onSelect(Object item, int position, boolean enable);
    }
}
