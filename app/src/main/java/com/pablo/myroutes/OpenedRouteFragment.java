package com.pablo.myroutes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.poi.ss.formula.functions.T;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.*;

/**
 * Created by Paul on 23.11.2017.
 */

public class OpenedRouteFragment extends Fragment {

    AutoCompleteTextView editTextEndPointAddress;
    EditText editTextTimeEnd, editTextKilometrageEnd;
    ProgressBar progressBar;
    TextView textViewProgress;

    private static final String ARG_PARAM = "param";
    private Route route;

    private IFragmentsInteractionListener mListener;

    Location location;
    LocationManager locationManager;
    LocationListener locationListener;
    //Pattern pattern;


    public OpenedRouteFragment() {
        // Required empty public constructor
    }

    public static OpenedRouteFragment newInstance(Route param) {
        OpenedRouteFragment fragment = new OpenedRouteFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            route = (Route) getArguments().getSerializable(ARG_PARAM);
        }
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            public void onLocationChanged(Location loc) {
                if (location == null) {
                    location = loc;
                    new HttpAsyncTask().execute(null, null);
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
                Toast.makeText(getContext(),provider+" не доступен",Toast.LENGTH_SHORT).show();
            }

            public void onProviderDisabled(String provider) {
                Toast.makeText(getContext(),provider+"доступен",Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_opened_route, container, false);

        TextView textViewStartPointAddress = view.findViewById(R.id.textViewStartPointAddress);
        textViewStartPointAddress.setText(route.getStartPoint());

        final TextView textViewTimeStart = view.findViewById(R.id.textViewTimeStart);
        textViewTimeStart.setText(route.getStartTime());

        final TextView textViewKilometrageStartPoint = view.findViewById(R.id.textViewKilometrageStartPoint);
        textViewKilometrageStartPoint.setText(String.valueOf(route.getStartKilometrage()));

        textViewProgress = view.findViewById(R.id.textViewProgress);
        progressBar = view.findViewById(R.id.progressBar);

        //editTextEndPointAddress = view.findViewById(R.id.editTextEndPointAddress);
        editTextTimeEnd = view.findViewById(R.id.editTextTimeEnd);
        editTextTimeEnd.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextKilometrageEnd = view.findViewById(R.id.editTextKilometrageEnd);
        editTextKilometrageEnd.setInputType(InputType.TYPE_CLASS_NUMBER);

        editTextEndPointAddress = view.findViewById(R.id.editTextEndPointAddress);
//        //String[] strings = getResources().getStringArray(R.array.addresses);
//        //List<String> l = Arrays.asList(strings);
        ArrayAdapter<String> addressListAdapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                Arrays.asList(getResources().getStringArray(R.array.addresses))
        );
        editTextEndPointAddress.setAdapter(addressListAdapter);
//        autoCompleteTextView.setText("123");



        final Button buttonPlus = view.findViewById(R.id.buttonPlus);
        buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextKilometrageEnd.setText(String.valueOf(Integer.parseInt(editTextKilometrageEnd.getText().toString()) + 1));
            }
        });

        final Button buttonMinus = view.findViewById(R.id.buttonMinus);
        buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextKilometrageEnd.setText(String.valueOf(Integer.parseInt(editTextKilometrageEnd.getText().toString()) - 1));
            }
        });

        //pattern = Pattern.compile("([01]?\\d|2[0-4]):([0-5]\\d)");
        final Button button = view.findViewById(R.id.buttonEndRoute);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button.getText().toString().equals("OK")) {

                    if(editTextEndPointAddress.getText().toString().equals("")){
                        Toast.makeText(getContext(),"Не указан адрес прибытия", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!Helper.isKilometragePositive(textViewKilometrageStartPoint.getText().toString(),editTextKilometrageEnd.getText().toString())){
                        Toast.makeText(getContext(),"Пробег не может быть отрицательным",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!Helper.isTimeCorrect(textViewTimeStart.getText().toString(),editTextTimeEnd.getText().toString())){
                        Toast.makeText(getContext(), "Некорректное время прибытия",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mListener.closeRoute(editTextEndPointAddress.getText().toString(), Integer.parseInt(editTextKilometrageEnd.getText().toString()), editTextTimeEnd.getText().toString());

                } else {
                    editTextEndPointAddress.setVisibility(View.VISIBLE);
                    //new HttpAsyncTask().execute(null, null);
                    //editTextEndPointAddress.setText(Helper.getAddress());
                    editTextTimeEnd.setVisibility(View.VISIBLE);
                    editTextTimeEnd.setText(Helper.getTimeNow());
                    editTextKilometrageEnd.setVisibility(View.VISIBLE);
                    editTextKilometrageEnd.setText(String.valueOf(route.getStartKilometrage() + 2));
                    buttonPlus.setVisibility(View.VISIBLE);
                    buttonMinus.setVisibility(View.VISIBLE);
                    button.setText("OK");
                }
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        locationManager.removeUpdates(locationListener);
        super.onPause();
    }

    @Override
    public void onResume() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            textViewProgress.setText("Получаем координаты...");
            textViewProgress.setVisibility(View.VISIBLE);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);
            //locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000 * 10, 10, locationListener);
        } catch (SecurityException ex) {
        }
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IFragmentsInteractionListener) {
            mListener = (IFragmentsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private class HttpAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            textViewProgress.setVisibility(View.VISIBLE);
            textViewProgress.setText("Получаем адрес...");
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                return AddressService.getAddressFromCoords(location.getLongitude(), location.getLatitude());
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String param) {
            editTextEndPointAddress.setText(param);
            textViewProgress.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            location = null;
        }
    }
}