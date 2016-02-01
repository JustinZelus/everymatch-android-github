package com.everymatch.saas.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataPeople;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.SquareImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by dors on 7/20/15.
 */
public class PeopleCarouselAdapter extends BaseRecyclerViewAdapter<PeopleCarouselAdapter.UserViewHolder> {

    private ArrayList<DataPeople> mUsers;

    private int mPictureSize;

    public PeopleCarouselAdapter(ArrayList<DataPeople> users) {
        this.mUsers = users;
        mPictureSize = EverymatchApplication.getContext().getResources().getDimensionPixelSize(R.dimen.people_carousel_height);
    }

    /**
     * View holder
     */
    static class UserViewHolder extends RecyclerView.ViewHolder {

        public SquareImageView image;
        public TextView tvMatch;

        public UserViewHolder(View v) {
            super(v);
            image = (SquareImageView) v.findViewById(R.id.imgMatch);
            tvMatch = (TextView) v.findViewById(R.id.tvPeopleMatch);
            image.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            image.setBackgroundColor(DataStore.getInstance().getIntColor(EMColor.FOG));

            /*image = (SquareImageView) v;
            image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            image.setBackgroundColor(DataStore.getInstance().getIntColor(EMColor.FOG));*/
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_people_carusale, viewGroup, false);
        return new UserViewHolder(v);
        //return new UserViewHolder(new SquareImageView(viewGroup.getContext()));
    }

    @Override
    public void onBindViewHolder(UserViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);

        DataPeople user = mUsers.get(position);

        if (user != null) {
            Picasso.with(EverymatchApplication.getContext()).load(Utils.getImageUrl(user.image_url, mPictureSize, mPictureSize)).
                    placeholder(DataManager.getInstance().getAvatarDrawable()).into(viewHolder.image);

            viewHolder.tvMatch.setVisibility(user.match == 0 ? View.INVISIBLE : View.VISIBLE);
            viewHolder.tvMatch.setText(user.match + "%");
        }
    }

    @Override
    public int getItemCount() {

        if (mUsers == null) {
            return 0;
        }

        if (mUsers.size() > 10) {
            return 10;
        }

        return mUsers.size();
    }
}
