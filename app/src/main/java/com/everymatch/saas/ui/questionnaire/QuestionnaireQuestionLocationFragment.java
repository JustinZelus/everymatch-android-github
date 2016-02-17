package com.everymatch.saas.ui.questionnaire;

import android.Manifest;
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
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.PlaceAutocompleteAdapter;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.client.data.Types;
import com.everymatch.saas.server.Data.DataLocation;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionnaireQuestionLocationFragment extends QuestionnaireQuestionBaseFragment implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final String TAG = QuestionnaireQuestionLocationFragment.class.getSimpleName();
    public static final float DEFAULT_ZOOM_LEVEL = 12;

    //Views
    MapView mMapView;
    TextView btnMyLocation;
    TextView mRadiusTextView;
    EditText mRadiusEditText;
    AutoCompleteTextView etAddressInput;
    BaseTextView tvSetSpecificArea;
    ViewAnimator viewAnimator;

    //Data
    double radius;
    int activityDistanceValue;
    private boolean isPlace;
    boolean flagUserSetRadius = false;
    private boolean isFromLocationClick;
    DataLocation mDataLocation;
    private boolean isEventMode = false;

    //MapData
    GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

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

        mAdapter = new PlaceAutocompleteAdapter(mActivity, R.layout.view_place_auto_complete, mGoogleApiClient, BOUNDS_GREATER_SYDNEY, null);
        mDataLocation = new DataLocation();

        if (mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.CREATE_EVENT || mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.EDIT_EVENT)
            isEventMode = true;

        Nammu.askForPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION, new PermissionCallback() {
            @Override
            public void permissionGranted() {
            }

            @Override
            public void permissionRefused() {
                EMLog.d(TAG, " permissionRefused LOCATION");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

        setHeader();
    }

    @Override
    protected void setHeader() {
        super.setHeader();

        if (mActivity.isInEditMode())
            mHeader.setSaveCancelMode(dm.getResourceText(R.string.Edit_Event_Location));
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
        btnMyLocation = (TextView) view.findViewById(R.id.btnMyLocation);
        btnMyLocation.setText(Consts.Icons.icon_GPS);
        btnMyLocation.setOnClickListener(this);

        etAddressInput = (AutoCompleteTextView) view.findViewById(R.id.location_input);
        mRadiusEditText = (EditText) view.findViewById(R.id.radius_edittext);
        setRadiusValue();
        mRadiusEditText.addTextChangedListener(radiusTextWatcher);

        view.findViewById(R.id.typeLocationHolder).setBackgroundDrawable(ShapeDrawableUtils.getButtonStroked(ds.getIntColor(EMColor.FOG)));
        view.findViewById(R.id.radiusWrapper).setBackgroundDrawable(ShapeDrawableUtils.getButtonStroked(ds.getIntColor(EMColor.FOG)));

        tvSetSpecificArea = (BaseTextView) view.findViewById(R.id.tvSetRadiusStageOne);
        tvSetSpecificArea.setOnClickListener(this);

        viewAnimator = (ViewAnimator) view.findViewById(R.id.viewAnimator);
        viewAnimator.setInAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left));
        viewAnimator.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right));


        if (isEventMode) {
            tvSetSpecificArea.setVisibility(View.GONE);
            viewAnimator.setDisplayedChild(0);
        }

        //set input field settings
        etAddressInput.setHint(dm.getResourceText(getString(R.string.Type_Name_Or_Location)));
        etAddressInput.setTextColor(ds.getIntColor(EMColor.NIGHT));
        etAddressInput.setInputType((InputType.TYPE_TEXT_FLAG_AUTO_CORRECT));
        etAddressInput.setOnItemClickListener(mAutocompleteClickListener);
        etAddressInput.setAdapter(mAdapter);
        etAddressInput.addTextChangedListener(textWatcher);
        etAddressInput.post(new Runnable() {
            @Override
            public void run() {
                etAddressInput.setDropDownWidth(etAddressInput.getMeasuredWidth() + Utils.dpToPx(50));
            }
        });

        recoverAnswer();

    }

    private void setRadiusValue() {
        /*set radius value according to the activity and user distance unit*/
        //if(mActivity.mGeneratedEvent.dataPublicEvent.getLocation().)
        try {
            activityDistanceValue = mActivity.mDataActivity.distance_value;
        } catch (Exception ex) {
            EMLog.e(TAG, "Could not load distance value for location question");
        }
        radius = activityDistanceValue / getUnit();
        mRadiusEditText.setText("" + Math.round(radius));
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
            if (!mQuestionAndAnswer.userAnswerData.has("value"))
                return;

            JSONObject jsonObject = mQuestionAndAnswer.userAnswerData.getJSONObject("value");
            mDataLocation = DataLocation.fromJsonObject(jsonObject);
            radius = mDataLocation.distance_value / getUnit();
            etAddressInput.setText(mDataLocation.text_address);
            onLocationSelected(mDataLocation.getLatLng());

            if (radius != (activityDistanceValue / getUnit())) {
                //user has set the radius manually
                mRadiusEditText.setText("" + Math.round(radius));
                tvSetSpecificArea.performClick();
            }
            setAnswer(mDataLocation.getTitle());
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
            case R.id.btnMyLocation:
                isFromLocationClick = true;
                /*show set specific area if needed*/
                if (!isEventMode)
                    tvSetSpecificArea.setVisibility(View.VISIBLE);
                else {
                    viewAnimator.setDisplayedChild(0);
                    tvSetSpecificArea.setVisibility(View.GONE);
                }
                if (btnMyLocation.getText().equals(Consts.Icons.icon_GPS))
                    setCurrentLocationSelected();
                else {
                    clearAnswer();
                }
                break;

            case R.id.tvSetRadiusStageOne:
                if (mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.EDIT_EVENT || mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.CREATE_EVENT)
                    return;
                viewAnimator.showNext();
                mRadiusEditText.setText(mRadiusEditText.getText());
                break;
        }
    }

    public void clearAnswer() {
        super.clearAnswer();
        etAddressInput.setText("");
        etAddressInput.clearListSelection();
        clearMarksOnMap();
        tvSetSpecificArea.setVisibility(View.GONE);
        viewAnimator.setDisplayedChild(0);

    }


    private void setCurrentLocationSelected() {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null) {
            onLocationSelected(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
            getLocationAddress(lastLocation.getLatitude(), lastLocation.getLongitude(), true);
            hideKeyboard();
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
            hideKeyboard();
            isFromLocationClick = false;

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
                EMLog.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            try {
                // Get the Place object from the buffer.
                final Place place = places.get(0);
                List<Integer> type = place.getPlaceTypes();
                if (type.contains(Place.TYPE_STREET_ADDRESS) || type.contains(Place.TYPE_ROUTE) || type.contains(Place.TYPE_LOCALITY) || type.contains(Place.TYPE_POSTAL_CODE)) {
                    EMLog.d(TAG, "Place is street address");
                    isPlace = false;
                } else {
                    EMLog.d(TAG, "Place is not address");
                    isPlace = true;
                    mDataLocation.place_name = place.getName().toString();
                }

                onLocationSelected(place.getLatLng());
                getLocationAddress(place.getLatLng().latitude, place.getLatLng().longitude, false);

                Log.i(TAG, "Place details received: " + place.getName());
            } catch (Exception e) {
                EMLog.e(TAG, e.getMessage());
            }
            places.release();
        }
    };


    private void onLocationSelected(final LatLng selectedPlaceLatLng) {
        onLocationSelected(selectedPlaceLatLng, true);
    }

    private void onLocationSelected(final LatLng selectedPlaceLatLng, boolean closeKeyboard) {
        //mSelectedPlaceLatLng = selectedPlaceLatLng;
        mDataLocation.setLatLon(selectedPlaceLatLng);
        GoogleMap map = mMapView.getMap();
        if (map != null) {
            Log.i(TAG, "setting the map to lat/lng: " + selectedPlaceLatLng);

            //hideKeyboard();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    markPlaceOnMap(mDataLocation.getLatLng());
                }
            }, 1000);
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
        if (ds.getUser().user_settings.getDistance().equals(Types.UNIT_KM))
            return 1 * 1000;
        else
            return 1 * 1600;
    }

    private void getLocationAddress(double latitude, double longitude, boolean setTextToEditText) {
        List<Address> addresses = null;

        try {
            Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude,/*In this sample, get just a single address.*/ 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isEventMode)
            tvSetSpecificArea.setVisibility(View.VISIBLE);

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
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

            //check if came from location button click of autoComplete item click
            if (isFromLocationClick) {
                String addressTmp = address.getThoroughfare() + " " + address.getSubThoroughfare() + " " + address.getLocality() + " " +
                        address.getCountryName();
                etAddressInput.setText(addressTmp);
            }

            mDataLocation.country_code = address.getCountryCode();
            mDataLocation.city = address.getLocality();
            mDataLocation.distance_units = ds.getUser().user_settings.getDistance();
            mDataLocation.distance_value = getRadius();
            mDataLocation.place_name = (isPlace ? mDataLocation.place_name : "");
            mDataLocation.text_address = etAddressInput.getText().toString();

            setAnswer(mDataLocation.getTitle());
        }
    }

    @Override
    public JSONObject createLocationJsonObject(String locationStr) {
        JSONObject value = new JSONObject();
        String radiusStr = mRadiusEditText.getText().toString();
        radius = TextUtils.isEmpty(radiusStr) ? 0 : Double.parseDouble(radiusStr);

        try {
            JSONObject coordinateJsonObject = new JSONObject();
            JSONArray coordinateJsonArray1 = new JSONArray();
            JSONArray coordinateJsonArray2 = new JSONArray();
            coordinateJsonArray2.put(mDataLocation.getLatLng().latitude);
            coordinateJsonArray2.put(mDataLocation.getLatLng().longitude);
            coordinateJsonArray1.put(coordinateJsonArray2);
            coordinateJsonObject.put("type", "point");
            coordinateJsonObject.put("value", coordinateJsonArray1);

            value.put("coordinates", coordinateJsonObject);
            value.put("city", mDataLocation.city);
            value.put("country_code", mDataLocation.country_code);
            value.put("country_name ", mDataLocation.country_name);
            value.put("text_address", mDataLocation.text_address);
            value.put("distance_units", ds.getUser().user_settings.getDistance());
            value.put("distance_value", getRadius());
            value.put("place_name", isPlace ? mDataLocation.place_name : "");
            value.put("place_id", mDataLocation.place_id);


        } catch (Exception e) {
            e.printStackTrace();
            EMLog.e(TAG, e.getMessage());
        }

        return value;
    }

    private double getUnit() {
        String userDistance = ds.getUser().user_settings.getDistance();
        if (userDistance.equals(Types.UNIT_KM)) {
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

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0)
                btnMyLocation.setText(Consts.Icons.icon_StatusNegative);
            else btnMyLocation.setText(Consts.Icons.icon_GPS);
        }
    };

    TextWatcher radiusTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            flagUserSetRadius = true;
            if (mHaveAnswer) {
                onLocationSelected(mDataLocation.getLatLng(), false);
                setAnswer(mDataLocation.getTitle());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().trim().equals("0"))
                mRadiusEditText.setText("1");
        }
    };

}
