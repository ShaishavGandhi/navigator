package com.shaishavgandhi.navigator.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.intellij.lang.Language
import com.intellij.psi.PsiType
import org.jetbrains.uast.*
import org.jetbrains.uast.kotlin.KotlinUastLanguagePlugin
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
        Implementation(NavigatorBindDetector::class.java,
            EnumSet.of(Scope.JAVA_FILE, Scope.TEST_SOURCES),
            EnumSet.of(Scope.JAVA_FILE),
            EnumSet.of(Scope.TEST_SOURCES))
    )
  }

  override fun createUastHandler(context: JavaContext): UElementHandler {
    return object : UElementHandler() {
      override fun visitAnnotation(node: UAnnotation) {
        if (!applicableAnnotations().contains(node.qualifiedName)) return

        var hasBindMethod = false
        val className = node.getContainingUClass()?.name

        node.getContainingUClass()?.accept(object : AbstractUastVisitor() {
          override fun visitCallExpression(node: UCallExpression): Boolean {
            if (node.isMethodCall() && node.methodIdentifier?.name == "bind") {
              val receiver = node.receiverType
              if (isValidBindMethod(node) || isValidExtensionMethod(node, receiver, context)) {
                hasBindMethod = true
              }
            }
            return super.visitCallExpression(node)
          }
        })

        if (!hasBindMethod)
          context.report(ISSUE, node, context.getLocation(node), "You have added fields with" +
              " @Extra annotation. However, you've not yet bound the variables by calling " +
              "${className}Binder.bind(). This will cause a null pointer exception later in your app. " +
              "Avoid this by calling bind() in your onCreate() method")
      }
    }
  }

  /**
   * @return whether the given [node] call expression is a valid Navigator
   * binding method.
   *
   * There are two cases when the `bind()` message is the one we're looking for.
   * Given an activity called MyActivity
   * 1. Should have 1 argument of type MyActivity.
   * 2. The receiver should be of type MyActivityBinder or MyActivityNavigator
   *
   * @param node the calling node.
   */
  private fun isValidBindMethod(node: UCallExpression): Boolean {
    val activity = node.getContainingUClass()
    val binderName = "${activity?.name}Binder"
    val navigatorName = "${activity?.name}Navigator"
    val arguments = node.valueArguments
    return (arguments.isNotEmpty()
        && arguments.first().getExpressionType()?.canonicalText == activity?.qualifiedName)
        && (node.resolve()?.containingClass?.name == binderName ||
        node.resolve()?.containingClass?.name == navigatorName)
  }

  /**
   * @return whether it's a valid invocation of the method `bind()` from a Kotlin
   * extension perspective.
   *
   * We check three things for a given class MyActivity:
   * 1. Receiver is MyActivity
   * 2. Arguments are empty
   * 3. It is a Kotlin file!
   */
  private fun isValidExtensionMethod(node: UCallExpression, receiver: PsiType?, context: JavaContext): Boolean {
    val isKotlin  = context.file.extension == "kt"
    return receiver?.canonicalText == node.getContainingUClass()?.qualifiedName
        && node.valueArguments.isEmpty()
        && isKotlin
  }

  override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UAnnotation::class.java)

  override fun applicableAnnotations(): List<String> = listOf("com.shaishavgandhi.navigator.Extra")
}
