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

    @Test public void testSimpleActivityBinder() {
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
                + "      if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n"
                + "        String name = bundle.getString(MainActivityBuilder.EXTRA_NAME);\n"
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

    @Test public void testSimpleActivityBinderWithProtected() {
        String className = "MainActivity";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.app.Activity;\n"
                + "\n"
                + "public class MainActivity extends Activity {\n"
                + " @Extra protected String name;\n"
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
                + "      if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n"
                + "        String name = bundle.getString(MainActivityBuilder.EXTRA_NAME);\n"
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

    @Test public void testSimpleActivityBinderWithStatic() {
        String className = "MainActivity";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.app.Activity;\n"
                + "\n"
                + "public class MainActivity extends Activity {\n"
                + " @Extra static String name;\n"
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
                + "      if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n"
                + "        String name = bundle.getString(MainActivityBuilder.EXTRA_NAME);\n"
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

    @Test public void testSimpleActivityNavigatorWithSerializable() {
        String className = "MainActivity";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import java.io.Serializable;"
                + "import android.app.Activity;\n"
                + "\n"
                + "public class MainActivity extends Activity {\n"
                + " @Extra public User name;\n"
                + " public class User implements Serializable {}\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("com.shaishavgandhi.sampleapp.test.MainActivityBinder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "\n"
                + "public final class MainActivityBinder {\n"
                + "  public static final void bind(MainActivity binder) {\n"
                + "    Bundle bundle = binder.getIntent().getExtras();\n"
                + "    if (bundle != null) {\n"
                + "      if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n"
                + "        MainActivity.User name = (MainActivity.User) bundle.getSerializable(MainActivityBuilder.EXTRA_NAME);\n"
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

    @Test public void testSimpleActivityNavigatorWithParcelable() {
        String className = "MainActivity";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
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

        JavaFileObject expected = JavaFileObjects.forSourceString("MainActivityBinder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "\n"
                + "public final class MainActivityBinder {\n"
                + "  public static final void bind(MainActivity binder) {\n"
                + "    Bundle bundle = binder.getIntent().getExtras();\n"
                + "    if (bundle != null) {\n"
                + "      if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n"
                + "        MainActivity.User name = bundle.getParcelable(MainActivityBuilder.EXTRA_NAME);\n"
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

    @Test public void testSimpleActivityNavigatorWithParcelableArrayList() {
        String className = "MainActivity";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
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

        JavaFileObject expected = JavaFileObjects.forSourceString("MainActivityBinder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "import java.util.ArrayList;\n"
                + "\n"
                + "public final class MainActivityBinder {\n"
                + "  public static final void bind(MainActivity binder) {\n"
                + "    Bundle bundle = binder.getIntent().getExtras();\n"
                + "    if (bundle != null) {\n"
                + "      if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n"
                + "        ArrayList<MainActivity.User> name = bundle.getParcelableArrayList(MainActivityBuilder.EXTRA_NAME);\n"
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

    @Test public void testSimpleActivityNavigatorWithParcelableArray() {
        String className = "MainActivity";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
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

        JavaFileObject expected = JavaFileObjects.forSourceString("MainActivityBinder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "\n"
                + "public final class MainActivityBinder {\n"
                + "  public static final void bind(MainActivity binder) {\n"
                + "    Bundle bundle = binder.getIntent().getExtras();\n"
                + "    if (bundle != null) {\n"
                + "      if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n"
                + "        MainActivity.User[] name = (MainActivity.User[])bundle.getParcelableArray(MainActivityBuilder.EXTRA_NAME);\n"
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

    @Test public void testSimpleActivityNavigatorWithSparseParcelableArray() {
        String className = "MainActivity";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
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

        JavaFileObject expected = JavaFileObjects.forSourceString("MainActivityBinder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "import android.util.SparseArray;\n"
                + "\n"
                + "public final class MainActivityBinder {\n"
                + "  public static final void bind(MainActivity binder) {\n"
                + "    Bundle bundle = binder.getIntent().getExtras();\n"
                + "    if (bundle != null) {\n"
                + "      if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n"
                + "        SparseArray<MainActivity.User> name = bundle.getSparseParcelableArray(MainActivityBuilder.EXTRA_NAME);\n"
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

    @Test public void testSimpleFragmentNavigatorWithNullables() {
        String className = "MainFragment";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.app.Fragment;\n"
                + "import android.support.annotation.Nullable;"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra @Nullable public Long points;\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("MainFragmentBinder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "import java.lang.Long;"
                + "\n"
                + "public final class MainFragmentBinder {\n"
                + "  public static final void bind(MainFragment binder) {\n"
                + "    Bundle bundle = binder.getArguments();\n"
                + "    if (bundle != null) {\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_POINTS)) {\n"
                + "        Long points = bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n"
                + "        binder.points = points;\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}\n");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.sampleapp.test.MainFragmentBinder")
                .hasSourceEquivalentTo(expected);
    }

    @Test public void testSimpleAndroidXFragmentNavigatorWithNullables() {
        String className = "MainFragment";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import androidx.fragment.app.Fragment;\n"
                + "import android.support.annotation.Nullable;"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra @Nullable public Long points;\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("MainFragmentBinder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "import java.lang.Long;"
                + "\n"
                + "public final class MainFragmentBinder {\n"
                + "  public static final void bind(MainFragment binder) {\n"
                + "    Bundle bundle = binder.getArguments();\n"
                + "    if (bundle != null) {\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_POINTS)) {\n"
                + "        Long points = bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n"
                + "        binder.points = points;\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}\n");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.sampleapp.test.MainFragmentBinder")
                .hasSourceEquivalentTo(expected);
    }

    @Test public void testSimpleClassNavigatorWithOptional() {
        String className = "MainFragment";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import com.shaishavgandhi.navigator.Optional;\n"
                + "import android.app.Fragment;\n"
                + "import android.support.annotation.Nullable;"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra @Optional long points;\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("MainFragmentBinder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "\n"
                + "public final class MainFragmentBinder {\n"
                + "  public static final void bind(MainFragment binder) {\n"
                + "    Bundle bundle = binder.getArguments();\n"
                + "    if (bundle != null) {\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_POINTS)) {\n"
                + "        long points = bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n"
                + "        binder.points = points;\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}\n");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.sampleapp.test.MainFragmentBinder")
                .hasSourceEquivalentTo(expected);
    }

    @Test public void testSimpleFragmentWithNonNullPrimitive() {
        String className = "MainFragment";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.app.Fragment;\n"
                + "import android.support.annotation.Nullable;"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra long points = -1;\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("MainFragmentBinder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "\n"
                + "public final class MainFragmentBinder {\n"
                + "  public static final void bind(MainFragment binder) {\n"
                + "    Bundle bundle = binder.getArguments();\n"
                + "    if (bundle != null) {\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_POINTS)) {\n"
                + "        long points = bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n"
                + "        binder.points = points;\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}\n");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.sampleapp.test.MainFragmentBinder")
                .hasSourceEquivalentTo(expected);
    }

    @Test public void testSimpleClassWithCustomKey() {
        String className = "MainFragment";
        String customKey = "com.navigator.weird_key";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.app.Fragment;\n"
                + "import android.support.annotation.Nullable;"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra(key = \"" + customKey + "\")"
                + " long points = -1;\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("MainFragmentBinder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "\n"
                + "public final class MainFragmentBinder {\n"
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
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.sampleapp.test.MainFragmentBinder")
                .hasSourceEquivalentTo(expected);
    }

    @Test public void testSimpleClassWithSetters() {
        String className = "MainFragment";
        String customKey = "com.navigator.something-weird";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.app.Fragment;\n"
                + "import android.support.annotation.Nullable;"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra(key = \"" + customKey + "\")"
                + " private long points = -1;\n"
                + " public void setPoints(long points) {"
                + "   this.points = points;"
                + " }"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("MainFragmentBinder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "\n"
                + "public final class MainFragmentBinder {\n"
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
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.sampleapp.test.MainFragmentBinder")
                .hasSourceEquivalentTo(expected);
    }

    @Test public void testSimpleClassWithAllExtras() {
        String className = "MainFragment";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
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

        JavaFileObject expected = JavaFileObjects.forSourceString("MainFragmentBinder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import android.os.Bundle;\n"
                + "import java.lang.Boolean;\n"
                + "import java.lang.CharSequence;\n"
                + "import java.lang.Double;\n"
                + "import java.lang.Float;\n"
                + "import java.lang.Integer;\n"
                + "import java.lang.Long;\n"
                + "import java.lang.String;\n"
                + "import java.util.ArrayList;\n"
                + "\n"
                + "public final class MainFragmentBinder {\n"
                + "  public static final void bind(MainFragment binder) {\n"
                + "    Bundle bundle = binder.getArguments();\n"
                + "    if (bundle != null) {\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_POINTS)) {\n"
                + "        long points = bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n"
                + "        binder.points = points;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_NAME)) {\n"
                + "        String name = bundle.getString(MainFragmentBuilder.EXTRA_NAME);\n"
                + "        binder.name = name;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_NAMES)) {\n"
                + "        String[] names = bundle.getStringArray(MainFragmentBuilder.EXTRA_NAMES);\n"
                + "        binder.names = names;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_NAME_LIST)) {\n"
                + "        ArrayList<String> nameList = bundle.getStringArrayList(MainFragmentBuilder.EXTRA_NAME_LIST);\n"
                + "        binder.nameList = nameList;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_INT)) {\n"
                + "        int smallInt = bundle.getInt(MainFragmentBuilder.EXTRA_SMALL_INT);\n"
                + "        binder.smallInt = smallInt;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_INT_ARRAY)) {\n"
                + "        int[] smallIntArray = bundle.getIntArray(MainFragmentBuilder.EXTRA_SMALL_INT_ARRAY);\n"
                + "        binder.smallIntArray = smallIntArray;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_INT_LIST)) {\n"
                + "        ArrayList<Integer> intList = bundle.getIntegerArrayList(MainFragmentBuilder.EXTRA_INT_LIST);\n"
                + "        binder.intList = intList;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_BIG_LONG)) {\n"
                + "        Long bigLong = bundle.getLong(MainFragmentBuilder.EXTRA_BIG_LONG);\n"
                + "        binder.bigLong = bigLong;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_LONG_ARRAY)) {\n"
                + "        long[] longArray = bundle.getLongArray(MainFragmentBuilder.EXTRA_LONG_ARRAY);\n"
                + "        binder.longArray = longArray;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_DOUBLE)) {\n"
                + "        double smallDouble = bundle.getDouble(MainFragmentBuilder.EXTRA_SMALL_DOUBLE);\n"
                + "        binder.smallDouble = smallDouble;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_BIG_DOUBLE)) {\n"
                + "        Double bigDouble = bundle.getDouble(MainFragmentBuilder.EXTRA_BIG_DOUBLE);\n"
                + "        binder.bigDouble = bigDouble;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_DOUBLE_ARRAY)) {\n"
                + "        double[] doubleArray = bundle.getDoubleArray(MainFragmentBuilder.EXTRA_DOUBLE_ARRAY);\n"
                + "        binder.doubleArray = doubleArray;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_FLOAT)) {\n"
                + "        float smallFloat = bundle.getFloat(MainFragmentBuilder.EXTRA_SMALL_FLOAT);\n"
                + "        binder.smallFloat = smallFloat;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_BIG_FLOAT)) {\n"
                + "        Float bigFloat = bundle.getFloat(MainFragmentBuilder.EXTRA_BIG_FLOAT);\n"
                + "        binder.bigFloat = bigFloat;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_BYTE)) {\n"
                + "        byte smallByte = bundle.getByte(MainFragmentBuilder.EXTRA_SMALL_BYTE);\n"
                + "        binder.smallByte = smallByte;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_BYTE_ARRAY)) {\n"
                + "        byte[] byteArray = bundle.getByteArray(MainFragmentBuilder.EXTRA_BYTE_ARRAY);\n"
                + "        binder.byteArray = byteArray;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_SHORT)) {\n"
                + "        short smallShort = bundle.getShort(MainFragmentBuilder.EXTRA_SMALL_SHORT);\n"
                + "        binder.smallShort = smallShort;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_SHORT_ARRAY)) {\n"
                + "        short[] shortArray = bundle.getShortArray(MainFragmentBuilder.EXTRA_SHORT_ARRAY);\n"
                + "        binder.shortArray = shortArray;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_CHAR)) {\n"
                + "        char smallChar = bundle.getChar(MainFragmentBuilder.EXTRA_SMALL_CHAR);\n"
                + "        binder.smallChar = smallChar;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_CHAR_ARRAY)) {\n"
                + "        char[] charArray = bundle.getCharArray(MainFragmentBuilder.EXTRA_CHAR_ARRAY);\n"
                + "        binder.charArray = charArray;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_CHAR_SEQUENCE)) {\n"
                + "        CharSequence charSequence = bundle.getCharSequence(MainFragmentBuilder.EXTRA_CHAR_SEQUENCE);\n"
                + "        binder.charSequence = charSequence;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_CHAR_SEQ_ARRAY)) {\n"
                + "        CharSequence[] charSeqArray = bundle.getCharSequenceArray(MainFragmentBuilder.EXTRA_CHAR_SEQ_ARRAY);\n"
                + "        binder.charSeqArray = charSeqArray;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_CHAR_SEQ_LIST)) {\n"
                + "        ArrayList<CharSequence> charSeqList = bundle.getCharSequenceArrayList(MainFragmentBuilder.EXTRA_CHAR_SEQ_LIST);\n"
                + "        binder.charSeqList = charSeqList;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_IS_SMALL_BOOLEAN)) {\n"
                + "        boolean isSmallBoolean = bundle.getBoolean(MainFragmentBuilder.EXTRA_IS_SMALL_BOOLEAN);\n"
                + "        binder.isSmallBoolean = isSmallBoolean;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_BOOL_ARRAY)) {\n"
                + "        boolean[] smallBoolArray = bundle.getBooleanArray(MainFragmentBuilder.EXTRA_SMALL_BOOL_ARRAY);\n"
                + "        binder.smallBoolArray = smallBoolArray;\n"
                + "      }\n"
                + "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_IS_BIG_BOOL)) {\n"
                + "        Boolean isBigBool = bundle.getBoolean(MainFragmentBuilder.EXTRA_IS_BIG_BOOL);\n"
                + "        binder.isBigBool = isBigBool;\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}\n");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.sampleapp.test.MainFragmentBinder")
                .hasSourceEquivalentTo(expected);
    }

}
