package com.everymatch.saas.client.data;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.server.Data.DataActivity;
import com.everymatch.saas.server.Data.Resource;
import com.everymatch.saas.server.Data.Resources;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestResources;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseResources;
import com.everymatch.saas.singeltones.Preferences;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.FetchCallback;
import com.everymatch.saas.util.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Dacid on 30/06/2015.
 */
public class DataManager {

    private static final String TAG = DataManager.class.getSimpleName();

    private static DataManager sInstance = null;

    private String mDefaultActivityId;
    private DataActivity mAllActivitiesData[];
    private String AnsweredActivitiesProfile[];
    private Resources mResources;
    private Drawable mDefaultAvatarDrawable;
    private Drawable mDefaultEventImage;
    private Target mAvatarTarget;

    public static DataManager getInstance() {
        if (sInstance == null) {
            synchronized (DataManager.class) {
                if (sInstance == null) {
                    sInstance = new DataManager();
                }
            }
        }
        return sInstance;
    }


    // ============================== Resources ================================ //

    public boolean hasResources() {
        if (mResources == null) {
            mResources = Preferences.getInstance().getResources();
        }

        return mResources != null;
    }

    public Resource getResource(String key) {
        return mResources.resourcesMap.get(key);
    }

    public String getResourceText(String key) {
        Resource resource = getResource(key);
        if (resource == null) {
            return key;
        }

        return resource.value;
    }

    public String getResourceText(int keyRest) {
      return getResourceText(keyRest, false);
    }

    public String getResourceText(int keyRest, boolean firstLetterUpperCase) {
        String strKey = EverymatchApplication.getContext().getString(keyRest);
        Resource resource = getResource(strKey);
        if (resource == null) {
            return strKey;
        }

        if (firstLetterUpperCase){
            return Utils.setFirstLetterUpperCase(resource.value);
        }

        return resource.value;
    }

    public void fetchResources(final FetchCallback fetchCallback) {
        ServerConnector.getInstance().processRequest(new RequestResources(), new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                ResponseResources responseResources = (ResponseResources) baseResponse;
                mResources = responseResources.dataResources;
                Preferences.getInstance().setResources(mResources);
                fetchCallback.postFetch(true);
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                fetchCallback.postFetch(false);
            }
        }, RequestResources.class.getName());
    }

    public void cancelResourcesFetching() {
        ServerConnector.getInstance().cancelPendingRequests(RequestResources.class.getName());
    }

    // ============================== Images ================================ //

    public void fetchDefaultAvatarImage() {

        EMLog.i(TAG, "fetchDefaultAvatarImage");

        String fileDir = EverymatchApplication.getContext().getFilesDir().getAbsolutePath() + "/images/";
        File dir = new File(fileDir);

        if (!new File(fileDir).exists()) {
            dir.mkdirs();
        }

        final File avatarFile = new File(fileDir, "default_avatar.png");

        // Create target for the downloaded file
        mAvatarTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                try {
                    FileOutputStream fos = new FileOutputStream(avatarFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                    mDefaultAvatarDrawable = new BitmapDrawable(EverymatchApplication.getContext().getResources(), bitmap);
                } catch (Exception e) {
                    EMLog.e(TAG, "failed to save image");
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        if (avatarFile.exists()) {
            Picasso.with(EverymatchApplication.getContext()).load(avatarFile).into(mAvatarTarget);
        } else {
            Picasso.with(EverymatchApplication.getContext()).load(Constants.DEFAULT_AVATAR_IMAGE).into(mAvatarTarget);
        }
    }

    public Drawable getAvatarDrawable() {
        if (mDefaultAvatarDrawable == null)
            mDefaultAvatarDrawable = EverymatchApplication.getContext().getResources().getDrawable(R.drawable.img_user);
        return mDefaultAvatarDrawable;
    }

    public void fetchEventDefaultImage() {

        EMLog.i(TAG, "fetchDefaultAvatarImage");

        String fileDir = EverymatchApplication.getContext().getFilesDir().getAbsolutePath() + "/images/";
        File dir = new File(fileDir);

        if (!new File(fileDir).exists()) {
            dir.mkdirs();
        }

        final File eventFile = new File(fileDir, "default_event.png");

        // Create target for the downloaded file
        mAvatarTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                try {
                    FileOutputStream fos = new FileOutputStream(eventFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                    mDefaultEventImage = new BitmapDrawable(EverymatchApplication.getContext().getResources(), bitmap);
                } catch (Exception e) {
                    EMLog.e(TAG, "failed to save image");
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        if (eventFile.exists()) {
            Picasso.with(EverymatchApplication.getContext()).load(eventFile).into(mAvatarTarget);
        } else {
            Picasso.with(EverymatchApplication.getContext()).load(Constants.DEFAULT_EVENT_IMAGE).into(mAvatarTarget);
        }
    }

    public Drawable getEventDrawable() {
        return mDefaultEventImage;
    }
}
