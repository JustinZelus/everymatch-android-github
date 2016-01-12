package com.everymatch.saas.client.data;

import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.singeltones.GenericCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dors on 7/26/15.
 */
public class DataHelper {

    public static final String MENU_MESSAGES_ICON = "Mail";
    public static final String MENU_PROFILE_ICON = "Me";

    private static DataManager dm = DataManager.getInstance();

    public static List<PopupMenuItem> createDiscoverMenuItems() {
        List<PopupMenuItem> items = new ArrayList<>();
        int unread = DataStore.getInstance().getUser().getInbox().getUnread();
        items.add(new PopupMenuItem(dm.getResourceText(R.string.Inbox_Title), MENU_MESSAGES_ICON, unread <= 0 ? null : "" + unread));
        items.add(new PopupMenuItem(dm.getResourceText(R.string.Me), MENU_PROFILE_ICON));
        return items;
    }

    public static List<PopupMenuItem> createInboxMenuItems() {
        List<PopupMenuItem> items = new ArrayList<>();
        items.add(new PopupMenuItem(dm.getResourceText(R.string.Reply), null));
        items.add(new PopupMenuItem(dm.getResourceText(R.string.Archive), null));
        items.add(new PopupMenuItem(EverymatchApplication.getContext().getResources().getString(R.string.report_abuse), null));

        return items;
    }

    public static List<PopupMenuItem> createEventMenuItems(List<PopupMenuItem> items) {
        return items;
    }


    // use this method to call getApplication every time - it might change!
    public static void getApplication(GenericCallback callback) {
        //TODO  - implement this madafaca
    }

}
