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
 * Created by Paul on 23.11.2017.
 */

public class OpenDayFragment extends Fragment{

    private static final String ARG_PARAM = "param";
    private int kilometrageStartDay;

    private IFragmentsInteractionListener mListener;

    public OpenDayFragment() {
        // Required empty public constructor
    }

    public static OpenDayFragment newInstance(int kilometrageStartDay) {
        OpenDayFragment fragment = new OpenDayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM, kilometrageStartDay);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            kilometrageStartDay = getArguments().getInt(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_open_day, container, false);
        final EditText editText = view.findViewById(R.id.editTextKilometrage);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setText(String.valueOf(kilometrageStartDay));
        Button b = view.findViewById(R.id.buttonBeginningDay);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int kilometrage = Integer.parseInt(editText.getText().toString());
                mListener.openRoutingDay(kilometrage);
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