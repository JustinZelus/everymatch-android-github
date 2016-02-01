package com.everymatch.saas.ui.questionnaire;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.everymatch.saas.Constants;
import com.everymatch.saas.R;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.UploadImageTask;
import com.everymatch.saas.server.requests.RequestSaveUploadedImage;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseString;
import com.everymatch.saas.service.UploadImageService;
import com.everymatch.saas.ui.dialog.NetworkErrorMessageDialog;
import com.everymatch.saas.util.BitmapUtils;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.ImagePicker;
import com.everymatch.saas.view.BaseIconTextView;
import com.soundcloud.android.crop.Crop;
import com.soundcloud.android.crop.CropImageActivity;

import org.json.JSONObject;

import java.io.File;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionnaireQuestionPickImageFragment extends QuestionnaireQuestionBaseFragment {

    public static final String TAG = QuestionnaireQuestionPickImageFragment.class.getSimpleName();
    public static final String ARG_IS_INDEPENDENT_PROCESS = "arg.from.publish";
    public static final String ACTION_IMAGE_WAS_UPLOADED = "action.image.uploaded";
    public static final String EXTRA_IMAGE_JSON_DATA = "extra.image.url";
    private static final int REQUEST_CODE_SELECT_IMAGE = 151;
    public static final int REQUEST_CODE_UPLOAD_IMAGE = 152;
    public static final int REQUEST_CODE_GALLERY = 153;

    private Bitmap capturedImage;
    private Uri savedUri;
    //ImageView imgGalley;

    private File mFile;
    private BaseIconTextView mCameraButtonInPhoto;

    private String tmpUrl;
    private boolean mIsIndependentProcess;

    public QuestionnaireQuestionPickImageFragment() {
    }

    public static QuestionnaireQuestionPickImageFragment getInstance(boolean fromPublish) {
        QuestionnaireQuestionPickImageFragment answer = new QuestionnaireQuestionPickImageFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARG_IS_INDEPENDENT_PROCESS, fromPublish);
        answer.setArguments(bundle);
        return answer;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_IS_INDEPENDENT_PROCESS))
            mIsIndependentProcess = getArguments().getBoolean(ARG_IS_INDEPENDENT_PROCESS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(R.layout.question_pick_image, (ViewGroup) view.findViewById(R.id.answers_container));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerReceiverPrivate();
        mCameraButtonInPhoto = (BaseIconTextView) view.findViewById(R.id.camera_image2);

        /* do not show camera button if its  independent process*/
        //mCameraButtonInPhoto.setVisibility(mIsIndependentProcess ? View.GONE : View.VISIBLE);
        // if (mIsIndependentProcess) EasyImage.openChooser(QuestionnaireQuestionPickImageFragment.this, "Pick Image", true);

        mCameraButtonInPhoto.setOnClickListener(this);
    }

    @Override
    public void recoverDefaultAnswer() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            showPickerDialog();
        }
    }

    private void showPickerDialog() {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity());
        startActivityForResult(chooseImageIntent, REQUEST_CODE_SELECT_IMAGE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
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
                }

                break;
            case Crop.REQUEST_CROP:
                if (Activity.RESULT_OK == resultCode) {
                    //img.setImageURI(savedUri);
                }
                break;

            case REQUEST_CODE_GALLERY:
                //HandleImage(new File(path), yourSelectedImage);
                break;
            default:
                /* handle EasyImage results */
                EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
                    @Override
                    public void onImagePickerError(Exception e, EasyImage.ImageSource source) {
                        Toast.makeText(getActivity(), "There was an error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onImagePicked(File imageFile, EasyImage.ImageSource source) {
                        HandleImage(imageFile, null);
                    }
                });
                break;
        }
    }

    private void HandleImage(File imageFile, Bitmap bitmap) {
        mFile = imageFile;
        if (bitmap == null)
            capturedImage = getBitmapFromFile(imageFile);
        else
            capturedImage = bitmap;
        long sizeMB = imageFile.length() / (1024 * 1024);
        if (sizeMB > 5) {

        }

        if (capturedImage.getWidth() <= 320) {
            NetworkErrorMessageDialog.start(getChildFragmentManager(), dm.getResourceText(R.string.Blob_InvalidMinWidth));
            return;
        }
        if (capturedImage.getHeight() <= 320) {
            NetworkErrorMessageDialog.start(getChildFragmentManager(), dm.getResourceText(R.string.Blob_InvalidMinHeight));
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

    public static Bitmap getBitmapFromFile(File imageFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath(), options);
        return bitmap;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UploadImageService.ACTION_UPLOAD_ERROR:
                    String error = intent.getStringExtra(UploadImageService.EXTRA_UPLOAD_ERROR_DATA);
                    EMLog.e(TAG, "Upload image error: " + error);
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    break;
                case UploadImageService.ACTION_UPLOAD:
                    mActivity.progressDialog.dismiss();
                    tmpUrl = intent.getStringExtra(UploadImageService.EXTRA_UPLOAD_ID);
                    tmpUrl = intent.getStringExtra(UploadImageService.EXTRA_UPLOAD_IMAGE_URL);
                    if (tmpUrl != null) {
                        Crop.of(savedUri, savedUri).asSquare().withMaxSize(1920, 1280).start(context, QuestionnaireQuestionPickImageFragment.this);
                    }
                    break;

                case CropImageActivity.ACTION_CROP:
                    handleCrop(intent);
                    break;
            }
        }
    };

    private void handleCrop(Intent intent) {
        int l = intent.getIntExtra("l", 0);
        int t = intent.getIntExtra("t", 0);
        int r = intent.getIntExtra("r", 0);
        final int b = intent.getIntExtra("b", 0);
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


        /*now..if its independent process just return the final URL to the caller...else upload to server (upload image question)*/
        if (mIsIndependentProcess) {
            // ((QuestionnarePublishFragment) getTargetFragment()).onImageTaken(output.toString());

            Intent data = new Intent();
            data.setAction(ACTION_IMAGE_WAS_UPLOADED);
            data.putExtra(EXTRA_IMAGE_JSON_DATA, output.toString());

            getActivity().setResult(REQUEST_CODE_UPLOAD_IMAGE, data);


            //mActivity.getSupportFragmentManager().popBackStackImmediate();
            // LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(data);
            return;
        }

        ((QuestionnaireActivity) getActivity()).progressDialog.show();

        server.processRequest(new RequestSaveUploadedImage(output.toString()),
                new ServerConnector.OnResultListener() {
                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        mActivity.progressDialog.dismiss();
                        ResponseString responseString = (ResponseString) baseResponse;
                        String savedCroppedImageUrl = responseString.responseStr;
                        savedCroppedImageUrl = savedCroppedImageUrl.replace("\"", "");
                        EMLog.i(TAG, "RequestSaveUploadedImage onSuccess");
                        QuestionnaireQuestionPickImageFragment.this.setAnswer(savedCroppedImageUrl);
                        /* go to next question */
                        onOneIconClicked();
                    }

                    @Override
                    public void onFailure(ErrorResponse errorResponse) {
                        mActivity.progressDialog.dismiss();
                        Log.i(TAG, "RequestSaveUploadedImage onFailure");
                        Toast.makeText(mActivity, "RequestSaveUploadedImage onFailure", Toast.LENGTH_LONG);
                    }
                });

    }

    private void registerReceiverPrivate() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CropImageActivity.ACTION_CROP);
        intentFilter.addAction(UploadImageService.ACTION_UPLOAD_ERROR);
        intentFilter.addAction(UploadImageService.ACTION_UPLOAD);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           /* case R.id.imgGallery:

                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                startActivityForResult(chooserIntent, REQUEST_CODE_GALLERY);

                break;*/
            case R.id.camera_image2:
                //EasyImage.openChooser(QuestionnaireQuestionPickImageFragment.this, "Pick Image", true);

                showPickerDialog();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);

    }

}