package com.everymatch.saas.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataPeople;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.singeltones.PeopleListener;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseIconTextView;
import com.everymatch.saas.view.BaseTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Lior Iluz on 10/03/14.
 */
public class PeopleUsersAdapter extends EmBaseAdapter<DataPeople> {

    private int mMode = DataStore.ADAPTER_MODE_TEXT;
    private PeopleListener mPeopleListener;
    public HashSet<String> mSelectedIds;
    private IconListener mIconListener;

    public PeopleUsersAdapter(Context context, ArrayList<DataPeople> dataArray, int mode) {
        this(context, dataArray, mode, null);
    }

    public PeopleUsersAdapter(Context context, ArrayList<DataPeople> dataArray, int mode, PeopleListener mPeopleListener) {
        mContext = context;
        mData = dataArray;
        this.mMode = mode;
        mSelectedIds = new HashSet<>();
        setListener(mPeopleListener);
    }

    public void setListener(PeopleListener peopleListener) {
        this.mPeopleListener = peopleListener;
    }

    public void setIconListener(IconListener iconListener) {
        this.mIconListener = iconListener;
    }

    @Override
    public View getFinalView(final int position, View view, ViewGroup viewGroup) {

        final ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.people_list_item, viewGroup, false);

            holder = new ViewHolder();

            holder.userImage = (ImageView) view.findViewById(R.id.people_list_item_image);
            holder.name = (BaseTextView) view.findViewById(R.id.people_list_item_name);
            holder.details = (BaseTextView) view.findViewById(R.id.people_list_item_details);
            holder.icon = (BaseIconTextView) view.findViewById(R.id.people_list_item_right_icon);
            holder.rightText = (BaseTextView) view.findViewById(R.id.people_list_item_right_text);

            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        final DataPeople dataPeople = getItem(position);

        loadUserImage(dataPeople.image_url, holder);
        holder.name.setText(dataPeople.first_name + ", " + dataPeople.last_name);

        String text = String.valueOf(dataPeople.age);

        if (!TextUtils.isEmpty(dataPeople.city)) {
            text += " - " + dataPeople.city;
        }

        holder.details.setText(text);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedIds.contains(dataPeople.users_id)) {
                    mSelectedIds.remove(dataPeople.users_id);
                } else {
                    mSelectedIds.add(dataPeople.users_id);
                }

                notifyDataSetChanged();

                if (mMode != DataStore.ADAPTER_MODE_COUNTER) {
                    if (mPeopleListener != null) {
                        mPeopleListener.onUserClick(dataPeople);
                    }
                }
            }
        });

        if (mMode != DataStore.ADAPTER_MODE_COUNTER) {
            holder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mIconListener != null) {
                        mIconListener.onIconClick(dataPeople, position);
                    }
                }
            });
        }

        switch (mMode) {
            case DataStore.ADAPTER_MODE_TEXT:
                holder.icon.setText(dataPeople.username);
                break;

            case DataStore.ADAPTER_MODE_LIKE:
                holder.icon.setText(dataPeople.is_friend ? Consts.Icons.icon_Unfavorite : Consts.Icons.icon_Favorite);
                break;

            case DataStore.ADAPTER_MODE_COUNTER:
                holder.icon.setText(mSelectedIds.contains(dataPeople.users_id) ? Consts.Icons.icon_StatusPositive : Consts.Icons.icon_selectEmpty);
                break;

            case DataStore.ADAPTER_MODE_NONE:
                holder.icon.setText("");
                view.findViewById(R.id.line).setVisibility(View.INVISIBLE);
                break;

            case DataStore.ADAPTER_MODE_MATCH:
                holder.icon.setText(Consts.Icons.icon_Match);
                holder.icon.setTextColor(DataStore.getInstance().getIntColor(EMColor.PRIMARY));
                holder.rightText.setVisibility(View.VISIBLE);
                holder.rightText.setTextColor(DataStore.getInstance().getIntColor(EMColor.PRIMARY));
                holder.rightText.setText(dataPeople.match + "%");
                break;
        }

        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void loadUserImage(String image_url, final ViewHolder holder) {
        String url = Utils.getImageUrl(image_url, holder.userImage.getLayoutParams().width, holder.userImage.getLayoutParams().width);
        Picasso.with(EverymatchApplication.getContext()).load(url).placeholder(DataManager.getInstance().getAvatarDrawable()).into(holder.userImage);
    }

    @Override
    public boolean filterObject(DataPeople dataPeople, String constraint) {
        return (!TextUtils.isEmpty(dataPeople.first_name) && dataPeople.first_name.toLowerCase().contains(constraint.toLowerCase())) ||
                (!TextUtils.isEmpty(dataPeople.last_name) && dataPeople.last_name.toLowerCase().contains(constraint.toLowerCase()));
    }

    public String getSelectedIds() {
        String collegeString = "";
        for (String s : mSelectedIds) {
            collegeString += (collegeString.trim().equals("") ? "" : ",") + s.trim();
        }

        return collegeString;
    }

    static class ViewHolder {
        protected ImageView userImage;
        protected BaseTextView name;
        protected BaseTextView details;
        protected BaseIconTextView icon;
        protected BaseTextView rightText;
    }

    public interface IconListener {
        void onIconClick(DataPeople user, int position);
    }
}
