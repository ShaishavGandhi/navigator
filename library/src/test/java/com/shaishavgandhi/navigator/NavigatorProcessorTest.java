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

    public static final String PACKAGE = "com.shaishavgandhi.sampleapp.test.";

    public static String getName(String className) {
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

    @Test public void testSimpleFragmentActivityCompilation() {
        String className = "MainActivity";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.support.v4.app.FragmentActivity;\n"
                + "\n"
                + "public class MainActivity extends FragmentActivity {\n"
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

    @Test public void testSimpleAndroidXFragmentCompilation() {
        String className = "MainFragment";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import androidx.fragment.app.Fragment;\n"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra public String name;\n"
                +"}\n");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();

    }

    @Test public void testSimpleSupportFragmentCompilation() {
        String className = "MainFragment";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.support.v4.app.Fragment;\n"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra public String name;\n"
                +"}\n");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();

    }

    @Test public void testSimpleClassWithFinalVariable() {
        String className = "MainFragment";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.support.v4.app.Fragment;\n"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra final String name;\n"
                +"}\n");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).failed();

    }

    @Test public void testNonSerializableVariable() {
        String className = "MainFragment";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.support.v4.app.Fragment;\n"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra User user;\n"
                + " public class User {}\n"
                +"}\n");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).failed();

    }

    @Test public void testSimpleDialogFragmentCompilation() {
        String className = "MainFragment";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.app.DialogFragment;\n"
                + "\n"
                + "public class MainFragment extends DialogFragment {\n"
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
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "\n"
                + "public final class Navigator {\n"
                + "  public static final void bind(MainActivity binder) {\n"
                + "    MainActivityBinder.bind(binder);\n"
                + "  }\n\n"
                + "\n"
                + "}\n");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.navigator.test.Navigator")
                .hasSourceEquivalentTo(expected);
    }

    @Test public void testSimpleActivityNavigatorWithNullable() {
        String className = "MainActivity";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.support.annotation.Nullable;"
                + "import android.app.Activity;\n"
                + "\n"
                + "public class MainActivity extends Activity {\n"
                + " @Extra @Nullable String name;\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("Navigator", ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "\n"
                + "public final class Navigator {\n"
                + "  public static final void bind(MainActivity binder) {\n"
                + "    MainActivityBinder.bind(binder);\n"
                + "  }\n\n"
                + "}\n");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.navigator.test.Navigator")
                .hasSourceEquivalentTo(expected);
    }

    @Test public void testSimpleActivityNavigatorWithSerializable() {
        String className = "MainActivity";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import java.io.Serializable;"
                + "import android.app.Activity;\n"
                + "\n"
                + "public class MainActivity extends Activity {\n"
                + " @Extra public User name;\n"
                + " public class User implements Serializable {}\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("Navigator", ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "\n"
                + "public final class Navigator {\n"
                + "  public static final void bind(MainActivity binder) {\n"
                + "    MainActivityBinder.bind(binder);\n"
                + "  }\n\n"
                + "\n"
                + "}\n");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.navigator.test.Navigator")
                .hasSourceEquivalentTo(expected);
    }

}
