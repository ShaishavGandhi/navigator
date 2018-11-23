package com.shaishavgandhi.navigator.lint

import com.android.tools.lint.checks.infrastructure.TestFiles.java
import com.android.tools.lint.checks.infrastructure.TestFiles.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Ignore
import org.junit.Test

class NavigatorBindDetectorTest {

  companion object {
    private val EXTRA = java("""
      package com.shaishavgandhi.navigator;

      public @interface Extra {}
      """).indented()
  }

  @Test fun callsBindFromOnCreate() {
    lint()
        .files(EXTRA, java("""
          package foo;

          import android.app.Activity;
          import com.shaishavgandhi.navigator.Extra;
          import android.os.Bundle;

          public class MyActivity extends Activity {
            @Extra String name;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
              super.onCreate(savedInstanceState);
              MyActivityBinder.bind(this);
            }
          }
        """).indented())
        .issues(NavigatorBindDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Ignore
  @Test fun noBindCalled() {
    lint()
        .files(kotlin("""
          package foo

          import com.shaishavgandhi.navigator.Extra
          import android.os.Bundle
          import androidx.appcompat.app.AppCompatActivity

          class MyActivity : AppCompatActivity() {
            @Extra var name: String? = ""
            @Extra lateinit var whatever: String

            override fun onCreate(savedInstanceState: Bundle) {
              super.onCreate(savedInstanceState)
            }
          }
        """).indented())
        .issues(NavigatorBindDetector.ISSUE)
        .run()
        .expect("""

        """.trimIndent())
  }
}