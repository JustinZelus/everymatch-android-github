package com.everymatch.saas.ui.event;


import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.everymatch.saas.R;
import com.everymatch.saas.util.IconManager;
import com.everymatch.saas.view.BaseTextView;
import com.everymatch.saas.view.EventHeader;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventLocationFragment extends Fragment implements GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener, EventHeader.OnEventHeader {

    public static final String TAG = EventLocationFragment.class.getSimpleName();
    public static final String EVENT_TITLE = "eventTitle";
    public static final String LAT = "lat";
    public static final String LON = "lon";
    public static final String ADDRESS = "address";

    private EventHeader mHeader;

    private static final float MIN_ZOOM = 4.0f;
    private static final float DEFAULT_ZOOM = 15.0f;
    private static final int DEFAULT_ANIMATION_TIME = 1000;

    protected MapView mMapView;
    protected GoogleMap mGoogleMap;
    protected Marker mMyMarker;
    protected Marker mProviderMarker;
    protected Marker mLastFocusedMarker;
    protected Location mMyLastLocation;
    private String mEventTitle;
    private double mLat;
    private double mLon;
    private String mAddress;
    private BaseTextView mAddressText;
    private BaseTextView mAddressDetailsText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_location, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {

            mLat = bundle.getDouble(LAT);
            mLon = bundle.getDouble(LON);
            mAddress = bundle.getString(ADDRESS);
            mEventTitle = bundle.getString(EVENT_TITLE);
        }

        mMapView = (MapView) view.findViewById(R.id.map);


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHeader = (EventHeader) view.findViewById(R.id.event_location_eventHeader);
        mHeader.setListener(this);

        mHeader.getBackButton().setText(IconManager.getInstance(getActivity()).getIconString("ArrowBack"));
        mHeader.getIconOne().setVisibility(View.GONE);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setVisibility(View.GONE);
        mHeader.setTitle(mEventTitle);

        mMapView.onCreate(savedInstanceState);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(getActivity());

        // Gets to GoogleMap from the MapView and does initialization stuff
        mGoogleMap = mMapView.getMap();
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setOnCameraChangeListener(this);
        mGoogleMap.setOnMarkerClickListener(this);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(mLat, mLon)).zoom(12).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        addMeToMap();

        mAddressText = (BaseTextView)view.findViewById(R.id.event_location_address);
        mAddressDetailsText = (BaseTextView)view.findViewById(R.id.event_location_address_details);
        mAddressText.setText(mAddress);
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        String address = null;
        try {
            List<Address> listAddresses = geocoder.getFromLocation(mLat, mLon, 1);
            if (null != listAddresses && listAddresses.size() > 0) {
                address = listAddresses.get(0).getAddressLine(0) + ", " + listAddresses.get(0).getAddressLine(1) + ", " + listAddresses.get(0).getAddressLine(2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mAddressDetailsText.setText(address);
    }


    @Override
    public void onResume() {
        if (mMapView != null) {
            mMapView.onResume();
        }
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    /**
     * Add my marker to the map
     * <p/>
     * Returns true if my market was added to the map for the first time
     */
    protected boolean addMeToMap() {
        if (mMyMarker == null && mMyLastLocation != null) {
            mMyMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mMyLastLocation.getLatitude(), mMyLastLocation.getLongitude()))
                    .title("ME")
                    /*.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_a_location))*/);
            return true;
        } else {
            mMyMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mLat, mLon))
                    .title("ME")
                    /*.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_a_location))*/);
            return true;
        }

        //return false;
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

        /*// We limit the zoom so the map will be visible when covered by another UI components
        if (cameraPosition.zoom < MIN_ZOOM) {
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(MIN_ZOOM));
        }*/


        /*CameraPosition cameraPosition1 = new CameraPosition.Builder().target(
                new LatLng(31.771959, 35.217018)).zoom(12).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));*/

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if (!isMarkerClickAllowed()) {
            return true;
        }

        if (mLastFocusedMarker != null) {

            // Close the info window
            mLastFocusedMarker.hideInfoWindow();

            // Is the marker the same marker that was already open
            if (mLastFocusedMarker.equals(marker)) {
                // Nullify the lastOpened object
                mLastFocusedMarker = null;
                // Return so that the info window isn't opened again
                return true;
            }
        }

        // Open the info window for the marker
        marker.showInfoWindow();

        // Re-assign the last opened such that we can close it later
        mLastFocusedMarker = marker;

        centerMarker(marker);
        return true;
    }

    /**
     * Return true if marker can be clicked, false otherwise
     */
    protected boolean isMarkerClickAllowed() {
        return true;
    }

    /**
     * Hide the opened marker info window
     */
    protected void hideMarkerInfo() {
        if (mLastFocusedMarker != null) {

            // Close the info window
            mLastFocusedMarker.hideInfoWindow();
            mLastFocusedMarker = null;
        }
    }

    // OVERLOAD
    protected void centerMarker(Marker marker) {
        centerMarker(marker, DEFAULT_ANIMATION_TIME, DEFAULT_ZOOM);
    }

    /**
     * Simply remove marker from map
     */
    protected void removeMarkerFromMap(Marker marker) {
        if (marker != null) {
            marker.remove();

            if (marker == mProviderMarker) {
                mProviderMarker = null;
            }
        }
    }

    /**
     * Center the map to the user location, considering the offset
     * in order to change the natural center
     *
     * @param duration the duration for the animation
     * @param zoom     the required zoom
     */
    protected void centerMarker(Marker marker, int duration, float zoom) {
        Log.i(TAG, "centerMe");

        if (marker == null) {
            return;
        }

        // Save current zoom
        float originalZoom = mGoogleMap.getCameraPosition().zoom;

        // Move temporarily camera zoom
        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));

        Point pointInScreen = mGoogleMap.getProjection().toScreenLocation(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
        Point newPoint = new Point();
        int[] centerOffset = getCenterOffset();
        newPoint.x = pointInScreen.x - centerOffset[0];
        newPoint.y = pointInScreen.y - centerOffset[1];
        LatLng newCenterLatLng = mGoogleMap.getProjection().fromScreenLocation(newPoint);

        // Restore original zoom
        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(originalZoom));

        // Animate a camera with new Latlng center and required zoom.
        if (duration > 0) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newCenterLatLng, zoom), duration, null);
        } else {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newCenterLatLng, zoom));
        }
    }

    /**
     * Return the offset of the center of map
     */
    protected int[] getCenterOffset() {
        return new int[]{0, 0};
    }

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
}


