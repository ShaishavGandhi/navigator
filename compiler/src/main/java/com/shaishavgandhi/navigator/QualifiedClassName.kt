package com.shaishavgandhi.navigator

import com.squareup.javapoet.ClassName

data class QualifiedClassName(private val fullyQualifiedClassName: String) {

    val javaClass = ClassName.bestGuess(fullyQualifiedClassName)
    val kotlinClass = KtClassName.bestGuess(fullyQualifiedClassName)
}

typealias KtClassName = com.squareup.kotlinpoet.ClassName
