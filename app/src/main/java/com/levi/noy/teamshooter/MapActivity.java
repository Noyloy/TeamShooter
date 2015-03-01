package com.levi.noy.teamshooter;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;

    private Location location;
    private LocationManager locationManager;

    private double lat;
    private double longi;

    private long minTimeUpdate = 1000*10; // every 10 seconds
    private float minDistUpdate = 10; // every 10 meters
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpLocationManager();
                setUpMap();
            }
        }
    }

    private void setUpLocationManager(){
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            // if we can get location
            if (isGPSEnabled || isNetworkEnabled){
                canGetLocation = true;
                // can we get location by network
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            locationManager.NETWORK_PROVIDER,
                            minTimeUpdate,
                            minDistUpdate,
                            this
                    );
                    if (locationManager != null) {
                        location =
                                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            lat = location.getLatitude();
                            longi = location.getLongitude();
                        }

                    }
                }
                // can we get location by GPS
                if (isGPSEnabled){
                    if (location == null){
                        locationManager.requestLocationUpdates(
                                locationManager.GPS_PROVIDER,
                                minTimeUpdate,
                                minDistUpdate,
                                this
                        );
                    }
                    if (locationManager != null){
                        location =
                                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null){
                            lat = location.getLatitude();
                            longi = location.getLongitude();
                        }

                    }
                }
            }
            // we cant get location
            else{
                Toast.makeText(getApplicationContext(),"No Location Services Found",Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(lat,longi));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(20);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, longi)).title("You").icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker)));
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).title("Enemy")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker))
                .rotation((float) Math.toDegrees(Math.atan((lat - latLng.latitude) / (longi - latLng.longitude))) + 180));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTitle().toString().toLowerCase().equals("enemy")){
            marker.remove();
        }
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {

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
}
