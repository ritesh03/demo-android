package com.maktoday.utils;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Animatable;
import androidx.annotation.IntRange;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maktoday.R;

public class StateProgressBar extends ConstraintLayout
{
    private ProgressBar progressBar;
    private TextView[] progressState;
    private Context context;
    private int position;

    public StateProgressBar(Context context)
    {
        super(context);
        this.context=context;
        initialize();
    }

    public StateProgressBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context=context;
        initialize();
    }

    public StateProgressBar(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        this.context=context;
        initialize();
    }

    private void initialize()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_state_progress, this, true);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressState = new TextView[4];
        progressState[0] = (TextView) findViewById(R.id.stateOne);
        progressState[1] = (TextView) findViewById(R.id.stateTwo);
        progressState[2] = (TextView) findViewById(R.id.stateThree);
        progressState[3] = (TextView) findViewById(R.id.stateFour);
    }

    public void setCurrentPosition(@IntRange(from = 0, to = 4) int position)
    {
        this.position=position;
        switch (position)
        {
            case 0:
                setProgress(0);
                setActiveState(progressState[0]);
                setDeactiveState(progressState[1]);
                setDeactiveState(progressState[2]);
                setDeactiveState(progressState[3]);
                break;

            case 1:
                setProgress(34);
                setChecked(progressState[0]);
                setActiveState(progressState[1]);
                setDeactiveState(progressState[2]);
                setDeactiveState(progressState[3]);
                break;

            case 2:
                setProgress(68);
                setChecked(progressState[1]);
                setActiveState(progressState[2]);
                setDeactiveState(progressState[3]);
                break;

            case 3:
                setProgress(100);
                setChecked(progressState[2]);
                setActiveState(progressState[3]);
                break;

            case 4:
                setProgress(100);
                setChecked(progressState[3]);

                break;
        }
    }

    public int getCurrentPosition()
    {
        return position;
    }

    private void setProgress(@IntRange(from = 0, to = 100) int progress)
    {
        ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", progress);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    private void setChecked(TextView textView)
    {
        AnimatedVectorDrawableCompat drawableCompat = AnimatedVectorDrawableCompat.create(getContext(),
                R.drawable.vector_animated_tick);
        textView.setBackgroundResource(R.drawable.circle_white_solid);
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableCompat, null, null, null);
        ((Animatable) textView.getCompoundDrawablesRelative()[0]).start();

    }

    private void setActiveState(TextView textView)
    {
        textView.setBackgroundResource(R.drawable.circle_blue);
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
        textView.setTextColor(ContextCompat.getColor(context,R.color.app_color));
    }

    private void setDeactiveState(TextView textView) {
        textView.setBackgroundResource(R.drawable.circle_grey);
        textView.setTextColor(ContextCompat.getColor(context,R.color.colorBlack45));
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
    }
}