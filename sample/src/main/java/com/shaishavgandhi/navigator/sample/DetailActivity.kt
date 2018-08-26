package com.shaishavgandhi.navigator.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.Nullable
import android.util.SparseArray
import android.widget.TextView
import com.shaishavgandhi.navigato.sampler.R
import com.shaishavgandhi.navigator.Extra
import com.shaishavgandhi.navigator.Navigator

class DetailActivity : AppCompatActivity() {

    @Extra @Nullable lateinit var userList: ArrayList<User>
    @Extra @Nullable lateinit var userSparseArray: SparseArray<User>
    @Extra @Nullable lateinit var userArray: Array<User>
    @Extra var points: Points? = null
    @Extra var userId: Long? = null
    @Extra var source: String? = null
    @Extra var intArray: IntArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        Navigator.bind(this)

        DetailFragmentBuilder.builder(userList.first()).bundle
        prepareDetailFragment(user = userList.first())
        findViewById<TextView>(R.id.whatever).text = userList.first().name

    }
}
