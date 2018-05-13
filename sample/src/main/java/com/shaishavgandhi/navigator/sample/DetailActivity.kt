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
    protected var whatever: Long? = null
    @Extra
    var strings: Array<String>? = null
    @Extra
    var user: User? = null
    @Extra
    var points: Points? = null
    @Extra
    var userList: ArrayList<User>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        Navigator.bind(this)
        findViewById<TextView>(R.id.whatever).text = "${userList?.size}"
        Toast.makeText(applicationContext, strings?.reduce { acc, s -> acc.plus(s) }, Toast
                .LENGTH_SHORT)
                .show()
        Toast.makeText(applicationContext, user?.name + " has got " + points?.value + " points",
                Toast
                .LENGTH_SHORT).show()

    }

    fun setWhatever(whatever: Long) {
        this.whatever = whatever
    }
}
