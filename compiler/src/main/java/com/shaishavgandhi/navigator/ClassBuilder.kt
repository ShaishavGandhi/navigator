package com.shaishavgandhi.navigator

import com.squareup.javapoet.ParameterSpec

/**
 * Data class representing an Activity/Fragment's builder class.
 *
 * This class contains the elements required for constructing the builder.
 * i.e Activity/Fragment name, constructor params, optional params etc.
 */
data class ClassBuilder(val name: QualifiedClassName,
                        val constructorParams: List<ParameterSpec>) {
}
