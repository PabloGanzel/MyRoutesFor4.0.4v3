package com.pablo.myroutes;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Paul on 23.11.2017.
 */

public class OpenedRouteFragment extends Fragment {

    EditText editTextEndPointAddress, editTextTimeEnd, editTextKilometrageEnd;

    private static final String ARG_PARAM = "param";
    private Route route;

    private IFragmentsInteractionListener mListener;

    Location location;
    LocationManager locationManager;
    LocationListener locationListener;


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

        TextView textViewTimeStart = view.findViewById(R.id.textViewTimeStart);
        textViewTimeStart.setText(route.getStartTime());

        TextView textViewKilometrageStartPoint = view.findViewById(R.id.textViewKilometrageStartPoint);
        textViewKilometrageStartPoint.setText(String.valueOf(route.getStartKilometrage()));

        editTextEndPointAddress = view.findViewById(R.id.editTextEndPointAddress);
        editTextTimeEnd = view.findViewById(R.id.editTextTimeEnd);
        editTextTimeEnd.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextKilometrageEnd = view.findViewById(R.id.editTextKilometrageEnd);
        editTextKilometrageEnd.setInputType(InputType.TYPE_CLASS_NUMBER);

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

        final Button button = view.findViewById(R.id.buttonEndRoute);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button.getText().toString().equals("OK")) {
                    mListener.closeRoute(editTextEndPointAddress.getText().toString(), Integer.parseInt(editTextKilometrageEnd.getText().toString()), editTextTimeEnd.getText().toString());

                } else {
                    editTextEndPointAddress.setVisibility(View.VISIBLE);
                    //new HttpAsyncTask().execute(null, null);
                    //editTextEndPointAddress.setText(Helper.getAddress());
                    editTextTimeEnd.setVisibility(View.VISIBLE);
                    editTextTimeEnd.setText(Helper.getTimeNow().toString());
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

    class HttpAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
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
        }
    }
}