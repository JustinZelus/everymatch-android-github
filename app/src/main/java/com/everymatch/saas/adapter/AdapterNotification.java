package com.everymatch.saas.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataNotifications;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseIconTextView;
import com.ocpsoft.pretty.time.PrettyTime;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 09/09/2015.
 */
public class AdapterNotification extends EmBaseAdapter<DataNotifications> {
    Context con;
    LayoutInflater inflater;

    public AdapterNotification(ArrayList<DataNotifications> data, Context con) {
        this.mData = data;
        this.con = con;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    protected View getFinalView(int position, View convertView, ViewGroup parent) {
        DataNotifications item = getItem(position);

        View v = convertView;
        if (convertView == null)
            v = inflater.inflate(R.layout.view_notification, null);

        RelativeLayout rlHolder = (RelativeLayout) v.findViewById(R.id.rlNotificationHolder);
        TextView tvAgo = (TextView) v.findViewById(R.id.tvAgo);
        TextView tvContent = (TextView) v.findViewById(R.id.tvNotificationMessage);

        ImageView imgUser = (ImageView) v.findViewById(R.id.imgNotification);
        BaseIconTextView tvStatus = (BaseIconTextView) v.findViewById(R.id.tvNotificationStatus);

        rlHolder.setBackgroundColor(item.read ? Color.WHITE : ColorUtils.setAlphaComponent(DataStore.getInstance().getIntColor(EMColor.PRIMARY), (int) (255 * 0.3)));

        switch (item.status) {
            case "invited":
                tvStatus.setText(Consts.Icons.icon_StatusInvited);
                tvStatus.setTextColor(Color.parseColor("#A8DE00"));
                break;
            case "info":
                tvStatus.setText(Consts.Icons.icon_StatusMaybe);
                tvStatus.setTextColor(Color.parseColor("#A8DE00"));
                break;
            case "pending":
                tvStatus.setText(Consts.Icons.icon_StatusMaybe);
                tvStatus.setTextColor(Color.parseColor("#A8DE00"));
                break;
            case "maybe":
                tvStatus.setText(Consts.Icons.icon_StatusMaybe);
                tvStatus.setTextColor(Color.parseColor("#A8DE00"));
                break;
        }

        if (!TextUtils.isEmpty(item.image_url)) {
            Picasso.with(con).
                    load(Utils.getImageUrl(item.image_url, 50, 50))
                    .placeholder(R.drawable.img_user)
                    .into(imgUser);
        }
        try {
            PrettyTime p = new PrettyTime();
            String t = (p.format(Utils.getDateDromDataDate(item.updated_date)));
            tvAgo.setText(t);
            if (item.message != null)
                tvContent.setText(Html.fromHtml(item.message));
        } catch (Exception ex) {
        }
        return v;
    }

}
