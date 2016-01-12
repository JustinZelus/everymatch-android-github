package com.everymatch.saas.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataChatBlock;
import com.everymatch.saas.server.Data.DataChatMessage;
import com.everymatch.saas.server.Data.DataConversation;
import com.everymatch.saas.server.Data.DataDate;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestChatMessages;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseString;
import com.everymatch.saas.util.ShapeDrawableUtils;
import com.everymatch.saas.view.BaseTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

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

    public AdapterChatMessage(ArrayList<DataChatMessage> data, Context con, DataConversation conversation) {
        mData = data;
        this.conversation = conversation;
        this.con = con;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myUserId = ds.getUser().users_id;
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

        final String urlString = "https://cdn2.everymatch.com/remote/emprod.everymatchintern.netdna-cdn.com/memberprofilepictures/" + item.sender + "/Current/L." + item.sender + ".jpg?width=500&height=500&format=jpg";

        //this is my message
        if (item.sender.equals(myUserId)) {
            tvContent.setTextColor(ds.getIntColor(EMColor.NIGHT));
            //balloon.setBackgroundResource(R.drawable.bg_chat_sent_first);
            balloon.setBackgroundDrawable(ShapeDrawableUtils.getMyChatMessage(true, true));

            // is this the same sender that have sent more then one message???
            if (i > 0 && mData.get(i - 1).sender.equals(item.sender)) {
                imgUserImage.setVisibility(View.INVISIBLE);
                //balloon.setBackgroundResource(R.drawable.bg_chat_sent);
                balloon.setBackgroundDrawable(ShapeDrawableUtils.getMyChatMessage(true, false));


            } else {
                imgUserImage.setVisibility(View.VISIBLE);
                Picasso.with(imgUserImage.getContext())
                        .load(urlString)
                                //.placeholder(DataManager.getInstance().getAvatarDrawable())
                        .placeholder(R.drawable.picasso_loader)
                        .into(imgUserImage);
            }

            //hide other user image
            imgOtherUserImage.setVisibility(View.INVISIBLE);

            //other user message
        } else {
            tvContent.setTextColor(ds.getIntColor(EMColor.WHITE));
            //balloon.setBackgroundResource(R.drawable.bg_chat_received_first);
            balloon.setBackgroundDrawable(ShapeDrawableUtils.getMyChatMessage(false, true));


            // is this the same sender?
            if (i > 1 && mData.get(i - 1).sender.equals(item.sender)) {
                imgOtherUserImage.setVisibility(View.INVISIBLE);
                //balloon.setBackgroundResource(R.drawable.bg_chat_received);
                balloon.setBackgroundDrawable(ShapeDrawableUtils.getMyChatMessage(false, false));
            } else {
                // load other user image
                imgOtherUserImage.setVisibility(View.VISIBLE);
                Picasso.with(imgOtherUserImage.getContext())
                        .load(urlString)
                                //.placeholder(DataManager.getInstance().getAvatarDrawable())
                        .placeholder(R.drawable.picasso_loader)
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

        if (i > 1 && !item.updated_date.isSameDay(mData.get(i - 1).updated_date)) {
            rlTime.setVisibility(View.VISIBLE);
            TextView tvTime = (TextView) v.findViewById(R.id.tvViewChatTime);
            tvTime.setText("" + item.updated_date.day + "/" + item.updated_date.month + "/" + item.updated_date.year);
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

    private void loadMoreMessages() {
        flag_loading = true;
        ServerConnector.getInstance().processRequest(new RequestChatMessages(conversation._id, -1/*not really metter*/, ((mData.size() - 1) * -1) - 10), new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                try {
                    JSONObject jsonObject = new JSONObject(((ResponseString) baseResponse).responseStr);
                    DataChatBlock dataChatBlock = new DataChatBlock(jsonObject);
                    addMessages(dataChatBlock.messages);
                    flag_loading = false;
                } catch (Exception ex) {
                    Log.i("parse Error", ex.getMessage());
                }
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                //Toast.makeText(getActivity(), errorResponse.getServerRawResponse(), Toast.LENGTH_SHORT).show();
            }
        });
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
