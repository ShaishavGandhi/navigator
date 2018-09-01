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
public class NavigatorBuilderTest {

    @Test public void testSimpleFragmentBuilder() {
        String className = "MainFragment";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.app.Fragment;\n"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra public String name;\n"
                + "}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("MainFragmentBuilder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import android.support.annotation.NonNull;\n" +
                "import android.support.annotation.Nullable;\n" +
                "import java.lang.String;\n" +
                "\n" +
                "public final class MainFragmentBuilder {\n" +
                "  public static final String EXTRA_NAME = \"name\";\n\n" +
                "  private Bundle extras;\n" +
                "\n" +
                "  @NonNull\n" +
                "  private String name;\n" +
                "\n" +
                "  private MainFragmentBuilder(@NonNull final String name) {\n" +
                "    this.name = name;\n" +
                "  }\n" +
                "  public static final MainFragmentBuilder builder(@NonNull final String name) {\n" +
                "    return new MainFragmentBuilder(name);\n" +
                "  }" +
                "\n" +
                "  /**\n" +
                "   * Returns a {@link android.os.Bundle} built from all extras that have been set \n" +
                "   * using {@linkplain Navigator}'s prepare method.\n" +
                "   *\n" +
                "   * Used internally to simply get the {@link android.os.Bundle} that will be \n" +
                "   * sent along with the {@link android.content.Intent}.\n" +
                "   *\n" +
                "   * Exposed publicly to allow custom usage of the {@link android.os.Bundle}. \n" +
                "   *\n" +
                "   * Example: It can be useful while navigating to a {@link android.support.v4.app.Fragment}\n" +
                "   * to use {@linkplain Navigator}'s prepare method to \n" +
                "   * build your bundle and call this method to get extras that can be set as \n" +
                "   * arguments to your {@linkplain android.support.v4.app.Fragment}.\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public Bundle getBundle() {\n" +
                "    Bundle bundle = new Bundle();\n" +
                "    bundle.putString(EXTRA_NAME, name);\n" +
                "    if (extras != null) {\n" +
                "      bundle.putAll(extras);\n" +
                "    }\n" +
                "    return bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Setter for adding a {@link android.os.Bundle} to the existing bundle \n" +
                "   * that will be sent along with the {@link android.content.Intent}.\n" +
                "   *\n" +
                "   * Effectively serves as an overload for {@link android.content.Intent#putExtras\n" +
                "   *\n" +
                "   * @see #getBundle\n" +
                "   * @param extras that will be appended to the current bundle\n" +
                "   * @return Builder class for chaining other methods\n" +
                "   */\n" +
                "  public MainFragmentBuilder setExtras(@Nullable final Bundle extras) {\n" +
                "    this.extras = extras;\n" +
                "    return this;\n" +
                "  }\n" +
                "}"
        );
        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.sampleapp.test.MainFragmentBuilder")
                .hasSourceEquivalentTo(expected);
    }

    @Test public void testSimpleFragmentBuilderWithNullable() {
        String className = "MainFragment";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.app.Fragment;\n"
                + "import android.support.annotation.Nullable;\n"
                + "import android.support.annotation.NonNull;\n"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra @Nullable public String name;\n"
                + "}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("MainFragmentBuilder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import android.support.annotation.NonNull;\n" +
                "import android.support.annotation.Nullable;\n" +
                "import java.lang.String;\n" +
                "\n" +
                "public final class MainFragmentBuilder {\n" +
                "  public static final String EXTRA_NAME = \"name\";\n\n" +
                "  private Bundle extras;\n" +
                "\n" +
                "  @Nullable\n" +
                "  private String name;\n" +
                "\n" +
                "  private MainFragmentBuilder() {\n" +
                "  }\n" +
                "\n" +
                "  public final MainFragmentBuilder setName(@Nullable String name) {\n" +
                "    this.name = name;\n" +
                "    return this;\n" +
                "  }\n" +
                "  public static final MainFragmentBuilder builder() {\n" +
                "    return new MainFragmentBuilder();\n" +
                "  }" +
                "\n" +
                "  /**\n" +
                "   * Returns a {@link android.os.Bundle} built from all extras that have been set \n" +
                "   * using {@linkplain Navigator}'s prepare method.\n" +
                "   *\n" +
                "   * Used internally to simply get the {@link android.os.Bundle} that will be \n" +
                "   * sent along with the {@link android.content.Intent}.\n" +
                "   *\n" +
                "   * Exposed publicly to allow custom usage of the {@link android.os.Bundle}. \n" +
                "   *\n" +
                "   * Example: It can be useful while navigating to a {@link android.support.v4.app.Fragment}\n" +
                "   * to use {@linkplain Navigator}'s prepare method to \n" +
                "   * build your bundle and call this method to get extras that can be set as \n" +
                "   * arguments to your {@linkplain android.support.v4.app.Fragment}.\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public Bundle getBundle() {\n" +
                "    Bundle bundle = new Bundle();\n" +
                "    bundle.putString(EXTRA_NAME, name);\n" +
                "    if (extras != null) {\n" +
                "      bundle.putAll(extras);\n" +
                "    }\n" +
                "    return bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Setter for adding a {@link android.os.Bundle} to the existing bundle \n" +
                "   * that will be sent along with the {@link android.content.Intent}.\n" +
                "   *\n" +
                "   * Effectively serves as an overload for {@link android.content.Intent#putExtras\n" +
                "   *\n" +
                "   * @see #getBundle\n" +
                "   * @param extras that will be appended to the current bundle\n" +
                "   * @return Builder class for chaining other methods\n" +
                "   */\n" +
                "  public MainFragmentBuilder setExtras(@Nullable final Bundle extras) {\n" +
                "    this.extras = extras;\n" +
                "    return this;\n" +
                "  }\n" +
                "}"
        );
        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.sampleapp.test.MainFragmentBuilder")
                .hasSourceEquivalentTo(expected);
    }

    @Test public void testSimpleFragmentBuilderWithSerializable() {
        String className = "MainFragment";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import java.io.Serializable;"
                + "import android.app.Fragment;\n"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra public User name;\n"
                + " public class User implements Serializable {}\n"
                + "}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("MainFragmentBuilder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import android.support.annotation.NonNull;\n" +
                "import android.support.annotation.Nullable;\n" +
                "import java.lang.String;\n" +
                "\n" +
                "public final class MainFragmentBuilder {\n" +
                "  public static final String EXTRA_NAME = \"name\";\n\n" +
                "  private Bundle extras;\n" +
                "\n" +
                "  @NonNull\n" +
                "  private MainFragment.User name;\n" +
                "\n" +
                "  private MainFragmentBuilder(@NonNull final MainFragment.User name) {\n" +
                "    this.name = name;\n" +
                "  }\n" +
                "  public static final MainFragmentBuilder builder(@NonNull final MainFragment.User name) {\n" +
                "    return new MainFragmentBuilder(name);\n" +
                "  }" +
                "\n" +
                "\n" +
                "  /**\n" +
                "   * Returns a {@link android.os.Bundle} built from all extras that have been set \n" +
                "   * using {@linkplain Navigator}'s prepare method.\n" +
                "   *\n" +
                "   * Used internally to simply get the {@link android.os.Bundle} that will be \n" +
                "   * sent along with the {@link android.content.Intent}.\n" +
                "   *\n" +
                "   * Exposed publicly to allow custom usage of the {@link android.os.Bundle}. \n" +
                "   *\n" +
                "   * Example: It can be useful while navigating to a {@link android.support.v4.app.Fragment}\n" +
                "   * to use {@linkplain Navigator}'s prepare method to \n" +
                "   * build your bundle and call this method to get extras that can be set as \n" +
                "   * arguments to your {@linkplain android.support.v4.app.Fragment}.\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public Bundle getBundle() {\n" +
                "    Bundle bundle = new Bundle();\n" +
                "    bundle.putSerializable(EXTRA_NAME, name);\n" +
                "    if (extras != null) {\n" +
                "      bundle.putAll(extras);\n" +
                "    }\n" +
                "    return bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Setter for adding a {@link android.os.Bundle} to the existing bundle \n" +
                "   * that will be sent along with the {@link android.content.Intent}.\n" +
                "   *\n" +
                "   * Effectively serves as an overload for {@link android.content.Intent#putExtras\n" +
                "   *\n" +
                "   * @see #getBundle\n" +
                "   * @param extras that will be appended to the current bundle\n" +
                "   * @return Builder class for chaining other methods\n" +
                "   */\n" +
                "  public MainFragmentBuilder setExtras(@Nullable final Bundle extras) {\n" +
                "    this.extras = extras;\n" +
                "    return this;\n" +
                "  }\n" +
                "}"
        );
        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.sampleapp.test.MainFragmentBuilder")
                .hasSourceEquivalentTo(expected);
    }

    @Test public void testSimpleFragmentBuilderWithParcelables() {
        String className = "MainFragment";

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.os.Parcelable;\n"
                + "import android.os.Parcel;\n"
                + "import android.app.Fragment;\n"
                + "import java.util.ArrayList;\n"
                + "import android.util.SparseArray;\n"
                + "\n"
                + "public class MainFragment extends Fragment {\n"
                + " @Extra public User name;\n"
                + " @Extra public ArrayList<User> users;\n"
                + " @Extra public User[] userArray;\n"
                + " @Extra public SparseArray<User> sparseArray;\n"
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
                + "}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("MainFragmentBuilder", ""
                + "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import android.support.annotation.NonNull;\n" +
                "import android.support.annotation.Nullable;\n" +
                "import android.util.SparseArray;\n" +
                "import java.lang.String;\n" +
                "import java.util.ArrayList;\n" +
                "\n" +
                "public final class MainFragmentBuilder {\n" +
                "  public static final String EXTRA_NAME = \"name\";\n" +
                "\n" +
                "  public static final String EXTRA_USERS = \"users\";\n" +
                "\n" +
                "  public static final String EXTRA_USER_ARRAY = \"userArray\";\n" +
                "\n" +
                "  public static final String EXTRA_SPARSE_ARRAY = \"sparseArray\";\n" +
                "\n" +
                "  private Bundle extras;\n" +
                "\n" +
                "  @NonNull\n" +
                "  private MainFragment.User name;\n" +
                "\n" +
                "  @NonNull\n" +
                "  private ArrayList<MainFragment.User> users;\n" +
                "\n" +
                "  @NonNull\n" +
                "  private MainFragment.User[] userArray;\n" +
                "\n" +
                "  @NonNull\n" +
                "  private SparseArray<MainFragment.User> sparseArray;\n" +
                "\n" +
                "  private MainFragmentBuilder(@NonNull final MainFragment.User name, \n" +
                "      @NonNull final ArrayList<MainFragment.User> users, @NonNull final " +
                "MainFragment.User[] userArray, @NonNull final SparseArray<MainFragment.User> " +
                "sparseArray) {\n" +
                "    this.name = name;\n" +
                "    this.users = users;\n" +
                "    this.userArray = userArray;\n" +
                "    this.sparseArray = sparseArray;\n" +
                "  }\n" +
                "  public static final MainFragmentBuilder builder(@NonNull final MainFragment.User name,\n" +
                "      @NonNull final ArrayList<MainFragment.User> users,\n" +
                "      @NonNull final MainFragment.User[] userArray,\n" +
                "      @NonNull final SparseArray<MainFragment.User> sparseArray) {\n" +
                "    return new MainFragmentBuilder(name, users, userArray, sparseArray);\n" +
                "  }" +
                "\n" +
                "\n" +
                "  /**\n" +
                "   * Returns a {@link android.os.Bundle} built from all extras that have been set \n" +
                "   * using {@linkplain Navigator}'s prepare method.\n" +
                "   *\n" +
                "   * Used internally to simply get the {@link android.os.Bundle} that will be \n" +
                "   * sent along with the {@link android.content.Intent}.\n" +
                "   *\n" +
                "   * Exposed publicly to allow custom usage of the {@link android.os.Bundle}. \n" +
                "   *\n" +
                "   * Example: It can be useful while navigating to a {@link android.support.v4.app.Fragment}\n" +
                "   * to use {@linkplain Navigator}'s prepare method to \n" +
                "   * build your bundle and call this method to get extras that can be set as \n" +
                "   * arguments to your {@linkplain android.support.v4.app.Fragment}.\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public Bundle getBundle() {\n" +
                "    Bundle bundle = new Bundle();\n" +
                "    bundle.putParcelable(EXTRA_NAME, name);\n" +
                "    bundle.putParcelableArrayList(EXTRA_USERS, users);\n" +
                "    bundle.putParcelableArray(EXTRA_USER_ARRAY, userArray);\n" +
                "    bundle.putSparseParcelableArray(EXTRA_SPARSE_ARRAY, sparseArray);\n" +
                "    if (extras != null) {\n" +
                "      bundle.putAll(extras);\n" +
                "    }\n" +
                "    return bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Setter for adding a {@link android.os.Bundle} to the existing bundle \n" +
                "   * that will be sent along with the {@link android.content.Intent}.\n" +
                "   *\n" +
                "   * Effectively serves as an overload for {@link android.content.Intent#putExtras\n" +
                "   *\n" +
                "   * @see #getBundle\n" +
                "   * @param extras that will be appended to the current bundle\n" +
                "   * @return Builder class for chaining other methods\n" +
                "   */\n" +
                "  public MainFragmentBuilder setExtras(@Nullable final Bundle extras) {\n" +
                "    this.extras = extras;\n" +
                "    return this;\n" +
                "  }\n" +
                "}"
        );
        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedSourceFile("com.shaishavgandhi.sampleapp.test.MainFragmentBuilder")
                .hasSourceEquivalentTo(expected);
    }

    @Test public void testSimpleActivityBuilder() {
        String className = "MainActivity";
        JavaFileObject javaFileObject = JavaFileObjects.forSourceString(getName(className), ""
                + "package com.shaishavgandhi.sampleapp.test;\n"
                + "\n"
                + "import com.shaishavgandhi.navigator.Extra;\n"
                + "import android.support.annotation.Nullable;\n"
                + "import android.app.Activity;\n"
                + "\n"
                + "public class MainActivity extends Activity {\n"
                + " @Extra public String name;\n"
                + " @Extra @Nullable Integer age;\n"
                +"}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("MainActivityBuilder", "" +
                "package com.shaishavgandhi.sampleapp.test;\n" +
                "\n" +
                "import android.app.Activity;\n" +
                "import android.content.Context;\n" +
                "import android.content.Intent;\n" +
                "import android.os.Bundle;\n" +
                "import android.support.annotation.NonNull;\n" +
                "import android.support.annotation.Nullable;\n" +
                "import android.support.v4.app.Fragment;\n" +
                "import java.lang.Integer;\n" +
                "import java.lang.String;\n" +
                "\n" +
                "public final class MainActivityBuilder {\n" +
                "  public static final String EXTRA_NAME = \"name\";\n" +
                "\n" +
                "  public static final String EXTRA_AGE = \"age\";\n" +
                "\n" +
                "  private int flags = -1;\n" +
                "\n" +
                "  private String action;\n" +
                "\n" +
                "  private Bundle extras;\n" +
                "\n" +
                "  @NonNull\n" +
                "  private String name;\n" +
                "\n" +
                "  @Nullable\n" +
                "  private Integer age;\n" +
                "\n" +
                "  private MainActivityBuilder(@NonNull final String name) {\n" +
                "    this.name = name;\n" +
                "  }\n" +
                "  public final MainActivityBuilder setAge(@Nullable Integer age) {\n" +
                "    this.age = age;\n" +
                "    return this;\n" +
                "  }" +
                "\n" +
                "  public static final MainActivityBuilder builder(@NonNull final String name) {\n" +
                "    return new MainActivityBuilder(name);\n" +
                "  }" +
                "\n" +
                "  /**\n" +
                "   * Terminating method in the builder. Passes the built bundle, \n" +
                "   * sets any {@link android.content.Intent} flags if any and starts the \n" +
                "   * activity\n" +
                "   *\n" +
                "   * @see #getBundle\n" +
                "   * @param context\n" +
                "   */\n" +
                "  public void start(@NonNull Context context) {\n" +
                "    Intent intent = getDestinationIntent(context);\n" +
                "    context.startActivity(intent);\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Terminating method in the builder. Passes the built bundle, \n" +
                "   * sets any {@link android.content.Intent} flags if any and starts the \n" +
                "   * activity\n" +
                "   *\n" +
                "   * @see #getBundle\n" +
                "   * @param fragment\n" +
                "   */\n" +
                "  public void start(@NonNull Fragment fragment) {\n" +
                "    Intent intent = getDestinationIntent(fragment.getActivity());\n" +
                "    fragment.startActivity(intent);\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Terminating method in builder. Passes the built bundle, sets any \n" +
                "   * {@link android.content.Intent} flags if any and starts the activity with \n" +
                "   * the provided requestCode.\n" +
                "   *\n" +
                "   * @param activity\n" +
                "   * @param requestCode\n" +
                "   */\n" +
                "  public void startForResult(@NonNull Activity activity, int requestCode) {\n" +
                "    Intent intent = getDestinationIntent(activity);\n" +
                "    activity.startActivityForResult(intent, requestCode);\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Terminating method in builder. Passes the built bundle, sets any \n" +
                "   * {@link android.content.Intent} flags if any and starts the activity with \n" +
                "   * the provided requestCode.\n" +
                "   *\n" +
                "   * @param fragment\n" +
                "   * @param requestCode\n" +
                "   */\n" +
                "  public void startForResult(@NonNull Fragment fragment, int requestCode) {\n" +
                "    Intent intent = getDestinationIntent(fragment.getActivity());\n" +
                "    fragment.startActivityForResult(intent, requestCode);\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Returns the {@link android.content.Intent} that will be used to start the Activity.\n" +
                "   *\n" +
                "   * Sets optional fields like {@link flags}, {@link action} if they are supplied by\n" +
                "   * you in the builder methods.\n" +
                "   *\n" +
                "   * @param context the context used in Intent.\n" +
                "   * @return the constructed Intent\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  private Intent getDestinationIntent(@NonNull Context context) {\n" +
                "    Intent intent = new Intent(context, MainActivity.class);\n" +
                "    intent.putExtras(getBundle());\n" +
                "    if (flags != -1) {\n" +
                "      intent.setFlags(flags);\n" +
                "    }\n" +
                "    if (action != null) {\n" +
                "      intent.setAction(action);\n" +
                "    }\n" +
                "    return intent;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Terminating method in builder. Passes the built bundle, sets any \n" +
                "   * {@link android.content.Intent} flags if any and starts the activity with \n" +
                "   * the provided requestCode and {@link android.os.Bundle extras}.\n" +
                "   *\n" +
                "   * @param activity\n" +
                "   * @param requestCode\n" +
                "   */\n" +
                "  public void startForResult(@NonNull Activity activity, int requestCode, @Nullable final Bundle extras) {\n" +
                "    Intent intent = getDestinationIntent(activity);\n" +
                "    activity.startActivityForResult(intent, requestCode, extras);\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Terminating method in builder. Passes the built bundle, sets any \n" +
                "   * {@link android.content.Intent} flags if any and starts the activity with \n" +
                "   * the provided requestCode and {@link android.os.Bundle extras}.\n" +
                "   *\n" +
                "   * @param fragment\n" +
                "   * @param requestCode\n" +
                "   */\n" +
                "  public void startForResult(@NonNull Fragment fragment, int requestCode,\n" +
                "      @Nullable final Bundle extras) {\n" +
                "    Intent intent = getDestinationIntent(fragment.getActivity());\n" +
                "    fragment.startActivityForResult(intent, requestCode, extras);\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Terminating method in builder. Passes the built bundle, \n" +
                "   * sets any {@link android.content.Intent} flags if any and additionally \n" +
                "   * starts the activity with the provided {@link android.os.Bundle bundle}.\n" +
                "   *\n" +
                "   * Example: When using Shared Element Transition or any kind of Activity \n" +
                "   * transition, you can use this method to pass the {@link android.os.Bundle} \n" +
                "   * created by {@link android.app.ActivityOptions}.\n" +
                "   *\n" +
                "   * @see #getBundle\n" +
                "   * @param context\n" +
                "   * @param extras\n" +
                "   */\n" +
                "  public void startWithExtras(@NonNull Context context, Bundle extras) {\n" +
                "    Intent intent = getDestinationIntent(context);\n" +
                "    context.startActivity(intent, extras);\n" +
                "  }\n" +
                "\n" +
                "\n" +
                "  /**\n" +
                "   * Terminating method in builder. Passes the built bundle, \n" +
                "   * sets any {@link android.content.Intent} flags if any and additionally \n" +
                "   * starts the activity with the provided {@link android.os.Bundle bundle}.\n" +
                "   *\n" +
                "   * Example: When using Shared Element Transition or any kind of Activity \n" +
                "   * transition, you can use this method to pass the {@link android.os.Bundle} \n" +
                "   * created by {@link android.app.ActivityOptions}.\n" +
                "   *\n" +
                "   * @see #getBundle\n" +
                "   * @param fragment\n" +
                "   * @param extras\n" +
                "   */\n" +
                "  public void startWithExtras(@NonNull Fragment fragment, Bundle extras) {\n" +
                "    Intent intent = getDestinationIntent(fragment.getActivity());\n" +
                "    fragment.startActivity(intent, extras);\n" +
                "  }" +
                "  /**\n" +
                "   * Set intent flags.\n" +
                "   * For the correct flag values see: {@link android.content.Intent}\n" +
                "   *\n" +
                "   * @param flags  The desired flags.\n" +
                "   * @return Returns the same Builder object for chaining multiple calls\n" +
                "   */\n" +
                "  public MainActivityBuilder setFlags(final int flags) {\n" +
                "    this.flags = flags;\n" +
                "    return this;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Set intent action.\n" +
                "   * For example: {@link android.content.Intent.ACTION_VIEW}\n" +
                "   *\n" +
                "   * @param action  The desired action.\n" +
                "   * @return Returns the same Builder object for chaining multiple calls\n" +
                "   */\n" +
                "  public MainActivityBuilder setAction(final String action) {\n" +
                "    this.action = action;\n" +
                "    return this;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Returns a {@link android.os.Bundle} built from all extras that have been set \n" +
                "   * using {@linkplain Navigator}'s prepare method.\n" +
                "   *\n" +
                "   * Used internally to simply get the {@link android.os.Bundle} that will be \n" +
                "   * sent along with the {@link android.content.Intent}.\n" +
                "   *\n" +
                "   * Exposed publicly to allow custom usage of the {@link android.os.Bundle}. \n" +
                "   *\n" +
                "   * Example: It can be useful while navigating to a {@link android.support.v4.app.Fragment}\n" +
                "   * to use {@linkplain Navigator}'s prepare method to \n" +
                "   * build your bundle and call this method to get extras that can be set as \n" +
                "   * arguments to your {@linkplain android.support.v4.app.Fragment}.\n" +
                "   */\n" +
                "  @NonNull\n" +
                "  public Bundle getBundle() {\n" +
                "    Bundle bundle = new Bundle();\n" +
                "    bundle.putString(EXTRA_NAME, name);\n" +
                "    bundle.putInt(EXTRA_AGE, age);\n" +
                "    if (extras != null) {\n" +
                "      bundle.putAll(extras);\n" +
                "    }\n" +
                "    return bundle;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Setter for adding a {@link android.os.Bundle} to the existing bundle \n" +
                "   * that will be sent along with the {@link android.content.Intent}.\n" +
                "   *\n" +
                "   * Effectively serves as an overload for {@link android.content.Intent#putExtras\n" +
                "   *\n" +
                "   * @see #getBundle\n" +
                "   * @param extras that will be appended to the current bundle\n" +
                "   * @return Builder class for chaining other methods\n" +
                "   */\n" +
                "  public MainActivityBuilder setExtras(@Nullable final Bundle extras) {\n" +
                "    this.extras = extras;\n" +
                "    return this;\n" +
                "  }\n" +
                "}"
        );

        Compilation compilation = Compiler.javac().withProcessors(new NavigatorProcessor()).compile(javaFileObject);
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedSourceFile(NavigatorProcessorTest.PACKAGE + "MainActivityBuilder")
                .hasSourceEquivalentTo(expected);
    }

}
