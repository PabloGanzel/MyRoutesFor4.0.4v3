package com.pablo.myroutes;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IFragmentsInteractionListener {

    public static final String DAYS_LIST_TAG = "list_of_day";
    public static final String CURRENT_DAY_TAG = "current_day";
    public static final String CURRENT_ROUTE_TAG = "current_route";

    int kilometrageStartDay;

    ArrayList<RoutingDay> routingDayList;
    RoutingDay routingDay;
    Route route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            routingDayList = (ArrayList<RoutingDay>) Helper.getObjectByTag(DAYS_LIST_TAG, getBaseContext());
            kilometrageStartDay = routingDayList.get(routingDayList.size() - 1).getKilometrageOnEndingDay();
        } catch (Exception e) {
            routingDayList = new ArrayList<RoutingDay>();
        }

        try {
            routingDay = (RoutingDay) Helper.getObjectByTag(CURRENT_DAY_TAG, getBaseContext());//routingDay = (RoutingDay) deSerializeObject(CURRENT_DAY_TAG);

        } catch (Exception e) { }

        try {
            route = (Route) Helper.getObjectByTag(CURRENT_ROUTE_TAG, getBaseContext()); //route = (Route) deSerializeObject(CURRENT_ROUTE_TAG);

        } catch (Exception e) {
            //routingDayList = new ArrayList<RoutingDay>();
        }

        if (routingDay != null && routingDay.isOpen()) {
            //route = routingDay.getLastRoute();
            if (route != null && route.isOpen()) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, OpenedRouteFragment.newInstance(route)).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, ClosedRouteFragment.newInstance(route)).commit();
            }
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, OpenDayFragment.newInstance(kilometrageStartDay), "OpenDayFragment").commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            Helper.saveObject(routingDay, CURRENT_DAY_TAG, getBaseContext());
            Helper.saveObject(route, CURRENT_ROUTE_TAG, getBaseContext());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        try {
            routingDayList = (ArrayList<RoutingDay>) Helper.getObjectByTag(DAYS_LIST_TAG, getBaseContext());
            kilometrageStartDay = routingDayList.get(routingDayList.size() - 1).getKilometrageOnEndingDay();
        } catch (Exception e) {
            routingDayList = new ArrayList<RoutingDay>();
        }

        try {
            routingDay = (RoutingDay) Helper.getObjectByTag(CURRENT_DAY_TAG, getBaseContext());//routingDay = (RoutingDay) deSerializeObject(CURRENT_DAY_TAG);

        } catch (Exception e) { }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            Intent intent = new Intent(MainActivity.this, EditorActivity.class);

            startActivity(intent);
            return true;
        }
        if (id == R.id.save) {
            try {
                Helper.backup(new Object[]{routingDayList, routingDay, route});
            } catch (IOException ex) {

            }
            //showPopupMenu());
            //Intent intent = new Intent(MainActivity.this,EditorActivity.class);
            //startActivity(intent);
            return true;
        }
        if (id == R.id.restore) {
            try {
                Object[] objects = Helper.restore();
                if(objects[0]!=null) routingDayList = (ArrayList<RoutingDay>)objects[0];
                if(objects[1]!=null) routingDay = (RoutingDay)objects[1];
                if(objects[2]!=null) route = (Route)objects[2];
                Helper.saveObject(routingDay, CURRENT_DAY_TAG, getBaseContext());
                Helper.saveObject(route, CURRENT_ROUTE_TAG, getBaseContext());
                Helper.saveObject(routingDayList, DAYS_LIST_TAG, getBaseContext());
            } catch (Exception ex) {
                Log.d(ex.toString(),ex.getMessage());

            }

            //showPopupMenu());
            //Intent intent = new Intent(MainActivity.this,EditorActivity.class);
            //startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void openRoutingDay(int kilometrageStartDay) {
        routingDay = new RoutingDay(Helper.getDate(), kilometrageStartDay);
        openRoute();
    }

    @Override
    public void openRoute() {

        String startPointAddress;
        int kilometrageStartRoute;
        try {
            startPointAddress = routingDay.getLastRoute().getEndPoint();
            kilometrageStartRoute = routingDay.getLastRoute().getEndKilometrage();
        } catch (Exception ex) {
            startPointAddress = Helper.DEFAULT_POINT_ADDRESS;
            kilometrageStartRoute = routingDay.getKilometrageOnBeginningDay();
        }
        route = new Route(startPointAddress, Helper.getTimeNow(), kilometrageStartRoute);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, OpenedRouteFragment.newInstance(route)).commit();
    }

    @Override
    public void closeRoute(String endPointAddress, int endKilometrage, String endTime) {

        route.setEndPoint(endPointAddress);
        route.setEndKilometrage(endKilometrage);
        route.setEndTime(endTime);
        route.close();

        routingDay.addRoute(route);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, ClosedRouteFragment.newInstance(route)).commit();
    }

    @Override
    public void closeRoutingDay() {

        routingDay.setKilometrageOnEndingDay(routingDay.getLastRoute().getEndKilometrage());
        routingDay.close();
        kilometrageStartDay = routingDay.getKilometrageOnEndingDay();
        routingDayList.add(routingDay);
        try{
            Helper.saveObject(routingDayList,DAYS_LIST_TAG,getBaseContext()); //serializeObject(routingDayList,DAYS_LIST_TAG);

        }catch(Exception e){
            e.printStackTrace();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, OpenDayFragment.newInstance(kilometrageStartDay),"OpenDayFragment").commit();
    }
}
