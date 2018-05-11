package com.shaishavgandhi.navigator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    @Extra var id: Int? = null
    @Extra var key: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.button).setOnClickListener {
            Navigator.startDetailActivity(this, arrayOf("one", "two"),  User(name =
            "Shaishav", age = 14), 123, Points(value = 20))

        }

        findViewById<View>(R.id.button2).setOnClickListener {
            Navigator.startDetailActivity2(this,  "whatever", 100)
        }

    }
}
