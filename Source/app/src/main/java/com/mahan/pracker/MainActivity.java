package com.mahan.pracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.john.waveview.WaveView;
import com.skydoves.expandablelayout.ExpandableLayout;

import org.w3c.dom.Text;

import me.thanel.swipeactionview.SwipeActionView;
import me.thanel.swipeactionview.SwipeGestureListener;

public class MainActivity extends AppCompatActivity {

    ExpandableLayout expandableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context context = this;

        expandableLayout = findViewById(R.id.expandable);

        final RoundCornerProgressBar progressBar = expandableLayout.parentLayout.findViewById(R.id.Iconprogress);

        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setProgress(40);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


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

        expandableLayout.secondLayout.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTask(expandableLayout);
            }
        });

        expandableLayout.secondLayout.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask(expandableLayout);
            }
        });


    }


    private void updateTask(ExpandableLayout expandableLayout){
        EditText editText = expandableLayout.secondLayout.findViewById(R.id.progressDelta);

        TextView taskProgress = ((TextView) expandableLayout.parentLayout.findViewById(R.id.TaskProgress));

        int current = Integer.parseInt(taskProgress.getText().toString().split("/")[0]);
        int prog = Integer.parseInt(editText.getText().toString()) + current;
        TextView targetView = expandableLayout.secondLayout.findViewById(R.id.target);
        int max = Integer.parseInt(targetView.getText().toString().replace("Target: ",""));
        prog = Math.min(prog,max);
        float barVal = ((float)prog/(float)max)*100;
        ((RoundCornerProgressBar) expandableLayout.parentLayout.findViewById(R.id.Iconprogress)).setProgress(barVal);

        taskProgress.setText(String.valueOf(prog) + "/" + String.valueOf(max));
    }

    private void deleteTask(ExpandableLayout expandableLayout){
        ((ConstraintLayout) expandableLayout.getParentLayout()).removeView(expandableLayout);
    }

    public void onAddTask(View view){
        Toast.makeText(this,"Task Added",Toast.LENGTH_SHORT).show();
    }

}