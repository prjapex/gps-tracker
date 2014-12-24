package apex.prj300.ie.myapplication2.app;

import android.graphics.Color;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements LocationListener,
        ConnectionCallbacks, OnConnectionFailedListener,
        OnMyLocationButtonClickListener, View.OnClickListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationClient mLocationClient;
    private static final LocationRequest mLocationRequest = LocationRequest.create()
            .setInterval(20)              // 20ms
            .setFastestInterval(16)         // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    // ArrayList to save LatLng points
    List<LatLng> route = new ArrayList<LatLng>();
    Polyline line;

    OrientationEventListener mOrientationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        orientationEventManager();
        setUpMapIfNeeded();
    }

    private void orientationEventManager() {

        mOrientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                // redraw polyline
                line = mMap.addPolyline(new PolylineOptions()
                        .addAll(route)
                        .width(5f)
                        .color(Color.BLUE)
                        .geodesic(true));
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        wakeUpLocationClient();
        mLocationClient.connect();
    }

    private void wakeUpLocationClient() {
        if(mLocationClient == null) {
            mLocationClient = new LocationClient(getApplicationContext(),
                    this,       // Connection Callbacks
                    this);      // OnConnectionFailedListener
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mLocationClient != null){
            mLocationClient.disconnect();
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        goToMyLocation(latitude, longitude);
        saveRoute(latitude, longitude);
    }

    private void saveRoute(double lat, double lng) {
        route.add(new LatLng(lat, lng));
        Toast.makeText(getApplicationContext(), "Lat: " + lat + " Long: " + lng, Toast.LENGTH_SHORT).show();
        line = mMap.addPolyline(new PolylineOptions()
                .addAll(route)
                .width(5f)
                .color(Color.BLUE)
                .geodesic(true));
    }

    private void goToMyLocation(double lat, double lng) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(lat, lng))
                        .bearing(0)
                        .zoom(15.5f)
                        .tilt(25)
                        .build()
        ), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                //TODO
            }

            @Override
            public void onCancel() {
                //TODO
            }
        });
    }

    private void changeCamera(CameraUpdate update, GoogleMap.CancelableCallback callback) {
        mMap.moveCamera(update);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationClient.requestLocationUpdates(
                mLocationRequest,
                this);  // LocationListener

    }

    @Override
    public void onDisconnected() {
        Toast.makeText(getApplicationContext(),
                "Disconnected!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),
                "Lost Connection! Check your connection",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

}
