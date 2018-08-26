package com.pablo.myroutes;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Paul on 23.11.2017.
 */

public class ClosedRouteFragment extends Fragment {

    private static final String ARG_PARAM = "param";
    private Route route;

    private IFragmentsInteractionListener mListener;

    public ClosedRouteFragment() {
        // Required empty public constructor
    }

    public static ClosedRouteFragment newInstance(Route param) {
        ClosedRouteFragment fragment = new ClosedRouteFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            route = (Route)getArguments().getSerializable(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_closed_route, container, false);

        TextView textViewStartPointAddress =  view.findViewById(R.id.textViewStartPointAddress);
        textViewStartPointAddress.setText(route.getStartPoint());

        TextView textViewEndPointAddress =  view.findViewById(R.id.textViewEndPointAddress);
        textViewEndPointAddress.setText(route.getEndPoint());

        TextView textViewTimeStart = view.findViewById(R.id.textViewTimeStart);
        textViewTimeStart.setText(route.getStartTime());

        TextView textViewTimeEnd = view.findViewById(R.id.textViewTimeEnd);
        textViewTimeEnd.setText(route.getEndTime());

        TextView textViewRouteKilometrage = view.findViewById(R.id.textViewRouteKilometrage);
        textViewRouteKilometrage.setText(String.valueOf(route.getLength())+" км.");

        TextView textViewRouteTime = view.findViewById(R.id.textViewRouteTime);
        textViewRouteTime.setText(Helper.getTimeDifference(route.getStartTime(),route.getEndTime())+" мин.");

        Button buttonNewRoute = view.findViewById(R.id.buttonOk);
        buttonNewRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.openRoute();
            }
        });

        Button buttonCloseDay = view.findViewById(R.id.buttonCloseDay);
        buttonCloseDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                alert.setTitle("Закрыть день?");

                alert.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mListener.closeRoutingDay();
                    }
                });

                alert.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });

        return view;
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
}