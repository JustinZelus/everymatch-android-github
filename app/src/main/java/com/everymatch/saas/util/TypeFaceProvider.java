package com.everymatch.saas.util;

import android.graphics.Typeface;
import android.widget.TextView;

import com.everymatch.saas.EverymatchApplication;

import java.util.Hashtable;

/**
 * Created by Lior Iluz on 20/02/14.
 * Hold the fonts in memory so they won't get allocated each time
 * they need to be used.
 */
public class TypeFaceProvider {


    public static final String FONT_LATO= "fonts/Lato-Regular.ttf";
    public static final String FONT_LATO_LIGHT= "fonts/Lato-Light.ttf";
    public static final String FONT_ICOMOON= "fonts/icomoon.ttf";

    private static Hashtable<String, Typeface> sTypeFaces = new Hashtable<>(3);

    public static Typeface getTypeFace(String fontPath) {
      //  fontPath = FONT_FS_SINCLAIR_LIGHT_ITALIC;
        Typeface selectedTypeFace = sTypeFaces.get(fontPath);

        if (selectedTypeFace == null) {
            selectedTypeFace = Typeface.createFromAsset(EverymatchApplication.getContext().getAssets(), fontPath);
            sTypeFaces.put(fontPath, selectedTypeFace);
        }
        return selectedTypeFace;
    }

    public static void initByType(int font, TextView absTextView) {

        switch (font) {
            case 1:
                absTextView.setTypeface(TypeFaceProvider.getTypeFace(FONT_LATO_LIGHT));
                break;
            default:
                absTextView.setTypeface(TypeFaceProvider.getTypeFace(FONT_LATO));
                break;
        }
    }
}
