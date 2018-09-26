package com.shaishavgandhi.navigator.sample;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.shaishavgandhi.navigator.Extra;



public class SampleActivity extends AppCompatActivity {

    @Extra String message;
    @Extra Short javaShort;
    Long points = -1L;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleActivityNavigator.bind(this);
    }
}
