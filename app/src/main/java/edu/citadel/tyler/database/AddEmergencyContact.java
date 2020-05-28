package edu.citadel.tyler.database;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class AddEmergencyContact extends Fragment {
// Do I need below?***************************************
    public interface AddFragmentListener{
        public void onAddComplete(long rowID);
    }
// Do I need above? **************************************
    private OnFragmentInteractionListener mListener;

    private EditText editName;
    private EditText editPhone;



    public static AddEmergencyContact newInstance(String param1, String param2) {
        AddEmergencyContact fragment = new AddEmergencyContact();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AddEmergencyContact() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_add_emergency_contact, false);
        editName = (EditText) view.findViewById(R.id.editName);
        editPhone = (EditText) view.findViewById(R.id.editPhone);
        Button buttonSubmit = (Button) view.findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(Save);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    OnClickListener Save = new OnClickListener() {
        @Override
        public void OnClick(View v){

        AsyncTask<Object, Object, Object> SaveTask = new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                Save();
                return null;
            }

            @Override
            protected void onPostExecute(Object result) {
                InputMethodManager imm = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                // finish this*********************************************
            }
        };
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void Save(){
    DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());

    }
}
