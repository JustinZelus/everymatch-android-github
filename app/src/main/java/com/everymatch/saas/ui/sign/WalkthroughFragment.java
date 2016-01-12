package com.everymatch.saas.ui.sign;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.server.responses.ResponseApplication;
import com.everymatch.saas.view.BaseImageView;
import com.everymatch.saas.view.IconImageView;
import com.squareup.picasso.Picasso;

public class WalkthroughFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_WALKTHROU = "walk";


    private ResponseApplication.Start.DataModelApplication mWalkthroughData;
    private int screenWidth;
    private int screenHeight;


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WalkthroughFragment newInstance(int sectionNumber, ResponseApplication.Start.DataModelApplication dataModelApplication) {
        //WalkthroughFragment fragment =  WalkthroughFragment.newInstance(sectionNumber,walkthroughData);
        WalkthroughFragment fragment = new WalkthroughFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(ARG_WALKTHROU, dataModelApplication);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWalkthroughData = (ResponseApplication.Start.DataModelApplication) getArguments().getSerializable(ARG_WALKTHROU);

        screenHeight = getResources().getDisplayMetrics().heightPixels ;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_walkthrough, container, false);
        return rootView;
    }

    public void onViewCreated(final View view, Bundle savedInstanceState) {
        TextView tv = (TextView) view.findViewById(R.id.title_textview);
        tv.setText(mWalkthroughData.text_title);
        tv = (TextView) view.findViewById(R.id.subtitle_textview);
        tv.setText(mWalkthroughData.text_description);

        // set the icon
        IconImageView imageView = (IconImageView) view.findViewById(R.id.icon_image);
        Spannable span = new SpannableString(mWalkthroughData.icon_image_url);
        //span.setSpan(new RelativeSizeSpan(0.8f), 0, 0, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        imageView.setIconImage(mWalkthroughData.icon_font, mWalkthroughData.icon_image_url);

        ImageView bgImage = (BaseImageView) view.findViewById(R.id.background_image);
        String bg = mWalkthroughData.background_image + "?width=" + screenWidth + "&height=" + screenHeight + "&mode=max";
        Picasso.with(getContext()).load(bg).into(bgImage);
    }
}