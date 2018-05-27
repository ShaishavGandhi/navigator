package com.shaishavgandhi.navigator;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import javax.tools.JavaFileObject;
import static com.google.testing.compile.CompilationSubject.assertThat;

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

    @Test public void testNonActivityFails() {
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
}
