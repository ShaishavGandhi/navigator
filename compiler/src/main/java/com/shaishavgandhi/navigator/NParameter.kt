package com.shaishavgandhi.navigator

import javax.lang.model.type.TypeMirror

data class NParameter(val type: TypeMirror,
                      val name: String,
                      val customKey: Boolean)