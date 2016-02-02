package com.everymatch.saas.ui.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.util.EMLog;

/**
 * Created by PopApp_laptop on 26/11/2015.
 */
public class NetworkErrorMessageDialog extends DialogFragment {
    public static final String TAG = "NetworkErrorMessageDialog";

    public static final String ACTION_NETWORK_ERROR = "action.network.error";
    public static final String EXTRA_NETWORK_ERROR_TITLE = "action.network.error.title";
    public static final String ARG_MESSAGE = "arg.message";
    public static final String ARG_TITLE = "arg.title";

    private String title;
    private String message;

    public static boolean isShowing = false;

    //Views
    TextView tvTitle, tvMessage, tvClose;

    public static void start(FragmentManager fragmentManager, String message) {
        try {
            NetworkErrorMessageDialog networkErrorMessageDialog = NetworkErrorMessageDialog.getInstance(message);
            networkErrorMessageDialog.show(fragmentManager, "");
        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }
    }

    public static NetworkErrorMessageDialog getInstance(String message) {
        NetworkErrorMessageDialog answer = new NetworkErrorMessageDialog();
        Bundle bundle = new Bundle(2);
        bundle.putString(ARG_MESSAGE, message);
        bundle.putString(ARG_TITLE, DataManager.getInstance().getResourceText(R.string.Error));
        answer.setArguments(bundle);
        return answer;
    }

    public static NetworkErrorMessageDialog getInstance(String message, String title) {
        NetworkErrorMessageDialog answer = new NetworkErrorMessageDialog();
        Bundle bundle = new Bundle(2);
        bundle.putString(ARG_MESSAGE, message);
        bundle.putString(ARG_TITLE, title);
        answer.setArguments(bundle);
        return answer;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isShowing = true;
        message = getArguments().getString(ARG_MESSAGE);
        title = getArguments().getString(ARG_TITLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_network_error_message, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        isShowing = false;
    }

    private void init(View v) {
        tvClose = (TextView) v.findViewById(R.id.tvClose);
        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvMessage = (TextView) v.findViewById(R.id.tvMessage);

        tvTitle.setText(title);
        tvMessage.setText(message);

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowing = false;
                dismiss();
            }
        });
    }
}
