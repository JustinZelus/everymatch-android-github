package com.everymatch.saas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.responses.ResponseApplication;

import java.util.ArrayList;

public class AdapterCountryPhone extends ArrayAdapter<ResponseApplication.DataCountryPhoneCode> {
    private ArrayList<ResponseApplication.DataCountryPhoneCode> items;
    private ArrayList<ResponseApplication.DataCountryPhoneCode> itemsAll;
    private ArrayList<ResponseApplication.DataCountryPhoneCode> suggestions;
    private int viewResourceId;

    public AdapterCountryPhone(Context context, int viewResourceId, ArrayList<ResponseApplication.DataCountryPhoneCode> items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll = (ArrayList<ResponseApplication.DataCountryPhoneCode>) items.clone();
        this.suggestions = new ArrayList<>();
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.view_country_phone_code_row, null);
        }
        ResponseApplication.DataCountryPhoneCode customer = items.get(position);
        if (customer != null) {

            TextView tv = (TextView) v.findViewById(R.id.tvValue);
            if (tv != null) {
                tv.setTextColor(DataStore.getInstance().getIntColor(EMColor.MOON));
                tv.setText(customer.country);
            }
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((ResponseApplication.DataCountryPhoneCode) resultValue).country;
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (ResponseApplication.DataCountryPhoneCode customer : itemsAll) {
                    if (customer.country.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(customer);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<ResponseApplication.DataCountryPhoneCode> filteredList = (ArrayList<ResponseApplication.DataCountryPhoneCode>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (ResponseApplication.DataCountryPhoneCode c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };

}