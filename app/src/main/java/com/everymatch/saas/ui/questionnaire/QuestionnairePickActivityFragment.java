package com.everymatch.saas.ui.questionnaire;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.Data.DataActivity;
import com.everymatch.saas.server.Data.DataQuestion;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseImageView;
import com.everymatch.saas.view.EventHeader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionnairePickActivityFragment extends ListFragment implements AdapterView.OnItemClickListener {
    public static final String TAG = "QuestionnairePickActivityFragment";
    int screenWidth, screenHeight;
    DataActivity[] mAllDataActivities;
    private EventHeader mHeader;

    public QuestionnairePickActivityFragment() {
        //mAllDataActivities = DataManager.getInstance().getAllActivitiesData();
        mAllDataActivities = DataStore.getInstance().getApplicationData().getActivities();

        // Filter only the ones that the user NOT(!) attached to
        String[] activityIds = DataStore.getInstance().getUser().getAnswerActivityProfile();
        ArrayList<String> answeredIdsList = new ArrayList<String>();
        for (String str : activityIds) answeredIdsList.add(str);

        ArrayList<DataActivity> activitiesArray = new ArrayList<>();

        for (DataActivity dataActivity : mAllDataActivities) {
            if (!answeredIdsList.contains(dataActivity.client_id)) {
                activitiesArray.add(dataActivity);
                continue;
            }
        }

        mAllDataActivities = activitiesArray.toArray(new DataActivity[activitiesArray.size()]);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_questionaire_pick_activity, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHeader(view);
        getListView().setOnItemClickListener(this);

        mHeader.post(new Runnable() {
            @Override
            public void run() {
                screenWidth = getResources().getDisplayMetrics().widthPixels;
                screenHeight = getListView().getMeasuredHeight();
                setListAdapter(new PickActivityAdapter());
            }
        });
    }

    private void setHeader(View view) {
        mHeader = (EventHeader) view.findViewById(R.id.eventHeader);
        mHeader.getBackButton().setText(Consts.Icons.icon_New_Close);
        mHeader.setTitle(DataManager.getInstance().getResourceText(getString(R.string.Add_Profile)));
        mHeader.getIconOne().setVisibility(View.GONE);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setVisibility(View.GONE);
        mHeader.setListener(new EventHeader.OnEventHeader() {
            @Override
            public void onBackButtonClicked() {
                getActivity().onBackPressed();
            }

            @Override
            public void onOneIconClicked() {

            }

            @Override
            public void onTwoIconClicked() {

            }

            @Override
            public void onThreeIconClicked() {

            }
        });

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DataActivity activity = mAllDataActivities[position];
        ((QuestionnaireActivity) getActivity()).mDataActivity = activity;

        /**Here we need to take:
         * application.activity[x].questions +
         * application.user_profile_questions
         **/
        final ArrayList<DataQuestion> output = new ArrayList<DataQuestion>();


        output.addAll(Arrays.asList(activity.questions));
        /*convention!
        * if user has at least one profile! no need to add user profile questions
        * else: leave only activity questions
        * */
        if (Utils.isArrayEmpty(DataStore.getInstance().getUser().profiles.activity_profiles)) {
            output.addAll(Arrays.asList(DataStore.getInstance().getApplicationData().getUser_profile_questions()));
        }

        DataQuestion[] questions = output.toArray(new DataQuestion[output.size()]);
        ((QuestionnaireActivity) getActivity()).prepareArrays(questions);

        ((QuestionnaireActivity) getActivity()).goToWelcome();

    }

    private class PickActivityAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mAllDataActivities.length;
        }

        @Override
        public Object getItem(int position) {
            return mAllDataActivities[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.item_pick_activity, parent, false);
            }

            RelativeLayout Holder = (RelativeLayout) convertView.findViewById(R.id.Holder);
            String url = mAllDataActivities[position].background.url;

            if (mAllDataActivities.length > 0) {
                /*set image height*/
                int wantedHeight = screenHeight / mAllDataActivities.length;
                int minHeight = Utils.dpToPx(120);
                int maxHeight = screenWidth;

                if (wantedHeight < minHeight)
                    wantedHeight = minHeight;
                if (wantedHeight > maxHeight)
                    wantedHeight = maxHeight;

                wantedHeight-=2;
                AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) convertView.getLayoutParams();
                Holder.setLayoutParams(layoutParams);
                Holder.setPadding(0,0,0,0);
                //url += "?width=" + screenWidth + "&height=" + wantedHeight + "&mode=max";
                url += "?width=" + screenWidth + "&height=" + wantedHeight + "&mode=crop";
            }

            ((TextView) convertView.findViewById(R.id.activity_name)).setText(mAllDataActivities[position].text_title);

            try {
                //UrlImageViewHelper.setUrlDrawable((ImageView)convertView.findViewById(R.id.background_image),mAllDataActivities[position].background.url);
                BaseImageView imageView = (BaseImageView) convertView.findViewById(R.id.background_image);
                Picasso.with(convertView.getContext())
                        .load(url)
                        .into(imageView);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }
}
