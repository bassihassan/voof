package me.bassihassan.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface RowCell {
    String label() default "";
    int index() default -1;
}
