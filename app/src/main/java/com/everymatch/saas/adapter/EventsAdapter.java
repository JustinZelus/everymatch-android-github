package com.everymatch.saas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseIconTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 09/09/2015.
 */
public class EventsAdapter extends EmBaseAdapter<DataEvent> {

    private LayoutInflater inflater;
    private int imageWidth;

    public static final int TYPE_STATUS = 1;
    public static final int TYPE_MATCH = 2;

    private int showType;

    public EventsAdapter(ArrayList<DataEvent> data, Context con, int type) {
        mData = data;
        this.mContext = con;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        showType = type;

        // Width of screen minus left and right padding
        imageWidth = con.getResources().getDisplayMetrics().widthPixels - con.getResources().getDimensionPixelSize(R.dimen.margin_s);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getFinalView(int i, View view, ViewGroup viewGroup) {

        DataEvent event = getItem(i);

        if (view == null) {
            view = inflater.inflate(R.layout.view_event, viewGroup, false);
            view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;

            ImageView imagePlaceHolder = (ImageView) view.findViewById(R.id.view_event_image_placeholder);
            imagePlaceHolder.setBackgroundDrawable(DataManager.getInstance().getEventDrawable());
        }

        TextView title = (TextView) view.findViewById(R.id.view_event_text_title);
        TextView subTitle = (TextView) view.findViewById(R.id.view_event_text_sub_title);
        TextView participants = (TextView) view.findViewById(R.id.view_event_text_participants);
        TextView match = (TextView) view.findViewById(R.id.view_event_text_match);
        BaseIconTextView matchIcon = (BaseIconTextView) view.findViewById(R.id.view_event_tex);

        ImageView image = (ImageView) view.findViewById(R.id.view_event_image);

        if (event != null && event.dataPublicEvent != null) {
            title.setText(event.dataPublicEvent.event_title);
            subTitle.setText(Utils.getEventDate(event.dataPublicEvent.schedule.from));

            int participantsResId;

            // Unlimited
            if (event.dataPublicEvent.spots == -1) {
                participants.setText(event.dataPublicEvent.user_count + " " + DataManager.getInstance().getResourceText(R.string.Participants));
            } else {
                participants.setText(event.dataPublicEvent.user_count + "/" + event.dataPublicEvent.spots + " " +
                        DataManager.getInstance().getResourceText(R.string.Participants));
            }

            switch (showType) {
                case TYPE_MATCH:
                    match.setText(event.dataPublicEvent.match + "%");
                    break;

                case TYPE_STATUS:
                    match.setText(event.dataPublicEvent.user_event_status.status.toString().toUpperCase());
                    break;
            }
            matchIcon.setVisibility(showType == TYPE_MATCH ? View.VISIBLE : View.INVISIBLE);


            try {
                Picasso.with(mContext).load(Utils.getImageUrl(
                        event.dataPublicEvent.image_url, imageWidth, image.getLayoutParams().height, "crop")).into(image);
            } catch (Exception ex) {
                image.setImageDrawable(null);
            }
        }
        return view;
    }

    @Override
    public boolean filterObject(DataEvent dataEvent, String constraint) {
        return dataEvent.dataPublicEvent.event_title.toLowerCase().contains(constraint);
    }
}
