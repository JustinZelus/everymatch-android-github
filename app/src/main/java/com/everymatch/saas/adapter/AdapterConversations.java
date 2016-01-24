package com.everymatch.saas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataHelper;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.PopupMenuItem;
import com.everymatch.saas.server.Data.DataConversation;
import com.everymatch.saas.server.Data.DataParticipant;
import com.everymatch.saas.ui.chat.ConversationsFragment;
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
public class AdapterConversations extends BaseAdapter {

    public ArrayList<DataConversation> data, filtered;
    private String conversationType = ConversationsFragment.CONVERSATION_TYPE_ACTIVE;
    Context con;
    LayoutInflater inflater;
    private ListPopupWindow mMorePopup;
    private List<PopupMenuItem> mMoreData;
    private inboxCallback callback;

    public AdapterConversations(ArrayList<DataConversation> data, Context con, inboxCallback callback) {
        this.data = data;
        filtered = new ArrayList<>();
        this.con = con;
        this.callback = callback;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return filtered.size();
    }

    @Override
    public Object getItem(int i) {
        return filtered.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        DataConversation item = filtered.get(i);

        View v = view;
        if (view == null)
            v = inflater.inflate(R.layout.view_conversation_item, null);

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
                if (mMoreData == null) {
                    mMoreData = DataHelper.createInboxMenuItems();
                }

                if (mMorePopup == null) {
                    mMorePopup = new ListPopupWindow(con);
                }

                mMorePopup.setAnchorView(view);
                mMorePopup.setAdapter(new DiscoverMoreAdapter(mMoreData));
                mMorePopup.setWidth(Utils.dpToPx(DiscoverFragment.MORE_MENU_WIDTH));
                mMorePopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            if (callback != null) callback.onReplyClick(i);
                        } else if (position == 1) {
                            if (callback != null) callback.onArchiveClick(i);
                        } else {
                            if (callback != null) callback.onReportClick(i);
                        }
                        mMorePopup.dismiss();
                    }
                });

                mMorePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        int abc = 3;
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
            final String urlString = "https://cdn2.everymatch.com/remote/emprod.everymatchintern.netdna-cdn.com/memberprofilepictures/" + userId + "/Current/L." + userId + ".jpg?width=500&height=500&format=jpg";
            Picasso.with(con)
                    .load(urlString)
                            //.placeholder(R.drawable.ic_placeholder) // optional
                            //.error(R.drawable.ic_error_fallback)         // optional
                    .into(imgUserImage);
        } else {
            // the last message is not me!
            final String urlString = "https://cdn2.everymatch.com/remote/emprod.everymatchintern.netdna-cdn.com/memberprofilepictures/" + item.getLast_message().sender + "/Current/L." + item.getLast_message().sender + ".jpg?width=500&height=500&format=jpg";
            Picasso.with(con)
                    .load(urlString)
                            //.placeholder(R.drawable.ic_placeholder) // optional
                            //.error(R.drawable.ic_error_fallback)         // optional
                    .into(imgUserImage);
            //item.last_message.
        }

        tvContent.setText(item.getLast_message().message);
        PrettyTime p = new PrettyTime();

        //String t = (p.format(getDate(item.last_message.updated_date)));
        String t = (p.format(Utils.getDateDromDataDate(item.getLast_message().updated_date)));
        tvAgo.setText(t);
        return v;
    }

    public void add(DataConversation dataConversation) {
        data.add(dataConversation);
        refresh(conversationType);
    }

    public void add(List<DataConversation> data) {
        data.addAll(data);
        refresh(conversationType);
    }

    public void refresh(String conversationType) {
        this.conversationType = conversationType;
        filtered.clear();
        for (DataConversation conversation : data) {
            if (conversation.status.equals(this.conversationType))
                filtered.add(conversation);
        }
        notifyDataSetChanged();
    }

    public interface inboxCallback {
        void onReplyClick(int pos);

        void onArchiveClick(int pos);

        void onReportClick(int pos);
    }
}

