package com.shaishavgandhi.navigator.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.shaishavgandhi.navigato.sampler.R
import com.shaishavgandhi.navigator.Extra
import com.shaishavgandhi.navigator.Navigator

class DetailActivity : AppCompatActivity() {

    @Extra
    var userList: ArrayList<User>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        intent.flags

        Navigator.bind(this)
        findViewById<TextView>(R.id.whatever).text = "${userList?.size}"

    }
}
