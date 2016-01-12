package com.everymatch.saas.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.everymatch.saas.R;
import com.everymatch.saas.server.Data.DataModelHolder;
import com.everymatch.saas.util.Utils;
import com.squareup.picasso.Picasso;

/**
 * Created by PopApp_laptop on 15/11/2015.
 */
public class ViewDiscoverAd extends LinearLayout implements View.OnClickListener {

    private final Context mContext;
    BaseTextView tvTitle, tvSubTitle;
    BaseImageView img;

    public ViewDiscoverAd(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.view_discover_ad, this);
        tvTitle = (BaseTextView) findViewById(R.id.tvAdTitle);
        img = (BaseImageView) findViewById(R.id.imgAd);

        img.setOnClickListener(this);
    }

    public void setModel(final DataModelHolder.DataModel dataModel) {
        tvTitle.setText(dataModel.text_title);
        if (!TextUtils.isEmpty(dataModel.background_image))
            Picasso.with(mContext)
                    .load(Utils.getImageUrl(dataModel.background_image,getResources().getDisplayMetrics().widthPixels, 0))
                    .into(img);
        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(dataModel.text_url)) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(dataModel.text_url));
                    mContext.startActivity(i);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
