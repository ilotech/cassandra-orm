package org.ilot.lib.cassandra.dao.reflect.annotation;


import org.ilot.lib.cassandra.dao.reflect.DataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface PrimaryKeyColumn {
    String name() default "";
    int ordinal();
    PrimaryKeyType type();
    DataType dataType() default DataType.GENERATED;
    Ordering ordering() default Ordering.DESC;
}