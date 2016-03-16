package com.everymatch.saas.client.data;

/**
 * Created by dors on 7/26/15.
 */
public class PopupMenuItem {
    public String icon;
    public String title;
    public String badge;
    public int color;

    public PopupMenuItem(String title, String icon) {
        this(title, icon, null);
    }

    public PopupMenuItem(String title, String icon, String badge) {
        this(title, icon, null, 0);
    }

    public PopupMenuItem(String title, String icon, String badge, int color) {
        this.title = title;
        this.icon = icon;
        this.badge = badge;
        this.color = color;
    }
}
