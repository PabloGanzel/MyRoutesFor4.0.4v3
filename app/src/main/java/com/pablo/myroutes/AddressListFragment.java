package com.pablo.myroutes;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.poi.ss.formula.functions.T;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by Paul on 23.11.2017.
 */

public class AddressListFragment extends ListFragment implements IEditor {

    private static final String ARG_PARAM = "param";

    Context context;

    ListAdapter singleChoiceAdapter;
    ListAdapter multiChoiceAdapter;

    //private ArrayList<RoutingDay> daysList;
    private ArrayList<Integer> indexesForDelete;
    private ArrayList<String> addressList;

    Snackbar snackbar;

    public static AddressListFragment newInstance(ArrayList<String> param) {
        AddressListFragment fragment = new AddressListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        EditorActivity.CURRENT_FRAGMENT_TAG = "AddressListFragment";

        try {
            addressList = (ArrayList<String>) getArguments().getSerializable(ARG_PARAM);
        }
        catch (Exception ex){

        }

        singleChoiceAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, addressList);
//        multiChoiceAdapter = new ArrayAdapter<>(getActivity(),
//                android.R.layout.simple_list_item_multiple_choice, addressList);
        multiChoiceAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.my_list_item_multiplie_choice, addressList);
        //setListAdapter(new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,addressList));
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
    public void onListItemClick(ListView l, View v, final int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (getListAdapter().equals(singleChoiceAdapter)) {

//            getActivity()
//                    .getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.frameLayout,
//                            RoutesListFragment.newInstance(addressList.get(position)),
//                            "RoutesListFragment")
//                    .addToBackStack(null)
//                    .commit();
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

            alert.setTitle("Редактировать");
            //alert.setMessage("Message");

// Set an EditText view to get user input
            final EditText input = new EditText(getContext());
            input.setText(addressList.get(position));
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = input.getText().toString();
                    addressList.set(position, value);
                    try {
                        Helper.saveObject(addressList, MainActivity.ADDRESS_LIST_TAG, getContext());
                        Toast.makeText(getContext(), "Сохранено", Toast.LENGTH_SHORT).show();
                        setListAdapter(singleChoiceAdapter);
                    } catch (Exception ex) {
                        Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    //Toast.makeText(getContext(),value, Toast.LENGTH_SHORT).show();
                    // Do something with value!
                }
            });

            alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();
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
                                    addressList.remove(ifd);
                                    //dateList.remove(ifd);

                                }
                                Helper.saveObject(addressList, MainActivity.ADDRESS_LIST_TAG, getContext());
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

    }
}