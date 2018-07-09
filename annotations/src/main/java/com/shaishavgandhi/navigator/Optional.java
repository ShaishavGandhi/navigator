package com.shaishavgandhi.navigator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Marks annotated field as "Optional" to Navigator. This implies
 * that the generated `Builder` class will not add it to the
 * static factory method, but will create a builder method to set
 * the field. This is especially useful when dealing with primitive
 * types. It doesn't make sense to mark primitives as Nullable.
 * If you want your primitive field to be Nullable, annotate with
 * {@link Optional}
 *
 * @see Extra
 */
@Target(ElementType.FIELD)
@Retention(SOURCE)
public @interface Optional {
}
