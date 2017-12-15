package com.pablo.myroutes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

/**
 * Created by Paul on 23.11.2017.
 */

public class EditorActivity extends AppCompatActivity implements ISaver {

    ArrayList<RoutingDay> RoutingDayList;

    IEditor iAdapterChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            RoutingDayList = (ArrayList<RoutingDay>) Helper.getObjectByTag(MainActivity.DAYS_LIST_TAG, getBaseContext());
        } catch (Exception e) {
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, RoutingDaysListFragment.newInstance(RoutingDayList), "RoutingDaysListFragment").commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        RoutingDaysListFragment fragment = (RoutingDaysListFragment) getSupportFragmentManager().findFragmentByTag("RoutingDaysListFragment");
        if (fragment.isVisible()) {
            iAdapterChanger = fragment;
        } else {
            iAdapterChanger = (RoutesListFragment) getSupportFragmentManager().findFragmentByTag("RoutesListFragment");
        }

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
            Helper.saveObject(RoutingDayList, MainActivity.DAYS_LIST_TAG, getBaseContext());
        } catch (Exception e) {
        }
    }
}
