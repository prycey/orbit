package com.example.orbit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.LocationCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

public class locationGive {
    static Location loc = new Location("");

    public static ParseGeoPoint userLoc(Context context) {

        final ParseGeoPoint point = new ParseGeoPoint();
        LocationManager locationManager;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.i("yee", longitude + " " + latitude);


                }
            });
        } else if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.i("yee", longitude + " " + latitude);

                }
            });


//        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
//        fusedLocationProviderClient.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if (location != null) {
//                    double lat = location.getLatitude();
//                    double lon = location.getLongitude();
//                    loc.setLongitude(lon);
//                    loc.setLatitude(lat);
//                    Log.i("tag", location.toString());
//                }
//
//            }
//
//
//        });
//
//        point.getCurrentLocationInBackground(1000, new LocationCallback() {
//            @Override
//            public void done(ParseGeoPoint geoPoint, ParseException e) {
//                point.setLatitude(geoPoint.getLatitude());
//                point.setLongitude(geoPoint.getLongitude());
//            }
//        });
        getLocation(new locationCallback() {
            @Override
            public void onCallback(Location location) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                loc.setLongitude(lon);
                loc.setLatitude(lat);
            }
        }, context);

        Log.i("tag", loc.toString());
        return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
    }

    public static void getLocation(final locationCallback locationCallback, Context context) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    loc.setLongitude(lon);
                    loc.setLatitude(lat);
                    Log.i("tag", location.toString());
                    locationCallback.onCallback(loc);
                }

            }

        });
    }
}
