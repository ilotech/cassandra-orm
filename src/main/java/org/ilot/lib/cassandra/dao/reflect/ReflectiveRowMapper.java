package org.ilot.lib.cassandra.dao.reflect;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.querybuilder.Insert;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ReflectiveRowMapper<E> {
    private final Class clazz = getClass().getGenericSuperclass().getClass();
    private final Map<Field, Method> setterMap = getSetterMappings();
    private final List<Field> fields = new CopyOnWriteArrayList<>(clazz.getDeclaredFields());
//            Arrays.asList(clazz.getDeclaredFields());

    void mapInsertValues(Insert statement, E entity) throws IllegalAccessException {
        fields.forEach(field -> {
            field.setAccessible(true);
            if (field.getAnnotations().length == 0) {
                return;
            }
            try {
                statement.value(field.getName(), field.get(entity));
            } catch (IllegalAccessException e) {
//                throw e;
            }
        });
    }

    E mapResultSet(ResultSet resultSet) throws IllegalAccessException, InstantiationException {
        Object entity = clazz.newInstance();

        resultSet.forEach(row -> {

            setterMap.forEach((field, setter) -> {
                try {
                    setter.invoke(entity, row.get(field.getName(), field.getType()));

                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });


        });

        return (E) entity;
    }

    private Map<Field, Method> getSetterMappings() {
        Map<Field, Method> setterMapping = new ConcurrentHashMap<>();
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            if (!isSetter(method)) continue;

            fields.forEach(field -> {
                if (method.getName().contains(field.getName())) {
                    setterMapping.put(field, method);
                }
            });
        }
        return setterMapping;
    }

    private boolean isSetter(Method method){
        if(!method.getName().startsWith("set")) return false;
        return method.getParameterTypes().length == 1;
    }
}