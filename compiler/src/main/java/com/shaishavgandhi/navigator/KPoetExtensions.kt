package com.shaishavgandhi.navigator

import com.squareup.kotlinpoet.AnnotationSpec

typealias KotlinAnnotationSpec = AnnotationSpec
typealias JavaAnnotationSpec = com.squareup.javapoet.AnnotationSpec

inline fun JavaAnnotationSpec.toKPoet(): KotlinAnnotationSpec {
  return KotlinAnnotationSpec.builder(KtClassName.bestGuess(this.type.toString())).build()
}
