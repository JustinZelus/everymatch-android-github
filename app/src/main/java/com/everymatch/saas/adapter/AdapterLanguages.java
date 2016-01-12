package com.everymatch.saas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.responses.ResponseApplication;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.view.EventDataRow;

import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 12/01/2016.
 */
public class AdapterLanguages extends BaseAdapter {
    ArrayList<ResponseApplication.DataCulture> data;
    Context con;
    LayoutInflater inflater;
    private ResponseApplication.DataCulture selectedCulture;

    public AdapterLanguages(Context con) {
        this.data = DataStore.getInstance().getApplicationData().getCultures();
        this.con = con;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //set current culture
        for (ResponseApplication.DataCulture culture : DataStore.getInstance().getApplicationData().getCultures()) {
            if (DataStore.getInstance().getCulture().equals(culture.culture_name)) {
                selectedCulture = culture;
                break;
            }
        }
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

        final ResponseApplication.DataCulture item = data.get(i);
        EventDataRow edr = new EventDataRow(con);
        edr.getLeftIcon().setVisibility(View.GONE);
        edr.getRightIcon().setVisibility(View.GONE);
        edr.getRightText().setVisibility(View.GONE);

        edr.setDetails(null);
        edr.setTitle(item.text_title);

        edr.setRightIconText((selectedCulture != null && selectedCulture.equals(item)) ? Consts.Icons.icon_StatusPositive : Consts.Icons.icon_selectEmpty);
        edr.getRightIcon().setTextColor((selectedCulture != null && selectedCulture.equals(item)) ? DataStore.getInstance().getIntColor(EMColor.PRIMARY) : 0);

        edr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataStore.getInstance().getUser().user_settings.default_culture = item.culture_name;
                selectedCulture = item;
                notifyDataSetChanged();
            }
        });
        return edr;
    }
}
