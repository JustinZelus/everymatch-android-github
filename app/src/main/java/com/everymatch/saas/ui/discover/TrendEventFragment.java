package com.everymatch.saas.ui.discover;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataPeople;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.ui.event.EventActivity;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.IconImageView;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by PopApp_laptop on 29/02/2016.
 */
public class TrendEventFragment extends BaseFragment implements View.OnClickListener {
    public static final String TAG = "TrendEventFragment";
    public static final String ARG_EVENT = "arg.event";

    //DATA
    private DataEvent mEvent;

    //VIEWS
    private ImageView imgBg, imgMap;
    private TextView tvTitle, tvAddress, tvDate, tvParticipantsCount;
    LinearLayout holder;
    LinearLayout participantHolder;
    IconImageView icon;

    public static TrendEventFragment getInstance(DataEvent mEvent) {
        TrendEventFragment answer = new TrendEventFragment();
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(ARG_EVENT, mEvent);
        answer.setArguments(bundle);
        return answer;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_EVENT))
            mEvent = (DataEvent) getArguments().getSerializable(ARG_EVENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trend_event, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgBg = (ImageView) view.findViewById(R.id.imgTrendEventBackground);
        imgMap = (ImageView) view.findViewById(R.id.imgMap);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvAddress = (TextView) view.findViewById(R.id.tvAddress);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvParticipantsCount = (TextView) view.findViewById(R.id.tvParticipantsCount);
        holder = (LinearLayout) view.findViewById(R.id.trendEventHolder);
        participantHolder = (LinearLayout) view.findViewById(R.id.participantsHolder);
        icon = (IconImageView) view.findViewById(R.id.icon);
        icon.setIconImage(mEvent.dataPublicEvent.getIcon());

        setImages();
        tvTitle.setText(mEvent.dataPublicEvent.event_title);
        tvAddress.setText(mEvent.getLocationText());
        tvDate.setText(Utils.getEventSchedule(mEvent));
        tvParticipantsCount.setText("" + mEvent.getTrend_users().size() + " " + dm.getResourceText(R.string.Participants));
        int width = Utils.dpToPx(40);
        int i = 0;
        for (DataPeople people : mEvent.getTrend_users()) {
            if (i >= 7) break;
            //participantHolder.addView();
            CircularImageView circularImageView = new CircularImageView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
            if (i++ > 0) params.setMargins(-Utils.dpToPx(12), 0, 0, 0);
            circularImageView.setLayoutParams(params);
            if (!TextUtils.isEmpty(people.image_url))
                Picasso.with(getActivity()).load(people.image_url).fit().into(circularImageView);

            participantHolder.addView(circularImageView);
        }


        holder.setOnClickListener(this);

    }

    private void setImages() {
        holder.post(new Runnable() {
            @Override
            public void run() {
                imgBg.measure(0, 0);
                imgMap.measure(0, 0);
                //make it square
                ViewGroup.LayoutParams params = holder.getLayoutParams();
                params.height = params.width;
                holder.setLayoutParams(params);

                if (!TextUtils.isEmpty(mEvent.dataPublicEvent.image_url)) {
                    Picasso.with(getActivity())
                            .load(mEvent.dataPublicEvent.image_url)
                            .fit()
                            .into(imgBg);
                }

                if (!TextUtils.isEmpty(mEvent.map_image_url)) {
                    Picasso.with(getActivity())
                            .load(Utils.getImageUrl(mEvent.map_image_url, imgMap.getMeasuredWidth(), imgMap.getMeasuredWidth(), "max"))
                            .into(imgMap);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trendEventHolder:
                EventActivity.startActivity(getActivity(), mEvent);
                break;
        }
    }
}
