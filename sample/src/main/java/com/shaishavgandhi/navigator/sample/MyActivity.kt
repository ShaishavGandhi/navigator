package com.shaishavgandhi.navigator.sample

import com.shaishavgandhi.navigator.Extra
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MyActivity : AppCompatActivity() {
  @Extra var name: String? = ""
  @Extra lateinit var whatever: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }
}
