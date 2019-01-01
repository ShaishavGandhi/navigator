package com.shaishavgandhi.navigator.sample;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.shaishavgandhi.navigato.sampler.R;
import com.shaishavgandhi.navigator.Extra;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

public class UserDetailActivity extends AppCompatActivity {

    @Extra User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        UserDetailActivityBinder.bind(this);

        TransitionSet inSet = new TransitionSet();
        TransitionInflater inflater = TransitionInflater.from(this);
        Transition transition = inflater.inflateTransition(R.transition.arc);

        inSet.addTransition(transition);
        inSet.setDuration(380);
        inSet.setInterpolator(new AccelerateDecelerateInterpolator());

        getWindow().setSharedElementEnterTransition(inSet);

        AppCompatTextView nameView = findViewById(R.id.name);
        nameView.setText(user.getName());

        AppCompatImageView colorView = findViewById(R.id.color);
        colorView.setBackground(new ColorDrawable(user.getColor()));
    }
}
