package com.pablo.myroutes;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Created by Paul on 26.11.2017.
 */

public class EditRouteFragment extends Fragment implements IEditor {

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

        EditorActivity.CURRENT_FRAGMENT_TAG = "EditRouteFragment";

        if (getArguments() != null) {
            route = (Route) getArguments().getSerializable(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_edit_route, container, false);

        ArrayAdapter adapter = new ArrayAdapter<>(
                getContext(),
                R.layout.my_dropdown_item,
                //android.R.layout.simple_dropdown_item_1line,
                Helper.ADDRESS_LIST);

        final AutoCompleteTextView editTextStartAddress = view.findViewById(R.id.editTextStartAddress);
        editTextStartAddress.setText(route.getStartPoint());
        editTextStartAddress.setAdapter(adapter);

        final AutoCompleteTextView editTextEndAddress = view.findViewById(R.id.editTextEndAddress);
        editTextEndAddress.setText(route.getEndPoint());
        editTextEndAddress.setAdapter(adapter);

        final TextView textViewStartTime = view.findViewById(R.id.textViewStartTime);
        //textViewStartTime.setInputType(InputType.TYPE_CLASS_NUMBER);
        textViewStartTime.setText(route.getStartTime());
        final TextView textViewEndTime = view.findViewById(R.id.textViewEndTime);
        textViewEndTime.setInputType(InputType.TYPE_CLASS_NUMBER);
        textViewEndTime.setText(route.getEndTime());
        final EditText editTextLength = view.findViewById(R.id.editTextLength);
        editTextLength.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextLength.setText(String.valueOf(route.getLength()));

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String[] timeString = Helper.getTimeNow().split(":");
                int hour = Integer.parseInt(timeString[0]);
                int minute = Integer.parseInt(timeString[1]);
                new TimePickerDialog(getContext(),new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        TextView textView = (TextView)view;
//                        if(hour>=10){
//                            textView.setText(hour+":"+minute);}
//                        else{textView.setText("0"+hour+":"+minute);}
                        String strHour="";
                        String strMinute="";
                        if(hour<10){
                            strHour="0"+String.valueOf(hour);
                        }
                        else strHour = String.valueOf(hour);
                        if(minute<10){
                            strMinute="0"+String.valueOf(minute);
                        }
                        else {
                            strMinute = String.valueOf(minute);
                        }
                        // editTextTimeEnd.setText(hour+":"+minute);}

                        textView.setText(strHour+":"+strMinute);
                    }
                },hour,minute,true).show();
            }
        };

        textViewStartTime.setOnClickListener(onClickListener);
        textViewEndTime.setOnClickListener(onClickListener);

        Button buttonOk = view.findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextEndAddress.getText().toString().equals("")
                        || editTextStartAddress.getText().toString().equals("")
                        || textViewStartTime.getText().toString().equals("")
                        || textViewEndTime.getText().toString().equals("")
                        || editTextLength.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Helper.isTimeCorrect(textViewStartTime.getText().toString(), textViewEndTime.getText().toString())) {
                    Toast.makeText(getContext(), "Некорректное время", Toast.LENGTH_SHORT).show();
                    return;
                }
                route.setStartPoint(editTextStartAddress.getText().toString());
                route.setEndPoint(editTextEndAddress.getText().toString());
                route.setStartTime(textViewStartTime.getText().toString());
                route.setEndTime(textViewEndTime.getText().toString());
                route.setLength(Integer.parseInt(editTextLength.getText().toString()));
                mListener.save();
                Toast.makeText(getActivity().getBaseContext(), "Сохранено", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
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

    @Override
    public void deleting() {
        //mListener.deleteRouteAndSave(route);
        Toast.makeText(getActivity().getBaseContext(), "лол22", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void exporting() {

    }
}