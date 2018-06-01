package com.shaishavgandhi.navigator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@Target(ElementType.FIELD)
@Retention(SOURCE)
public @interface Optional {
}
