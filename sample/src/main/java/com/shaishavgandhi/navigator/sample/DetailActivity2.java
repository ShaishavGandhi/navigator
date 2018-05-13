package com.shaishavgandhi.navigator.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.shaishavgandhi.navigato.sampler.R;
import com.shaishavgandhi.navigator.Extra;
import com.shaishavgandhi.navigator.Navigator;

public class DetailActivity2 extends AppCompatActivity {

    @Extra
    static String key;
    @Extra int something;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);
        Navigator.bind(this);

        Toast.makeText(this, String.valueOf(key) + " = " + something, Toast.LENGTH_SHORT)
                .show();
    }

    public static void setKey(String key) {
        DetailActivity2.key = key;
    }

    public void setSomething(int something) {
        this.something = something;
    }
}
