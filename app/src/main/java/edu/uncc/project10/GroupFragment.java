package edu.uncc.project10;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupFragment extends Fragment {

    private static final String EMAIL_PARAM = "email";
    private static final String FIRST_NAME_PARAM = "fName";
    private static final String SCORED_GROUP_NAME_PARAM = "scoredGroupName";

    String email, fName, scoredGroupName;

    IFragmentListener mListener;

    public GroupFragment() {
        // Required empty public constructor
    }

    public static GroupFragment newInstance(String email, String fName, String scoredGroupName) {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL_PARAM, email);
        args.putString(FIRST_NAME_PARAM, fName);
        args.putString(SCORED_GROUP_NAME_PARAM, scoredGroupName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof IFragmentListener) {
            mListener = (IFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString(EMAIL_PARAM);
            fName = getArguments().getString(FIRST_NAME_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Student Groups");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        TextView txtViewWelcome = view.findViewById(R.id.textViewWelcome);
        txtViewWelcome.setText("Welcome " + fName);

        ViewGroup rgp = (RadioGroup) view.findViewById(R.id.radioGroup);
        int buttons = 5;
        for (int i = 0; i < buttons ; i++) {
            RadioButton rbn = new RadioButton(getActivity());
            int id = View.generateViewId();
            rbn.setId(View.generateViewId());
            rbn.setText("RadioButton " + i);
            rgp.addView(rbn);

            rbn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("scorer", rbn.getText().toString());
                    mListener.gotoQuestionnaire(email, fName, rbn.getText().toString());
                }
            });
        }

        return view;
    }
}