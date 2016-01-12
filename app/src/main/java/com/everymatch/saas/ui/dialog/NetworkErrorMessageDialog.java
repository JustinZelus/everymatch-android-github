package com.everymatch.saas.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;

/**
 * Created by PopApp_laptop on 26/11/2015.
 */
public class NetworkErrorMessageDialog extends Dialog {

    public static final String ACTION_NETWORK_ERROR = "action.network.error";
    public static final String ACTION_NETWORK_ERROR_TITLE = "action.network.error.title";


    private String title;
    private String message;

    //Views
    TextView tvTitle, tvMessage, tvClose;


    public NetworkErrorMessageDialog(Context context, String message) {
        this(context, DataManager.getInstance().getResourceText(R.string.Error), message);
    }

    public NetworkErrorMessageDialog(Context context, String title, String message) {
        super(context);
        this.title = title;
        this.message = message;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_network_error_message);
        init();
    }

    private void init() {
        tvClose = (TextView) findViewById(R.id.tvClose);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvMessage = (TextView) findViewById(R.id.tvMessage);

        tvTitle.setText(title);
        tvMessage.setText(message);

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
