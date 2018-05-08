package com.shaishavgandhi.navigator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class DetailActivity : AppCompatActivity() {

    @Extra protected var whatever: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        Navigator.bind(this)
        findViewById<TextView>(R.id.whatever).text = "$whatever"
    }
}
