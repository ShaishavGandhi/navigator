package com.shaishavgandhi.navigator

import com.squareup.kotlinpoet.*
import java.io.File
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.tools.Diagnostic

class ExtensionWriter(private val processingEnvironment: ProcessingEnvironment) {

    val typeUtils = processingEnvironment.typeUtils
    val elementUtil = processingEnvironment.elementUtils
    val messager = processingEnvironment.messager

    val navigatorClass = ClassName.bestGuess("com.shaishavgandhi.navigator.Navigator")

    fun generateExtensions(annotations: LinkedHashMap<QualifiedClassName, LinkedHashSet<Element>>) {
        val annotationsPerClass = LinkedHashMap<QualifiedClassName, LinkedHashSet<Element>>()
        annotationsPerClass.putAll(annotations)
        val kaptGeneratedDirPath = processingEnvironment.options["kapt.kotlin.generated"]
            ?.replace("kaptKotlin", "kapt")
                ?: run {
                    // If the option does not exist this is not being processed by kapt,
                    // so we don't need to generate kotlin extensions
                    return
                }

        if (annotationsPerClass.isEmpty()) {
            return
        }

        for (entry in annotationsPerClass.entries) {
            val className = entry.key.kotlinClass

            val extensionFileName = "${className.simpleName()}NavigatorExtensions"
            val fileBuilder = FileSpec.builder(className.packageName(), extensionFileName)

            val classBinder = ClassName.bestGuess("${className.packageName()}.${className.simpleName()}Binder")

            // Static method to add to Navigator
            fileBuilder.addFunction(FunSpec.builder("bind")
                .receiver(navigatorClass)
                .addParameter("binder", className)
                .addStatement("%T.bind(binder)", classBinder)
                .build())

            // Extension on the activity/fragment
            fileBuilder.addFunction(FunSpec.builder("bind")
                .receiver(className)
                .addParameter("binder", className)
                .addStatement("%T.bind(binder)", classBinder)
                .build())

            fileBuilder.build().writeTo(File(kaptGeneratedDirPath))
        }

    }
}