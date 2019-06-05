package com.protection.plpt.plpt.mpkz.mpkz.method;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.protection.plpt.plpt.mpkz.mpkz.net.AsyncRequestor;
import com.protection.plpt.plpt.mpkz.mpkz.net.request.InfoRequest;

public class LocationMethod {

    private static Location resultLocation;

    public static Location getLocationCoordinates(Context context, AsyncRequestor requestor, InfoRequest request) {

        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {

            @Override
            public void gotLocation(Location location) {
                Log.i("BACKUP", "longitude: " + location.getLongitude() + " latitude: " + location.getLatitude());
                resultLocation = location;
            }
        };

        MyLocation myLocation = new MyLocation(requestor, request);
        myLocation.getLocation(context, locationResult);


        return resultLocation;
    }

}
