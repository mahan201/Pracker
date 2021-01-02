package com.mahan.pracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.flask.colorpicker.ColorPickerView;

import java.util.ArrayList;

public class addTask extends AppCompatDialogFragment implements CompoundButton.OnCheckedChangeListener {

    private View view;
    private EditText taskNameEdit;
    private Button okButton;
    private Button cancelButton;
    private addTaskDialogListener listener;
    private ColorPickerView colorPickerView;
    private EditText taskProgressEdit;
    private CheckBox isDependent;
    private CheckBox isRepeating;

    private Spinner independentTaskSpinner;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.activity_add_task,null);

        builder.setView(view);

        taskNameEdit = view.findViewById(R.id.taskNameEdit);
        okButton = view.findViewById(R.id.ok_btn);
        cancelButton = view.findViewById(R.id.cancel_btn);
        colorPickerView = view.findViewById(R.id.colorWheel);
        taskProgressEdit = view.findViewById(R.id.taskProgressEdit);
        isDependent = view.findViewById(R.id.isDependent);
        isRepeating = view.findViewById(R.id.isRepeating);

        final ArrayList<String> taskNames = getArguments().getStringArrayList("TaskNames");
        final int lastID = getArguments().getInt("LastID");

        if(taskNames.size() > 0){
            independentTaskSpinner = view.findViewById(R.id.independentSpinner);
            ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),R.layout.colored_spinner_layout,taskNames);
            adapter.setDropDownViewResource(R.layout.colored_spinner_layout_dropdown);
            independentTaskSpinner.setAdapter(adapter);
        }
        else{
            isDependent.setVisibility(View.GONE);
        }



        isDependent.setOnCheckedChangeListener(this);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int taskColor = colorPickerView.getSelectedColor();
                String taskName = taskNameEdit.getText().toString();

                if (taskName.equals("")){
                    taskNameEdit.setHintTextColor(Color.RED);
                    return;
                }

                String taskProgressStr = taskProgressEdit.getText().toString();
                int taskProgress = 0;

                if (!taskProgressStr.equals("")){
                    taskProgress = Integer.parseInt(taskProgressStr);
                }
                else{
                    taskProgressEdit.setHintTextColor(Color.RED);
                    return;
                }

                int dependency = -1;
                if(isDependent.isChecked()){
                    dependency = independentTaskSpinner.getSelectedItemPosition();
                }


                System.out.println(dependency);

                listener.addNewTask(lastID+1, taskColor,taskName,taskProgress,dependency,isRepeating.isChecked());
                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (addTaskDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.isDependent:
                if(isChecked){
                    view.findViewById(R.id.dependentView).setVisibility(View.VISIBLE);
                }
                else {
                    view.findViewById(R.id.dependentView).setVisibility(View.GONE);
                }
                break;

        }
    }

    public interface addTaskDialogListener{
        void addNewTask(int id, int taskColor, String taskName, int taskProgress, int dependencyPos, boolean isRepeating);
    }

}