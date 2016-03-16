package com.everymatch.saas.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataHelper;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.client.data.PopupMenuItem;
import com.everymatch.saas.server.Data.DataConversation;
import com.everymatch.saas.server.Data.DataParticipant;
import com.everymatch.saas.server.Data.DataReadBy;
import com.everymatch.saas.ui.discover.DiscoverFragment;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseIconTextView;
import com.everymatch.saas.view.BaseTextView;
import com.ocpsoft.pretty.time.PrettyTime;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PopApp_laptop on 09/08/2015.
 */
public class AdapterConversations extends EmBaseAdapter<DataConversation> {

    private DataStore ds = DataStore.getInstance();
    //public ArrayList<DataConversation> data, filtered;
    private String conversationType = "Active";
    Context con;
    LayoutInflater inflater;
    private ListPopupWindow mMorePopup;
    private List<PopupMenuItem> mMoreData;
    private inboxCallback callback;

    public AdapterConversations(ArrayList<DataConversation> data, Context con, inboxCallback callback) {
        this.mData = data;
        //filtered = new ArrayList<>();
        this.con = con;
        this.callback = callback;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /*@Override
    public int getCount() {
        return filtered.size();
    }*/

    /*@Override
    public Object getItem(int i) {
        return filtered.get(i);
    }*/


    @Override
    protected View getFinalView(int position, View convertView, ViewGroup parent) {
        final DataConversation item = getItem(position);

        View v = convertView;
        if (convertView == null)
            v = inflater.inflate(R.layout.view_conversation_item, null);
        setView(v, item);
        return v;
    }

    /* @Override
     public View getView(final int i, View view, ViewGroup viewGroup) {

     }
     */
    private void setView(View v, final DataConversation item) {
        ImageView imgUserImage = (ImageView) v.findViewById(R.id.img_view_conversation_image);
        BaseTextView tvUser = (BaseTextView) v.findViewById(R.id.tv_view_conversation_username);
        BaseTextView tvContent = (BaseTextView) v.findViewById(R.id.tv_view_conversation_content);

        BaseTextView tvAgo = (BaseTextView) v.findViewById(R.id.tv_view_conversation_service_ago);
        final BaseIconTextView iconMore = (BaseIconTextView) v.findViewById(R.id.tv_view_conversation_iconMore);
        iconMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMorePopup != null && mMorePopup.isShowing()) {
                    mMorePopup.dismiss();
                    return;
                }
                //if (mMoreData == null) {
                mMoreData = DataHelper.createInboxMenuItems(item);
                //}

                if (mMorePopup == null) {
                    mMorePopup = new ListPopupWindow(con);
                }

                mMorePopup.setAnchorView(view);
                mMorePopup.setAdapter(new DiscoverMoreAdapter(mMoreData));
                mMorePopup.setWidth(Utils.dpToPx(DiscoverFragment.MORE_MENU_WIDTH));
                mMorePopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 2) {
                            //if (callback != null) callback.onReplyClick(item);
                        } else if (position == 0) {
                            if (callback != null) {
                                if (item.status.toLowerCase().equals("active"))
                                    callback.onArchiveClick(item);
                                else if (item.status.toLowerCase().equals("archive"))
                                    callback.onUnArchiveClick(item);
                            }
                        } else if (position == 1) {
                            if (callback != null) callback.onDeleteClick(item);
                        }
                        mMorePopup.dismiss();
                    }
                });

                mMorePopup.setModal(true);
                mMorePopup.show();
            }
        });
        String userId = DataStore.getInstance().getUser().users_id;

        if (item.getLast_message().sender.equals(userId)) {
            // the last message is me!
            DataParticipant participant = null;
            for (DataParticipant participantTmp : item.participants) {
                if (!participantTmp.users_id.equals(userId)) {
                    participant = participantTmp;
                    break;
                }
            }
            // now we've got the participant!
            //set the name
            if (participant != null) {
                tvUser.setText(participant.first_name + " " + participant.last_name);
            }

            //set userImage
            final String urlString = "https://cdn2.everymatch.com/remote/emprod.everymatchintern.netdna-cdn.com/memberprofilepictures/" + userId + "/Current/L." + userId + ".jpg?width=200&height=200&format=jpg";
            try {
                Picasso.with(con)
                        .load(item.getLast_message().image_url)
                                //.placeholder(R.drawable.ic_placeholder) // optional
                                //.error(R.drawable.ic_error_fallback)         // optional
                        .into(imgUserImage);
            } catch (Exception ex) {
            }

        } else {
            try {
                Picasso.with(con)
                        .load(item.getLast_message().image_url)
                                //.placeholder(R.drawable.ic_placeholder) // optional
                                //.error(R.drawable.ic_error_fallback)         // optional
                        .into(imgUserImage);
            } catch (Exception ex) {
            }
            // the last message is not me!
            final String urlString = "https://cdn2.everymatch.com/remote/emprod.everymatchintern.netdna-cdn.com/memberprofilepictures/" + item.getLast_message().sender + "/Current/L." + item.getLast_message().sender + ".jpg?width=200&height=200&format=jpg";

            //item.last_message.
        }

        tvContent.setText(item.getLast_message().message);
        PrettyTime p = new PrettyTime();

        boolean isRead = false;
        try {
            for (DataReadBy dataReadBy : item.getLast_message().getRead_by()) {
                if (dataReadBy.users_id.equals(ds.getUser().users_id)) {
                    isRead = true;
                    break;
                }
            }
        } catch (Exception ex) {
        }
        RelativeLayout rlHolder = (RelativeLayout) v.findViewById(R.id.rlConversationHolder);
        rlHolder.setBackgroundColor(isRead ? Color.WHITE : ColorUtils.setAlphaComponent(ds.getIntColor(EMColor.PRIMARY), (int) (255 * 0.3)));

        String t = (p.format(Utils.getDateDromDataDate(item.getLast_message().getUpdated_date())));
        tvAgo.setText(t);
    }

    public void add(DataConversation dataConversation) {
        mData.add(dataConversation);
        refresh(conversationType);
    }

    public void add(List<DataConversation> data) {
        data.addAll(data);
        refresh(conversationType);
    }

    public void refresh(String conversationType) {
        String realFilter = "";
        if (conversationType.equals(DataManager.getInstance().getResourceText(R.string.Archive)))
            realFilter = "Archive";
        else if (conversationType.equals(DataManager.getInstance().getResourceText(R.string.Inbox_title)))
            realFilter = "Active";

        this.conversationType = realFilter;

        /*filtered.clear();
        for (DataConversation conversation : data) {
            if (conversation.status.toLowerCase().equals(this.conversationType.toLowerCase()))
                filtered.add(conversation);
        }*/

        notifyDataSetChanged();
    }

    public interface inboxCallback {
        void onReplyClick(DataConversation dataConversation);

        void onArchiveClick(DataConversation dataConversation);

        void onDeleteClick(DataConversation dataConversation);

        void onUnArchiveClick(DataConversation dataConversation);
    }

    @Override
    public boolean filterObject(DataConversation dataConversation, String constraint) {
        try {
            return dataConversation.getLast_message().message.toLowerCase().contains(constraint.toLowerCase());
            // ||dataConversation.p.toLowerCase().contains(constraint.toLowerCase());
        } catch (Exception ex) {
            return false;
        }


    }
}

