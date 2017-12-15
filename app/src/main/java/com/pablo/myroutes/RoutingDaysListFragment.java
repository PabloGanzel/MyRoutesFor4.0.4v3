package com.pablo.myroutes;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by Paul on 23.11.2017.
 */

public class RoutingDaysListFragment extends ListFragment implements IEditor {

    private static final String ARG_PARAM = "param";

    Context context;

    ListAdapter singleChoiceAdapter;
    ListAdapter multiChoiceAdapter;

    private ArrayList<RoutingDay> daysList;
    private ArrayList<Integer> indexesForDelete;
    private ArrayList<String> dateList;

    Snackbar snackbar;

    public static RoutingDaysListFragment newInstance(ArrayList<RoutingDay> param) {
        RoutingDaysListFragment fragment = new RoutingDaysListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* / получение массива дат */
        if (getArguments() != null) {
            daysList = (ArrayList<RoutingDay>) getArguments().getSerializable(ARG_PARAM);
            dateList = new ArrayList<>();
            try {
                for (int i = 0; i < daysList.size(); i++) {
                    dateList.add(daysList.get(i).date);
                }
            } catch (Exception e) {
            }
        }

        singleChoiceAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, dateList);
        multiChoiceAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_multiple_choice, dateList);
        setListAdapter(singleChoiceAdapter);
        ListView l = getListView();
        l.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            /* / обработка долгого нажатия на элемент */
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                deleting();
                return true;
            }
        });
    }

    /* / обработка нажатия на элемент */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (getListAdapter().equals(singleChoiceAdapter)) {
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout,
                            RoutesListFragment.newInstance(daysList.get(position)),
                            "RoutesListFragment")
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
                                for (int i = indexesForDelete.size() - 1; i >= 0; i--) {
                                    int ifd = indexesForDelete.get(i);
                                    daysList.remove(ifd);
                                    dateList.remove(ifd);

                                }
                                Helper.saveObject(daysList, MainActivity.DAYS_LIST_TAG, getContext());
                                //Toast.makeText(getContext(),s,Toast.LENGTH_SHORT).show();
                                Toast.makeText(getContext(), "удалено", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            }
                            setListAdapter(singleChoiceAdapter);
                            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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

        final ArrayList<RoutingDay> rdl = new ArrayList<>();
        if (snackbar == null) {
            snackbar = Snackbar.make(getListView(), "Выберите объекты", Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.export, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Toast.makeText(getContext(), "экспорт", Toast.LENGTH_SHORT).show();
                            //try {
                            for (int i = indexesForDelete.size() - 1; i >= 0; i--) {
                                rdl.add(daysList.get(indexesForDelete.get(i)));
                            }
                            new ExportAsyncTask().execute(rdl.toArray(new RoutingDay[rdl.size()]));

                            setListAdapter(singleChoiceAdapter);
                            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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
        }
    }

    private class ExportAsyncTask extends AsyncTask<RoutingDay[], String, String> {

        @Override
        protected void onPreExecute() {
            Toast.makeText(getContext(), "Экспорт будет произведен в фоновом режиме", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(RoutingDay[]... strings) {
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
