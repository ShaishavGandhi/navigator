# Navigator


![CircleCI branch](https://img.shields.io/circleci/project/github/shaishavgandhi05/navigator/master.svg)

<img src="assets/navigator-logo.png" width="275px"/>



Utility library that generates activity navigation boilerplate for you, along with all it's bindings. 

## Download

[![Maven Central](https://img.shields.io/maven-central/v/com.shaishavgandhi.navigator/navigator.svg)](https://mvnrepository.com/artifact/com.shaishavgandhi.navigator/navigator)

```groovy
dependencies {
  implementation 'com.shaishavgandhi.navigator:navigator:x.y.z'
  annotationProcessor 'com.shaishavgandhi.navigator:navigator-compiler:x.y.z'
  
  // Or if using Kotlin
  kapt 'com.shaishavgandhi.navigator:navigator-compiler:x.y.z'
}
```
Snapshots of the development version are available in [Sonatype's snapshots repository.](https://oss.sonatype.org/content/repositories/snapshots/)

## Use Case

Navigating to another activity requires a lot of boilerplate code in both activites. 

Source activity:
```java
public final class MainActivity extends Activity {
  
  protected void openDetailActivity() {
    Intent intent = new Intent(context, DetailActivity.class);
    intent.putParcelableExtra("user", user);
    intent.putString("source", source);
    intent.putString("title", title);
    intent.putString("subtitle", subtitle);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK || Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }

}
```

The destination activity is even more complicated:

```java

public final class DetailActivity extends Activity {
  
  String title;
  String source;
  String subtitle;
 
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    parseIntent();
  }
  
  private void parseIntent() {
    if (getIntent() != null && getIntent().getExtras() != null) {
      Bundle bundle = getIntent().getExtras();
      if (bundle.containsKey("title")) {
        title = bundle.getString("title");
      }
      
      // So on for every attribute
    
    }
  }
}

```

There are lots of things that can go wrong with this. 
1. There is no type safety.
2. There is no implicit contract between any two activities which state what is required and what is optional.
3. Everything is nullable and that's not good. 

## Usage

Navigator provides a simple builder API, that provides an implicit contract between the source and destination activites, as well as removes the binding boilerplate for you.

You only need to annotate your fields with `@Extra` in your destination activity and Navigator will generate a builder API for you to start that activity. 

By default, Navigator will treat all fields with `@Extra` as necessary and required for the destination activity to start. Fields that are not _required_ by the activity but might be expected from some places can be annotated with `@Nullable` from android support-annotations. 

Fields annotated with `@Extra` must be public or package-private. If the fields are private, then you must provide a setter for them that will be used by Navigator to bind the data.

Using the same example:

```java

public final class DetailActivity extends Activity {
  
  @Extra String title; // Annotate with @Extra to tell Navigator that this is required when opening activity
  @Extra @Nullable String source; // @Nullable tells Navigator that this is an optional extra
  @Extra String subtitle;
 
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    DetailActivityBinder.bind(this); // Automatically bind extras
  }
}
```

```java
public final class MainActivity extends Activity {
  
  protected void openDetailActivity() {
    DetailActivityBuilder.builder(title, subtitle) // Required extras by ActivityC go in static factory method
      .setSource(source) // optional extras
      .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK || Intent.FLAG_ACTIVITY_CLEAR_TASK)
      .start(this);
  }

}
```

The sample example would work in Kotlin as well. 

## Fragments

The same examples mentioned above work for fragments as well. However, **Navigator is not interested in being a navigation library for fragments**. A whole different library can be written about that. 

Navigator does support binding of arguments passed to the fragment as well as constructing the arguments required for a fragment in an API that is very similar to Activities. 

#### Get arguments

```java
Bundle arguments = DetailFragmentBuilder.builder(userList)
                .setPoints(points)
                .getBundle();

MyFragment fragment = new MyFragment();
fragment.setArguments(arguments);
```

#### Bind arguments

```java
class DetailFragment extends Fragment {

    @Extra User user;
    @Extra Point points;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DetailFragmentBinder.bind(this);
    }

}
```

#### Alternative To SafeArgs

If you've used the Jetpack Navigation library from Google, you might have used SafeArgs to safely pass data between two fragments. Navigator seems to have a better approach than SafeArgs since you can bind all your Fragment variables in one go instead of actually getting them individually like in SafeArgs. You can also use handy Kotlin extensions that make the API better. More on that in the next section.

## Kotlin

Navigator has first class support for Kotlin and it's language features. If you use `kapt` as your annotation processor, 
Navigator will generate handy Kotlin extensions for you which simplify the API.


#### Bind arguments

```kotlin

class DetailActivity : Activity() {
  
  @Extra lateinit var title: String 
  @Extra var source: String? = null // null type indicates that it is optional
  @Extra lateinit var subtitle: String
 
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    bind() // Simply call bind extension on DetailActivity
  }
}
```

Using kapt will also simplify your API when using it in a Java class. 

```java
public class DetailActivity extends Activity {
    
    @Extra String message;
    
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DetailActivityNavigator.bind(this); // Generated kotlin extension with a nicer API
    }
    
}
```

#### Creating Builder

You can also use a handy Kotlin extension to get rid of the static factory builders. If you're preparing `Bundle` for DetailFragment, you can do:
```kotlin
class MainFragment: Fragment() {
  
  fun showDetail(post: Post, authors: List<Author>?) {
    val bundle = detailFragmentBuilder(post)
                   .setAuthors(authors)
                   
    val fragment = DetailFragment()
    fragment.setArguments(bundle)
    replaceFragment(fragment)
  }

}
```

## Advanced Usage

Navigator exposes most ways to start an activity. 

#### Start Activity For Result

```kotlin
DetailActivityBuilder.builder(users, source)
               .setPoints(points)
               .startForResult(activity, requestCode)
```

#### Start Activity With Transition Bundle
```kotlin
DetailActivityBuilder.builder(users, source)
               .setPoints(points)
               .startWithExtras(activity, transitionBundle)
```

#### Supply your own keys

It is easy to transition to Navigator in a large codebase. For example, you can mark a variable 
as `@Extra` in an existing class, which already has logic to parse out the Bundle. But you cannot
 possibly change the key to that particular variable in every place from which it's called. With 
 Navigator, you can easily specify your own custom key which will be used to execute the binding 
 of all the extras. Example:
 
 ```java
 public final class MyActivity extends Activity {
    
    @Extra(key = FragmentExtras.CUSTOM_KEY) // Custom key that other classes use when invoking MyActivity
    String extra;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityNavigator.bind(this);
    }
 }
 ```

#### Bundle

In cases where you just want to use the type-safety and implicit contract of Navigator, you can easily use the builder to get the bundle created by Navigator

```java
Bundle bundle = DetailActivityBuilder.builder(users, source)
               .setPoints(points)
               .getBundle();
```

## Add Ons

If you're using Kotlin, [BundleX](https://github.com/shaishavgandhi05/BundleX) is a useful add-on to Navigator. [BundleX](https://github.com/shaishavgandhi05/BundleX) generates extensions on the `Bundle` using the same `@Extra` annotation. 

```kotlin
class MyActivity: AppcompatActivity {
  
  @Extra lateinit var message: String
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
        
    val bundle = intent.extras
    message = bundle.getMessage(defaultValue = "hello world") // Generated extension
  }
  
  fun startActivity(message: String) {
    val bundle = Bundle()
    bundle.putMessage(message) // Use generated setter 
    // Start activity with bundle
  }
    
}
```

Simply add to your build.gradle
```groovy
dependencies {
  kapt 'com.shaishavgandhi:bundlex-compiler:x.y.z'
}
```



## Thanks

* [Baran Pirincal](https://github.com/baranpirincal) for the excellent logo.
* [Jake Wharton](https://github.com/JakeWharton) and [ButterKnife](https://github.com/JakeWharton/butterknife) for the annotation processing references. 

## License
    
    Copyright 2018 Shaishav Gandhi.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
