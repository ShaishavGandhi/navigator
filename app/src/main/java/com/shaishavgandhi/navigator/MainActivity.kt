package com.shaishavgandhi.navigator

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.view.View

class MainActivity : AppCompatActivity() {

    @Extra var id: Int? = null
    @NonNull
    @Extra var key: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.button).setOnClickListener {
//            Navigator.startDetailActivity(this, 123)
            val intent = Intent(this, DetailActivity2::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.button2).setOnClickListener {
            Navigator.startDetailActivity2(this, "count", 100)
        }

    }
}
