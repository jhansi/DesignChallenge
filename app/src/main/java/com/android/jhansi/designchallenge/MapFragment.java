package com.android.jhansi.designchallenge;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static View view;

    MapView mMapView;
    private static GoogleMap googleMap;
    private static Double latitude, longitude;
    private LocationRequest mLocationRequest;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleApiClient mGoogleApiClient;

    private TextView textViewUserInfo;
    private TextView textViewAddress;

    private CameraPosition cp;

    public static final String TAG = MapFragment.class.getSimpleName();

    /* GPS Constant Permission */
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container == null) {
            return null;
        }

        view = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) view.findViewById(R.id.location_map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        // retain this fragment
        setRetainInstance(true);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setUpMapIfNeeded(); // For setting up the MapFragment

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        textViewUserInfo = (TextView) view.findViewById(R.id.tv_user_info);
        textViewUserInfo.setText(CommonUtil.getUserName());

        textViewAddress = (TextView) view.findViewById(R.id.tv_address);
        return view;

    }


    /*****
     * Sets up the map if it is possible to do so
     *****/
    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            googleMap = mMapView.getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null)
                setUpMap();
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the
     * camera.
     * This should only be called once and when we are sure that {@link #googleMap}
     * is not null.
     */
    private void setUpMap() {
        MapsInitializer.initialize(getActivity());
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
        if (cp != null) {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
            cp = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        cp = googleMap.getCameraPosition();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

        googleMap = null;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");

        Location location = null;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_ACCESS_FINE_LOCATION);
            }
        } else {
            LocationManager mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }

            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        }

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//
//        //Location location = null;
//        switch (requestCode) {
//            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // We can now safely use the API we requested access to
//                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                        Location myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//                    }
//                } else {
//
//                    // permission denied,
//                }
//                return;
//            }
//        }
//    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);

        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("My Home").snippet("Home Address");

        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_2_9_location_pin));


        // For dropping a marker at a point on the Map
        googleMap.addMarker(marker);

        // For zooming automatically to the Dropped PIN Location
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(15).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        String strAdress = getCompleteAddressString(latitude, longitude);
        textViewAddress.setText(strAdress);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());

        }
    }


    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }


    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                strReturnedAddress.append(returnedAddress.getAddressLine(0)).append(" ");
                strReturnedAddress.append(returnedAddress.getLocality());
                strAdd = strReturnedAddress.toString();
                Log.w(TAG, "" + strReturnedAddress.toString());
            } else {
                Log.w(TAG, "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "Canont get Address!");
        }
        return strAdd;
    }


}
