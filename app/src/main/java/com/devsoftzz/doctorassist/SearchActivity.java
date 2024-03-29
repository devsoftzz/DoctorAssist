package com.devsoftzz.doctorassist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EdgeEffect;
import android.widget.TextView;
import android.widget.Toast;

import com.devsoftzz.doctorassist.ApiHelpers.APIClient;
import com.devsoftzz.doctorassist.ApiHelpers.ApiInterface;
import com.devsoftzz.doctorassist.Models.Place;
import com.devsoftzz.doctorassist.Models.notesAdapter;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback , notesAdapter.OnNoteListner {

    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;
    LatLng current = new LatLng(19.114029,72.882952);
    private CameraPosition mCameraPosition;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private Location mLastKnownLocation;
    LocationCallback locationCallback;

    private LocationRequest mLocationRequest;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private String type;
    private ArrayList<Place.Result> data = new ArrayList<>();
    private notesAdapter mAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        setContentView(R.layout.activity_search);
        progressDialog = ProgressDialog.show(this,"Please Wait","Finding Hospitals",true,false);
        type = getIntent().getStringExtra("Type");
        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        getLocationPermission();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            locationEnable();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    locationEnable();
                }
            }
        }
    }
    private void getDeviceLocation() {

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if(location!=null)
                    {
                        current = new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.setMyLocationEnabled(true);
                        ApiCall(current);
                        stopLocationUpdates();
                        break;
                    }
                }
            }};
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,locationCallback, null );
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case 101:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //Toast.makeText(SearchActivity.this, states.isLocationPresent() + "", Toast.LENGTH_SHORT).show();
                        getDeviceLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        //Toast.makeText(SearchActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }
    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
    public void locationEnable()
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10);
        mLocationRequest.setSmallestDisplacement(10);
        mLocationRequest.setFastestInterval(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        Task<LocationSettingsResponse> task=LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    getDeviceLocation();

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                resolvable.startResolutionForResult(
                                        SearchActivity.this, 101);
                            } catch (IntentSender.SendIntentException e) {
                            } catch (ClassCastException e) {}
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });
    }
/*--------------------------------------APi CALL-------------------------------------------------------------*/

    void ApiCall(final LatLng current)
    {
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);
        Call<Place> call = apiService.getPlaces(String.valueOf(current.latitude)+","+String.valueOf(current.longitude),"50000","hospital",type,"AIzaSyCHnirJxrTjyh0JYG9HZOe5RazRhtjYFl0");
        call.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {

                if (response.isSuccessful()) {

                    List<Place.Result> objectList = response.body().getResults();

                    for (int i = 0; i < objectList.size(); i++) {

                        Place.Result result = objectList.get(i);
                        data.add(result);
                    }

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(current);
                    for(int i=0;i<data.size();i++){
                        MarkerOptions markerOptions = new MarkerOptions();
                        Place.Result result = data.get(i);
                        String placeName = result.getName();
                        double lat = result.getGeometry().getLocation().getLat();
                        double lng = result.getGeometry().getLocation().getLng();

                        LatLng latLng = new LatLng(lat,lng);
                        markerOptions.position(latLng);
                        markerOptions.title(placeName);

                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        Marker marker = mMap.addMarker(markerOptions);
                        marker.setTag(String.valueOf(i));

                        builder.include(latLng);
                    }
                    LatLngBounds bounds = builder.build();
                    final int padding = 100;
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.animateCamera(cu);
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {

                            int position = Integer.valueOf(marker.getTag().toString());
                            Gson gson = new Gson();
                            Place.Result result = data.get(position);
                            String data = gson.toJson(result);
                            Intent intent = new Intent(SearchActivity.this,Hospital_Detailed.class);
                            intent.putExtra("Data",data);
                            intent.putExtra("Lat",String.valueOf(current.latitude));
                            intent.putExtra("Lng",String.valueOf(current.longitude));
                            startActivity(intent);
                            return false;
                        }
                    });
                    botoomSheetUpdate();
                } else {
                    Toast.makeText(SearchActivity.this, response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                Toast.makeText(SearchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void botoomSheetUpdate() {
        RecyclerView recyclerView = findViewById(R.id.r1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new notesAdapter(data , this);
        recyclerView.setAdapter(mAdapter);
        progressDialog.dismiss();
    }
    @Override
    public void onNoteClick(int position, Place.Result note) {
        Gson gson = new Gson();
        String data = gson.toJson(note);
        Intent intent = new Intent(SearchActivity.this,Hospital_Detailed.class);
        intent.putExtra("Data",data);
        intent.putExtra("Lat",String.valueOf(current.latitude));
        intent.putExtra("Lng",String.valueOf(current.longitude));
        startActivity(intent);
    }
}
