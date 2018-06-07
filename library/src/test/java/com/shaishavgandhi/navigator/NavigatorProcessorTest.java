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
                + "package com.shaishavgandhi.navigator;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "import android.support.annotation.NonNull;\n"
                + "import com.shaishavgandhi.navigator.test.MainActivity;\n"
                + "\n"
                + "public final class Navigator {\n"
                + "  public static final MainActivityBuilder prepareMainActivity(@NonNull final MainActivity.User name) {\n"
                + "    return new MainActivityBuilder(name);\n"
                + "  }\n"
                + "\n"
                + "  public static final void bind(MainActivity binder) {\n"
                + "    Bundle bundle = binder.getIntent().getExtras();\n"
                + "    if (bundle != null) {\n"
                + "      if (bundle.containsKey(\"name\")) {\n"
                + "        MainActivity.User name = (MainActivity.User) bundle.getSerializable(\"name\");\n"
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

    @Test public void testSimpleActivityNavigatorWithParcelable() {
        String className = "MainActivity";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import java.io.Serializable;"
                + "import android.app.Activity;\n"
                + "import android.os.Parcelable;\n"
                + "import android.os.Parcel;\n"
                + "\n"
                + "public class MainActivity extends Activity {\n"
                + " @Extra public User name;\n"
                + " public class User implements Parcelable {\n"
                + "  String name;"
                + "  protected User(Parcel in) {\n"
                + "   name = in.readString();\n"
                + "  }\n"
                + "\n"
                + "  public final Creator<User> CREATOR = new Creator<User>() {\n"
                + "   @Override\n"
                + "   public User createFromParcel(Parcel in) {\n"
                + "    return new User(in);\n"
                + "   }\n"
                + "\n"
                + "   @Override\n"
                + "   public User[] newArray(int size) {\n"
                + "    return new User[size];\n"
                + "   }\n"
                + "  };"
                + "  @Override\n"
                + "  public int describeContents() {\n"
                + "   return 0;\n"
                + "  }\n"
                + "\n"
                + "  @Override\n"
                + "  public void writeToParcel(Parcel dest, int flags) {\n"
                + "   dest.writeString(name);\n"
                + "  }\n"
                + " }"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("Navigator", ""
                + "package com.shaishavgandhi.navigator;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "import android.support.annotation.NonNull;\n"
                + "import com.shaishavgandhi.navigator.test.MainActivity;\n"
                + "\n"
                + "public final class Navigator {\n"
                + "  public static final MainActivityBuilder prepareMainActivity(@NonNull final MainActivity.User name) {\n"
                + "    return new MainActivityBuilder(name);\n"
                + "  }\n"
                + "\n"
                + "  public static final void bind(MainActivity binder) {\n"
                + "    Bundle bundle = binder.getIntent().getExtras();\n"
                + "    if (bundle != null) {\n"
                + "      if (bundle.containsKey(\"name\")) {\n"
                + "        MainActivity.User name = bundle.getParcelable(\"name\");\n"
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

    @Test public void testSimpleActivityNavigatorWithParcelableArrayList() {
        String className = "MainActivity";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import java.io.Serializable;"
                + "import android.app.Activity;\n"
                + "import android.os.Parcelable;\n"
                + "import android.os.Parcel;\n"
                + "import java.util.ArrayList;\n"
                + "\n"
                + "public class MainActivity extends Activity {\n"
                + " @Extra public ArrayList<User> name;\n"
                + " public class User implements Parcelable {\n"
                + "  String name;"
                + "  protected User(Parcel in) {\n"
                + "   name = in.readString();\n"
                + "  }\n"
                + "\n"
                + "  public final Creator<User> CREATOR = new Creator<User>() {\n"
                + "   @Override\n"
                + "   public User createFromParcel(Parcel in) {\n"
                + "    return new User(in);\n"
                + "   }\n"
                + "\n"
                + "   @Override\n"
                + "   public User[] newArray(int size) {\n"
                + "    return new User[size];\n"
                + "   }\n"
                + "  };"
                + "  @Override\n"
                + "  public int describeContents() {\n"
                + "   return 0;\n"
                + "  }\n"
                + "\n"
                + "  @Override\n"
                + "  public void writeToParcel(Parcel dest, int flags) {\n"
                + "   dest.writeString(name);\n"
                + "  }\n"
                + " }"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("Navigator", ""
                + "package com.shaishavgandhi.navigator;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "import android.support.annotation.NonNull;\n"
                + "import com.shaishavgandhi.navigator.test.MainActivity;\n"
                + "import java.util.ArrayList;\n"
                + "\n"
                + "public final class Navigator {\n"
                + "  public static final MainActivityBuilder prepareMainActivity(@NonNull final ArrayList<MainActivity.User> name) {\n"
                + "    return new MainActivityBuilder(name);\n"
                + "  }\n"
                + "\n"
                + "  public static final void bind(MainActivity binder) {\n"
                + "    Bundle bundle = binder.getIntent().getExtras();\n"
                + "    if (bundle != null) {\n"
                + "      if (bundle.containsKey(\"name\")) {\n"
                + "        ArrayList<MainActivity.User> name = bundle.getParcelableArrayList(\"name\");\n"
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

    @Test public void testSimpleActivityNavigatorWithParcelableArray() {
        String className = "MainActivity";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import java.io.Serializable;"
                + "import android.app.Activity;\n"
                + "import android.os.Parcelable;\n"
                + "import android.os.Parcel;\n"
                + "\n"
                + "public class MainActivity extends Activity {\n"
                + " @Extra public User[] name;\n"
                + " public class User implements Parcelable {\n"
                + "  String name;"
                + "  protected User(Parcel in) {\n"
                + "   name = in.readString();\n"
                + "  }\n"
                + "\n"
                + "  public final Creator<User> CREATOR = new Creator<User>() {\n"
                + "   @Override\n"
                + "   public User createFromParcel(Parcel in) {\n"
                + "    return new User(in);\n"
                + "   }\n"
                + "\n"
                + "   @Override\n"
                + "   public User[] newArray(int size) {\n"
                + "    return new User[size];\n"
                + "   }\n"
                + "  };"
                + "  @Override\n"
                + "  public int describeContents() {\n"
                + "   return 0;\n"
                + "  }\n"
                + "\n"
                + "  @Override\n"
                + "  public void writeToParcel(Parcel dest, int flags) {\n"
                + "   dest.writeString(name);\n"
                + "  }\n"
                + " }"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("Navigator", ""
                + "package com.shaishavgandhi.navigator;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "import android.support.annotation.NonNull;\n"
                + "import com.shaishavgandhi.navigator.test.MainActivity;\n"
                + "\n"
                + "public final class Navigator {\n"
                + "  public static final MainActivityBuilder prepareMainActivity(@NonNull final MainActivity.User[] name) {\n"
                + "    return new MainActivityBuilder(name);\n"
                + "  }\n"
                + "\n"
                + "  public static final void bind(MainActivity binder) {\n"
                + "    Bundle bundle = binder.getIntent().getExtras();\n"
                + "    if (bundle != null) {\n"
                + "      if (bundle.containsKey(\"name\")) {\n"
                + "        MainActivity.User[] name = (MainActivity.User[])bundle.getParcelableArray(\"name\");\n"
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

    @Test public void testSimpleActivityNavigatorWithSparseParcelableArray() {
        String className = "MainActivity";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import java.io.Serializable;"
                + "import android.app.Activity;\n"
                + "import android.os.Parcelable;\n"
                + "import android.os.Parcel;\n"
                + "import android.util.SparseArray;\n"
                + "\n"
                + "public class MainActivity extends Activity {\n"
                + " @Extra public SparseArray<User> name;\n"
                + " public class User implements Parcelable {\n"
                + "  String name;"
                + "  protected User(Parcel in) {\n"
                + "   name = in.readString();\n"
                + "  }\n"
                + "\n"
                + "  public final Creator<User> CREATOR = new Creator<User>() {\n"
                + "   @Override\n"
                + "   public User createFromParcel(Parcel in) {\n"
                + "    return new User(in);\n"
                + "   }\n"
                + "\n"
                + "   @Override\n"
                + "   public User[] newArray(int size) {\n"
                + "    return new User[size];\n"
                + "   }\n"
                + "  };"
                + "  @Override\n"
                + "  public int describeContents() {\n"
                + "   return 0;\n"
                + "  }\n"
                + "\n"
                + "  @Override\n"
                + "  public void writeToParcel(Parcel dest, int flags) {\n"
                + "   dest.writeString(name);\n"
                + "  }\n"
                + " }"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("Navigator", ""
                + "package com.shaishavgandhi.navigator;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "import android.support.annotation.NonNull;\n"
                + "import android.util.SparseArray;\n"
                + "import com.shaishavgandhi.navigator.test.MainActivity;\n"
                + "\n"
                + "public final class Navigator {\n"
                + "  public static final MainActivityBuilder prepareMainActivity(@NonNull final SparseArray<MainActivity.User> name) {\n"
                + "    return new MainActivityBuilder(name);\n"
                + "  }\n"
                + "\n"
                + "  public static final void bind(MainActivity binder) {\n"
                + "    Bundle bundle = binder.getIntent().getExtras();\n"
                + "    if (bundle != null) {\n"
                + "      if (bundle.containsKey(\"name\")) {\n"
                + "        SparseArray<MainActivity.User> name = bundle.getSparseParcelableArray(\"name\");\n"
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

    @Test public void testSimpleClassNavigatorWithOptional() {
        String className = "MainFragment";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import com.shaishavgandhi.navigator.Optional;\n"
                + "import android.app.Fragment;\n"
                + "import android.support.annotation.Nullable;"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra @Optional public long points;\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("Navigator", ""
                + "package com.shaishavgandhi.navigator;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "import com.shaishavgandhi.navigator.test.MainFragment;\n"
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

    @Test public void testSimpleClassWithCustomKey() {
        String className = "MainFragment";
        String customKey = "userPoints";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.app.Fragment;\n"
                + "import android.support.annotation.Nullable;"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra(key = \"" + customKey + "\")"
                + " public long points = -1;\n"
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
                + "      if (bundle.containsKey(\"" + customKey + "\")) {\n"
                + "        long points = bundle.getLong(\"" + customKey + "\");\n"
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

    @Test public void testSimpleClassWithSetters() {
        String className = "MainFragment";
        String customKey = "userPoints";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.app.Fragment;\n"
                + "import android.support.annotation.Nullable;"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra(key = \"" + customKey + "\")"
                + " long points = -1;\n"
                + " public void setPoints(long points) {"
                + "   this.points = points;"
                + " }"
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
                + "      if (bundle.containsKey(\"" + customKey + "\")) {\n"
                + "        long points = bundle.getLong(\"" + customKey + "\");\n"
                + "        binder.setPoints(points);\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}\n");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.navigator.Navigator")
                .hasSourceEquivalentTo(expected);
    }

    @Test public void testSimpleClassWithAllExtras() {
        String className = "MainFragment";
        String customKey = "userPoints";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.navigator.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import com.shaishavgandhi.navigator.Optional;"
                + "import android.app.Fragment;\n"
                + "import java.lang.String;\n"
                + "import java.util.ArrayList;\n"
                + "import java.lang.Long;\n"
                + "import java.lang.Double;\n"
                + "import java.lang.Float;\n"
                + "import java.lang.Integer;\n"
                + "import java.lang.CharSequence;\n"
                + "import java.lang.Boolean;\n"
                + "import android.support.annotation.Nullable;"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra @Optional public long points = -1;\n"
                + " @Extra @Nullable public String name;\n"
                + " @Extra @Optional public String[] names;\n"
                + " @Extra @Nullable public ArrayList<String> nameList;\n"
                + " @Extra @Optional public int smallInt;\n"
                + " @Extra @Nullable public int[] smallIntArray;\n"
                + " @Extra @Nullable public ArrayList<Integer> intList;\n"
                + " @Extra @Nullable public Long bigLong;\n"
                + " @Extra @Optional public long[] longArray;\n"
                + " @Extra @Optional public double smallDouble;\n"
                + " @Extra @Optional public Double bigDouble;\n"
                + " @Extra @Optional public double[] doubleArray;\n"
                + " @Extra @Optional public float smallFloat;\n"
                + " @Extra @Nullable public Float bigFloat;\n"
                + " @Extra @Optional public byte smallByte;\n"
                + " @Extra @Optional public byte[] byteArray;\n"
                + " @Extra @Optional public short smallShort;\n"
                + " @Extra @Optional public short[] shortArray;\n"
                + " @Extra @Optional public char smallChar;\n"
                + " @Extra @Optional public char[] charArray;\n"
                + " @Extra @Optional public CharSequence charSequence;\n"
                + " @Extra @Optional public CharSequence[] charSeqArray;\n"
                + " @Extra @Optional public ArrayList<CharSequence> charSeqList;\n"
                + " @Extra @Optional public boolean isSmallBoolean;\n"
                + " @Extra @Optional public boolean[] smallBoolArray;\n"
                + " @Extra @Optional public Boolean isBigBool;\n"
                + " // TODO: Fix big boolean array @Extra @Optional public Boolean[] bigBoolArray;\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("Navigator", ""
                + "package com.shaishavgandhi.navigator;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "import com.shaishavgandhi.navigator.test.MainFragment;\n"
                + "import java.lang.Boolean;\n"
                + "import java.lang.CharSequence;\n"
                + "import java.lang.Double;\n"
                + "import java.lang.Float;\n"
                + "import java.lang.Integer;\n"
                + "import java.lang.Long;\n"
                + "import java.lang.String;\n"
                + "import java.util.ArrayList;\n"
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
                + "        long points = bundle.getLong(\"points\");\n"
                + "        binder.points = points;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"name\")) {\n"
                + "        String name = bundle.getString(\"name\");\n"
                + "        binder.name = name;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"names\")) {\n"
                + "        String[] names = bundle.getStringArray(\"names\");\n"
                + "        binder.names = names;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"nameList\")) {\n"
                + "        ArrayList<String> nameList = bundle.getStringArrayList(\"nameList\");\n"
                + "        binder.nameList = nameList;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"smallInt\")) {\n"
                + "        int smallInt = bundle.getInt(\"smallInt\");\n"
                + "        binder.smallInt = smallInt;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"smallIntArray\")) {\n"
                + "        int[] smallIntArray = bundle.getIntArray(\"smallIntArray\");\n"
                + "        binder.smallIntArray = smallIntArray;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"intList\")) {\n"
                + "        ArrayList<Integer> intList = bundle.getIntegerArrayList(\"intList\");\n"
                + "        binder.intList = intList;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"bigLong\")) {\n"
                + "        Long bigLong = bundle.getLong(\"bigLong\");\n"
                + "        binder.bigLong = bigLong;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"longArray\")) {\n"
                + "        long[] longArray = bundle.getLongArray(\"longArray\");\n"
                + "        binder.longArray = longArray;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"smallDouble\")) {\n"
                + "        double smallDouble = bundle.getDouble(\"smallDouble\");\n"
                + "        binder.smallDouble = smallDouble;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"bigDouble\")) {\n"
                + "        Double bigDouble = bundle.getDouble(\"bigDouble\");\n"
                + "        binder.bigDouble = bigDouble;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"doubleArray\")) {\n"
                + "        double[] doubleArray = bundle.getDoubleArray(\"doubleArray\");\n"
                + "        binder.doubleArray = doubleArray;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"smallFloat\")) {\n"
                + "        float smallFloat = bundle.getFloat(\"smallFloat\");\n"
                + "        binder.smallFloat = smallFloat;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"bigFloat\")) {\n"
                + "        Float bigFloat = bundle.getFloat(\"bigFloat\");\n"
                + "        binder.bigFloat = bigFloat;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"smallByte\")) {\n"
                + "        byte smallByte = bundle.getByte(\"smallByte\");\n"
                + "        binder.smallByte = smallByte;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"byteArray\")) {\n"
                + "        byte[] byteArray = bundle.getByteArray(\"byteArray\");\n"
                + "        binder.byteArray = byteArray;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"smallShort\")) {\n"
                + "        short smallShort = bundle.getShort(\"smallShort\");\n"
                + "        binder.smallShort = smallShort;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"shortArray\")) {\n"
                + "        short[] shortArray = bundle.getShortArray(\"shortArray\");\n"
                + "        binder.shortArray = shortArray;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"smallChar\")) {\n"
                + "        char smallChar = bundle.getChar(\"smallChar\");\n"
                + "        binder.smallChar = smallChar;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"charArray\")) {\n"
                + "        char[] charArray = bundle.getCharArray(\"charArray\");\n"
                + "        binder.charArray = charArray;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"charSequence\")) {\n"
                + "        CharSequence charSequence = bundle.getCharSequence(\"charSequence\");\n"
                + "        binder.charSequence = charSequence;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"charSeqArray\")) {\n"
                + "        CharSequence[] charSeqArray = bundle.getCharSequenceArray(\"charSeqArray\");\n"
                + "        binder.charSeqArray = charSeqArray;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"charSeqList\")) {\n"
                + "        ArrayList<CharSequence> charSeqList = bundle.getCharSequenceArrayList(\"charSeqList\");\n"
                + "        binder.charSeqList = charSeqList;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"isSmallBoolean\")) {\n"
                + "        boolean isSmallBoolean = bundle.getBoolean(\"isSmallBoolean\");\n"
                + "        binder.isSmallBoolean = isSmallBoolean;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"smallBoolArray\")) {\n"
                + "        boolean[] smallBoolArray = bundle.getBooleanArray(\"smallBoolArray\");\n"
                + "        binder.smallBoolArray = smallBoolArray;\n"
                + "      }\n"
                + "      if (bundle.containsKey(\"isBigBool\")) {\n"
                + "        Boolean isBigBool = bundle.getBoolean(\"isBigBool\");\n"
                + "        binder.isBigBool = isBigBool;\n"
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
