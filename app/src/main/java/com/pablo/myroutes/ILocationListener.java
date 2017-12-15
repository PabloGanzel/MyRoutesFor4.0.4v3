package com.pablo.myroutes;

import android.location.Location;

/**
 * Created by Paul on 10.12.2017.
 */

public interface ILocationListener
{
    void startLocationUpdate();
    void pauseLocationUpdate();
    Location getLocation();
}
