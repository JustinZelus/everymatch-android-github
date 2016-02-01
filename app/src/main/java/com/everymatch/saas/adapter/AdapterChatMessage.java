package com.everymatch.saas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataChatMessage;
import com.everymatch.saas.server.Data.DataConversation;
import com.everymatch.saas.server.Data.DataDate;
import com.everymatch.saas.util.ShapeDrawableUtils;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by PopApp_laptop on 10/08/2015.
 */
public class AdapterChatMessage extends EmBaseAdapter<DataChatMessage> {
    //public ArrayList<DataChatMessage> data;
    Context con;
    LayoutInflater inflater;
    String myUserId;
    private boolean flag_loading;
    private DataConversation conversation;
    ChatCallback callback;
    DataStore ds = DataStore.getInstance();
    int mPictureSize;


    public AdapterChatMessage(ArrayList<DataChatMessage> data, Context con, DataConversation conversation) {
        mData = data;
        this.conversation = conversation;
        this.con = con;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myUserId = ds.getUser().users_id;
        mPictureSize = Utils.dpToPx(40);
    }

    @Override
    protected View getFinalView(int i, View convertView, ViewGroup parent) {
        View v = convertView;
        DataChatMessage item = getItem(i);
        // if (view == null)
        v = inflater.inflate(R.layout.view_chat_item, null);

        BaseTextView tvContent = (BaseTextView) v.findViewById(R.id.tv_view_chat_content);
        final ImageView imgUserImage = (ImageView) v.findViewById(R.id.imgChatMyUserImage);
        ImageView imgOtherUserImage = (ImageView) v.findViewById(R.id.imgChatOtherUserImage);
        RelativeLayout balloon = (RelativeLayout) v.findViewById(R.id.rl_view_chat_baloon);

        tvContent.setText(item.message);

        //this is my message
        if (item.sender.equals(myUserId)) {
            tvContent.setTextColor(ds.getIntColor(EMColor.NIGHT));
            balloon.setBackgroundDrawable(ShapeDrawableUtils.getMyChatMessage(true, true));

            // is this the same sender that have sent more then one message???
            if (i > 0 && mData.get(i - 1).sender.equals(item.sender)) {
                imgUserImage.setVisibility(View.INVISIBLE);
                balloon.setBackgroundDrawable(ShapeDrawableUtils.getMyChatMessage(true, false));


            } else {
                imgUserImage.setVisibility(View.VISIBLE);

                Picasso.with(con)
                        .load(item.image_url)
                        .placeholder(DataManager.getInstance().getAvatarDrawable())
                        .into(imgUserImage);
            }

            //hide other user image
            imgOtherUserImage.setVisibility(View.INVISIBLE);

            //other user message
        } else {
            tvContent.setTextColor(ds.getIntColor(EMColor.WHITE));
            balloon.setBackgroundDrawable(ShapeDrawableUtils.getMyChatMessage(false, true));


            // is this the same sender?
            if (i > 1 && mData.get(i - 1).sender.equals(item.sender)) {
                imgOtherUserImage.setVisibility(View.INVISIBLE);
                balloon.setBackgroundDrawable(ShapeDrawableUtils.getMyChatMessage(false, false));
            } else {
                // load other user image
                imgOtherUserImage.setVisibility(View.VISIBLE);


                Picasso.with(con)
                        .load(item.image_url)
                        .placeholder(DataManager.getInstance().getAvatarDrawable())
                        .into(imgOtherUserImage);
            }

            // hide left margin
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) imgUserImage.getLayoutParams();
            params.leftMargin = 0;
            imgUserImage.setLayoutParams(params);
            imgUserImage.getLayoutParams().width = 1;

        }

        //decide if to show time window
        RelativeLayout rlTime = (RelativeLayout) v.findViewById(R.id.rlViewChatTime);

        if (i > 1 && !item.getUpdated_date().isSameDay(mData.get(i - 1).getUpdated_date())) {
            rlTime.setVisibility(View.VISIBLE);
            TextView tvTime = (TextView) v.findViewById(R.id.tvViewChatTime);
            tvTime.setText("" + item.getUpdated_date().day + "/" + item.getUpdated_date().month + "/" + item.getUpdated_date().year);
        } else {
            rlTime.setVisibility(View.GONE);
        }

        return v;
    }

    /*
    we adding messages from the second position because the first is for load more messages
    * */
    public void addMessages(ArrayList<DataChatMessage> messages) {
        mData.addAll(0, messages);
        this.notifyDataSetChanged();
    }

    public void addMessages(DataChatMessage msg) {
        mData.add(msg);
        this.notifyDataSetChanged();
    }

    public static Date getDate(DataDate dataDate) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, dataDate.year);
        cal.set(Calendar.MONTH, dataDate.month);
        cal.set(Calendar.DAY_OF_MONTH, dataDate.day);
        cal.set(Calendar.HOUR_OF_DAY, dataDate.hour);
        cal.set(Calendar.MINUTE, dataDate.minute);
        cal.set(Calendar.SECOND, dataDate.second);
        return cal.getTime();
    }

    public void setListiner(ChatCallback callback) {
        this.callback = callback;
    }

    @Override
    public boolean filterObject(DataChatMessage dataChatMessage, String constraint) {
        return dataChatMessage.message.contains(constraint);
    }

    public interface ChatCallback {
        void onLoadMoreClicked();
    }
}
