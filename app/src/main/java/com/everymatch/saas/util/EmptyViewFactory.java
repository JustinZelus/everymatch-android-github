package com.everymatch.saas.util;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.singeltones.Consts;

/**
 * Created by dors on 11/30/15.
 */
public class EmptyViewFactory {

    public static final int TYPE_PEOPLE = 1;
    public static final int TYPE_EVENTS = 2;
    public static final int TYPE_DISCOVER = 3;
    public static final int TYPE_MESSAGES = 4;
    public static final int TYPE_NOTIFICATIONS = 5;

    /**
     * Cals
     * @param type
     * @return
     */
    public static View createEmptyView(int type){
        return createEmptyView(type, null);
    }

    /**
     * Create and return empty view by given type
     *
     * @param buttonListener listener for button clicks
     */
    public static View createEmptyView(int type, final ButtonListener buttonListener) {

        final View view = LayoutInflater.from(EverymatchApplication.getContext()).inflate(R.layout.view_empty, null);
        final TextView title = (TextView) view.findViewById(R.id.view_empty_text_title);
        final TextView summary = (TextView) view.findViewById(R.id.view_empty_text_summary);
        final TextView icon = (TextView) view.findViewById(R.id.view_empty_image_icon);
        final Button firstButton = (Button) view.findViewById(R.id.view_empty_button_first);
        final Button secondButton = (Button) view.findViewById(R.id.view_empty_button_second);

        DataManager dm = DataManager.getInstance();

        switch (type) {
            case TYPE_PEOPLE:
                title.setText(dm.getResourceText(R.string.No_people));
                summary.setText(dm.getResourceText(R.string.No_people_subtitle));
                icon.setText(Consts.Icons.icon_People);
                firstButton.setText(dm.getResourceText(R.string.Discover));
                secondButton.setVisibility(View.INVISIBLE);
                break;

            case TYPE_EVENTS:
                title.setText(dm.getResourceText(R.string.No_Events));
                summary.setText(dm.getResourceText(R.string.No_Events_Subtitle));
                icon.setText(Consts.Icons.icon_Event);
                firstButton.setText(dm.getResourceText(R.string.Discover));
                secondButton.setText(dm.getResourceText(R.string.Create_Event));
                break;

            case TYPE_DISCOVER:
                title.setText(dm.getResourceText(R.string.No_suggestion));
                summary.setText(dm.getResourceText(R.string.No_suggestion_subtitle));
                icon.setText(Consts.Icons.icon_Discover);
                firstButton.setText(dm.getResourceText(R.string.Edit_Profile));
                secondButton.setText(dm.getResourceText(R.string.Create_Your_Event));
                break;

            case TYPE_MESSAGES:
                title.setText(dm.getResourceText(R.string.No_messages));
                summary.setText(dm.getResourceText(R.string.No_messages_subtitle));
                icon.setText(Consts.Icons.icon_Chat);
                firstButton.setVisibility(View.INVISIBLE);
                secondButton.setVisibility(View.INVISIBLE);
                break;

            case TYPE_NOTIFICATIONS:
                title.setText(dm.getResourceText(R.string.No_notifications));
                summary.setText(dm.getResourceText(R.string.No_notifications_subtitle));
                icon.setText(Consts.Icons.icon_NotificationFull);
                firstButton.setVisibility(View.INVISIBLE);
                secondButton.setVisibility(View.INVISIBLE);
                break;

            default:
                view.setVisibility(View.GONE);
        }

        // In order to make sure the size of the font equals to the size of the view
        icon.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                icon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Utils.pxToDp(v.getMeasuredHeight()));
            }
        });

        // Set button listeners
        if (buttonListener != null){
            firstButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonListener.onEmptyViewFirstButtonClick();
                }
            });

            secondButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonListener.onEmptyViewSecondButtonClick();
                }
            });
        }

        return view;
    }

    public interface ButtonListener {
        void onEmptyViewFirstButtonClick();

        void onEmptyViewSecondButtonClick();
    }
}
