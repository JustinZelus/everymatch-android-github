package com.everymatch.saas.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.singeltones.YesNoCallback;
import com.everymatch.saas.util.ShapeDrawableUtils;
import com.everymatch.saas.view.BaseButton;
import com.everymatch.saas.view.BaseTextView;

/**
 * Created by PopApp_laptop on 02/12/2015.
 */
public class DialogYesNo extends Dialog implements View.OnClickListener {

    String title;
    String message;
    String negativeText;
    String positiveText;

    BaseButton btnNegative, btnPositive;
    BaseTextView tvTitle, tvMessage;
    YesNoCallback callback;

    public DialogYesNo(Context context, String title, String message, YesNoCallback callback) {
        this(context, title, message, context.getString(R.string.No), context.getString(R.string.Yes), callback);
    }


    public DialogYesNo(Context context, String title, String message, String negativeText, String positiveText, YesNoCallback callback) {
        super(context);
        this.title = title;
        this.message = message;
        this.negativeText = negativeText;
        this.positiveText = positiveText;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //getWindow.sets(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);
        setContentView(R.layout.dialog_yes_no);
        init();
        setBackground();
    }

    private void setBackground() {
        int radius = 14;
        Drawable bgWrapper = ShapeDrawableUtils.getBackground(DataStore.getInstance().getIntColor(EMColor.WHITE), radius, radius, radius, radius);
        findViewById(R.id.wrapper).setBackgroundDrawable(bgWrapper);

        Drawable bgRight = ShapeDrawableUtils.getBackground(DataStore.getInstance().getIntColor(EMColor.WHITE), 0, 0, 0, radius);
        Drawable bgLeft = ShapeDrawableUtils.getBackground(DataStore.getInstance().getIntColor(EMColor.WHITE), 0, 0, radius, 0);

        btnPositive.setBackgroundDrawable(bgLeft);
        btnNegative.setBackgroundDrawable(bgRight);
    }

    private void init() {
        tvTitle = (BaseTextView) findViewById(R.id.tvTitle);
        tvMessage = (BaseTextView) findViewById(R.id.tvMessage);
        btnNegative = (BaseButton) findViewById(R.id.btnNegative);
        btnPositive = (BaseButton) findViewById(R.id.btnPositive);

        tvTitle.setText(title);
        tvMessage.setText(message);
        btnNegative.setText(negativeText);
        btnPositive.setText(positiveText);

        btnNegative.setOnClickListener(this);
        btnPositive.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPositive:
                if (callback != null)
                    callback.onYes();
                dismiss();
                break;

            case R.id.btnNegative:
                if (callback != null)
                    callback.onNo();
                dismiss();
                break;
        }
        //dismiss();
    }
}
