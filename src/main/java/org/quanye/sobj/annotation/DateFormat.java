package org.quanye.sobj.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * DateFormat
 *
 * @author QuanyeChen
 * This source code is license on the Apache-License v2.0
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface DateFormat {
    public final static String FORMAT_STYLE = "yyyy-MM-dd hh:mm:ss";

    String value() default FORMAT_STYLE;
}
