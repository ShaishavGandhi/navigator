package com.shaishavgandhi.navigator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Marks a field as an `extra`. Using this annotation will indicate
 * to Navigator to generate `Builder` and `Binder` classes. `Builder`
 * class will provide convenience methods to set the annotated field
 * to the Bundle and the `Binder` does the binding of the Bundle data
 * to the annotated fields.
 *
 * Fields annotated with {@link Extra} are non-null by default and will
 * be added to the static factory method of the `Builder`. If the field
 * is optional, then you should use the {@link android.support.Nullable}
 * annotation (if you're using Java) or make the field nullable in Kotlin
 * with the "?".
 *
 * Example:
 * <pre>
 *     public class MyActivity extends Activity {
 *         {@literal @}Extra String message;
 *         {@literal @}Nullable
 *         {@literal @}Extra User user;
 *     }
 * </pre>
 *
 * @see Optional
 */
@Target(ElementType.FIELD)
@Retention(SOURCE)
public @interface Extra {
    /**
     * The custom key for the annotated field. This helps
     * with backward compatibility, where you can incrementally
     * move to Navigator. If no key is specified, the field
     * name is taken as the key.
     *
     * @return The key of the field.
     */
    String key() default "";
}
