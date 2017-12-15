package com.pablo.myroutes;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Paul on 23.11.2017.
 */

public class RoutesListFragment extends ListFragment implements IEditor {

    Context context;

    private static final String ARG_PARAM = "param";

    private RoutingDay routingDay;

    ListAdapter singleChoiceAdapter;
    ListAdapter multiChoiceAdapter;

    private ArrayList<Integer> indexesForDelete;
    ArrayList<Map<String, String>> arrayList;

    Snackbar snackbar;

    public static RoutesListFragment newInstance(RoutingDay param) {
        RoutesListFragment fragment = new RoutesListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            routingDay = (RoutingDay) getArguments().getSerializable(ARG_PARAM);
        }
        arrayList = new ArrayList<>();
        //String[] routes = new String[routingDay.getListOfRoutes().size()];
        for (int i = 0; i < routingDay.getListOfRoutes().size(); i++) {
            Map map = new HashMap<>();
            map.put("route", routingDay.getListOfRoutes().get(i).getStartPoint() + " - " + routingDay.getListOfRoutes().get(i).getEndPoint());
            map.put("info", routingDay.getListOfRoutes().get(i).getLength() + " км., " + routingDay.getListOfRoutes().get(i).getDuration() + " мин.");
            arrayList.add(map);
        }

        singleChoiceAdapter = new SimpleAdapter(getContext(),
                arrayList,
                android.R.layout.simple_list_item_2,
                new String[]{"route", "info"},
                new int[]{android.R.id.text1, android.R.id.text2});

        multiChoiceAdapter = new SimpleAdapter(getContext(),
                arrayList,
                android.R.layout.simple_list_item_multiple_choice,
                new String[]{"route", "info"},
                new int[]{android.R.id.text1, android.R.id.text2});

        setListAdapter(singleChoiceAdapter);
        ListView l = getListView();
        l.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            //            /* / обработка долгого нажатия на элемент */
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                deleting();
                return true;
            }
        });
    }

    //ArrayList<Integer> listOfItemForDeleting = new ArrayList<Integer>();

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (getListAdapter().equals(singleChoiceAdapter)) {
            //TODO: переход на редактирование маршрута
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout,
                            EditRouteFragment.newInstance(
                                    routingDay.getListOfRoutes().get(position)),
                                    "EditRouteFragment")
                    .addToBackStack(null)
                    .commit();
        }
        try {
            SparseBooleanArray chosen = getListView().getCheckedItemPositions();
            indexesForDelete = new ArrayList<>();
            for (int i = 0; i < chosen.size(); i++) {
                if (chosen.valueAt(i)) {
                    indexesForDelete.add(chosen.keyAt(i));
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void deleting() {
        if (snackbar == null) {
            snackbar = Snackbar.make(getListView(), "Выберите объекты", Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.delete, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        List<RoutingDay> daysList = (ArrayList)Helper.getObjectByTag(MainActivity.DAYS_LIST_TAG, getContext());
                        int index = daysList.indexOf(routingDay);
                        for (int i = indexesForDelete.size() - 1; i >= 0; i--) {
                            int ifd = indexesForDelete.get(i);
                            routingDay.getListOfRoutes().remove(ifd);
                            arrayList.remove(ifd);

                            //EditorActivity.
                        }
                        daysList.set(index, routingDay);
                        Helper.saveObject(daysList,MainActivity.DAYS_LIST_TAG,getContext());
                        Toast.makeText(getContext(), "удалено", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                    setListAdapter(singleChoiceAdapter);
                    getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    //Toast.makeText(getContext(), "удаление маршрута", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (getListAdapter() == singleChoiceAdapter) {
            setListAdapter(multiChoiceAdapter);
            getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            snackbar.show();
        } else {
            setListAdapter(singleChoiceAdapter);
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            snackbar.dismiss();
            snackbar = null;
        }
    }

    @Override
    public void exporting() {
        if (snackbar == null) {
            snackbar = Snackbar.make(getListView(), "Экспортировать?", Snackbar.LENGTH_SHORT).setAction(R.string.export, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ExportAsyncTask().execute(routingDay);
                }
            });
        }
//        if (getListAdapter() == singleChoiceAdapter) {
//            setListAdapter(multiChoiceAdapter);
//            getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        snackbar.show();
//        } else {
//            setListAdapter(singleChoiceAdapter);
//            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//            snackbar.dismiss();
//        }
    }

    private class ExportAsyncTask extends AsyncTask<RoutingDay, String, String> {

        @Override
        protected void onPreExecute() {
            Toast.makeText(getContext(), "Экспорт будет произведен в фоновом режиме", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(RoutingDay... strings) {
            try {
                ExportService.Export(strings[0]);
                return "Экспорт звершен";
            } catch (FileNotFoundException e) {
                return "Ошибка доступа к карте памяти";
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String param) {
            Toast.makeText(context, param, Toast.LENGTH_SHORT).show();
        }
    }
}