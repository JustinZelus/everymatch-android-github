package com.everymatch.saas.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataEvent;
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

    public static final String ACTION_COUNTER_CLICK = "action.counter.click";
    public static final String EXTRA_SELECTION_COUNT = "extra.is.selection.empty";

    //DATA
    private DataEvent mEvent;
    private int mMode = DataStore.ADAPTER_MODE_TEXT;
    private PeopleListener mPeopleListener;
    public HashSet<String> mSelectedIds;
    public HashSet<String> loadingIds;
    private IconListener mIconListener;
    private DataStore ds = DataStore.getInstance();
    private PeopleUsersAdapterCallback callback;


    public PeopleUsersAdapter(Context context, ArrayList<DataPeople> dataArray, int mode, DataEvent dataEvent, PeopleUsersAdapterCallback callback) {
        this(context, dataArray, mode, null, dataEvent, callback);
    }

    public PeopleUsersAdapter(Context context, ArrayList<DataPeople> dataArray, int mode, PeopleListener mPeopleListener, DataEvent dataEvent, PeopleUsersAdapterCallback callback) {
        mContext = context;
        mData = dataArray;
        this.mMode = mode;
        mSelectedIds = new HashSet<>();
        loadingIds = new HashSet<>();
        setListener(mPeopleListener);
        this.mEvent = dataEvent;
        this.callback = callback;
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
            holder.bottomText = (BaseTextView) view.findViewById(R.id.people_list_item_bottom_text);
            holder.rightPart = (LinearLayout) view.findViewById(R.id.rightPart);
            holder.rightPartHolder = (LinearLayout) view.findViewById(R.id.rightPartHolder);
            holder.loader = (RelativeLayout) view.findViewById(R.id.userLoader);

            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        final DataPeople dataPeople = getItem(position);

        loadUserImage(dataPeople.image_url, holder);
        holder.name.setText(dataPeople.first_name + ", " + dataPeople.last_name);

        String text = String.valueOf(dataPeople.age);

        holder.loader.setVisibility(loadingIds.contains(dataPeople.users_id) ? View.VISIBLE : View.GONE);
        if (!TextUtils.isEmpty(dataPeople.city)) {
            text += " - " + dataPeople.city;
        }

        holder.details.setText(text);


        if (mMode == DataStore.ADAPTER_MODE_COUNTER_WITH_PERCENT || mMode == DataStore.ADAPTER_MODE_COUNTER || mMode == DataStore.ADAPTER_MODE_LIKE)
            holder.rightPart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if (mEvent == null) return;
                    //int currentParticipatingCount = mEvent.dataPublicEvent.getAllUsers(Participation_Type.PARTICIPATING).size();

                    if (mSelectedIds.contains(dataPeople.users_id)) {
                        mSelectedIds.remove(dataPeople.users_id);
                        notifyDataSetChanged();
                        // notify who ever listen that a click has made (set button clickable or something (INVITE PARTICIPANT FRAGMENT))
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(ACTION_COUNTER_CLICK).putExtra(EXTRA_SELECTION_COUNT, mSelectedIds.size()));
                        //notify about selection size to parents
                        if (callback != null) callback.onSelectionMade(mSelectedIds.size());

                    } else {
                        if (callback != null && !callback.canProceed()) /*mEvent.dataPublicEvent.spots <= mSelectedIds.size() + currentParticipatingCount*/ {
                            callback.onLimitExeeded();
                            return;
                        }

                        mSelectedIds.add(dataPeople.users_id);
                        notifyDataSetChanged();
                        // notify who ever listen that a click has made (set button clickable or something (INVITE PARTICIPANT FRAGMENT))
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(ACTION_COUNTER_CLICK).putExtra(EXTRA_SELECTION_COUNT, mSelectedIds.size()));
                        //notify about selection size to parents
                        if (callback != null) callback.onSelectionMade(mSelectedIds.size());
                    }
                }
            });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPeopleListener != null) {
                    mPeopleListener.onUserClick(dataPeople);
                }
            }
        });

        if (mMode != DataStore.ADAPTER_MODE_COUNTER && mMode != DataStore.ADAPTER_MODE_COUNTER_WITH_PERCENT) {
            ((LinearLayout) holder.icon.getParent()).setOnClickListener(new View.OnClickListener() {
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
                holder.icon.setTextColor(dataPeople.is_friend ? ds.getIntColor(EMColor.PRIMARY) : ds.getIntColor(EMColor.MOON));
                break;

            case DataStore.ADAPTER_MODE_COUNTER:
            case DataStore.ADAPTER_MODE_COUNTER_WITH_PERCENT:
                holder.icon.setText(mSelectedIds.contains(dataPeople.users_id) ? Consts.Icons.icon_StatusPositive : Consts.Icons.icon_selectEmpty);
                holder.bottomText.setVisibility(mMode == DataStore.ADAPTER_MODE_COUNTER_WITH_PERCENT ? View.VISIBLE : View.GONE);
                holder.bottomText.setText("" + dataPeople.match + "%");
                break;

            case DataStore.ADAPTER_MODE_NONE:
                holder.icon.setText("");
                view.findViewById(R.id.line).setVisibility(View.INVISIBLE);
                break;

            case DataStore.ADAPTER_MODE_MATCH:
                holder.icon.setText(Consts.Icons.icon_Match);
                holder.icon.setTextColor(DataStore.getInstance().getIntColor(EMColor.PRIMARY));
                holder.rightText.setVisibility(View.VISIBLE);
                holder.rightPartHolder.setVisibility(View.GONE);
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

    public void addLoader(String userId) {
        loadingIds.add(userId);
        notifyDataSetChanged();
    }

    public void removeLoader(String userId) {
        loadingIds.remove(userId);
        notifyDataSetChanged();
    }

    @Override
    public boolean filterObject(DataPeople dataPeople, String constraint) {
        return (!TextUtils.isEmpty(dataPeople.first_name) && dataPeople.first_name.toLowerCase().contains(constraint.toLowerCase())) ||
                (!TextUtils.isEmpty(dataPeople.last_name) && dataPeople.last_name.toLowerCase().contains(constraint.toLowerCase()));
    }

    public int getSelectedCount() {
        return mSelectedIds.size();
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
        protected BaseTextView bottomText;
        protected LinearLayout rightPart;
        protected LinearLayout rightPartHolder;
        protected RelativeLayout loader;

    }

    public interface IconListener {
        void onIconClick(DataPeople user, int position);
    }

    public interface PeopleUsersAdapterCallback {
        void onLimitExeeded();

        void onSelectionMade(int selectionCount);

        boolean canProceed();
    }
}
