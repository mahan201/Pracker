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
import android.os.Handler;
import android.os.Looper;
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
import java.util.Timer;

import me.thanel.swipeactionview.SwipeActionView;
import me.thanel.swipeactionview.SwipeGestureListener;

public class MainActivity extends AppCompatActivity implements addTask.addTaskDialogListener {

    final Context context = this;
    LinearLayout mainLinear;

    AdView mAdView;

    SharedPreferences sharedPreferences;
    LayoutInflater layoutInflater;

    ArrayList<Task> taskList;
    ArrayList<String> taskNames;
    ExpandableLayout currentlyOpen;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        handler = new Handler(Looper.getMainLooper());

        mAdView = findViewById(R.id.adView);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Toast.makeText(context, "You have fed a man with this action.", Toast.LENGTH_LONG).show();
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        sharedPreferences = getSharedPreferences("com.mahan.pracker", MODE_PRIVATE);
        layoutInflater = getLayoutInflater();

        mainLinear = findViewById(R.id.mainLinearLayout);

        taskList = new ArrayList<>();
        taskNames = new ArrayList<>();

        loadTasks();

    }

    private void loadTasks() {
        try {

            String taskListSerialized = sharedPreferences.getString("taskList", ObjectSerializer.serialize(new ArrayList<String[]>()));
            ArrayList<String[]> taskStrings = (ArrayList<String[]>) ObjectSerializer.deserialize(taskListSerialized);
            for (int i = 0; i < taskStrings.size(); i++) {
                String[] task = taskStrings.get(i);
                System.out.println(task.toString());
                Task taskObj = new Task(task);
                addTask(taskObj);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private void updateTaskIndependent(final int pos1, int n){
        ExpandableLayout expandableLayout = mainLinear.getChildAt(pos1).findViewById(R.id.expandable);

        int pos = taskList.size() - pos1 - 1;

        final Task task = taskList.get(pos);
        task.increase(n);

        expandableLayout.collapse();

        RoundCornerProgressBar progressBar = expandableLayout.parentLayout.findViewById(R.id.taskProgressBar);
        progressBar.setProgress(task.progress);

        TextView parentProg = expandableLayout.parentLayout.findViewById(R.id.TaskProgress);
        parentProg.setText(task.progress + "/" + task.target);

        TextView secondTarget = expandableLayout.secondLayout.findViewById(R.id.target);
        secondTarget.setText("Target: " + task.target);

        if(task.isComplete()){
            for (int i = 0; i < taskList.size(); i++) {
                if(taskList.get(i).dependent == pos){
                    updateTaskIndependent(i,1);
                }
            }

            if(task.isRepeating){
                final int val = -1 * task.target;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateTaskIndependent(pos1,val);
                    }
                },1000);
            }

        }
    }


    private void updateTask(final int pos1) {
        ExpandableLayout expandableLayout = mainLinear.getChildAt(pos1).findViewById(R.id.expandable);
        int pos = taskList.size() - pos1 - 1;
        Task task = taskList.get(pos);
        System.out.println(task.dependent);

        EditText textDelta = expandableLayout.secondLayout.findViewById(R.id.progressDelta);
        if(textDelta.getText().toString().equals("")){
            textDelta.setHintTextColor(Color.RED);
            return;
        }
        else {textDelta.setHintTextColor(Color.WHITE);}
        task.increase(Integer.parseInt(textDelta.getText().toString()));

        expandableLayout.collapse();
        textDelta.setText("");

        RoundCornerProgressBar progressBar = expandableLayout.parentLayout.findViewById(R.id.taskProgressBar);
        progressBar.setProgress(task.progress);

        TextView parentProg = expandableLayout.parentLayout.findViewById(R.id.TaskProgress);
        parentProg.setText(task.progress + "/" + task.target);

        TextView secondTarget = expandableLayout.secondLayout.findViewById(R.id.target);
        secondTarget.setText("Target: " + task.target);

        if(task.isComplete()){
            for (int i = 0; i < taskList.size(); i++) {
                if(taskList.get(i).dependent == pos){
                    int n = taskList.size() - i - 1;
                    System.out.println(n);
                    updateTaskIndependent(n,1);
                }
            }

            if(task.isRepeating){
                textDelta.setText("-" + task.target);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateTask(pos1);
                    }
                },1000);
            }

        }
    }

    private void deleteTask(int pos) {
        taskNames.remove(pos);
        mainLinear.removeViewAt(pos);
        pos = taskList.size() - pos - 1;
        taskList.remove(pos);
    }

    public void onAddTask(View view) {
        addTask dialog = new addTask();
        Bundle args = new Bundle();
        args.putStringArrayList("TaskNames",taskNames);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "MahanDialog");
    }

    @Override
    public void addTask(Task task) {
        int backgroundColor = manipulateColor(task.color, 0.4f);

        final LinearLayout taskView = (LinearLayout) layoutInflater.inflate(R.layout.task_view, null);

        final ExpandableLayout expandableLayout = taskView.findViewById(R.id.expandable);
        RoundCornerProgressBar progressBar = expandableLayout.parentLayout.findViewById(R.id.taskProgressBar);

        progressBar.setProgress(task.progress);
        progressBar.setMax(task.target);
        progressBar.setProgressBackgroundColor(backgroundColor);
        progressBar.setProgressColor(task.color);


        expandableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandableLayout.isExpanded()) {
                    expandableLayout.collapse();
                } else {
                    if (currentlyOpen != null)
                    {
                        currentlyOpen.collapse();
                    }
                    expandableLayout.expand();
                    currentlyOpen = expandableLayout;
                }
            }
        });

        expandableLayout.secondLayout.setBackgroundColor(backgroundColor);

        TextView taskName = expandableLayout.parentLayout.findViewById(R.id.TaskTitle);
        taskName.setText(task.name);

        TextView taskprogress1 = expandableLayout.parentLayout.findViewById(R.id.TaskProgress);
        taskprogress1.setText(task.progress + "/" + task.target);

        TextView target = expandableLayout.secondLayout.findViewById(R.id.target);
        target.setText("Target: " + task.target);

        if(task.isRepeating){
            expandableLayout.secondLayout.findViewById(R.id.repeatImage).setVisibility(View.VISIBLE);
        }

        final int position = mainLinear.getChildCount();

        Button deleteBtn = expandableLayout.secondLayout.findViewById(R.id.deleteButton);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mainLinear.getChildCount(); i++) {
                    if (mainLinear.getChildAt(i) == taskView) {
                        deleteTask(i);
                    }
                }

            }
        });

        Button applyBtn = expandableLayout.secondLayout.findViewById(R.id.okButton);
        if(task.dependent == -1) {
            applyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mainLinear.getChildCount(); i++) {
                        if (mainLinear.getChildAt(i) == taskView) {
                            updateTask(i);
                        }
                    }
                }
            });
        }else {
            applyBtn.setVisibility(View.GONE);
            expandableLayout.secondLayout.findViewById(R.id.addView).setVisibility(View.GONE);
            TextView dependentDescription = expandableLayout.secondLayout.findViewById(R.id.dependentDescription);
            dependentDescription.setText("Dependent on: " + taskNames.get(taskNames.size() - task.dependent - 1));
            dependentDescription.setVisibility(View.VISIBLE);
        }

        mainLinear.addView(taskView,0);

        taskList.add(task);
        taskNames.add(0,task.name);

        storeTasks();

    }

    private int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }

    private void storeTasks() {

        try {
            ArrayList<String[]> taskStrings = new ArrayList<>();
            for (Task task:
                 taskList) {
                taskStrings.add(task.toArray());
            }
            String taskListSerialized = ObjectSerializer.serialize(taskStrings);

            sharedPreferences.edit().putString("taskList", taskListSerialized).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        storeTasks();
    }
}