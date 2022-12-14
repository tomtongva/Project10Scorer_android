package edu.uncc.project10;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuestionnaireFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionnaireFragment extends Fragment {

    private static final String EMAIL_PARAM = "email";
    private static final String FIRST_NAME_PARAM = "fname";
    private static final String STUDENT_GROUP_PARAM = "StudentGroup";

    String email, fName, studentGroup;

    List<String> questions = new ArrayList<>();
    Set<String> answers = new HashSet<>();

    IFragmentListener mListener;

    public QuestionnaireFragment() {
        // Required empty public constructor
    }

    public static QuestionnaireFragment newInstance(String email, String fName, String studentGroup) {
        QuestionnaireFragment fragment = new QuestionnaireFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL_PARAM, email);
        args.putString(FIRST_NAME_PARAM, fName);
        args.putString(STUDENT_GROUP_PARAM, studentGroup);
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
            studentGroup = getArguments().getString(STUDENT_GROUP_PARAM);
            fName = getArguments().getString(FIRST_NAME_PARAM);
        }

        questions.add("Project goal(s) clearly stated");
        questions.add("Project complexity");
        questions.add("Balanced individual team member contribution");
        questions.add("Originality");
        questions.add("Overall presentation");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Group " + studentGroup);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_questionnaire, container, false);
        TextView txtViewQuestion = view.findViewById(R.id.textViewQuestion);

        if (questions.isEmpty())
            mListener.gotoGroupFragment(email, fName, studentGroup);

        txtViewQuestion.setText(questions.get(0));

        RadioButton radioButtonPoor = (RadioButton) view.findViewById(R.id.radioButtonPoor);
        RadioButton radioButtonFair = (RadioButton) view.findViewById(R.id.radioButtonFair);
        RadioButton radioButtonGood = (RadioButton) view.findViewById(R.id.radioButtonGood);
        RadioButton radioButtonVeryGood = (RadioButton) view.findViewById(R.id.radioButtonVeryGood);
        RadioButton radioButtonSuperior = (RadioButton) view.findViewById(R.id.radioButtonSuperior);

        onClick(radioButtonPoor, txtViewQuestion);
        onClick(radioButtonFair, txtViewQuestion);
        onClick(radioButtonGood, txtViewQuestion);
        onClick(radioButtonVeryGood, txtViewQuestion);
        onClick(radioButtonSuperior, txtViewQuestion);

        return view;
    }

    private void onClick(RadioButton button, TextView textViewQuestion) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("scorer", button.getText().toString());
                questions.remove(0);
                if (!questions.isEmpty())
                    textViewQuestion.setText(questions.get(0));
                else
                    mListener.gotoGroupFragment(email, fName, studentGroup);
                button.setChecked(false);
            }
        });
    }
}