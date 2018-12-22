package com.shaishavgandhi.navigator.sample


import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shaishavgandhi.navigato.sampler.R
import com.shaishavgandhi.navigator.Extra

/**
 * A simple [Fragment] subclass.
 *
 */
class DetailFragment : androidx.fragment.app.Fragment() {

    @Extra lateinit var user: User
    @Extra @DrawableRes var resId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }


}
