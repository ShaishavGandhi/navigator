package com.shaishavgandhi.navigator.sample

import android.view.View
import androidx.core.util.Pair

interface UserListListener {
  fun onUserClicked(user: User, vararg view: Pair<View, String>)
}
