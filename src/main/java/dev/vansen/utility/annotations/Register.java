package dev.vansen.utility.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Register {
    String name();

    String description();

    String[] aliases() default {};

    String permission() default "";
}
