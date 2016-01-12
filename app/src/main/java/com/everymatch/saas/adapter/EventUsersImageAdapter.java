package com.everymatch.saas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Lior Iluz on 10/03/14.
 */
public class EventUsersImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList mUsersId;

    public EventUsersImageAdapter(Context context, ArrayList<String> usersId) {
        mContext = context;
        mUsersId = usersId;
    }


    @Override
    public int getCount() {
        return mUsersId.size();
    }

    @Override
    public Integer getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        final ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.image_user_list_item, viewGroup, false);

            holder = new ViewHolder();

            holder.userImage = (ImageView) view.findViewById(R.id.image_user_list_item_image);


            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        final String urlString = "https://cdn2.everymatch.com/remote/emprod.everymatchintern.netdna-cdn.com/memberprofilepictures/" + mUsersId.get(position) +
                "/Current/L." + mUsersId.get(position) + ".jpg?width=500&height=500&format=jpg";
        Picasso.with(EverymatchApplication.getContext()).load(urlString).placeholder(DataManager.getInstance().getAvatarDrawable()).into(holder.userImage);

        return view;
    }

    static class ViewHolder {
        protected ImageView userImage;
    }
}
