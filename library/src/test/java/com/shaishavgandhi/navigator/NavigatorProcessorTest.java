package com.shaishavgandhi.navigator;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import javax.tools.JavaFileObject;
import static com.google.testing.compile.CompilationSubject.assertThat;
import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.JVM)
public class NavigatorProcessorTest {

    private static final String PACKAGE = "com.shaishavgandhi.navigator.test.";

    private static String getName(String className) {
        return PACKAGE + className;
    }

    @Test public void testSimpleActivityCompilation() {
        String className = "MainActivity";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
        + "package com.shaishavgandhi.navigator.test;\n"
        + "\n"
        + "import com.shaishavgandhi.navigator.Extra;\n"
        + "import android.app.Activity;\n"
        + "\n"
        + "public class MainActivity extends Activity {\n"
        + " @Extra public String name;\n"
        +"}\n");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();

    }

    @Test public void testSimpleFragmentCompilation() {
        String className = "MainFragment";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.app.Fragment;\n"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra public String name;\n"
                +"}\n");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();

    }

    @Test public void testNonActivityCompileFails() {
        String className = "MainActivity";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.app.Activity;\n"
                + "\n"
                + "public class MainActivity {\n"
                + " @Extra public String name;\n"
                +"}\n");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).failed();
    }

    @Test public void testSimpleActivityNavigator() {
        String className = "MainActivity";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.app.Activity;\n"
                + "\n"
                + "public class MainActivity extends Activity {\n"
                + " @Extra public String name;\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("Navigator", ""
                + "package com.shaishavgandhi.navigator;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "import android.support.annotation.NonNull;\n"
                + "import com.shaishavgandhi.navigator.test.MainActivity;\n"
                + "import java.lang.String;\n"
                + "\n"
                + "public final class Navigator {\n"
                + "  public static final MainActivityBuilder prepareMainActivity(@NonNull final String name) {\n"
                + "    return new MainActivityBuilder(name);\n"
                + "  }\n"
                + "\n"
                + "  public static final void bind(MainActivity binder) {\n"
                + "    Bundle bundle = binder.getIntent().getExtras();\n"
                + "    if (bundle != null) {\n"
                + "      if (bundle.containsKey(\"name\")) {\n"
                + "        String name = bundle.getString(\"name\");\n"
                + "        binder.name = name;\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}\n");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.navigator.Navigator")
                .hasSourceEquivalentTo(expected);
    }

    @Test public void testSimpleFragmentNavigatorWithNullables() {
        String className = "MainFragment";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.app.Fragment;\n"
                + "import android.support.annotation.Nullable;"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra @Nullable public Long points;\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("Navigator", ""
                + "package com.shaishavgandhi.navigator;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "import com.shaishavgandhi.navigator.test.MainFragment;\n"
                + "import java.lang.Long;"
                + "\n"
                + "public final class Navigator {\n"
                + "  public static final MainFragmentBuilder prepareMainFragment()"
                + " {\n"
                + "    return new MainFragmentBuilder();\n"
                + "  }\n"
                + "\n"
                + "  public static final void bind(MainFragment binder) {\n"
                + "    Bundle bundle = binder.getArguments();\n"
                + "    if (bundle != null) {\n"
                + "      if (bundle.containsKey(\"points\")) {\n"
                + "        Long points = bundle.getLong(\"points\");\n"
                + "        binder.points = points;\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}\n");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.navigator.Navigator")
                .hasSourceEquivalentTo(expected);
    }

    @Test public void testSimpleFragmentWithNonNullPrimitive() {
        String className = "MainFragment";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.app.Fragment;\n"
                + "import android.support.annotation.Nullable;"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra public long points = -1;\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("Navigator", ""
                + "package com.shaishavgandhi.navigator;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "import android.support.annotation.NonNull;\n"
                + "import com.shaishavgandhi.navigator.test.MainFragment;\n"
                + "\n"
                + "public final class Navigator {\n"
                + "  public static final MainFragmentBuilder prepareMainFragment(@NonNull final long points)"
                + " {\n"
                + "    return new MainFragmentBuilder(points);\n"
                + "  }\n"
                + "\n"
                + "  public static final void bind(MainFragment binder) {\n"
                + "    Bundle bundle = binder.getArguments();\n"
                + "    if (bundle != null) {\n"
                + "      if (bundle.containsKey(\"points\")) {\n"
                + "        long points = bundle.getLong(\"points\");\n"
                + "        binder.points = points;\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}\n");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.navigator.Navigator")
                .hasSourceEquivalentTo(expected);
    }
}
