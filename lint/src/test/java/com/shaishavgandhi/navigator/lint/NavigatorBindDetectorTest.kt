package com.shaishavgandhi.navigator.lint

import com.android.tools.lint.checks.infrastructure.TestFiles.java
import com.android.tools.lint.checks.infrastructure.TestFiles.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Ignore
import org.junit.Test

class NavigatorBindDetectorTest {

  companion object {
    private val EXTRA = java("test/com/shaishavgandhi/navigator/Extra.java", """
      package com.shaishavgandhi.navigator;

      public @interface Extra {}
      """).indented()
  }

  @Test fun noBindCalled() {
    lint()
        .files(EXTRA,
            java("""
          package foo;

          import com.shaishavgandhi.navigator.Extra;
          import java.lang.String;

          public class MyActivity {
            @Extra String name;

            void onCreate() {
            }
          }""").indented())
        .detector(NavigatorBindDetector())
        .issues(NavigatorBindDetector.ISSUE)
        .run()
        .expect("""src/foo/MyActivity.java:7: Error: ${NavigatorBindDetector.SHORT_DESCRIPTION} [Navigator]
          |  @Extra String name;
          |  ~~~~~~
          |1 errors, 0 warnings""".trimMargin())
  }

  @Test fun kotlinNoBindCalled() {
    lint()
        .files(EXTRA,
            kotlin("""
          package foo

          import com.shaishavgandhi.navigator.Extra;
          import kotlin.String

          class MyActivity {
            @Extra lateinit var name: String

            fun onCreate() {
            }
          }""").indented())
        .detector(NavigatorBindDetector())
        .issues(NavigatorBindDetector.ISSUE)
        .run()
        .expect("""src/foo/MyActivity.kt:7: Error: ${NavigatorBindDetector.SHORT_DESCRIPTION} [Navigator]
          |  @Extra lateinit var name: String
          |  ~~~~~~
          |1 errors, 0 warnings""".trimMargin())
  }

  @Test fun javaBindCalled() {
    val binder = java("""
      package foo;

      import foo.MyActivity;

      public class MyActivityBinder {
        public static void bind(MyActivity activity) {}
      }
    """).indented()
    lint()
        .files(EXTRA,
            binder,
            java("""
          package foo;

          import com.shaishavgandhi.navigator.Extra;
          import java.lang.String;

          public class MyActivity {
            @Extra String name;

            void onCreate() {
              MyActivityBinder.bind(this);
            }
          }""").indented())
        .detector(NavigatorBindDetector())
        .issues(NavigatorBindDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test fun javaBindCalledOnNavigator() {
    val binder = java("""
      package foo;

      import foo.MyActivity;

      public class MyActivityNavigator {
        public static void bind(MyActivity activity) {}
      }
    """).indented()
    lint()
        .files(EXTRA,
            binder,
            java("""
          package foo;

          import com.shaishavgandhi.navigator.Extra;
          import java.lang.String;

          public class MyActivity {
            @Extra String name;

            void onCreate() {
              MyActivityNavigator.bind(this);
            }
          }""").indented())
        .detector(NavigatorBindDetector())
        .issues(NavigatorBindDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test fun kotlinBindCalled() {
    val binder = java("""
      package foo;

      import foo.MyActivity;

      public class MyActivityBinder {
        public static void bind(MyActivity activity) {}
      }
    """).indented()
    lint()
        .files(EXTRA,
            binder,
            kotlin("""
          package foo

          import com.shaishavgandhi.navigator.Extra
          import kotlin.String

          class MyActivity {
            @Extra lateinit var name: String

            fun onCreate() {
              MyActivityBinder.bind(this)
            }
          }""").indented())
        .detector(NavigatorBindDetector())
        .issues(NavigatorBindDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test fun kotlinBindCalledViaExtension() {
    val binder = kotlin("""
      package foo

      import foo.MyActivity

      fun MyActivity.bind() {
      }
    """).indented()
    lint()
        .files(EXTRA,
            binder,
            kotlin("""
          package foo

          import com.shaishavgandhi.navigator.Extra
          import kotlin.String

          class MyActivity {
            @Extra lateinit var name: String

            fun onCreate() {
              bind()
            }
          }""").indented())
        .detector(NavigatorBindDetector())
        .issues(NavigatorBindDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test fun errorOnBindCalledOnMemberClass() {
    lint()
        .files(EXTRA,
            kotlin("""
          package foo

          import com.shaishavgandhi.navigator.Extra
          import kotlin.String

          class MyActivity {
            @Extra lateinit var name: String

            fun onCreate() {
              bind()
            }

            fun bind() {
            }
          }""").indented())
        .detector(NavigatorBindDetector())
        .issues(NavigatorBindDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Test fun kotlinNavigatorBind() {
    val navigator = kotlin("""
      package com.shaishavgandhi.navigator

      object Navigator
    """).indented()

    val extensionFile = kotlin("""
      package foo

      import com.shaishavgandhi.navigator.Navigator
      import foo.MyActivity

      fun Navigator.bind(binder: MyActivity) {}
    """).indented()

    lint()
        .files(EXTRA,
            navigator,
            extensionFile,
            kotlin("""
          package foo

          import com.shaishavgandhi.navigator.Extra
          import com.shaishavgandhi.navigator.Navigator
          import kotlin.String

          class MyActivity {
            @Extra lateinit var name: String

            fun onCreate() {
              Navigator.bind(this)
            }
          }""").indented())
        .detector(NavigatorBindDetector())
        .issues(NavigatorBindDetector.ISSUE)
        .run()
        .expectClean()
  }

  @Ignore
  @Test fun errorBindCalledOnMemberMethod() {
    lint()
        .files(EXTRA,
            java("""
          package foo;

          import com.shaishavgandhi.navigator.Extra;
          import java.lang.String;

          public class MyActivity {
            @Extra String name;

            void onCreate() {
              bind(this);
            }

            void bind(MyActivity activity) {}
          }""").indented())
        .detector(NavigatorBindDetector())
        .issues(NavigatorBindDetector.ISSUE)
        .run()
        .expect("""src/foo/MyActivity.java:7: Error: ${NavigatorBindDetector.SHORT_DESCRIPTION} [Navigator]
          |  @Extra String name;
          |  ~~~~~~
          |1 errors, 0 warnings""".trimMargin())
  }

  @Ignore
  @Test fun errorBindCalledOnMemberMethodWithNoParameters() {
    lint()
        .files(EXTRA,
            java("""
          package foo;

          import com.shaishavgandhi.navigator.Extra;
          import java.lang.String;

          public class MyActivity {
            @Extra String name;

            void onCreate() {
              bind();
            }

            void bind() {}
          }""").indented())
        .detector(NavigatorBindDetector())
        .issues(NavigatorBindDetector.ISSUE)
        .run()
        .expect("""src/foo/MyActivity.java:7: Error: ${NavigatorBindDetector.SHORT_DESCRIPTION} [Navigator]
          |  @Extra String name;
          |  ~~~~~~
          |1 errors, 0 warnings""".trimMargin())
  }
}
