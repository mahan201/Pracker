package com.mahan.pracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.flask.colorpicker.ColorPickerView;
import com.mahan.pracker.R;

public class addTask extends AppCompatDialogFragment {

    private EditText taskNameEdit;
    private Button okButton;
    private Button cancelButton;
    private addTaskDialogListener listener;
    private ColorPickerView colorPickerView;
    private EditText taskProgressEdit;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_add_task,null);

        builder.setView(view);

        taskNameEdit = view.findViewById(R.id.taskNameEdit);
        okButton = view.findViewById(R.id.ok_btn);
        cancelButton = view.findViewById(R.id.cancel_btn);
        colorPickerView = view.findViewById(R.id.colorWheel);
        taskProgressEdit = view.findViewById(R.id.taskProgressEdit);

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



                listener.addTask(taskColor,taskName, 0, taskProgress, true);
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

    public interface addTaskDialogListener{
        void addTask(int taskColor, String name, int progress, int max, boolean isNew);
    }

}