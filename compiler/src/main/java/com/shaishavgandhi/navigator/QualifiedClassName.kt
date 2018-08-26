package com.shaishavgandhi.navigator

import com.squareup.javapoet.ClassName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName

data class QualifiedClassName(private val fullyQualifiedClassName: String) {

    val javaClass = ClassName.bestGuess(fullyQualifiedClassName)
    val kotlinClass = KtClassName.bestGuess(fullyQualifiedClassName)
}

typealias KtClassName = com.squareup.kotlinpoet.ClassName
typealias KParameterSpec = ParameterSpec
typealias KTypeName = TypeName
