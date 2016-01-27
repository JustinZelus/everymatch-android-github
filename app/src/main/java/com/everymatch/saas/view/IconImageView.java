package com.everymatch.saas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.IconType;
import com.everymatch.saas.server.Data.DataIcon;
import com.everymatch.saas.util.IconManager;
import com.everymatch.saas.util.Utils;
import com.squareup.picasso.Picasso;

/**
 * Created by dors on 11/30/15.
 */
public class IconImageView extends FrameLayout {

    private BaseImageView mImage;
    private BaseIconTextView mIcon;

    public IconImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.icon_image_view, this, true);
        mIcon = (BaseIconTextView) findViewById(R.id.icon_image_view_icon);
        mImage = (BaseImageView) findViewById(R.id.icon_image_view_image);
        parseAttributes(attrs);
    }

    private void parseAttributes(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ResourceColor);
            if (typedArray != null) {
                int bgColor = typedArray.getInt(R.styleable.ResourceColor_bg_color, -1);

                if (bgColor != -1) {
                    mIcon.setBackgroundColor(Color.parseColor(DataStore.getInstance().getColor(bgColor)));
                }

                int textColor = typedArray.getInt(R.styleable.ResourceColor_text_color, -1);

                if (textColor != -1) {
                    mIcon.setTextColor(Color.parseColor(DataStore.getInstance().getColor(textColor)));
                }

                int textSize = (int) typedArray.getDimension(R.styleable.ResourceColor_text_size, -1);

                if (textSize != -1) {
                    mIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Utils.pxToDp(textSize));
                }

                typedArray.recycle();
            }
        }
    }

    public void setIconImage(DataIcon icon) {
        if (icon != null) {
            if (IconType.FONT.equals(icon.getType())) {
                mIcon.setVisibility(View.VISIBLE);
                mIcon.setText(IconManager.getInstance(getContext()).getIconString(icon.getValue()));
            } else {
                mImage.setVisibility(View.VISIBLE);
                Picasso.with(getContext()).load(icon.getValue()).into(mImage);
            }
        }
    }

    public void setIconImage(String icon, final String url) {
        if (TextUtils.isEmpty(url)) {
            mIcon.setVisibility(View.VISIBLE);
            mIcon.setText(IconManager.getInstance(getContext()).getIconString(icon));
        } else {
            mImage.setVisibility(View.VISIBLE);
            mImage.post(new Runnable() {
                @Override
                public void run() {
                    Picasso.with(getContext()).load(Utils.getImageUrl(url, mImage.getMeasuredWidth(), mImage.getMeasuredHeight()) + "&mode=max").into(mImage);
                }
            });
        }
    }

    public BaseIconTextView getIconTextView() {
        return mIcon;
    }
}
