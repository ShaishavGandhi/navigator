package com.shaishavgandhi.navigator.lint

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiType
import org.jetbrains.kotlin.psi.psiUtil.isFunctionalExpression
import org.jetbrains.uast.*
import org.jetbrains.uast.util.isMethodCall
import org.jetbrains.uast.visitor.AbstractUastVisitor
import java.util.*

/**
 * Detects whether the user has called bind() in their Activity/Fragment.
 *
 * If a user has added an annotation `@Extra`, the user must call `YourActivityBinder.bind()`
 * in their onCreate/onStart/onResume method to bind the variables.
 */
class NavigatorBindDetector : Detector(), SourceCodeScanner {
  companion object {
    val ISSUE: Issue = Issue.create(
        "NavigatorBindUsage",
        "You're not calling bind() in this class.",
        "You have added fields with @Extra annotation. However, you've not yet bound " +
            "the variables by calling YourActivityBinder.bind(). This will cause a null pointer " +
            "exception later in your app.",
        Category.CORRECTNESS,
        10,
        Severity.ERROR,
        Implementation(NavigatorBindDetector::class.java, EnumSet.of(Scope.JAVA_FILE)))
  }

  override fun applicableAnnotations(): List<String> = listOf("com.shaishavgandhi.navigator.Extra")

  override fun visitAnnotationUsage(context: JavaContext,
                                    node: UElement,
                                    type: AnnotationUsageType,
                                    annotation: UAnnotation,
                                    qualifiedName: String,
                                    method: PsiMethod?,
                                    referenced: PsiElement?,
                                    annotations: List<UAnnotation>,
                                    allMemberAnnotations: List<UAnnotation>,
                                    allClassAnnotations: List<UAnnotation>,
                                    allPackageAnnotations: List<UAnnotation>) {
    var hasBind = false
    val className = node.getContainingUClass()?.name
    context.report(ISSUE, node, context.getLocation(node), "You have added fields with" +
        " @Extra annotation. However, you've not yet bound the variables by calling " +
        "${className}Binder.bind(). This will cause a null pointer exception later in your app. " +
        "Avoid this by calling bind() in your onCreate() method")
  }

//  override fun visitAnnotationUsage(context: JavaContext,
//                                    node: UElement,
//                                    type: AnnotationUsageType,
//                                    annotation: UAnnotation,
//                                    qualifiedName: String,
//                                    method: PsiMethod?,
//                                    annotations: List<UAnnotation>,
//                                    allMemberAnnotations: List<UAnnotation>,
//                                    allClassAnnotations: List<UAnnotation>,
//                                    allPackageAnnotations: List<UAnnotation>) {
//    var hasBind = false
//    val className = node.getContainingUClass()?.name
//    context.report(ISSUE, node, context.getLocation(node), "You have added fields with" +
//        " @Extra annotation. However, you've not yet bound the variables by calling " +
//        "${className}Binder.bind(). This will cause a null pointer exception later in your app. " +
//        "Avoid this by calling bind() in your onCreate() method")
//
//    node.getContainingUClass()?.accept(object : AbstractUastVisitor() {
//      override fun visitCallExpression(node: UCallExpression): Boolean {
//        if (node.isMethodCall() && node.methodIdentifier?.name == "bind") {
//          hasBind = true
//        }
//        return super.visitCallExpression(node)
//      }
//    })
//
//    if (!hasBind)
//      context.report(ISSUE, node, context.getLocation(node), "You have added fields with" +
//          " @Extra annotation. However, you've not yet bound the variables by calling " +
//          "${className}Binder.bind(). This will cause a null pointer exception later in your app. " +
//          "Avoid this by calling bind() in your onCreate() method")
//
//  }
}
