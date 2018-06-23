package com.pablo.myroutes;

/**
 * Created by Paul on 05.12.2017.
 */

public interface ISaver {
    void save();
    void deleteRouteAndSave(Route route);
}
