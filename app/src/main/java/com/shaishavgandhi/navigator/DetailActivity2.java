package com.shaishavgandhi.navigator;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class DetailActivity2 extends AppCompatActivity {

    @Extra String key;
    @Extra int something;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);
        Navigator.bind(this);

        Toast.makeText(this, String.valueOf(key) + " = " + something, Toast.LENGTH_SHORT)
                .show();
    }

    public void setKey(String key) {
        this.key = key;
    }
}
