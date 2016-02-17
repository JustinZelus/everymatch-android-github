package com.everymatch.saas.ui.questionnaire;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.everymatch.saas.Constants;
import com.everymatch.saas.R;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.UploadImageTask;
import com.everymatch.saas.server.requests.RequestCreateEvent;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseString;
import com.everymatch.saas.service.UploadImageService;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.ui.event.InviteParticipantsFragment;
import com.everymatch.saas.util.BitmapUtils;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.ImagePicker;
import com.everymatch.saas.util.ShapeDrawableUtils;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseButton;
import com.everymatch.saas.view.EventHeader;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.soundcloud.android.crop.Crop;
import com.soundcloud.android.crop.CropImageActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;

/**
 * Created by PopApp_laptop on 19/10/2015.
 */
public class QuestionnarePublishFragment extends BaseFragment implements EventHeader.OnEventHeader, View.OnClickListener {

    private static final String EXTRA_MODE = "extra_mode";
    public static final int MODE_EDIT_EVENT = 13;
    private static final int REQUEST_CODE_SELECT_IMAGE = 151;
    public static final String TAG = QuestionnarePublishFragment.class.getSimpleName();
    private QuestionnaireActivity mActivity;

    private EventHeader mHeader;
    private BaseButton btnInvite, btnPublish;
    private ImageView img;
    private String tmpUrl;
    private TextView mTextTitle;
    private TextView mTextImage;
    private View mImageHolder;
    private View mButtonHolder;
    private EditText etEventName, etEventDesc;

    /*iamge data*/
    private File mFile;
    private Bitmap capturedImage;
    private Uri savedUri;

    private int mMode;
    private String selectedInvitees;

    public static QuestionnarePublishFragment getInstance(int mode) {
        QuestionnarePublishFragment questionnarePublishFragment = new QuestionnarePublishFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_MODE, mode);
        questionnarePublishFragment.setArguments(args);
        return questionnarePublishFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (QuestionnaireActivity) getActivity();

        if (getArguments() != null) {
            mMode = getArguments().getInt(EXTRA_MODE, -1);
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

                case UploadImageService.ACTION_UPLOAD_ERROR:
                    Toast.makeText(context, intent.getStringExtra(UploadImageService.EXTRA_UPLOAD_ERROR_DATA), Toast.LENGTH_SHORT).show();
                    break;
                case UploadImageService.ACTION_UPLOAD:
                    mActivity.progressDialog.dismiss();

                    tmpUrl = intent.getStringExtra(UploadImageService.EXTRA_UPLOAD_ID);
                    tmpUrl = intent.getStringExtra(UploadImageService.EXTRA_UPLOAD_IMAGE_URL);
                    if (tmpUrl != null) {
                        Picasso.with(getActivity()).load(tmpUrl).into(img);
                        Crop.of(savedUri, savedUri).asSquare().withMaxSize(1920, 1280).start(context, QuestionnarePublishFragment.this);
                    }
                    break;

                case InviteParticipantsFragment.ACTION_INVITEES_SELECTED:

                    selectedInvitees = intent.getStringExtra("selectedInvitees");
                    EMLog.d(TAG, "got selected invitees: " + selectedInvitees);
                    /* here we set the participant object that will be sent to server */
                    mActivity.dataSetupQuestionsObject.participants = selectedInvitees;

                    break;
                case CropImageActivity.ACTION_CROP:
                    int l = intent.getIntExtra("l", 0);
                    int t = intent.getIntExtra("t", 0);
                    int r = intent.getIntExtra("r", 0);
                    int b = intent.getIntExtra("b", 0);
                    Rect cropRect = new Rect(l, t, r, b);

                    Rect bitmapRect = new Rect(0, 0, capturedImage.getWidth(), capturedImage.getHeight());

                    int top = cropRect.top;
                    int left = cropRect.left;
                    int bottom = -(bitmapRect.height() - cropRect.bottom);
                    int right = -(bitmapRect.width() - cropRect.right);

                    JSONObject output = new JSONObject();
                    try {
                        output.put("image_url", tmpUrl);
                        output.put("crop", "" + top + "," + left + "," + bottom + "," + right);
                        output.put("rotate", "0");
                        output.put("id", mActivity.mGeneratedEvent._id);
                        output.put("is_upload_to_temp", false);
                    } catch (Exception ex) {
                        Log.e(getClass().getName(), ex.getMessage());
                    }

                    mActivity.progressDialog.show();
                    ServerConnector.getInstance().processRequest(new RequestCreateEvent("", "", output.toString(), RequestCreateEvent.REQUEST_TYPE.SEND_CROP_DATA, ""), new ServerConnector.OnResultListener() {
                        @Override
                        public void onSuccess(BaseResponse baseResponse) {
                            mActivity.progressDialog.dismiss();

                            Log.d(TAG, "RequestActivityProfile onSuccess");
                            String response = ((ResponseString) baseResponse).responseStr;
                            response = response.replace("\"", "");
                            UrlImageViewHelper.setUrlDrawable(img, response);
                        }

                        @Override
                        public void onFailure(ErrorResponse errorResponse) {
                            mActivity.progressDialog.dismiss();
                            Log.w(TAG, "RequestCreateEvent onFailure: " + errorResponse.getStatusCode() + " " + errorResponse.getServerRawResponse());
                        }
                    });
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_questionnare_publish, container, false);

        btnInvite = (BaseButton) v.findViewById(R.id.btnInviteParticipants);
        btnInvite.setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton());
        btnInvite.setOnClickListener(this);

        btnPublish = (BaseButton) v.findViewById(R.id.btnPublish);
        btnPublish.setOnClickListener(this);

        v.findViewById(R.id.uploadImageLayout).setOnClickListener(this);
        mTextImage = (TextView) v.findViewById(R.id.fragment_questionaire_publish_text_image);

        etEventName = (EditText) v.findViewById(R.id.etEventtName);
        etEventDesc = (EditText) v.findViewById(R.id.etEventDesc);
        etEventDesc.setHint(dm.getResourceText(R.string.Add_Event_Description));
        etEventName.setHint(dm.getResourceText(R.string.Event_Name));

        mTextTitle = (TextView) v.findViewById(R.id.event_data_row_details);
        mImageHolder = v.findViewById(R.id.fragment_questionaire_publish_image_holder);
        mButtonHolder = v.findViewById(R.id.fragment_questionaire_publish_button_holder);

        img = (ImageView) v.findViewById(R.id.imgPublishEventImage);

        if (!TextUtils.isEmpty(mActivity.mGeneratedEvent.dataPublicEvent.image_url)) {
            Picasso.with(getActivity()).load(mActivity.mGeneratedEvent.dataPublicEvent.image_url).into(img);
        }

        if (!TextUtils.isEmpty(mActivity.mGeneratedEvent.dataPublicEvent.event_title)) {
            etEventName.setText(mActivity.mGeneratedEvent.dataPublicEvent.event_title);
        }
        register();
        return v;
    }

    private void register() {
        IntentFilter intentFilter = new IntentFilter();

        /*crop has finished*/
        intentFilter.addAction(CropImageActivity.ACTION_CROP);
        /*image upload error*/
        intentFilter.addAction(UploadImageService.ACTION_UPLOAD_ERROR);
        /*image finished uploading*/
        intentFilter.addAction(UploadImageService.ACTION_UPLOAD);
        /*finished select people to invite*/
        intentFilter.addAction(InviteParticipantsFragment.ACTION_INVITEES_SELECTED);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHeader = (EventHeader) view.findViewById(R.id.eventHeader);
        setHeader();
    }

    private void setHeader() {
        mHeader.setListener(this);
        mHeader.getBackButton().setText(Consts.Icons.icon_Details);
        mHeader.getIconOne().setText(dm.getResourceText(R.string.Cancel_Exit));
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setVisibility(View.GONE);

        mHeader.setTitle("");

        if (mMode == MODE_EDIT_EVENT) {
            mHeader.setSaveCancelMode(dm.getResourceText(R.string.Edit_Event_Settings));
            mTextTitle.setVisibility(View.GONE);
            mButtonHolder.setVisibility(View.GONE);
            mHeader.setTitle(dm.getResourceText(R.string.Edit_Event_Settings));
            mTextImage.setText(dm.getResourceText(R.string.Change_Picture));
            mImageHolder.getLayoutParams().height = Utils.dpToPx(180);
            mImageHolder.requestLayout();

            if (!TextUtils.isEmpty(mActivity.mGeneratedEvent.dataPublicEvent.event_title)) {
                etEventName.setText(mActivity.mGeneratedEvent.dataPublicEvent.event_title);
            }
        } else {
            mHeader.getIconThree().setText(dm.getResourceText(R.string.Publish).toUpperCase());
        }
    }

    @Override
    public void onBackButtonClicked() {
        if (mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.EDIT_ACTIVITY || mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.EDIT_EVENT) {
            //getActivity().getSupportFragmentManager().popBackStackImmediate();
            //getActivity().onBackPressed();
            ((QuestionnaireActivity) getActivity()).forceBack();
            return;
        }

        mActivity.goToSummeryScreen(null);
    }

    @Override
    public void onOneIconClicked() {
        if (mActivity.isInEditMode()) {
            publishEvent();
            return;
        }
        // just quit events creation
        mActivity.showConfirmExitDialog(dm.getResourceText(R.string.CancelCreateWarningTitle), dm.getResourceText(R.string.CancelCreateActivitySubtitle));
        //getActivity().finish();
    }

    @Override
    public void onTwoIconClicked() {

    }

    @Override
    public void onThreeIconClicked() {
        //publish
        publishEvent();
    }

    private void publishEvent() {
        mActivity.mGeneratedEvent.dataPublicEvent.event_title = etEventName.getText().toString().trim();
        mActivity.mGeneratedEvent.dataPublicEvent.event_description = etEventDesc.getText().toString().trim();

        mActivity.sendAnswersToServer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnInviteParticipants:
                (mActivity).replaceFragment(R.id.fragment_container_full, InviteParticipantsFragment.getInstance(mActivity.mGeneratedEvent, 0, false, null/*no need for action*/), InviteParticipantsFragment.TAG, true, InviteParticipantsFragment.TAG);
                break;

            case R.id.btnPublish:
                publishEvent();
                break;
            case R.id.uploadImageLayout:
                //EasyImage.openChooser(QuestionnarePublishFragment.this, "Pick Image", true);
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity());
                startActivityForResult(chooseImageIntent, REQUEST_CODE_SELECT_IMAGE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SELECT_IMAGE:
                if (Activity.RESULT_OK == resultCode) {
                    capturedImage = ImagePicker.getImageFromResult(getActivity(), resultCode, data);
                    //File file = ImagePicker.getTempFile(getActivity());
                    File file = null;
                    String path = BitmapUtils.savePhoto(capturedImage, getActivity());
                    if (path != null)
                        file = new File(path);

                    HandleImage(file, capturedImage);
                    break;
                }
            default:
                break;
        }
    }

    private void HandleImage(File imageFile, Bitmap bitmap) {
        mFile = imageFile;
        if (bitmap == null)
            capturedImage = QuestionnaireQuestionPickImageFragment.getBitmapFromFile(imageFile);
        else
            capturedImage = bitmap;
        long sizeMB = imageFile.length() / (1024 * 1024);
        if (sizeMB > 5) {

        }

        if (capturedImage.getWidth() <= 320) {
            ((BaseActivity) getActivity()).showErrorDialog(dm.getResourceText(R.string.Blob_InvalidMinWidth));
            // NetworkErrorMessageDialog.start((BaseActivity) getActivity(), getChildFragmentManager(), dm.getResourceText(R.string.Blob_InvalidMinWidth));
            return;
        }
        if (capturedImage.getHeight() <= 320) {
            ((BaseActivity) getActivity()).showErrorDialog(dm.getResourceText(R.string.Blob_InvalidMinHeight));
            //NetworkErrorMessageDialog.start((BaseActivity) getActivity(), getChildFragmentManager(), dm.getResourceText(R.string.Blob_InvalidMinHeight));
            return;
        }

        savedUri = Uri.fromFile(mFile);

        mActivity.progressDialog.setTitle(R.string.Uploading);
        mActivity.progressDialog.show();

        //String path = BitmapUtils.savePhoto(capturedImage, getActivity());
        String path = mFile.getPath();
        //String path = savedUri.getPath();
        UploadImageTask uploadImageTask = new UploadImageTask();
        uploadImageTask.setFilePath(path);
        //uploadImageTask.setUrl(Constants.getLocalImageUploadUrl());
        uploadImageTask.setUrl(Constants.getImageUploadUrl());
        String uploadId = UploadImageService.uploadImage(getActivity(), uploadImageTask);
    }

    @Override
    protected void handleBroadcast(Serializable eventObject, String eventName) {
        //super.handleBroadcast(eventObject, eventName);
        //if(PusherManager.)
    }
}
