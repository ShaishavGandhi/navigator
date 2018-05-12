package com.shaishavgandhi.navigator

import android.content.Intent
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
//            Navigator.startDetailActivity(this, User(name =
//            "Shaishav", age = 14), Points(value = 20), 123, arrayOf("one", "two"))
            val users = arrayListOf(User(name = "Shaishav", age = 10),
                    User("Dimple", 13))
            Navigator.DetailActivityBuilder(100, Points(100),
                    arrayOf("One", "Two"), users, users[0])
                    .start(this)

        }

        findViewById<View>(R.id.button2).setOnClickListener {
//            Navigator.startDetailActivity2(this,  "whatever", 100)
        }

    }
}
