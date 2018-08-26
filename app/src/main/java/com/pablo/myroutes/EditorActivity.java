package com.pablo.myroutes;

import android.os.Bundle;
import android.support.constraint.solver.SolverVariable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

/**
 * Created by Paul on 23.11.2017.
 */

public class EditorActivity extends AppCompatActivity implements ISaver {

    ArrayList<RoutingDay> routingDayList;
    RoutingDay routingDay;
    ArrayList<String> addressList;

    IEditor iAdapterChanger;

    public static String CURRENT_FRAGMENT_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            routingDayList = (ArrayList<RoutingDay>) Helper.getObjectByTag(MainActivity.DAYS_LIST_TAG, getBaseContext());
            routingDay = (RoutingDay) Helper.getObjectByTag(MainActivity.CURRENT_DAY_TAG, getBaseContext());
            addressList = (ArrayList<String>) Helper.getObjectByTag(MainActivity.ADDRESS_LIST_TAG, getBaseContext());
        } catch (Exception e) {
        }
        String s = getIntent().getExtras().getString("type");
        if (getIntent().getExtras().getString("type").equals("RoutingDaysListFragment")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, RoutingDaysListFragment.newInstance(routingDayList), "RoutingDaysListFragment").commit();
        } else if (getIntent().getExtras().getString("type").equals("RoutesListFragment")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, RoutesListFragment.newInstance(routingDay), "RoutesListFragment").commit();
        } else if (getIntent().getExtras().getString("type").equals("AddressListFragment")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, AddressListFragment.newInstance(addressList), "AddressListFragment").commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (EditorActivity.CURRENT_FRAGMENT_TAG) {
            case "RoutingDaysListFragment":
                iAdapterChanger = (RoutingDaysListFragment) getSupportFragmentManager().findFragmentByTag(EditorActivity.CURRENT_FRAGMENT_TAG);
                break;
            case "RoutesListFragment":
                iAdapterChanger = (RoutesListFragment) getSupportFragmentManager().findFragmentByTag(EditorActivity.CURRENT_FRAGMENT_TAG);
                break;
            case "AddressListFragment":
                iAdapterChanger = (AddressListFragment) getSupportFragmentManager().findFragmentByTag(EditorActivity.CURRENT_FRAGMENT_TAG);
                break;
            case "EditRouteFragment":
                iAdapterChanger = (EditRouteFragment) getSupportFragmentManager().findFragmentByTag(EditorActivity.CURRENT_FRAGMENT_TAG);
                break;
            default:
                break;
        }

        //test.newInstance();
        //EditorActivity.CURRENT_FRAGMENT_TAG.getClass();

        //RoutingDaysListFragment fragment = (RoutingDaysListFragment) getSupportFragmentManager().findFragmentByTag("RoutingDaysListFragment");
        // (fragment.isVisible()) {
        //    iAdapterChanger = fragment;
        //} else {
        //    iAdapterChanger = (RoutesListFragment) getSupportFragmentManager().findFragmentByTag("RoutesListFragment");
        //}


        if (id == R.id.action_delete) {
            iAdapterChanger.deleting();
        }

        if (id == R.id.action_export) {
            iAdapterChanger.exporting();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void save() {
        try {
            Helper.saveObject(routingDayList, MainActivity.DAYS_LIST_TAG, getBaseContext());
            Helper.saveObject(routingDay, MainActivity.CURRENT_DAY_TAG, getBaseContext());
        } catch (Exception e) {
        }
    }

    @Override
    public void deleteRouteAndSave(Route route) {
        routingDayList.remove(route);
        save();
    }
}
