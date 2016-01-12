package com.everymatch.saas.ui.questionnaire;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.graphics.ColorUtils;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.PlaceAutocompleteAdapter;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.client.data.Types;
import com.everymatch.saas.server.Data.DataLocation;
import com.everymatch.saas.server.Data.DataLocation2;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.ShapeDrawableUtils;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseTextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionnaireQuestionLocationFragment extends QuestionnaireQuestionBaseFragment implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = QuestionnaireQuestionLocationFragment.class.getSimpleName();
    public static final float DEFAULT_ZOOM_LEVEL = 12;
    MapView mMapView;
    TextView mSetCurrentLoctionButton;
    Button mRadiusPlusButton, mRadiusMinusButton;
    TextView mRadiusTextView;
    EditText mRadiusEditText;
    AutoCompleteTextView mLoctionmAutocompleteView;
    BaseTextView tvSetSpecificArea;
    ViewAnimator viewAnimator;

    double radius;
    private boolean isPlace;
    private String placeName;
    LatLng mSelectedPlaceLatLng;
    String mCountryCode, mCountryName, mAddress, mCityCode;
    boolean flagUserSetRadius = false;

    GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    public QuestionnaireQuestionLocationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient
                .Builder(mActivity)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(R.layout.question_location, (ViewGroup) view.findViewById(R.id.answers_container));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) view.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mSetCurrentLoctionButton = (TextView) view.findViewById(R.id.set_loction_button);
        mSetCurrentLoctionButton.setText(Consts.Icons.icon_GPS);
        mSetCurrentLoctionButton.setOnClickListener(this);

        mLoctionmAutocompleteView = (AutoCompleteTextView) view.findViewById(R.id.location_input);
        mLoctionmAutocompleteView.setHint(dm.getResourceText(getString(R.string.Type_Name_Or_Location)));
        mLoctionmAutocompleteView.setTextColor(ds.getIntColor(EMColor.NIGHT));

        mRadiusEditText = (EditText) view.findViewById(R.id.radius_edittext);

        setRadiusValue();

        mRadiusEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                flagUserSetRadius = true;
                if (mHaveAnswer) {
                    onLocationSelected(mSelectedPlaceLatLng, false);
                    setAnswer(mAddress);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("0"))
                    mRadiusEditText.setText("1");
            }
        });

        mLoctionmAutocompleteView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    mSetCurrentLoctionButton.setText(Consts.Icons.icon_StatusNegative);
                else mSetCurrentLoctionButton.setText(Consts.Icons.icon_GPS);
            }
        });

        view.findViewById(R.id.typeLocationHolder).setBackgroundDrawable(ShapeDrawableUtils.getButtonStroked(ds.getIntColor(EMColor.FOG)));
        view.findViewById(R.id.radiusWrapper).setBackgroundDrawable(ShapeDrawableUtils.getButtonStroked(ds.getIntColor(EMColor.FOG)));
        mLoctionmAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAdapter = new PlaceAutocompleteAdapter(mActivity, R.layout.view_place_auto_complete, mGoogleApiClient, BOUNDS_GREATER_SYDNEY, null);
        mLoctionmAutocompleteView.setAdapter(mAdapter);
        mLoctionmAutocompleteView.post(new Runnable() {
            @Override
            public void run() {
                mLoctionmAutocompleteView.setDropDownWidth(mLoctionmAutocompleteView.getMeasuredWidth() + Utils.dpToPx(50));
            }
        });


        //TODO: not always should be visible!
        mRadiusTextView = (TextView) view.findViewById(R.id.radius_textview);
        mRadiusPlusButton = (Button) view.findViewById(R.id.button_plus);
        mRadiusMinusButton = (Button) view.findViewById(R.id.button_minus);
        mRadiusPlusButton.setOnClickListener(this);
        mRadiusMinusButton.setOnClickListener(this);

        tvSetSpecificArea = (BaseTextView) view.findViewById(R.id.tvSetRadiusStageOne);
        tvSetSpecificArea.setOnClickListener(this);

        viewAnimator = (ViewAnimator) view.findViewById(R.id.viewAnimator);
        viewAnimator.setInAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left));
        viewAnimator.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right));

        if (!TextUtils.isEmpty(mQuestionAndAnswer.userAnswerStr))
            recoverAnswer();
        else {
            if (mQuestionAndAnswer.question.default_value != null)
                recoverDefaultAnswer();
        }

        if (mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.CREATE_EVENT) {
            tvSetSpecificArea.setVisibility(View.GONE);
        }
        if (flagUserSetRadius && mActivity.create_mode != QuestionnaireActivity.CREATE_MODE.CREATE_EVENT)
            tvSetSpecificArea.setVisibility(View.VISIBLE);

        mLoctionmAutocompleteView.setInputType((InputType.TYPE_TEXT_FLAG_AUTO_CORRECT));

        mMapView.getMap().setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                float zoom = cameraPosition.zoom;
                Log.d(TAG, "zoom is:" + zoom);
            }
        });

    }

    private void setRadiusValue() {
        /*set radius value according to the activity and user distance unit*/
        int radius = mActivity.mDataActivity.distance_value;

        String userDistance = ds.getUser().user_settings.distance;
        if (userDistance == null || userDistance.equals("") || userDistance.toLowerCase().equals(Types.UNIT_KM)) {
            radius /= 1000;
        } else {
            radius /= 1600;
        }

        mRadiusEditText.setText("" + radius);
    }

    @Override
    public void recoverDefaultAnswer() {
        try {
            String str = mQuestionAndAnswer.question.default_value;
            setAnswer(str);
            recoverAnswer();
        } catch (Exception e) {
            e.printStackTrace();
            EMLog.e(TAG, "could not parse default value on questionId: " + mQuestionAndAnswer.question.questions_id);
        }
    }

    private void recoverAnswer() {
        if (TextUtils.isEmpty(mQuestionAndAnswer.userAnswerStr))
            return;

        try {

            try {
                DataLocation2 dataLocation = new Gson().fromJson(mQuestionAndAnswer.userAnswerData.get("value").toString(), DataLocation2.class);
                LatLng latLng = new LatLng(dataLocation.coordinates.value[0][0], dataLocation.coordinates.value[0][1]);

                mAddress = dataLocation.text_address.get(ds.getCulture());
                //(new JSONObject(dataLocation.text_address)).get(getString(R.string.host_language)).toString();
                mCountryCode = dataLocation.country_code;
                mCountryName = dataLocation.country;
                mCityCode = dataLocation.city_code;

                mLoctionmAutocompleteView.setText(mAddress);

                onLocationSelected(latLng);
                setAnswer(mAddress);
            } catch (Exception e) {
            }

            try {
                DataLocation dataLocation = new Gson().fromJson(mQuestionAndAnswer.userAnswerData.get("value").toString(), DataLocation.class);
                LatLng latLng = new LatLng(dataLocation.coordinates.value[0][0], dataLocation.coordinates.value[0][1]);

                mAddress = mQuestionAndAnswer.userAnswerStr;
                //(new JSONObject(dataLocation.text_address)).get(getString(R.string.host_language)).toString();
                mCountryCode = dataLocation.country_code;
                mCountryName = dataLocation.country;
                mCityCode = dataLocation.city_code;
                int radius = dataLocation.distance_value;
                mRadiusEditText.setText(radius);

                mLoctionmAutocompleteView.setText(mAddress);

                onLocationSelected(latLng);
                setAnswer(mAddress);
            } catch (Exception e1) {
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_loction_button:
                /*show set specific area if needed*/
                if (mActivity.create_mode != QuestionnaireActivity.CREATE_MODE.CREATE_EVENT)
                    tvSetSpecificArea.setVisibility(View.VISIBLE);

                if (mSetCurrentLoctionButton.getText().equals(Consts.Icons.icon_GPS))
                    setCurrentLoctionSelected();
                else clearAnswer();
                break;
            case R.id.button_plus:
                //TODO
                break;
            case R.id.button_minus:
                //TODO
                break;
            case R.id.tvSetRadiusStageOne:
                viewAnimator.showNext();
                mRadiusEditText.setText(mRadiusEditText.getText());
                break;
        }
    }

    public void clearAnswer() {
        super.clearAnswer();
        mLoctionmAutocompleteView.setText("");
        mLoctionmAutocompleteView.clearListSelection();

        clearMarksOnMap();
    }

    private void setCurrentLoctionSelected() {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null) {
            onLocationSelected(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
            getLocationAddress(lastLocation.getLatitude(), lastLocation.getLongitude(), true);
        }
    }

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
        // TODO(Developer): Check error code and notify the user of error state and resolution.
        EMLog.e(TAG, "Could not connect to Google API Client: " + connectionResult.getErrorCode());
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Log.i(TAG, "Called getPlaceById to get Place details for " + item.placeId);

            //mLoctionmAutocompleteView.setSelection(0);
        }
    };


    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            try {
                // Get the Place object from the buffer.
                final Place place = places.get(0);
                mAddress = place.getAddress().toString();
                List<Integer> type = place.getPlaceTypes();
                if (type.contains(Place.TYPE_STREET_ADDRESS) || type.contains(Place.TYPE_ROUTE) || type.contains(Place.TYPE_LOCALITY) || type.contains(Place.TYPE_POSTAL_CODE)) {
                    EMLog.d(TAG, "Place is street address");
                    isPlace = false;
                } else {
                    EMLog.d(TAG, "Place is not address");
                    isPlace = true;
                    placeName = place.getName().toString();
                }

                mCountryCode = place.getLocale().getCountry();
                mCountryName = place.getLocale().getDisplayCountry();
                mCityCode = place.getName().toString();
                if (TextUtils.isEmpty(mCountryCode))
                    mCountryCode = mAddress;
                if (TextUtils.isEmpty(mCountryName))
                    mCountryName = mAddress;
                if (TextUtils.isEmpty(mCityCode))
                    mCityCode = mAddress;

                onLocationSelected(place.getLatLng());
                setAnswer(mAddress);

                getLocationAddress(place.getLatLng().latitude, place.getLatLng().longitude, false);

                Log.i(TAG, "Place details received: " + place.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            places.release();
        }
    };


    private void onLocationSelected(final LatLng selectedPlaceLatLng) {
        onLocationSelected(selectedPlaceLatLng, true);
    }

    private void onLocationSelected(final LatLng selectedPlaceLatLng, boolean closeKeyboard) {
        mSelectedPlaceLatLng = selectedPlaceLatLng;

        GoogleMap map = mMapView.getMap();
        if (map != null) {
            Log.i(TAG, "setting the map to lat/lng: " + selectedPlaceLatLng);

            // close keyboard
            if (closeKeyboard) {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mLoctionmAutocompleteView.getWindowToken(), 0);
            }
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    markPlaceOnMap(selectedPlaceLatLng);
                }
            }, 2000);
        }
    }

    Circle mCircle = null;
    Marker mMarker = null;

    private void clearMarksOnMap() {
        if (mMarker != null) {
            mMarker.remove();
            mMarker = null;
        }

        if (mCircle != null) {
            mCircle.remove();
            mCircle = null;
        }
    }

    private void markPlaceOnMap(final LatLng selectedPlaceLatLng) {
        GoogleMap map = mMapView.getMap();
        if (map != null) {
            Log.i(TAG, "setting the ap to lat/lng: " + selectedPlaceLatLng);

            String radiusStr = mRadiusEditText.getText().toString();
            float radius = radiusStr.isEmpty() ? 1 : Float.parseFloat(radiusStr);
            float zoomLevel = getZoomLevel((radius * kmOrMile())) - 1;
            if (!flagUserSetRadius)
                zoomLevel = DEFAULT_ZOOM_LEVEL;


            CameraUpdate center = CameraUpdateFactory.newLatLng(selectedPlaceLatLng);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(zoomLevel);

            map.moveCamera(center);

            map.animateCamera(zoom);

            clearMarksOnMap();

            mMarker = map.addMarker(new MarkerOptions().position(selectedPlaceLatLng));
            int radiusColor = ColorUtils.setAlphaComponent(DataStore.getInstance().getIntColor(EMColor.PRIMARY), (int) (255 * 0.3));

            // Instantiates a new CircleOptions object and defines the center and radius
            CircleOptions circleOptions = new CircleOptions()
                    .center(selectedPlaceLatLng)
                    .fillColor(radiusColor) //parseColor("#55000033"))
                    .strokeColor(DataStore.getInstance().getIntColor(EMColor.PRIMARY))
                    .strokeWidth(4)
                    .radius(radius * kmOrMile()); // In meters

            // Get back the mutable Circle
            if (flagUserSetRadius) {
                mCircle = map.addCircle(circleOptions);
            }

        }
    }

    private float kmOrMile() {
        return 1 * 1000; //Km
        //TODO - mile
    }

    private void getLocationAddress(double latitude, double longitude, boolean setTextToEditText) {
        List<Address> addresses = null;

        try {
            Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    // In this sample, get just a single address.
                    1);
//        } catch (IOException ioException) {
//            // Catch network or other I/O problems.
//            errorMessage = getString(R.string.service_not_available);
//            Log.e(TAG, errorMessage, ioException);
//        } catch (IllegalArgumentException illegalArgumentException) {
//            // Catch invalid latitude or longitude values.
//            errorMessage = getString(R.string.invalid_lat_long_used);
//            Log.e(TAG, errorMessage + ". " +
//                    "Latitude = " + location.getLatitude() +
//                    ", Longitude = " +
//                    location.getLongitude(), illegalArgumentException);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
//            if (errorMessage.isEmpty()) {
//                errorMessage = getString(R.string.no_address_found);
//                Log.e(TAG, errorMessage);
//            }
//            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();
            StringBuilder addressStringBuilder = new StringBuilder();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
                addressStringBuilder.append(address.getAddressLine(i)).append(' ');
            }

            mCountryCode = address.getCountryCode();
            mCountryName = address.getCountryName();

            mCityCode = address.getLocality();

            if (setTextToEditText) {
                mAddress = addressStringBuilder.toString();
                mLoctionmAutocompleteView.setText(addressStringBuilder.toString());
            }

            setAnswer(mAddress);
        }

    }

    @Override
    public JSONObject createLocationJsonObject(String locationStr) {
        JSONObject jsonObject = new JSONObject();
        String raiusStr = mRadiusEditText.getText().toString();
        radius = TextUtils.isEmpty(raiusStr) ? 0 : Double.parseDouble(raiusStr);

        try {
            //"{\"type\":\"point\",\"value\":[[" + mSelectedPlaceLatLng.latitude + "," + mSelectedPlaceLatLng.longitude + "]]}"
            JSONObject coordinateJsonObject = new JSONObject();
            JSONArray coordinateJsonArray1 = new JSONArray();
            JSONArray coordinateJsonArray2 = new JSONArray();
            coordinateJsonArray2.put(mSelectedPlaceLatLng.latitude);
            coordinateJsonArray2.put(mSelectedPlaceLatLng.longitude);
            coordinateJsonArray1.put(coordinateJsonArray2);
            coordinateJsonObject.put("type", "point");
            coordinateJsonObject.put("value", coordinateJsonArray1);

            JSONObject addressJson = new JSONObject();
            addressJson.put(ds.getCulture(), mAddress);

            jsonObject.put("coordinates", coordinateJsonObject);
            //jsonObject.put("city_code", mCityCode);
            jsonObject.put("city", mCityCode);
            jsonObject.put("country_code", mCountryCode);
            jsonObject.put("text_address", addressJson);
            jsonObject.put("distance_units", "km");
            jsonObject.put("country_name ", mCountryName);
            jsonObject.put("distance_value", getRadius());
            jsonObject.put("place_name", isPlace ? placeName : "");
            jsonObject.put("place_id", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    private double getUnit() {

        String userDistance = ds.getUser().user_settings.distance;
        if (userDistance == null || userDistance.equals("") || userDistance.toLowerCase().equals(Types.UNIT_KM)) {
            return 1000;
        } else
            return 1600;

    }

    public float getZoomLevel(float radius) {
        double scale = radius / 500;
        float answer = (int) (16 - Math.log(scale) / Math.log(2));
        return answer;
    }

    public double getRadius() {
        if (mRadiusEditText.getText().toString().length() == 0) {
            /*user does not set radius*/
                /*get default distance from activity*/
            return mActivity.mDataActivity.distance_value * getUnit();
        }

        String radiusStr = mRadiusEditText.getText().toString();
        double radius = TextUtils.isEmpty(radiusStr) ? 0 : Double.parseDouble(radiusStr);
        return radius * getUnit();
    }
}
