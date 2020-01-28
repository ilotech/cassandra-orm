package org.ilot.lib.cassandra.dao.reflect.annotation;


import org.ilot.lib.cassandra.dao.reflect.DataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CounterColumn {
    String name() default "generated";
    DataType type() default DataType.GENERATED;
    int ordinal();
}