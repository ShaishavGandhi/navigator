package com.shaishavgandhi.navigator.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.shaishavgandhi.navigator.Extra;



public class SampleActivity extends AppCompatActivity {

    @Extra String message;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleActivityNavigator.bind(this);
    }
}
