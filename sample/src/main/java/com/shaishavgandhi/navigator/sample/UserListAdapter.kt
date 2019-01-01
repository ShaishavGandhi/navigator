package com.shaishavgandhi.navigator.sample

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.shaishavgandhi.navigato.sampler.R

class UserListAdapter(
    private val users: List<User>,
    private val listener: UserListListener
): RecyclerView.Adapter<UserListAdapter.UserHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_user, parent, false)
    return UserHolder(view)
  }

  override fun getItemCount(): Int {
    return users.size
  }

  override fun onBindViewHolder(holder: UserHolder, position: Int) {
    val user = users[position]
    holder.user = user
    holder.itemView.setOnClickListener {
      val logoArgs = Pair(holder.logo as View, holder.logo.transitionName)
      listener.onUserClicked(user, logoArgs)
    }
  }

  class UserHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val logo: AppCompatImageView = itemView.findViewById(R.id.color)
    val name: AppCompatTextView = itemView.findViewById(R.id.name)

    var user: User? = null
    set(value) {
      field = value
      setData(value)
    }

    private fun setData(user: User?) {
      user?.let {
        name.text = it.name
        itemView.findViewById<AppCompatTextView>(R.id.age).text = it.age.toString()
        logo.background = ColorDrawable(it.color)
      }
    }

  }
}