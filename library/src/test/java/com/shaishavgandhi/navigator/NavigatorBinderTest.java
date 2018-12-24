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
                + "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import androidx.annotation.NonNull;\n" +
                "import androidx.annotation.Nullable;\n" +
                "import java.lang.String;\n" +
                "\n" +
                "public final class MainActivityBinder {\n" +
                "  @NonNull\n" +
                "  private Bundle bundle;\n" +
                "\n" +
                "  private MainActivityBinder() {\n" +
                "  }\n" +
                "\n" +
                "  private MainActivityBinder(@NonNull Bundle bundle) {\n" +
                "    this.bundle = bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for name\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getName(String)}\n" +
                "   *\n" +
                "   * @return the name\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public String getName() {\n" +
                "    if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n" +
                "      return bundle.getString(MainActivityBuilder.EXTRA_NAME);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for name\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the name\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public String getName(@NonNull final String defaultValue) {\n" +
                "    if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)\n" +
                "        && bundle.get(MainActivityBuilder.EXTRA_NAME) != null) {\n" +
                "      return bundle.getString(MainActivityBuilder.EXTRA_NAME);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Binds the fields in {@link MainActivity} annotated with {@link com.shaishavgandhi.navigator.Extra}\n" +
                "   *\n" +
                "   * This requires that the fields in {@link MainActivity} be at least package-private or if they \n" +
                "   * are private, they have a defined setter in the class.\n" +
                "   * You should call this method in an early part of the activity lifecycle like onCreate on onStart.\n" +
                "   *\n" +
                "   * @param binder the activity/fragment whose variables are being bound\n" +
                "   */\n" +
                "  public static final void bind(MainActivity binder) {\n" +
                "    Bundle bundle = binder.getIntent().getExtras();\n" +
                "    if (bundle != null) {\n" +
                "      if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n" +
                "        String name = bundle.getString(MainActivityBuilder.EXTRA_NAME);\n" +
                "        binder.name = name;\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Static factory method to instantiate {@link MainActivityBinder}\n" +
                "   *\n" +
                "   * @param bundle non null bundle that will be used to unwrap the data.\n" +
                "   * @return the binder that will expose getters.\n" +
                "   */\n" +
                "  public static MainActivityBinder from(@NonNull final Bundle bundle) {\n" +
                "    return new MainActivityBinder(bundle);\n" +
                "  }\n" +
                "}");

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
                + "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import androidx.annotation.NonNull;\n" +
                "import androidx.annotation.Nullable;\n" +
                "import java.lang.String;\n" +
                "\n" +
                "public final class MainActivityBinder {\n" +
                "  @NonNull\n" +
                "  private Bundle bundle;\n" +
                "\n" +
                "  private MainActivityBinder() {\n" +
                "  }\n" +
                "\n" +
                "  private MainActivityBinder(@NonNull Bundle bundle) {\n" +
                "    this.bundle = bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for name\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getName(String)}\n" +
                "   *\n" +
                "   * @return the name\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public String getName() {\n" +
                "    if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n" +
                "      return bundle.getString(MainActivityBuilder.EXTRA_NAME);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for name\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the name\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public String getName(@NonNull final String defaultValue) {\n" +
                "    if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)\n" +
                "        && bundle.get(MainActivityBuilder.EXTRA_NAME) != null) {\n" +
                "      return bundle.getString(MainActivityBuilder.EXTRA_NAME);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Binds the fields in {@link MainActivity} annotated with {@link com.shaishavgandhi.navigator.Extra}\n" +
                "   *\n" +
                "   * This requires that the fields in {@link MainActivity} be at least package-private or if they \n" +
                "   * are private, they have a defined setter in the class.\n" +
                "   * You should call this method in an early part of the activity lifecycle like onCreate on onStart.\n" +
                "   *\n" +
                "   * @param binder the activity/fragment whose variables are being bound\n" +
                "   */\n" +
                "  public static final void bind(MainActivity binder) {\n" +
                "    Bundle bundle = binder.getIntent().getExtras();\n" +
                "    if (bundle != null) {\n" +
                "      if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n" +
                "        String name = bundle.getString(MainActivityBuilder.EXTRA_NAME);\n" +
                "        binder.name = name;\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Static factory method to instantiate {@link MainActivityBinder}\n" +
                "   *\n" +
                "   * @param bundle non null bundle that will be used to unwrap the data.\n" +
                "   * @return the binder that will expose getters.\n" +
                "   */\n" +
                "  public static MainActivityBinder from(@NonNull final Bundle bundle) {\n" +
                "    return new MainActivityBinder(bundle);\n" +
                "  }\n" +
                "}");

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
                + "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import androidx.annotation.NonNull;\n" +
                "import androidx.annotation.Nullable;\n" +
                "import java.lang.String;\n" +
                "\n" +
                "public final class MainActivityBinder {\n" +
                "  @NonNull\n" +
                "  private Bundle bundle;\n" +
                "\n" +
                "  private MainActivityBinder() {\n" +
                "  }\n" +
                "\n" +
                "  private MainActivityBinder(@NonNull Bundle bundle) {\n" +
                "    this.bundle = bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for name\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getName(String)}\n" +
                "   *\n" +
                "   * @return the name\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public String getName() {\n" +
                "    if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n" +
                "      return bundle.getString(MainActivityBuilder.EXTRA_NAME);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for name\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the name\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public String getName(@NonNull final String defaultValue) {\n" +
                "    if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)\n" +
                "        && bundle.get(MainActivityBuilder.EXTRA_NAME) != null) {\n" +
                "      return bundle.getString(MainActivityBuilder.EXTRA_NAME);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Binds the fields in {@link MainActivity} annotated with {@link com.shaishavgandhi.navigator.Extra}\n" +
                "   *\n" +
                "   * This requires that the fields in {@link MainActivity} be at least package-private or if they \n" +
                "   * are private, they have a defined setter in the class.\n" +
                "   * You should call this method in an early part of the activity lifecycle like onCreate on onStart.\n" +
                "   *\n" +
                "   * @param binder the activity/fragment whose variables are being bound\n" +
                "   */\n" +
                "  public static final void bind(MainActivity binder) {\n" +
                "    Bundle bundle = binder.getIntent().getExtras();\n" +
                "    if (bundle != null) {\n" +
                "      if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n" +
                "        String name = bundle.getString(MainActivityBuilder.EXTRA_NAME);\n" +
                "        binder.name = name;\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Static factory method to instantiate {@link MainActivityBinder}\n" +
                "   *\n" +
                "   * @param bundle non null bundle that will be used to unwrap the data.\n" +
                "   * @return the binder that will expose getters.\n" +
                "   */\n" +
                "  public static MainActivityBinder from(@NonNull final Bundle bundle) {\n" +
                "    return new MainActivityBinder(bundle);\n" +
                "  }\n" +
                "}");

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
                + "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import androidx.annotation.NonNull;\n" +
                "import androidx.annotation.Nullable;\n" +
                "\n" +
                "public final class MainActivityBinder {\n" +
                "  @NonNull\n" +
                "  private Bundle bundle;\n" +
                "\n" +
                "  private MainActivityBinder() {\n" +
                "  }\n" +
                "\n" +
                "  private MainActivityBinder(@NonNull Bundle bundle) {\n" +
                "    this.bundle = bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for name\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getName(MainActivity.User)}\n" +
                "   *\n" +
                "   * @return the name\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public MainActivity.User getName() {\n" +
                "    if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n" +
                "      return (MainActivity.User) bundle.getSerializable(MainActivityBuilder.EXTRA_NAME);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for name\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the name\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public MainActivity.User getName(@NonNull final MainActivity.User defaultValue) {\n" +
                "    if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)\n" +
                "        && bundle.get(MainActivityBuilder.EXTRA_NAME) != null) {\n" +
                "      return (MainActivity.User) bundle.getSerializable(MainActivityBuilder.EXTRA_NAME);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Binds the fields in {@link MainActivity} annotated with {@link com.shaishavgandhi.navigator.Extra}\n" +
                "   *\n" +
                "   * This requires that the fields in {@link MainActivity} be at least package-private or if they \n" +
                "   * are private, they have a defined setter in the class.\n" +
                "   * You should call this method in an early part of the activity lifecycle like onCreate on onStart.\n" +
                "   *\n" +
                "   * @param binder the activity/fragment whose variables are being bound\n" +
                "   */\n" +
                "  public static final void bind(MainActivity binder) {\n" +
                "    Bundle bundle = binder.getIntent().getExtras();\n" +
                "    if (bundle != null) {\n" +
                "      if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n" +
                "        MainActivity.User name = (MainActivity.User) bundle.getSerializable(MainActivityBuilder.EXTRA_NAME);\n" +
                "        binder.name = name;\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Static factory method to instantiate {@link MainActivityBinder}\n" +
                "   *\n" +
                "   * @param bundle non null bundle that will be used to unwrap the data.\n" +
                "   * @return the binder that will expose getters.\n" +
                "   */\n" +
                "  public static MainActivityBinder from(@NonNull final Bundle bundle) {\n" +
                "    return new MainActivityBinder(bundle);\n" +
                "  }\n" +
                "}");

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
                + "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import androidx.annotation.NonNull;\n" +
                "import androidx.annotation.Nullable;\n" +
                "\n" +
                "public final class MainActivityBinder {\n" +
                "  @NonNull\n" +
                "  private Bundle bundle;\n" +
                "\n" +
                "  private MainActivityBinder() {\n" +
                "  }\n" +
                "\n" +
                "  private MainActivityBinder(@NonNull Bundle bundle) {\n" +
                "    this.bundle = bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for name\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getName(MainActivity.User)}\n" +
                "   *\n" +
                "   * @return the name\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public MainActivity.User getName() {\n" +
                "    if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n" +
                "      return bundle.getParcelable(MainActivityBuilder.EXTRA_NAME);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for name\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the name\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public MainActivity.User getName(@NonNull final MainActivity.User defaultValue) {\n" +
                "    if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)\n" +
                "        && bundle.get(MainActivityBuilder.EXTRA_NAME) != null) {\n" +
                "      return bundle.getParcelable(MainActivityBuilder.EXTRA_NAME);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Binds the fields in {@link MainActivity} annotated with {@link com.shaishavgandhi.navigator.Extra}\n" +
                "   *\n" +
                "   * This requires that the fields in {@link MainActivity} be at least package-private or if they \n" +
                "   * are private, they have a defined setter in the class.\n" +
                "   * You should call this method in an early part of the activity lifecycle like onCreate on onStart.\n" +
                "   *\n" +
                "   * @param binder the activity/fragment whose variables are being bound\n" +
                "   */\n" +
                "  public static final void bind(MainActivity binder) {\n" +
                "    Bundle bundle = binder.getIntent().getExtras();\n" +
                "    if (bundle != null) {\n" +
                "      if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n" +
                "        MainActivity.User name = bundle.getParcelable(MainActivityBuilder.EXTRA_NAME);\n" +
                "        binder.name = name;\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Static factory method to instantiate {@link MainActivityBinder}\n" +
                "   *\n" +
                "   * @param bundle non null bundle that will be used to unwrap the data.\n" +
                "   * @return the binder that will expose getters.\n" +
                "   */\n" +
                "  public static MainActivityBinder from(@NonNull final Bundle bundle) {\n" +
                "    return new MainActivityBinder(bundle);\n" +
                "  }\n" +
                "}");

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
                + "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import androidx.annotation.NonNull;\n" +
                "import androidx.annotation.Nullable;\n" +
                "import java.util.ArrayList;\n" +
                "\n" +
                "public final class MainActivityBinder {\n" +
                "  @NonNull\n" +
                "  private Bundle bundle;\n" +
                "\n" +
                "  private MainActivityBinder() {\n" +
                "  }\n" +
                "\n" +
                "  private MainActivityBinder(@NonNull Bundle bundle) {\n" +
                "    this.bundle = bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for name\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getName(ArrayList<MainActivity.User>)}\n" +
                "   *\n" +
                "   * @return the name\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public ArrayList<MainActivity.User> getName() {\n" +
                "    if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n" +
                "      return bundle.getParcelableArrayList(MainActivityBuilder.EXTRA_NAME);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for name\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the name\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public ArrayList<MainActivity.User> getName(\n" +
                "      @NonNull final ArrayList<MainActivity.User> defaultValue) {\n" +
                "    if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)\n" +
                "        && bundle.get(MainActivityBuilder.EXTRA_NAME) != null) {\n" +
                "      return bundle.getParcelableArrayList(MainActivityBuilder.EXTRA_NAME);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Binds the fields in {@link MainActivity} annotated with {@link com.shaishavgandhi.navigator.Extra}\n" +
                "   *\n" +
                "   * This requires that the fields in {@link MainActivity} be at least package-private or if they \n" +
                "   * are private, they have a defined setter in the class.\n" +
                "   * You should call this method in an early part of the activity lifecycle like onCreate on onStart.\n" +
                "   *\n" +
                "   * @param binder the activity/fragment whose variables are being bound\n" +
                "   */\n" +
                "  public static final void bind(MainActivity binder) {\n" +
                "    Bundle bundle = binder.getIntent().getExtras();\n" +
                "    if (bundle != null) {\n" +
                "      if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n" +
                "        ArrayList<MainActivity.User> name = bundle.getParcelableArrayList(MainActivityBuilder.EXTRA_NAME);\n" +
                "        binder.name = name;\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Static factory method to instantiate {@link MainActivityBinder}\n" +
                "   *\n" +
                "   * @param bundle non null bundle that will be used to unwrap the data.\n" +
                "   * @return the binder that will expose getters.\n" +
                "   */\n" +
                "  public static MainActivityBinder from(@NonNull final Bundle bundle) {\n" +
                "    return new MainActivityBinder(bundle);\n" +
                "  }\n" +
                "}");

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
                + "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import androidx.annotation.NonNull;\n" +
                "import androidx.annotation.Nullable;\n" +
                "\n" +
                "public final class MainActivityBinder {\n" +
                "  @NonNull\n" +
                "  private Bundle bundle;\n" +
                "\n" +
                "  private MainActivityBinder() {\n" +
                "  }\n" +
                "\n" +
                "  private MainActivityBinder(@NonNull Bundle bundle) {\n" +
                "    this.bundle = bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for name\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getName(MainActivity.User[])}\n" +
                "   *\n" +
                "   * @return the name\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public MainActivity.User[] getName() {\n" +
                "    if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n" +
                "      return (MainActivity.User[]) bundle.getParcelableArray(MainActivityBuilder.EXTRA_NAME);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for name\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the name\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public MainActivity.User[] getName(@NonNull final MainActivity.User[] defaultValue) {\n" +
                "    if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)\n" +
                "        && bundle.get(MainActivityBuilder.EXTRA_NAME) != null) {\n" +
                "      return (MainActivity.User[]) bundle.getParcelableArray(MainActivityBuilder.EXTRA_NAME);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Binds the fields in {@link MainActivity} annotated with {@link com.shaishavgandhi.navigator.Extra}\n" +
                "   *\n" +
                "   * This requires that the fields in {@link MainActivity} be at least package-private or if they \n" +
                "   * are private, they have a defined setter in the class.\n" +
                "   * You should call this method in an early part of the activity lifecycle like onCreate on onStart.\n" +
                "   *\n" +
                "   * @param binder the activity/fragment whose variables are being bound\n" +
                "   */\n" +
                "  public static final void bind(MainActivity binder) {\n" +
                "    Bundle bundle = binder.getIntent().getExtras();\n" +
                "    if (bundle != null) {\n" +
                "      if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n" +
                "        MainActivity.User[] name = (MainActivity.User[]) bundle.getParcelableArray(MainActivityBuilder.EXTRA_NAME);\n" +
                "        binder.name = name;\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Static factory method to instantiate {@link MainActivityBinder}\n" +
                "   *\n" +
                "   * @param bundle non null bundle that will be used to unwrap the data.\n" +
                "   * @return the binder that will expose getters.\n" +
                "   */\n" +
                "  public static MainActivityBinder from(@NonNull final Bundle bundle) {\n" +
                "    return new MainActivityBinder(bundle);\n" +
                "  }\n" +
                "}");

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
                + "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import android.util.SparseArray;\n" +
                "import androidx.annotation.NonNull;\n" +
                "import androidx.annotation.Nullable;\n" +
                "\n" +
                "public final class MainActivityBinder {\n" +
                "  @NonNull\n" +
                "  private Bundle bundle;\n" +
                "\n" +
                "  private MainActivityBinder() {\n" +
                "  }\n" +
                "\n" +
                "  private MainActivityBinder(@NonNull Bundle bundle) {\n" +
                "    this.bundle = bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for name\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getName(SparseArray<MainActivity.User>)}\n" +
                "   *\n" +
                "   * @return the name\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public SparseArray<MainActivity.User> getName() {\n" +
                "    if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n" +
                "      return bundle.getSparseParcelableArray(MainActivityBuilder.EXTRA_NAME);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for name\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the name\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public SparseArray<MainActivity.User> getName(\n" +
                "      @NonNull final SparseArray<MainActivity.User> defaultValue) {\n" +
                "    if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)\n" +
                "        && bundle.get(MainActivityBuilder.EXTRA_NAME) != null) {\n" +
                "      return bundle.getSparseParcelableArray(MainActivityBuilder.EXTRA_NAME);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Binds the fields in {@link MainActivity} annotated with {@link com.shaishavgandhi.navigator.Extra}\n" +
                "   *\n" +
                "   * This requires that the fields in {@link MainActivity} be at least package-private or if they \n" +
                "   * are private, they have a defined setter in the class.\n" +
                "   * You should call this method in an early part of the activity lifecycle like onCreate on onStart.\n" +
                "   *\n" +
                "   * @param binder the activity/fragment whose variables are being bound\n" +
                "   */\n" +
                "  public static final void bind(MainActivity binder) {\n" +
                "    Bundle bundle = binder.getIntent().getExtras();\n" +
                "    if (bundle != null) {\n" +
                "      if (bundle.containsKey(MainActivityBuilder.EXTRA_NAME)) {\n" +
                "        SparseArray<MainActivity.User> name = bundle.getSparseParcelableArray(MainActivityBuilder.EXTRA_NAME);\n" +
                "        binder.name = name;\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Static factory method to instantiate {@link MainActivityBinder}\n" +
                "   *\n" +
                "   * @param bundle non null bundle that will be used to unwrap the data.\n" +
                "   * @return the binder that will expose getters.\n" +
                "   */\n" +
                "  public static MainActivityBinder from(@NonNull final Bundle bundle) {\n" +
                "    return new MainActivityBinder(bundle);\n" +
                "  }\n" +
                "}");

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
                + "import androidx.annotation.Nullable;"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra @Nullable public Long points;\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("MainFragmentBinder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import androidx.annotation.NonNull;\n" +
                "import androidx.annotation.Nullable;\n" +
                "import java.lang.Long;\n" +
                "\n" +
                "public final class MainFragmentBinder {\n" +
                "  @NonNull\n" +
                "  private Bundle bundle;\n" +
                "\n" +
                "  private MainFragmentBinder() {\n" +
                "  }\n" +
                "\n" +
                "  private MainFragmentBinder(@NonNull Bundle bundle) {\n" +
                "    this.bundle = bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for points\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getPoints(Long)}\n" +
                "   *\n" +
                "   * @return the points\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public Long getPoints() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_POINTS)) {\n" +
                "      return bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for points\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the points\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public Long getPoints(@NonNull final Long defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_POINTS)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_POINTS) != null) {\n" +
                "      return bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Binds the fields in {@link MainFragment} annotated with {@link com.shaishavgandhi.navigator.Extra}\n" +
                "   *\n" +
                "   * This requires that the fields in {@link MainFragment} be at least package-private or if they \n" +
                "   * are private, they have a defined setter in the class.\n" +
                "   * You should call this method in an early part of the activity lifecycle like onCreate on onStart.\n" +
                "   *\n" +
                "   * @param binder the activity/fragment whose variables are being bound\n" +
                "   */\n" +
                "  public static final void bind(MainFragment binder) {\n" +
                "    Bundle bundle = binder.getArguments();\n" +
                "    if (bundle != null) {\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_POINTS)) {\n" +
                "        Long points = bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n" +
                "        binder.points = points;\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Static factory method to instantiate {@link MainFragmentBinder}\n" +
                "   *\n" +
                "   * @param bundle non null bundle that will be used to unwrap the data.\n" +
                "   * @return the binder that will expose getters.\n" +
                "   */\n" +
                "  public static MainFragmentBinder from(@NonNull final Bundle bundle) {\n" +
                "    return new MainFragmentBinder(bundle);\n" +
                "  }\n" +
                "}");

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
                + "import androidx.annotation.Nullable;"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra @Nullable public Long points;\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("MainFragmentBinder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import androidx.annotation.NonNull;\n" +
                "import androidx.annotation.Nullable;\n" +
                "import java.lang.Long;\n" +
                "\n" +
                "public final class MainFragmentBinder {\n" +
                "  @NonNull\n" +
                "  private Bundle bundle;\n" +
                "\n" +
                "  private MainFragmentBinder() {\n" +
                "  }\n" +
                "\n" +
                "  private MainFragmentBinder(@NonNull Bundle bundle) {\n" +
                "    this.bundle = bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for points\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getPoints(Long)}\n" +
                "   *\n" +
                "   * @return the points\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public Long getPoints() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_POINTS)) {\n" +
                "      return bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for points\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the points\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public Long getPoints(@NonNull final Long defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_POINTS)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_POINTS) != null) {\n" +
                "      return bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Binds the fields in {@link MainFragment} annotated with {@link com.shaishavgandhi.navigator.Extra}\n" +
                "   *\n" +
                "   * This requires that the fields in {@link MainFragment} be at least package-private or if they \n" +
                "   * are private, they have a defined setter in the class.\n" +
                "   * You should call this method in an early part of the activity lifecycle like onCreate on onStart.\n" +
                "   *\n" +
                "   * @param binder the activity/fragment whose variables are being bound\n" +
                "   */\n" +
                "  public static final void bind(MainFragment binder) {\n" +
                "    Bundle bundle = binder.getArguments();\n" +
                "    if (bundle != null) {\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_POINTS)) {\n" +
                "        Long points = bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n" +
                "        binder.points = points;\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Static factory method to instantiate {@link MainFragmentBinder}\n" +
                "   *\n" +
                "   * @param bundle non null bundle that will be used to unwrap the data.\n" +
                "   * @return the binder that will expose getters.\n" +
                "   */\n" +
                "  public static MainFragmentBinder from(@NonNull final Bundle bundle) {\n" +
                "    return new MainFragmentBinder(bundle);\n" +
                "  }\n" +
                "}\n");

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
                + "import androidx.annotation.Nullable;"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra @Optional long points;\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("MainFragmentBinder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import androidx.annotation.NonNull;\n" +
                "\n" +
                "public final class MainFragmentBinder {\n" +
                "  @NonNull\n" +
                "  private Bundle bundle;\n" +
                "\n" +
                "  private MainFragmentBinder() {\n" +
                "  }\n" +
                "\n" +
                "  private MainFragmentBinder(@NonNull Bundle bundle) {\n" +
                "    this.bundle = bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for points\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getPoints(long)}\n" +
                "   *\n" +
                "   * @return the points\n" +
                "   */\n" +
                "  public long getPoints() {\n" +
                "    return bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for points\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the points\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public long getPoints(@NonNull final long defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_POINTS)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_POINTS) != null) {\n" +
                "      return bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Binds the fields in {@link MainFragment} annotated with {@link com.shaishavgandhi.navigator.Extra}\n" +
                "   *\n" +
                "   * This requires that the fields in {@link MainFragment} be at least package-private or if they \n" +
                "   * are private, they have a defined setter in the class.\n" +
                "   * You should call this method in an early part of the activity lifecycle like onCreate on onStart.\n" +
                "   *\n" +
                "   * @param binder the activity/fragment whose variables are being bound\n" +
                "   */\n" +
                "  public static final void bind(MainFragment binder) {\n" +
                "    Bundle bundle = binder.getArguments();\n" +
                "    if (bundle != null) {\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_POINTS)) {\n" +
                "        long points = bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n" +
                "        binder.points = points;\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Static factory method to instantiate {@link MainFragmentBinder}\n" +
                "   *\n" +
                "   * @param bundle non null bundle that will be used to unwrap the data.\n" +
                "   * @return the binder that will expose getters.\n" +
                "   */\n" +
                "  public static MainFragmentBinder from(@NonNull final Bundle bundle) {\n" +
                "    return new MainFragmentBinder(bundle);\n" +
                "  }\n" +
                "}");

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
                + "import androidx.annotation.Nullable;"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra long points = -1;\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("MainFragmentBinder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import androidx.annotation.NonNull;\n" +
                "\n" +
                "public final class MainFragmentBinder {\n" +
                "  @NonNull\n" +
                "  private Bundle bundle;\n" +
                "\n" +
                "  private MainFragmentBinder() {\n" +
                "  }\n" +
                "\n" +
                "  private MainFragmentBinder(@NonNull Bundle bundle) {\n" +
                "    this.bundle = bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for points\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getPoints(long)}\n" +
                "   *\n" +
                "   * @return the points\n" +
                "   */\n" +
                "  public long getPoints() {\n" +
                "    return bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for points\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the points\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public long getPoints(@NonNull final long defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_POINTS)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_POINTS) != null) {\n" +
                "      return bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Binds the fields in {@link MainFragment} annotated with {@link com.shaishavgandhi.navigator.Extra}\n" +
                "   *\n" +
                "   * This requires that the fields in {@link MainFragment} be at least package-private or if they \n" +
                "   * are private, they have a defined setter in the class.\n" +
                "   * You should call this method in an early part of the activity lifecycle like onCreate on onStart.\n" +
                "   *\n" +
                "   * @param binder the activity/fragment whose variables are being bound\n" +
                "   */\n" +
                "  public static final void bind(MainFragment binder) {\n" +
                "    Bundle bundle = binder.getArguments();\n" +
                "    if (bundle != null) {\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_POINTS)) {\n" +
                "        long points = bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n" +
                "        binder.points = points;\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Static factory method to instantiate {@link MainFragmentBinder}\n" +
                "   *\n" +
                "   * @param bundle non null bundle that will be used to unwrap the data.\n" +
                "   * @return the binder that will expose getters.\n" +
                "   */\n" +
                "  public static MainFragmentBinder from(@NonNull final Bundle bundle) {\n" +
                "    return new MainFragmentBinder(bundle);\n" +
                "  }\n" +
                "}");

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
                + "import androidx.annotation.Nullable;"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra(key = \"" + customKey + "\")"
                + " long points = -1;\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("MainFragmentBinder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import androidx.annotation.NonNull;\n" +
                "\n" +
                "public final class MainFragmentBinder {\n" +
                "  @NonNull\n" +
                "  private Bundle bundle;\n" +
                "\n" +
                "  private MainFragmentBinder() {\n" +
                "  }\n" +
                "\n" +
                "  private MainFragmentBinder(@NonNull Bundle bundle) {\n" +
                "    this.bundle = bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for points\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getPoints(long)}\n" +
                "   *\n" +
                "   * @return the points\n" +
                "   */\n" +
                "  public long getPoints() {\n" +
                "    return bundle.getLong(\"com.navigator.weird_key\");\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for points\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the points\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public long getPoints(@NonNull final long defaultValue) {\n" +
                "    if (bundle.containsKey(\"com.navigator.weird_key\")\n" +
                "        && bundle.get(\"com.navigator.weird_key\") != null) {\n" +
                "      return bundle.getLong(\"com.navigator.weird_key\");\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Binds the fields in {@link MainFragment} annotated with {@link com.shaishavgandhi.navigator.Extra}\n" +
                "   *\n" +
                "   * This requires that the fields in {@link MainFragment} be at least package-private or if they \n" +
                "   * are private, they have a defined setter in the class.\n" +
                "   * You should call this method in an early part of the activity lifecycle like onCreate on onStart.\n" +
                "   *\n" +
                "   * @param binder the activity/fragment whose variables are being bound\n" +
                "   */\n" +
                "  public static final void bind(MainFragment binder) {\n" +
                "    Bundle bundle = binder.getArguments();\n" +
                "    if (bundle != null) {\n" +
                "      if (bundle.containsKey(\"com.navigator.weird_key\")) {\n" +
                "        long points = bundle.getLong(\"com.navigator.weird_key\");\n" +
                "        binder.points = points;\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Static factory method to instantiate {@link MainFragmentBinder}\n" +
                "   *\n" +
                "   * @param bundle non null bundle that will be used to unwrap the data.\n" +
                "   * @return the binder that will expose getters.\n" +
                "   */\n" +
                "  public static MainFragmentBinder from(@NonNull final Bundle bundle) {\n" +
                "    return new MainFragmentBinder(bundle);\n" +
                "  }\n" +
                "}");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.sampleapp.test.MainFragmentBinder")
                .hasSourceEquivalentTo(expected);
    }

    @Test public void testSimpleClassWithObjectTypes() {
        String className = "MainFragment";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.app.Fragment;\n"
                + "import java.lang.Long;\n"

                + "import androidx.annotation.Nullable;"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra\n"
                + " Long points = -1L;\n"
                + " @Extra\n"
                + " Integer javaInt = -1;\n"
                + " @Extra\n"
                + " Double javaDouble = 1.0;\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("MainFragmentBinder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import androidx.annotation.NonNull;\n" +
                "import androidx.annotation.Nullable;\n" +
                "import java.lang.Double;\n" +
                "import java.lang.Integer;\n" +
                "import java.lang.Long;\n" +
                "\n" +
                "public final class MainFragmentBinder {\n" +
                "  @NonNull\n" +
                "  private Bundle bundle;\n" +
                "\n" +
                "  private MainFragmentBinder() {\n" +
                "  }\n" +
                "\n" +
                "  private MainFragmentBinder(@NonNull Bundle bundle) {\n" +
                "    this.bundle = bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for points\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getPoints(Long)}\n" +
                "   *\n" +
                "   * @return the points\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public Long getPoints() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_POINTS)) {\n" +
                "      return bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for points\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the points\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public Long getPoints(@NonNull final Long defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_POINTS)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_POINTS) != null) {\n" +
                "      return bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for javaInt\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getJavaInt(Integer)}\n" +
                "   *\n" +
                "   * @return the javaInt\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public Integer getJavaInt() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_JAVA_INT)) {\n" +
                "      return bundle.getInt(MainFragmentBuilder.EXTRA_JAVA_INT);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for javaInt\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the javaInt\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public Integer getJavaInt(@NonNull final Integer defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_JAVA_INT)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_JAVA_INT) != null) {\n" +
                "      return bundle.getInt(MainFragmentBuilder.EXTRA_JAVA_INT);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for javaDouble\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getJavaDouble(Double)}\n" +
                "   *\n" +
                "   * @return the javaDouble\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public Double getJavaDouble() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_JAVA_DOUBLE)) {\n" +
                "      return bundle.getDouble(MainFragmentBuilder.EXTRA_JAVA_DOUBLE);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for javaDouble\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the javaDouble\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public Double getJavaDouble(@NonNull final Double defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_JAVA_DOUBLE)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_JAVA_DOUBLE) != null) {\n" +
                "      return bundle.getDouble(MainFragmentBuilder.EXTRA_JAVA_DOUBLE);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Binds the fields in {@link MainFragment} annotated with {@link com.shaishavgandhi.navigator.Extra}\n" +
                "   *\n" +
                "   * This requires that the fields in {@link MainFragment} be at least package-private or if they \n" +
                "   * are private, they have a defined setter in the class.\n" +
                "   * You should call this method in an early part of the activity lifecycle like onCreate on onStart.\n" +
                "   *\n" +
                "   * @param binder the activity/fragment whose variables are being bound\n" +
                "   */\n" +
                "  public static final void bind(MainFragment binder) {\n" +
                "    Bundle bundle = binder.getArguments();\n" +
                "    if (bundle != null) {\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_POINTS)) {\n" +
                "        Long points = bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n" +
                "        binder.points = points;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_JAVA_INT)) {\n" +
                "        Integer javaInt = bundle.getInt(MainFragmentBuilder.EXTRA_JAVA_INT);\n" +
                "        binder.javaInt = javaInt;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_JAVA_DOUBLE)) {\n" +
                "        Double javaDouble = bundle.getDouble(MainFragmentBuilder.EXTRA_JAVA_DOUBLE);\n" +
                "        binder.javaDouble = javaDouble;\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Static factory method to instantiate {@link MainFragmentBinder}\n" +
                "   *\n" +
                "   * @param bundle non null bundle that will be used to unwrap the data.\n" +
                "   * @return the binder that will expose getters.\n" +
                "   */\n" +
                "  public static MainFragmentBinder from(@NonNull final Bundle bundle) {\n" +
                "    return new MainFragmentBinder(bundle);\n" +
                "  }\n" +
                "}");

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
                + "import androidx.annotation.Nullable;"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra(key = \"" + customKey + "\")"
                + " private long points = -1;\n"
                + " public void setPoints(long points) {"
                + "   this.points = points;"
                + " }"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("MainFragmentBinder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import androidx.annotation.NonNull;\n" +
                "\n" +
                "public final class MainFragmentBinder {\n" +
                "  @NonNull\n" +
                "  private Bundle bundle;\n" +
                "\n" +
                "  private MainFragmentBinder() {\n" +
                "  }\n" +
                "\n" +
                "  private MainFragmentBinder(@NonNull Bundle bundle) {\n" +
                "    this.bundle = bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for points\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getPoints(long)}\n" +
                "   *\n" +
                "   * @return the points\n" +
                "   */\n" +
                "  public long getPoints() {\n" +
                "    return bundle.getLong(\"com.navigator.something-weird\");\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for points\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the points\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public long getPoints(@NonNull final long defaultValue) {\n" +
                "    if (bundle.containsKey(\"com.navigator.something-weird\")\n" +
                "        && bundle.get(\"com.navigator.something-weird\") != null) {\n" +
                "      return bundle.getLong(\"com.navigator.something-weird\");\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Binds the fields in {@link MainFragment} annotated with {@link com.shaishavgandhi.navigator.Extra}\n" +
                "   *\n" +
                "   * This requires that the fields in {@link MainFragment} be at least package-private or if they \n" +
                "   * are private, they have a defined setter in the class.\n" +
                "   * You should call this method in an early part of the activity lifecycle like onCreate on onStart.\n" +
                "   *\n" +
                "   * @param binder the activity/fragment whose variables are being bound\n" +
                "   */\n" +
                "  public static final void bind(MainFragment binder) {\n" +
                "    Bundle bundle = binder.getArguments();\n" +
                "    if (bundle != null) {\n" +
                "      if (bundle.containsKey(\"com.navigator.something-weird\")) {\n" +
                "        long points = bundle.getLong(\"com.navigator.something-weird\");\n" +
                "        binder.setPoints(points);\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Static factory method to instantiate {@link MainFragmentBinder}\n" +
                "   *\n" +
                "   * @param bundle non null bundle that will be used to unwrap the data.\n" +
                "   * @return the binder that will expose getters.\n" +
                "   */\n" +
                "  public static MainFragmentBinder from(@NonNull final Bundle bundle) {\n" +
                "    return new MainFragmentBinder(bundle);\n" +
                "  }\n" +
                "}");

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
                + "import androidx.annotation.Nullable;"
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
                + "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import androidx.annotation.NonNull;\n" +
                "import androidx.annotation.Nullable;\n" +
                "import java.lang.Boolean;\n" +
                "import java.lang.CharSequence;\n" +
                "import java.lang.Double;\n" +
                "import java.lang.Float;\n" +
                "import java.lang.Integer;\n" +
                "import java.lang.Long;\n" +
                "import java.lang.String;\n" +
                "import java.util.ArrayList;\n" +
                "\n" +
                "public final class MainFragmentBinder {\n" +
                "  @NonNull\n" +
                "  private Bundle bundle;\n" +
                "\n" +
                "  private MainFragmentBinder() {\n" +
                "  }\n" +
                "\n" +
                "  private MainFragmentBinder(@NonNull Bundle bundle) {\n" +
                "    this.bundle = bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for points\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getPoints(long)}\n" +
                "   *\n" +
                "   * @return the points\n" +
                "   */\n" +
                "  public long getPoints() {\n" +
                "    return bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for points\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the points\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public long getPoints(@NonNull final long defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_POINTS)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_POINTS) != null) {\n" +
                "      return bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for name\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getName(String)}\n" +
                "   *\n" +
                "   * @return the name\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public String getName() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_NAME)) {\n" +
                "      return bundle.getString(MainFragmentBuilder.EXTRA_NAME);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for name\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the name\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public String getName(@NonNull final String defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_NAME)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_NAME) != null) {\n" +
                "      return bundle.getString(MainFragmentBuilder.EXTRA_NAME);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for names\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getNames(String[])}\n" +
                "   *\n" +
                "   * @return the names\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public String[] getNames() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_NAMES)) {\n" +
                "      return bundle.getStringArray(MainFragmentBuilder.EXTRA_NAMES);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for names\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the names\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public String[] getNames(@NonNull final String[] defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_NAMES)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_NAMES) != null) {\n" +
                "      return bundle.getStringArray(MainFragmentBuilder.EXTRA_NAMES);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for nameList\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getNameList(ArrayList<String>)}\n" +
                "   *\n" +
                "   * @return the nameList\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public ArrayList<String> getNameList() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_NAME_LIST)) {\n" +
                "      return bundle.getStringArrayList(MainFragmentBuilder.EXTRA_NAME_LIST);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for nameList\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the nameList\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public ArrayList<String> getNameList(@NonNull final ArrayList<String> defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_NAME_LIST)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_NAME_LIST) != null) {\n" +
                "      return bundle.getStringArrayList(MainFragmentBuilder.EXTRA_NAME_LIST);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for smallInt\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getSmallInt(int)}\n" +
                "   *\n" +
                "   * @return the smallInt\n" +
                "   */\n" +
                "  public int getSmallInt() {\n" +
                "    return bundle.getInt(MainFragmentBuilder.EXTRA_SMALL_INT);\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for smallInt\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the smallInt\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public int getSmallInt(@NonNull final int defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_INT)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_SMALL_INT) != null) {\n" +
                "      return bundle.getInt(MainFragmentBuilder.EXTRA_SMALL_INT);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for smallIntArray\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getSmallIntArray(int[])}\n" +
                "   *\n" +
                "   * @return the smallIntArray\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public int[] getSmallIntArray() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_INT_ARRAY)) {\n" +
                "      return bundle.getIntArray(MainFragmentBuilder.EXTRA_SMALL_INT_ARRAY);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for smallIntArray\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the smallIntArray\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public int[] getSmallIntArray(@NonNull final int[] defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_INT_ARRAY)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_SMALL_INT_ARRAY) != null) {\n" +
                "      return bundle.getIntArray(MainFragmentBuilder.EXTRA_SMALL_INT_ARRAY);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for intList\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getIntList(ArrayList<Integer>)}\n" +
                "   *\n" +
                "   * @return the intList\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public ArrayList<Integer> getIntList() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_INT_LIST)) {\n" +
                "      return bundle.getIntegerArrayList(MainFragmentBuilder.EXTRA_INT_LIST);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for intList\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the intList\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public ArrayList<Integer> getIntList(@NonNull final ArrayList<Integer> defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_INT_LIST)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_INT_LIST) != null) {\n" +
                "      return bundle.getIntegerArrayList(MainFragmentBuilder.EXTRA_INT_LIST);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for bigLong\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getBigLong(Long)}\n" +
                "   *\n" +
                "   * @return the bigLong\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public Long getBigLong() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_BIG_LONG)) {\n" +
                "      return bundle.getLong(MainFragmentBuilder.EXTRA_BIG_LONG);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for bigLong\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the bigLong\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public Long getBigLong(@NonNull final Long defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_BIG_LONG)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_BIG_LONG) != null) {\n" +
                "      return bundle.getLong(MainFragmentBuilder.EXTRA_BIG_LONG);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for longArray\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getLongArray(long[])}\n" +
                "   *\n" +
                "   * @return the longArray\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public long[] getLongArray() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_LONG_ARRAY)) {\n" +
                "      return bundle.getLongArray(MainFragmentBuilder.EXTRA_LONG_ARRAY);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for longArray\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the longArray\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public long[] getLongArray(@NonNull final long[] defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_LONG_ARRAY)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_LONG_ARRAY) != null) {\n" +
                "      return bundle.getLongArray(MainFragmentBuilder.EXTRA_LONG_ARRAY);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for smallDouble\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getSmallDouble(double)}\n" +
                "   *\n" +
                "   * @return the smallDouble\n" +
                "   */\n" +
                "  public double getSmallDouble() {\n" +
                "    return bundle.getDouble(MainFragmentBuilder.EXTRA_SMALL_DOUBLE);\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for smallDouble\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the smallDouble\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public double getSmallDouble(@NonNull final double defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_DOUBLE)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_SMALL_DOUBLE) != null) {\n" +
                "      return bundle.getDouble(MainFragmentBuilder.EXTRA_SMALL_DOUBLE);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for bigDouble\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getBigDouble(Double)}\n" +
                "   *\n" +
                "   * @return the bigDouble\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public Double getBigDouble() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_BIG_DOUBLE)) {\n" +
                "      return bundle.getDouble(MainFragmentBuilder.EXTRA_BIG_DOUBLE);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for bigDouble\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the bigDouble\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public Double getBigDouble(@NonNull final Double defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_BIG_DOUBLE)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_BIG_DOUBLE) != null) {\n" +
                "      return bundle.getDouble(MainFragmentBuilder.EXTRA_BIG_DOUBLE);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for doubleArray\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getDoubleArray(double[])}\n" +
                "   *\n" +
                "   * @return the doubleArray\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public double[] getDoubleArray() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_DOUBLE_ARRAY)) {\n" +
                "      return bundle.getDoubleArray(MainFragmentBuilder.EXTRA_DOUBLE_ARRAY);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for doubleArray\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the doubleArray\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public double[] getDoubleArray(@NonNull final double[] defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_DOUBLE_ARRAY)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_DOUBLE_ARRAY) != null) {\n" +
                "      return bundle.getDoubleArray(MainFragmentBuilder.EXTRA_DOUBLE_ARRAY);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for smallFloat\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getSmallFloat(float)}\n" +
                "   *\n" +
                "   * @return the smallFloat\n" +
                "   */\n" +
                "  public float getSmallFloat() {\n" +
                "    return bundle.getFloat(MainFragmentBuilder.EXTRA_SMALL_FLOAT);\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for smallFloat\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the smallFloat\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public float getSmallFloat(@NonNull final float defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_FLOAT)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_SMALL_FLOAT) != null) {\n" +
                "      return bundle.getFloat(MainFragmentBuilder.EXTRA_SMALL_FLOAT);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for bigFloat\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getBigFloat(Float)}\n" +
                "   *\n" +
                "   * @return the bigFloat\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public Float getBigFloat() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_BIG_FLOAT)) {\n" +
                "      return bundle.getFloat(MainFragmentBuilder.EXTRA_BIG_FLOAT);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for bigFloat\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the bigFloat\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public Float getBigFloat(@NonNull final Float defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_BIG_FLOAT)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_BIG_FLOAT) != null) {\n" +
                "      return bundle.getFloat(MainFragmentBuilder.EXTRA_BIG_FLOAT);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for smallByte\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getSmallByte(byte)}\n" +
                "   *\n" +
                "   * @return the smallByte\n" +
                "   */\n" +
                "  public byte getSmallByte() {\n" +
                "    return bundle.getByte(MainFragmentBuilder.EXTRA_SMALL_BYTE);\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for smallByte\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the smallByte\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public byte getSmallByte(@NonNull final byte defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_BYTE)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_SMALL_BYTE) != null) {\n" +
                "      return bundle.getByte(MainFragmentBuilder.EXTRA_SMALL_BYTE);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for byteArray\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getByteArray(byte[])}\n" +
                "   *\n" +
                "   * @return the byteArray\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public byte[] getByteArray() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_BYTE_ARRAY)) {\n" +
                "      return bundle.getByteArray(MainFragmentBuilder.EXTRA_BYTE_ARRAY);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for byteArray\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the byteArray\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public byte[] getByteArray(@NonNull final byte[] defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_BYTE_ARRAY)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_BYTE_ARRAY) != null) {\n" +
                "      return bundle.getByteArray(MainFragmentBuilder.EXTRA_BYTE_ARRAY);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for smallShort\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getSmallShort(short)}\n" +
                "   *\n" +
                "   * @return the smallShort\n" +
                "   */\n" +
                "  public short getSmallShort() {\n" +
                "    return bundle.getShort(MainFragmentBuilder.EXTRA_SMALL_SHORT);\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for smallShort\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the smallShort\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public short getSmallShort(@NonNull final short defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_SHORT)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_SMALL_SHORT) != null) {\n" +
                "      return bundle.getShort(MainFragmentBuilder.EXTRA_SMALL_SHORT);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for shortArray\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getShortArray(short[])}\n" +
                "   *\n" +
                "   * @return the shortArray\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public short[] getShortArray() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_SHORT_ARRAY)) {\n" +
                "      return bundle.getShortArray(MainFragmentBuilder.EXTRA_SHORT_ARRAY);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for shortArray\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the shortArray\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public short[] getShortArray(@NonNull final short[] defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_SHORT_ARRAY)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_SHORT_ARRAY) != null) {\n" +
                "      return bundle.getShortArray(MainFragmentBuilder.EXTRA_SHORT_ARRAY);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for smallChar\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getSmallChar(char)}\n" +
                "   *\n" +
                "   * @return the smallChar\n" +
                "   */\n" +
                "  public char getSmallChar() {\n" +
                "    return bundle.getChar(MainFragmentBuilder.EXTRA_SMALL_CHAR);\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for smallChar\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the smallChar\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public char getSmallChar(@NonNull final char defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_CHAR)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_SMALL_CHAR) != null) {\n" +
                "      return bundle.getChar(MainFragmentBuilder.EXTRA_SMALL_CHAR);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for charArray\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getCharArray(char[])}\n" +
                "   *\n" +
                "   * @return the charArray\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public char[] getCharArray() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_CHAR_ARRAY)) {\n" +
                "      return bundle.getCharArray(MainFragmentBuilder.EXTRA_CHAR_ARRAY);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for charArray\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the charArray\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public char[] getCharArray(@NonNull final char[] defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_CHAR_ARRAY)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_CHAR_ARRAY) != null) {\n" +
                "      return bundle.getCharArray(MainFragmentBuilder.EXTRA_CHAR_ARRAY);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for charSequence\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getCharSequence(CharSequence)}\n" +
                "   *\n" +
                "   * @return the charSequence\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public CharSequence getCharSequence() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_CHAR_SEQUENCE)) {\n" +
                "      return bundle.getCharSequence(MainFragmentBuilder.EXTRA_CHAR_SEQUENCE);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for charSequence\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the charSequence\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public CharSequence getCharSequence(@NonNull final CharSequence defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_CHAR_SEQUENCE)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_CHAR_SEQUENCE) != null) {\n" +
                "      return bundle.getCharSequence(MainFragmentBuilder.EXTRA_CHAR_SEQUENCE);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for charSeqArray\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getCharSeqArray(CharSequence[])}\n" +
                "   *\n" +
                "   * @return the charSeqArray\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public CharSequence[] getCharSeqArray() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_CHAR_SEQ_ARRAY)) {\n" +
                "      return bundle.getCharSequenceArray(MainFragmentBuilder.EXTRA_CHAR_SEQ_ARRAY);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for charSeqArray\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the charSeqArray\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public CharSequence[] getCharSeqArray(@NonNull final CharSequence[] defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_CHAR_SEQ_ARRAY)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_CHAR_SEQ_ARRAY) != null) {\n" +
                "      return bundle.getCharSequenceArray(MainFragmentBuilder.EXTRA_CHAR_SEQ_ARRAY);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for charSeqList\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getCharSeqList(ArrayList<CharSequence>)}\n" +
                "   *\n" +
                "   * @return the charSeqList\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public ArrayList<CharSequence> getCharSeqList() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_CHAR_SEQ_LIST)) {\n" +
                "      return bundle.getCharSequenceArrayList(MainFragmentBuilder.EXTRA_CHAR_SEQ_LIST);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for charSeqList\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the charSeqList\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public ArrayList<CharSequence> getCharSeqList(\n" +
                "      @NonNull final ArrayList<CharSequence> defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_CHAR_SEQ_LIST)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_CHAR_SEQ_LIST) != null) {\n" +
                "      return bundle.getCharSequenceArrayList(MainFragmentBuilder.EXTRA_CHAR_SEQ_LIST);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for isSmallBoolean\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getIsSmallBoolean(boolean)}\n" +
                "   *\n" +
                "   * @return the isSmallBoolean\n" +
                "   */\n" +
                "  public boolean getIsSmallBoolean() {\n" +
                "    return bundle.getBoolean(MainFragmentBuilder.EXTRA_IS_SMALL_BOOLEAN);\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for isSmallBoolean\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the isSmallBoolean\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public boolean getIsSmallBoolean(@NonNull final boolean defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_IS_SMALL_BOOLEAN)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_IS_SMALL_BOOLEAN) != null) {\n" +
                "      return bundle.getBoolean(MainFragmentBuilder.EXTRA_IS_SMALL_BOOLEAN);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for smallBoolArray\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getSmallBoolArray(boolean[])}\n" +
                "   *\n" +
                "   * @return the smallBoolArray\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public boolean[] getSmallBoolArray() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_BOOL_ARRAY)) {\n" +
                "      return bundle.getBooleanArray(MainFragmentBuilder.EXTRA_SMALL_BOOL_ARRAY);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for smallBoolArray\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the smallBoolArray\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public boolean[] getSmallBoolArray(@NonNull final boolean[] defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_BOOL_ARRAY)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_SMALL_BOOL_ARRAY) != null) {\n" +
                "      return bundle.getBooleanArray(MainFragmentBuilder.EXTRA_SMALL_BOOL_ARRAY);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Nullable getter for isBigBool\n" +
                "   *\n" +
                "   * If you want a non-null instance of the type, see {@link #getIsBigBool(Boolean)}\n" +
                "   *\n" +
                "   * @return the isBigBool\n" +
                "   */\n" +
                "  @Nullable\n" +
                "  public Boolean getIsBigBool() {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_IS_BIG_BOOL)) {\n" +
                "      return bundle.getBoolean(MainFragmentBuilder.EXTRA_IS_BIG_BOOL);\n" +
                "    }\n" +
                "    return null;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Non-null getter for isBigBool\n" +
                "   *\n" +
                "   * @param defaultValue the default value in case the key isn't present in the Bundle.\n" +
                "   * @return the isBigBool\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public Boolean getIsBigBool(@NonNull final Boolean defaultValue) {\n" +
                "    if (bundle.containsKey(MainFragmentBuilder.EXTRA_IS_BIG_BOOL)\n" +
                "        && bundle.get(MainFragmentBuilder.EXTRA_IS_BIG_BOOL) != null) {\n" +
                "      return bundle.getBoolean(MainFragmentBuilder.EXTRA_IS_BIG_BOOL);\n" +
                "    }\n" +
                "    return defaultValue;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Binds the fields in {@link MainFragment} annotated with {@link com.shaishavgandhi.navigator.Extra}\n" +
                "   *\n" +
                "   * This requires that the fields in {@link MainFragment} be at least package-private or if they \n" +
                "   * are private, they have a defined setter in the class.\n" +
                "   * You should call this method in an early part of the activity lifecycle like onCreate on onStart.\n" +
                "   *\n" +
                "   * @param binder the activity/fragment whose variables are being bound\n" +
                "   */\n" +
                "  public static final void bind(MainFragment binder) {\n" +
                "    Bundle bundle = binder.getArguments();\n" +
                "    if (bundle != null) {\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_POINTS)) {\n" +
                "        long points = bundle.getLong(MainFragmentBuilder.EXTRA_POINTS);\n" +
                "        binder.points = points;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_NAME)) {\n" +
                "        String name = bundle.getString(MainFragmentBuilder.EXTRA_NAME);\n" +
                "        binder.name = name;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_NAMES)) {\n" +
                "        String[] names = bundle.getStringArray(MainFragmentBuilder.EXTRA_NAMES);\n" +
                "        binder.names = names;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_NAME_LIST)) {\n" +
                "        ArrayList<String> nameList = bundle.getStringArrayList(MainFragmentBuilder.EXTRA_NAME_LIST);\n" +
                "        binder.nameList = nameList;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_INT)) {\n" +
                "        int smallInt = bundle.getInt(MainFragmentBuilder.EXTRA_SMALL_INT);\n" +
                "        binder.smallInt = smallInt;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_INT_ARRAY)) {\n" +
                "        int[] smallIntArray = bundle.getIntArray(MainFragmentBuilder.EXTRA_SMALL_INT_ARRAY);\n" +
                "        binder.smallIntArray = smallIntArray;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_INT_LIST)) {\n" +
                "        ArrayList<Integer> intList = bundle.getIntegerArrayList(MainFragmentBuilder.EXTRA_INT_LIST);\n" +
                "        binder.intList = intList;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_BIG_LONG)) {\n" +
                "        Long bigLong = bundle.getLong(MainFragmentBuilder.EXTRA_BIG_LONG);\n" +
                "        binder.bigLong = bigLong;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_LONG_ARRAY)) {\n" +
                "        long[] longArray = bundle.getLongArray(MainFragmentBuilder.EXTRA_LONG_ARRAY);\n" +
                "        binder.longArray = longArray;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_DOUBLE)) {\n" +
                "        double smallDouble = bundle.getDouble(MainFragmentBuilder.EXTRA_SMALL_DOUBLE);\n" +
                "        binder.smallDouble = smallDouble;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_BIG_DOUBLE)) {\n" +
                "        Double bigDouble = bundle.getDouble(MainFragmentBuilder.EXTRA_BIG_DOUBLE);\n" +
                "        binder.bigDouble = bigDouble;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_DOUBLE_ARRAY)) {\n" +
                "        double[] doubleArray = bundle.getDoubleArray(MainFragmentBuilder.EXTRA_DOUBLE_ARRAY);\n" +
                "        binder.doubleArray = doubleArray;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_FLOAT)) {\n" +
                "        float smallFloat = bundle.getFloat(MainFragmentBuilder.EXTRA_SMALL_FLOAT);\n" +
                "        binder.smallFloat = smallFloat;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_BIG_FLOAT)) {\n" +
                "        Float bigFloat = bundle.getFloat(MainFragmentBuilder.EXTRA_BIG_FLOAT);\n" +
                "        binder.bigFloat = bigFloat;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_BYTE)) {\n" +
                "        byte smallByte = bundle.getByte(MainFragmentBuilder.EXTRA_SMALL_BYTE);\n" +
                "        binder.smallByte = smallByte;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_BYTE_ARRAY)) {\n" +
                "        byte[] byteArray = bundle.getByteArray(MainFragmentBuilder.EXTRA_BYTE_ARRAY);\n" +
                "        binder.byteArray = byteArray;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_SHORT)) {\n" +
                "        short smallShort = bundle.getShort(MainFragmentBuilder.EXTRA_SMALL_SHORT);\n" +
                "        binder.smallShort = smallShort;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_SHORT_ARRAY)) {\n" +
                "        short[] shortArray = bundle.getShortArray(MainFragmentBuilder.EXTRA_SHORT_ARRAY);\n" +
                "        binder.shortArray = shortArray;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_CHAR)) {\n" +
                "        char smallChar = bundle.getChar(MainFragmentBuilder.EXTRA_SMALL_CHAR);\n" +
                "        binder.smallChar = smallChar;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_CHAR_ARRAY)) {\n" +
                "        char[] charArray = bundle.getCharArray(MainFragmentBuilder.EXTRA_CHAR_ARRAY);\n" +
                "        binder.charArray = charArray;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_CHAR_SEQUENCE)) {\n" +
                "        CharSequence charSequence = bundle.getCharSequence(MainFragmentBuilder.EXTRA_CHAR_SEQUENCE);\n" +
                "        binder.charSequence = charSequence;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_CHAR_SEQ_ARRAY)) {\n" +
                "        CharSequence[] charSeqArray = bundle.getCharSequenceArray(MainFragmentBuilder.EXTRA_CHAR_SEQ_ARRAY);\n" +
                "        binder.charSeqArray = charSeqArray;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_CHAR_SEQ_LIST)) {\n" +
                "        ArrayList<CharSequence> charSeqList = bundle.getCharSequenceArrayList(MainFragmentBuilder.EXTRA_CHAR_SEQ_LIST);\n" +
                "        binder.charSeqList = charSeqList;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_IS_SMALL_BOOLEAN)) {\n" +
                "        boolean isSmallBoolean = bundle.getBoolean(MainFragmentBuilder.EXTRA_IS_SMALL_BOOLEAN);\n" +
                "        binder.isSmallBoolean = isSmallBoolean;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_SMALL_BOOL_ARRAY)) {\n" +
                "        boolean[] smallBoolArray = bundle.getBooleanArray(MainFragmentBuilder.EXTRA_SMALL_BOOL_ARRAY);\n" +
                "        binder.smallBoolArray = smallBoolArray;\n" +
                "      }\n" +
                "      if (bundle.containsKey(MainFragmentBuilder.EXTRA_IS_BIG_BOOL)) {\n" +
                "        Boolean isBigBool = bundle.getBoolean(MainFragmentBuilder.EXTRA_IS_BIG_BOOL);\n" +
                "        binder.isBigBool = isBigBool;\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Static factory method to instantiate {@link MainFragmentBinder}\n" +
                "   *\n" +
                "   * @param bundle non null bundle that will be used to unwrap the data.\n" +
                "   * @return the binder that will expose getters.\n" +
                "   */\n" +
                "  public static MainFragmentBinder from(@NonNull final Bundle bundle) {\n" +
                "    return new MainFragmentBinder(bundle);\n" +
                "  }\n" +
                "}");

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.sampleapp.test.MainFragmentBinder")
                .hasSourceEquivalentTo(expected);
    }

}
