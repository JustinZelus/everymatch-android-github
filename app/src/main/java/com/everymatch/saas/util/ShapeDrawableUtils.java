package com.everymatch.saas.util;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.graphics.ColorUtils;

import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;

/**
 * Created by PopApp_laptop on 15/11/2015.
 */
public class ShapeDrawableUtils {

    public static Drawable getRoundendButton() {
        return getRoundendButton(DataStore.getInstance().getIntColor(EMColor.PRIMARY));
    }

    public static Drawable getRoundendButton(int color) {
        return getRoundendButton(color, Utils.dpToPx(4), -1, -1);
    }

    public static Drawable getRoundendButton(int color, int radius) {
        return getRoundendButton(color, radius, -1, -1);
    }

    public static Drawable getRoundendButton(int color, int radius, int strokeWidth, int strokeColor) {
        GradientDrawable answer = new GradientDrawable();
        answer.setColor(color);
        answer.setCornerRadius(radius);

        if (strokeWidth != -1 && strokeColor != -1) {
            answer.setStroke(strokeWidth, strokeColor);
        }

        return answer;
    }

    public static Drawable getButtonStroked(int color) {
        return getButtonStroked(color, Utils.dpToPx(1), Utils.dpToPx(4));
    }

    public static Drawable getButtonStroked(int color, int strokeWidth) {
        return getButtonStroked(color, strokeWidth, Utils.dpToPx(1));
    }

    public static Drawable getButtonStroked(int color, int strokeWidth, int radius) {
        GradientDrawable answer = new GradientDrawable();
        answer.setColor(Color.TRANSPARENT);
        answer.setCornerRadius(radius);
        answer.setStroke(strokeWidth, color);
        return answer;
    }

    public static Drawable getButtonStroked() {
        return getButtonStroked(DataStore.getInstance().getIntColor(EMColor.PRIMARY));
    }

    public static Drawable getMyChatMessage(boolean isMine, boolean isFirst) {
        DataStore ds = DataStore.getInstance();
        int radius = Utils.dpToPx(7);
        int color = ds.getIntColor(EMColor.PRIMARY);
        if (isMine)
            color = ColorUtils.setAlphaComponent(DataStore.getInstance().getIntColor(EMColor.PRIMARY), (int) (255 * 0.3));

        GradientDrawable answer = new GradientDrawable();
        answer.setColor(color);

        int topRight = (isMine && isFirst) ? 0 : radius;
        int topLeft = (!isMine && isFirst) ? 0 : radius;

        float[] radii = {topLeft, topLeft, topRight, topRight, radius, radius, radius, radius};
        answer.setCornerRadii(radii);

        return answer;
    }

    public static Drawable getBackground(int color, int topLeft, int topRight, int bottomRight, int bottomLeft) {

        GradientDrawable answer = new GradientDrawable();
        answer.setColor(color);
        float[] radii = {Utils.dpToPx(topLeft), Utils.dpToPx(topLeft), Utils.dpToPx(topRight), Utils.dpToPx(topRight), Utils.dpToPx(bottomRight), Utils.dpToPx(bottomRight), Utils.dpToPx(bottomLeft), Utils.dpToPx(bottomLeft)};
        answer.setCornerRadii(radii);

        return answer;
    }
}
