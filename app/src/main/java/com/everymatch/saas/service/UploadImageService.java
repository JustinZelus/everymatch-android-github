package com.everymatch.saas.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.everymatch.saas.server.UploadImageTask;
import com.everymatch.saas.singeltones.Preferences;
import com.everymatch.saas.util.EMLog;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class UploadImageService extends IntentService {

    private static final String TAG = UploadImageService.class.getSimpleName();

    public static final String ACTION_UPLOAD = "UploadImageService.upload.action";
    public static final String ACTION_UPLOAD_ERROR = "action.upload.error";
    public static final String EXTRA_UPLOAD_IMAGE_URL = TAG + "upload.image.url";
    public static final String EXTRA_UPLOAD_ID = TAG + ".extra.upload.id";
    public static final String EXTRA_UPLOAD_ERROR_DATA = "extra.upload.error.data";

    private static final String EXTRA_UPLOAD_TASK = TAG + ".extra.upload.task";

    public UploadImageService() {
        super("UploadImageService");
    }

    public UploadImageService(String name) {
        super(name);
    }

    /**
     * Start the service by a given upload image task
     */
    public static String uploadImage(Context context, UploadImageTask uploadImageTask) {
        Intent intent = new Intent(context, UploadImageService.class);
        intent.putExtra(EXTRA_UPLOAD_TASK, uploadImageTask);
        String uploadId = UUID.randomUUID().toString();
        intent.putExtra(EXTRA_UPLOAD_ID, uploadId);
        context.startService(intent);
        return uploadId;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null && intent.hasExtra(EXTRA_UPLOAD_TASK)) {
            UploadImageTask uploadImageTask = (UploadImageTask) intent.getSerializableExtra(EXTRA_UPLOAD_TASK);
            String uploadId = intent.getStringExtra(EXTRA_UPLOAD_ID);
            uploadImage(uploadImageTask, uploadId);
        }
    }

    /**
     * Uploads an image to the server by a given upload task
     */
    private void uploadImage(UploadImageTask uploadImageTask, String uploadId) {
        EMLog.i(TAG, "uploadImage. to url = " + uploadImageTask.getUrl() + ". file path - " + uploadImageTask.getFilePath());

        String uploadedUrl = null;

        int bytesRead;
        int bytesAvailable;
        int bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        HttpURLConnection conn = null;
        String response = null;
        try {

            String boundary = "------" + UUID.randomUUID();

            URL url = new URL(uploadImageTask.getUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");

            // Set Headers
            conn.setRequestProperty("Authorization", "Bearer " + Preferences.getInstance().getTokenType());
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-type", "multipart/form-data; boundary=" + boundary);
            conn.connect();

            // Create body
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes("--" + boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"" + "uploaded_file" + "\"; filename=\"" + "image.png" + "\"\r\n");
            dos.writeBytes("Content-Type: image/jpeg\r\n\r\n");

            // create a buffer of maximum size
            FileInputStream fileInputStream = new FileInputStream(new File(uploadImageTask.getFilePath()));
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            fileInputStream.close();
            dos.flush();
            dos.close();
            int responseCode = conn.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new Exception(String.format("Received the response code %d from the URL %s", responseCode, url));
            }

            // Read response
            InputStream is = conn.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];

            while ((bytesRead = is.read(bytes)) != -1) {
                baos.write(bytes, 0, bytesRead);
            }
            byte[] bytesReceived = baos.toByteArray();
            baos.close();

            is.close();
            response = new String(bytesReceived);
            JSONArray jsonArray = new JSONArray(response);
            uploadedUrl = jsonArray.getString(0);
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(ACTION_UPLOAD_ERROR);
            intent.putExtra(EXTRA_UPLOAD_ERROR_DATA, "there was an error->  try another image: " + e.getMessage());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        Intent intent = new Intent(ACTION_UPLOAD);
        intent.putExtra(EXTRA_UPLOAD_IMAGE_URL, uploadedUrl);
        intent.putExtra(EXTRA_UPLOAD_ID, uploadId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EMLog.i(TAG, "onDestroy");
    }

    public static void register() {

    }
}