package com.everymatch.saas.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.util.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by dors on 7/20/15.
 */
public class EventCarouselAdapter extends BaseRecyclerViewAdapter<EventCarouselAdapter.EventViewHolder> {

    private ArrayList<DataEvent> mEvents;
    private int imageWidth;
    private int imageHeight;
    Context context;

    public EventCarouselAdapter(Context context, ArrayList<DataEvent> events) {
        this.mEvents = events;
        this.context = context;
    }

    /**
     * View holder
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView subTitle;
        public TextView participants;
        public TextView match;
        public ImageView image;
        public ImageView imagePlaceHolder;
        public RelativeLayout rlHolder;

        public EventViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.view_event_text_title);
            subTitle = (TextView) v.findViewById(R.id.view_event_text_sub_title);
            participants = (TextView) v.findViewById(R.id.view_event_text_participants);
            match = (TextView) v.findViewById(R.id.view_event_text_match);
            image = (ImageView) v.findViewById(R.id.view_event_image);
            imagePlaceHolder = (ImageView) v.findViewById(R.id.view_event_image_placeholder);
            rlHolder = (RelativeLayout) v.findViewById(R.id.rlEventHolder);
        }
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_event, viewGroup, false);
        if (getItemCount() == 1 ) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            int width = display.getWidth();
            View holder = v.findViewById(R.id.rlEventHolder);
            holder.measure(0, 0);
            width -= Utils.dpToPx(20);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.getLayoutParams();
            params.width = width;
            holder.setLayoutParams(params);
            holder.requestLayout();

            imageWidth = width;
            imageHeight = v.getLayoutParams().height;

        } else {
            imageWidth = v.getLayoutParams().width;
            imageHeight = v.getLayoutParams().height;
        }
        EventViewHolder eventViewHolder = new EventViewHolder(v);
        eventViewHolder.imagePlaceHolder.setBackgroundDrawable(DataManager.getInstance().getEventDrawable());
        return eventViewHolder;
    }

    @Override
    public void onBindViewHolder(EventCarouselAdapter.EventViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);

        DataEvent event = mEvents.get(position);

        if (event != null && event.dataPublicEvent != null) {
            viewHolder.title.setText(event.dataPublicEvent.event_title);
            viewHolder.subTitle.setText(Utils.getEventDate(event.dataPublicEvent.schedule.from));

            // Unlimited
            if (event.dataPublicEvent.spots == -1) {
                viewHolder.participants.setText(event.dataPublicEvent.user_count + " " + DataManager.getInstance().getResourceText(R.string.Participants));
            } else {
                viewHolder.participants.setText(event.dataPublicEvent.user_count + "/" + event.dataPublicEvent.spots + " " + DataManager.getInstance().getResourceText(R.string.Participants));
            }

            //viewHolder.match.setText(event.dataPublicEvent.match + "%");

            if (!TextUtils.isEmpty(event.dataPublicEvent.image_url)) {
                Picasso.with(viewHolder.image.getContext()).load(Utils.getImageUrl
                        (event.dataPublicEvent.image_url, imageWidth, imageHeight, "crop")).into(viewHolder.image);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mEvents == null ? 0 : (mEvents.size() > 10 ? 10 : mEvents.size());
    }
}
