package com.makingdevs.mybarista.common

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

class LocationUtil implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String TAG = "LocationUtil"

    Location mLastLocation
    GoogleApiClient mGoogleApiClient
    private LocationRequest mLocationRequest
    private long UPDATE_INTERVAL = 10000
    private long FASTEST_INTERVAL = 2000
    String latitude
    String longitude

    void getClientGoogleLocation(Context context){
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build()
        }
    }

    private void startLocationUpdates() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                //.setNumUpdates(1)
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
    }

    Map getLocation(){
        ["latitude":latitude,"longitude":longitude]
    }

    @Override
    void onConnected(@Nullable Bundle bundle) {
        //Log.d(TAG, "GPS conectado....")
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        if (mLastLocation != null) {
            Log.d(TAG, "Ubicacion previa... " + mLastLocation.toString())
        }
        startLocationUpdates()
    }

    @Override
    void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Log.d(TAG, "GPS desconectado....")
        } else if (i == CAUSE_NETWORK_LOST) {
            Log.d(TAG, "Conexion perdida....")
        }
    }

    @Override
    void onLocationChanged(Location location) {
        latitude = location.getLatitude()
        longitude = location.getLongitude()
        getLocation()
        Log.d(TAG, "Ubicaion actual: $latitude $longitude")
    }

    @Override
    void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Error...." + connectionResult.errorMessage)
    }
}