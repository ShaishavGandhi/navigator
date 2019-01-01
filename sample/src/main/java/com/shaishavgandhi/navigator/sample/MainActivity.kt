package com.shaishavgandhi.navigator.sample

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shaishavgandhi.navigato.sampler.R

class MainActivity : AppCompatActivity(), UserListListener {

  private val recyclerView: RecyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView) }
  lateinit var adapter: UserListAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    recyclerView.layoutManager = LinearLayoutManager(this)
    adapter = UserListAdapter(sampleData(), this)
    recyclerView.adapter = adapter
  }

  private fun sampleData(): List<User> {
    val list = mutableListOf<User>()

    list.add(User(
        name = "Tom",
        age = 21,
        color = Color.CYAN
    ))

    list.add(User(
        name = "Robert",
        age = 24,
        color = Color.RED
    ))

    list.add(User(
        name = "William",
        age = 24,
        color = Color.MAGENTA
    ))

    return list
  }

  override fun onUserClicked(user: User, vararg view: Pair<View, String>) {
    val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this, *view).toBundle()
    userDetailActivityBuilder(user)
        .startWithExtras(this, bundle)
  }
}
