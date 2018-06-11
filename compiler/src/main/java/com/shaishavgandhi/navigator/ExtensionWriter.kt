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

        val fileBuilder = FileSpec.builder("com.shaishavgandhi.navigator.sample", "NavigatorExtensions")
        val extensionClass = TypeSpec.classBuilder("NavigatorExtensions")
        for (entry in annotationsPerClass.entries) {
            val className = entry.key.kotlinClass

            messager.printMessage(Diagnostic.Kind.WARNING, className.simpleName())

            fileBuilder.addFunction(FunSpec.builder("prepare${className.simpleName()}")
                .receiver(ClassName.bestGuess("android.app.Activity"))
                .addStatement("// This is a comment")
                .build())
        }

        fileBuilder.build().writeTo(File(kaptGeneratedDirPath))
    }
}