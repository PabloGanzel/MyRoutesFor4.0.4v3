package com.pablo.myroutes;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Paul on 26.11.2017.
 */

public class EditRouteFragment extends Fragment {

    private static final String ARG_PARAM = "param";
    private Route route;
    ISaver mListener;

    public EditRouteFragment() {
        // Required empty public constructor
    }

    public static EditRouteFragment newInstance(Route param) {
        EditRouteFragment fragment = new EditRouteFragment();
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

        final View view = inflater.inflate(R.layout.fragment_edit_route, container, false);

        final EditText editTextStartAddress = view.findViewById(R.id.editTextKilometrage);
        editTextStartAddress.setText(route.getStartPoint());
        final EditText editTextEndAddress = view.findViewById(R.id.editTextEndAddress);
        editTextEndAddress.setText(route.getEndPoint());
        final EditText editTextStartTime = view.findViewById(R.id.editTextStartTime);
        editTextStartTime.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextStartTime.setText(route.getStartTime());
        final EditText editTextEndTime = view.findViewById(R.id.editTextEndTime);
        editTextEndTime.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextEndTime.setText(route.getEndTime());
        final EditText editTextLength = view.findViewById(R.id.editTextLength);
        editTextLength.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextLength.setText(String.valueOf(route.getLength()));

        Button buttonOk = view.findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                route.setStartPoint(editTextStartAddress.getText().toString());
                route.setEndPoint(editTextEndAddress.getText().toString());
                route.setStartTime(editTextStartTime.getText().toString());
                route.setEndTime(editTextEndTime.getText().toString());
                mListener.save();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ISaver) {
            mListener = (ISaver) context;
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
