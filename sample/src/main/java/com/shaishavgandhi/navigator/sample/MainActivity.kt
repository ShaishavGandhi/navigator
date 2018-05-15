package com.shaishavgandhi.navigator.sample

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.shaishavgandhi.navigato.sampler.R
import com.shaishavgandhi.navigator.Extra
import com.shaishavgandhi.navigator.Navigator

class MainActivity : AppCompatActivity() {

    @Extra
    var id: Int? = null
    @Extra
    var key: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.button).setOnClickListener {
            val users = arrayListOf(User(name = "Shaishav", age = 10),
                    User("Dimple", 13))

            Navigator.prepareDetailActivity(users, Points(100), 100, "Yay")
                    // Clears the task
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .start(this)

        }

        findViewById<View>(R.id.button2).setOnClickListener {
        }

    }
}
