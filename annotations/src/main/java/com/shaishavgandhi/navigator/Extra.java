package com.shaishavgandhi.navigator;

import kotlin.annotation.AnnotationRetention;
import kotlin.annotation.AnnotationTarget;
import kotlin.annotation.Retention;
import kotlin.annotation.Target;

@Target(allowedTargets = AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
public @interface Extra {
}
