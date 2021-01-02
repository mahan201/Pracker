package com.mahan.pracker;

public class Task {
    int id;
    String name;
    int progress, target, color, dependent;
    boolean isRepeating;

    public Task(int mColor, String mName, int mTarget, int mProgress, int mDependent, boolean mRepeats){
        this.name = mName;
        this.color = mColor;
        this.target = mTarget;
        this.progress = mProgress;
        this.dependent = mDependent;
        this.isRepeating = mRepeats;
    }



    public Task(String[] arr){
        if(arr.length < 5){return;}
        this.color = Integer.parseInt(arr[0]);
        this.name = arr[1];
        this.progress = Integer.parseInt(arr[2]);
        this.target = Integer.parseInt(arr[3]);
        this.dependent = Integer.parseInt(arr[4]);
        this.isRepeating = Boolean.parseBoolean(arr[5]);
    }

    public boolean increase(){
        this.progress += 1;
        this.progress = Math.min(progress,target);
        return this.progress == this.target;
    }

    public boolean increase(int val){
        this.progress += val;
        this.progress = Math.min(progress,target);
        return this.progress == this.target;
    }

    public boolean isComplete(){
        return this.progress == this.target;
    }

    public String[] toArray(){
        return new String[]{String.valueOf(color), name, String.valueOf(progress), String.valueOf(target), String.valueOf(dependent), String.valueOf(isRepeating)};
    }


}
