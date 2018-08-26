package com.pablo.myroutes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IFragmentsInteractionListener {

    public static final String DAYS_LIST_TAG = "list_of_day";
    public static final String CURRENT_DAY_TAG = "current_day";
    public static final String CURRENT_ROUTE_TAG = "current_route";
    public static final String ADDRESS_LIST_TAG = "list_of_addresses";

    int kilometrageStartDay;

    ArrayList<RoutingDay> routingDayList;
    RoutingDay routingDay;
    Route route;

    private enum  iconColor {Red,Green}

    NotificationManager notificationManager;
    NotificationChannel mChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try{
            Helper.ADDRESS_LIST = (ArrayList<String>)Helper.getObjectByTag(ADDRESS_LIST_TAG,getBaseContext());
        }
        catch (Exception e){
            Helper.ADDRESS_LIST = new ArrayList<>();
        }

        try {
            routingDayList = (ArrayList<RoutingDay>) Helper.getObjectByTag(DAYS_LIST_TAG, getBaseContext());
            kilometrageStartDay = routingDayList.get(routingDayList.size() - 1).getKilometrageOnEndingDay();
        } catch (Exception e) {
            routingDayList = new ArrayList<>();
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

    //@RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onPause() {
        super.onPause();
        try {
            Helper.saveObject(routingDay, CURRENT_DAY_TAG, getBaseContext());
            Helper.saveObject(route, CURRENT_ROUTE_TAG, getBaseContext());
            Helper.saveObject(Helper.ADDRESS_LIST, ADDRESS_LIST_TAG, getBaseContext());

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(route.isOpen()) showNotification("Маршрут не завершен",iconColor.Red);
        else if(routingDay.isOpen()) showNotification("День не завершен",iconColor.Green);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            routingDayList = (ArrayList<RoutingDay>) Helper.getObjectByTag(DAYS_LIST_TAG, getBaseContext());
            kilometrageStartDay = routingDayList.get(routingDayList.size() - 1).getKilometrageOnEndingDay();
        } catch (Exception e) {
            routingDayList = new ArrayList<>();
        }

        try {
            routingDay = (RoutingDay) Helper.getObjectByTag(CURRENT_DAY_TAG, getBaseContext());//routingDay = (RoutingDay) deSerializeObject(CURRENT_DAY_TAG);

        } catch (Exception e) {
        }
        try {
            Helper.ADDRESS_LIST = (ArrayList<String>) Helper.getObjectByTag(ADDRESS_LIST_TAG, getBaseContext());
        } catch (Exception e) {

        }
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.edit_closed_days) {
            Intent intent = new Intent(MainActivity.this, EditorActivity.class);
            intent.putExtra("type", "RoutingDaysListFragment" );
            startActivity(intent);
            return true;
        }
        if(id == R.id.edit_current_day){
            Intent intent = new Intent(MainActivity.this, EditorActivity.class);
            intent.putExtra("type", "RoutesListFragment" );
            startActivity(intent);
            return true;
        }
        if(id==R.id.edit_address_list){
            Intent intent = new Intent(MainActivity.this, EditorActivity.class);
            intent.putExtra("type", "AddressListFragment" );
            startActivity(intent);
            return true;
        }

        if (id == R.id.save) {
            try {
                Helper.backup(new Object[]{routingDayList, routingDay, route, Helper.ADDRESS_LIST});
            } catch (IOException ex) {
                Toast.makeText(getBaseContext(),ex.getMessage(),Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (id == R.id.restore) {
            try {
                Object[] objects = Helper.restore();
                if(objects[0]!=null) routingDayList = (ArrayList<RoutingDay>)objects[0];
                if(objects[1]!=null) routingDay = (RoutingDay)objects[1];
                if(objects[2]!=null) route = (Route)objects[2];
                if(objects[3]!=null) Helper.ADDRESS_LIST = (ArrayList<String>)objects[3];

                Helper.saveObject(routingDay, CURRENT_DAY_TAG, getBaseContext());
                Helper.saveObject(route, CURRENT_ROUTE_TAG, getBaseContext());
                Helper.saveObject(routingDayList, DAYS_LIST_TAG, getBaseContext());
                Helper.saveObject(Helper.ADDRESS_LIST, ADDRESS_LIST_TAG, getBaseContext());

            } catch (Exception ex) {
                Toast.makeText(getBaseContext(),ex.getMessage(),Toast.LENGTH_SHORT).show();
            }
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

    private void showNotification(String message, iconColor IconColor) {

        int iconID;
        if (IconColor.equals(iconColor.Red)){iconID = R.drawable.icon_l_red;}
        else {iconID = R.drawable.icon_l;}

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager == null) {
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }

            // There are hardcoding only for show it just strings
            String name = "channel";
            String id = "channel_ID"; // The user-visible name of the channel.
            String description = ""; // The user-visible description of the channel.

            Intent intent;
            PendingIntent pendingIntent;
            Notification.Builder builder;

            mChannel = notificationManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
                mChannel.setDescription(description);
                notificationManager.createNotificationChannel(mChannel);
            }
            builder = new Notification.Builder(getBaseContext(), mChannel.getId());

            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            builder.setContentTitle(message)  // required
                    .setSmallIcon(iconID) // required
                    .setContentText("")  // required
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            notificationManager.createNotificationChannel(mChannel);

            notificationManager.notify(1, builder.build());
        }
        else{
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(iconID)
                            .setContentTitle(message)
                            .setContentText("");

            Intent resultIntent = new Intent(this, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());
        }
    }
}