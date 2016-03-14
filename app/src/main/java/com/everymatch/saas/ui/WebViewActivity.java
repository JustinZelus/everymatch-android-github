
package com.everymatch.saas.ui;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.MailTo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.everymatch.saas.R;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.view.EventHeader;

/**
 * A class that show a webview with a given URL (setted as intent parmater).
 */
public class WebViewActivity extends Activity //ActionBarActivity
{
    /*cookie for view's URL address*/
    public static final String EXTRA_VIEW_URL = "view_url";
    public static final String EXTRA_SCREEN_TITLE = "title";

    //public static final String EXTRA_RETURN_URL = "return_url";
    public static final String EXTRA_KEY = "key=";
    public static final String EXTRA_RETURN_DATA_NAME = "data";

    private static final String TAG = "WebViewActivity";
    public static final String EXTRA_VIEW_CONTENT = "extra_content";

    /**
     * loading gauge
     */
    private ProgressDialog mProgressDialog = null;

    /**
     * indicates if finish loading
     */
    private boolean mIsLoading = true;

    private WebView mWebView;

    private EventHeader mHeader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        final Bundle extras = getIntent().getExtras();
//        String screenTitle = extras.getString(EXTRA_SCREEN_TITLE);
//        if (TextUtils.isEmpty(screenTitle)) {
//            requestWindowFeature(Window.FEATURE_NO_TITLE);
//        }
//        else setTitle(screenTitle);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web);

        //create the web-view
        String url = extras.getString(EXTRA_VIEW_URL);
        mWebView = (WebView) findViewById(R.id.activity_web_view);

        String screenTitle = extras.getString(EXTRA_SCREEN_TITLE);

        if (!TextUtils.isEmpty(screenTitle)) {
            mHeader = (EventHeader) findViewById(R.id.activity_web_header);
            mHeader.setVisibility(View.VISIBLE);
            mHeader.getIconOne().setVisibility(View.GONE);
            mHeader.getIconTwo().setVisibility(View.GONE);
            mHeader.getIconThree().setVisibility(View.GONE);
            mHeader.setTitle(screenTitle);
            mHeader.getBackButton().setText(Consts.Icons.icon_Arrowback);
            mHeader.getBackButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            int margin = getResources().getDimensionPixelOffset(R.dimen.margin_s);
            ((LinearLayout.LayoutParams) mWebView.getLayoutParams()).bottomMargin = margin;
            ((LinearLayout.LayoutParams) mWebView.getLayoutParams()).rightMargin = margin;
            ((LinearLayout.LayoutParams) mWebView.getLayoutParams()).leftMargin = margin;
            ((LinearLayout.LayoutParams) mWebView.getLayoutParams()).topMargin = margin;

        }
        //web-view settings
//        mWebView.getSettings().setSupportZoom(true);
//        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        // added for embedded youtube support
        mWebView.getSettings().setPluginState(PluginState.ON);
        mWebView.setWebChromeClient(new WebChromeClient());

        //set a new web-view client
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                Log.i(TAG, "finish loading url: " + url);

                // hide loading gauge
                if (mProgressDialog != null)
                    mProgressDialog.dismiss();

                // mark that loading finished
                mIsLoading = false;

//                LogTag.i(TAG, "finish loading:", url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "shouldOverrideUrlLoading url: " + url);

                // check the url if we got what we need
                if (url.contains(EXTRA_KEY)) {
                    // if (url.startsWith(extras.getString(EXTRA_RETURN_URL))) {
                    Uri uri = Uri.parse(url);
                    String returnDataName = extras.getString(EXTRA_RETURN_DATA_NAME);
                    String valueToReturn = uri.getQueryParameter(returnDataName);

                    Intent intent = new Intent();
                    intent.putExtra(returnDataName, valueToReturn);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    return true;
                }
                // }
                if (url.startsWith("market://")) {
                    //this is a link to Android Market - launch an activity that handles such URLs
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                /**
                 *  @author or
                 */
                if (url.startsWith("mailto:")) {
                    MailTo mt = MailTo.parse(url);
                    Intent i = newEmailIntent(WebViewActivity.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                    startActivity(i);
                    view.reload();
                    return true;
                }

                //do not override, let the web-view load this page
                view.loadUrl(url);

                //return false so it will not be handled by default action
                return false;
            }

        });

        //set listener for download events
        mWebView.setDownloadListener(new DownloadListener() {
            //let the default "native" activity to handle the download process
            @Override
            public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        if (getIntent().hasExtra(EXTRA_VIEW_CONTENT)) {
            final String mimeType = "text/html";
            final String encoding = "UTF-8";
            mWebView.loadDataWithBaseURL("", getIntent().getStringExtra(EXTRA_VIEW_CONTENT), mimeType, encoding, "");
            return;
        }

        // load the web view, but first, check if network connection is needed, and if network connection is available
        boolean internetConnectionIsNeeded = (url.indexOf("file:///android_asset") < 0);
        if (internetConnectionIsNeeded && !isNetworkAvailable()) {
            mIsLoading = false;         // don't show loading gauge
//            setContentView(R.layout.no_internet_connection);
        } else {

            mWebView.loadUrl(url);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }


    @Override
    protected void onStart() {
        super.onStart();

        //show gauge
        if (mIsLoading)
            mProgressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.loading_web_page_message), false, true);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // free web view from memory
        mWebView.clearCache(true);
        mWebView.destroyDrawingCache();
        mWebView.destroy();
    }


    /**
     * @author or
     */
    @Override
    public void onBackPressed() {

        if (mWebView.isFocused() && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    /**
     * @param context
     * @param address
     * @param subject
     * @param body
     * @param cc
     * @return
     * @author or
     * redirect mailto: links to default mail client
     */
    public static Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
    }

}
