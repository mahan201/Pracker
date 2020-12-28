package com.mahan.pracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.john.waveview.WaveView;
import com.skydoves.expandablelayout.ExpandableLayout;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import me.thanel.swipeactionview.SwipeActionView;
import me.thanel.swipeactionview.SwipeGestureListener;

public class MainActivity extends AppCompatActivity implements addTask.addTaskDialogListener {

    final Context context = this;
    LinearLayout mainLinear;

    AdView mAdView;

    SharedPreferences sharedPreferences;
    LayoutInflater layoutInflater;

    ArrayList<String[]> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Toast.makeText(context,"You have fed a man with this action.", Toast.LENGTH_LONG).show();
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        sharedPreferences = getSharedPreferences("com.mahan.pracker",MODE_PRIVATE);
        layoutInflater = getLayoutInflater();

        mainLinear = findViewById(R.id.mainLinearLayout);

        try {
            String taskListSerialized = sharedPreferences.getString("taskList",ObjectSerializer.serialize(new ArrayList<String[]>()));
            taskList = (ArrayList<String[]>) ObjectSerializer.deserialize(taskListSerialized);
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadTasks();

    }

    private void loadTasks(){
        for (int i = 0; i < taskList.size(); i++) {
            String[] task = taskList.get(i);
            int color = Integer.parseInt(task[0]);
            String name = task[1];
            int progress = Integer.parseInt(task[2]);
            int max = Integer.parseInt(task[3]);

            addTask(color,name,progress,max, false);
        }
    }


    private void updateTask(int pos){
        ExpandableLayout expandableLayout = mainLinear.getChildAt(pos).findViewById(R.id.expandable);

        String[] task = taskList.remove(pos);

        EditText textDelta = expandableLayout.secondLayout.findViewById(R.id.progressDelta);
        int prog = Integer.parseInt(task[2]) + Integer.parseInt(textDelta.getText().toString());
        prog = Math.min(prog,Integer.parseInt(task[3]));

        textDelta.setText("");
        expandableLayout.collapse();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textDelta.getWindowToken(), 0);

        taskList.add(pos, new String[]{task[0], task[1], String.valueOf(prog), task[3]});

        RoundCornerProgressBar progressBar = expandableLayout.parentLayout.findViewById(R.id.taskProgressBar);
        progressBar.setProgress(prog);

        TextView parentProg = expandableLayout.parentLayout.findViewById(R.id.TaskProgress);
        parentProg.setText(prog + "/" + task[3]);

        TextView secondTarget = expandableLayout.secondLayout.findViewById(R.id.target);
        secondTarget.setText("Target: " + task[3]);

        storeTasks();
    }

    private void deleteTask(int pos){
        taskList.remove(pos);
        mainLinear.removeViewAt(pos);
        storeTasks();
    }

    public void onAddTask(View view){
        addTask dialog = new addTask();
        dialog.show(getSupportFragmentManager(),"MahanDialog");
    }

    @Override
    public void addTask(int taskColor, String name, int progress, int max, boolean isNew) {
        int backgroundColor = manipulateColor(taskColor,0.4f);

        final LinearLayout taskView = (LinearLayout) layoutInflater.inflate(R.layout.task_view,null);

        final ExpandableLayout expandableLayout = taskView.findViewById(R.id.expandable);
        RoundCornerProgressBar progressBar = expandableLayout.parentLayout.findViewById(R.id.taskProgressBar);

        progressBar.setProgress(progress);
        progressBar.setMax(max);
        progressBar.setBackgroundColor(backgroundColor);
        progressBar.setProgressColor(taskColor);

        expandableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expandableLayout.isExpanded()){
                    expandableLayout.collapse();
                }
                else {
                    expandableLayout.expand();
                }
            }
        });

        TextView taskName = expandableLayout.parentLayout.findViewById(R.id.TaskTitle);
        taskName.setText(name);

        TextView taskprogress1 = expandableLayout.parentLayout.findViewById(R.id.TaskProgress);
        taskprogress1.setText(progress+"/"+max);

        TextView target = expandableLayout.secondLayout.findViewById(R.id.target);
        target.setText("Target: " + max);

        final int position = mainLinear.getChildCount();

        Button deleteBtn = expandableLayout.secondLayout.findViewById(R.id.deleteButton);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mainLinear.getChildCount(); i++) {
                    if(mainLinear.getChildAt(i) == taskView){
                        deleteTask(i);
                    }
                }

            }
        });

        Button applyBtn = expandableLayout.secondLayout.findViewById(R.id.okButton);
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mainLinear.getChildCount(); i++) {
                    if(mainLinear.getChildAt(i) == taskView){
                        updateTask(i);
                    }
                }
            }
        });

        mainLinear.addView(taskView);
        
        if(isNew) {
            String[] currentTask = {String.valueOf(taskColor),name, String.valueOf(progress), String.valueOf(max)};
            taskList.add(currentTask);
            storeTasks();
        }

    }

    private int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r,255),
                Math.min(g,255),
                Math.min(b,255));
    }

    private void storeTasks(){

        try {
            String taskListSerialized = ObjectSerializer.serialize(taskList);

            sharedPreferences.edit().putString("taskList",taskListSerialized).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}