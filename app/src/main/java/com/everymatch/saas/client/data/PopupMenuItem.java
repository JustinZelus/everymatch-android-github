package com.everymatch.saas.client.data;

/**
 * Created by dors on 7/26/15.
 */
public class PopupMenuItem {
    public String icon;
    public String title;
    public String badge;

    public PopupMenuItem(String title, String icon) {
        this.icon = icon;
        this.title = title;
    }

    public PopupMenuItem(String title, String icon, String badge) {
        this(title, icon);
        this.badge = badge;
    }
}
