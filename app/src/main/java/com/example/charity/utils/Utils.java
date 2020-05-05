package com.example.charity.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

    /**
     * @param context current context
     * @return Status of Internet if enabled
     */
    public static boolean checkInternetAvailability(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    /**
     *
     * @param context Activity context
     * @return Gps enabled status
     */
    public static boolean checkGeolocalisationIfEnabled(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            return false;
        }
        return true;
    }
}
