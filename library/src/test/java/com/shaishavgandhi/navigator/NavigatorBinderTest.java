package com.shaishavgandhi.navigator;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.shaishavgandhi.navigator.NavigatorProcessorTest.getName;

@FixMethodOrder(MethodSorters.JVM)
public class NavigatorBinderTest {

    @Test
    public void testSimpleActivityNavigator() {
        String className = "MainActivity";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.app.Activity;\n"
                + "\n"
                + "public class MainActivity extends Activity {\n"
                + " @Extra String name;\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("com.shaishavgandhi.sampleapp.test.MainActivityBinder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "import java.lang.String;\n"
                + "\n"
                + "public final class MainActivityBinder {\n"
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
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.sampleapp.test.MainActivityBinder")
                .hasSourceEquivalentTo(expected);
    }

}
