package org.ilot.lib.cassandra.dao.reflect;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;


public class StaticTypeMapping {
    private static final Map<Class, DataType> typeMap;

    static {
        typeMap = new HashMap<>();

        typeMap.put(String.class, DataType.TEXT);
        typeMap.put(long.class, DataType.BIGINT);
        typeMap.put(Long.class, DataType.BIGINT);
        typeMap.put(ByteBuffer.class, DataType.BLOB);
        typeMap.put(Boolean.class, DataType.BOOLEAN);
        typeMap.put(boolean.class, DataType.BOOLEAN);
        typeMap.put(Integer.class, DataType.INT);
        typeMap.put(int.class, DataType.INT);
        typeMap.put(Double.class, DataType.DOUBLE);
        typeMap.put(double.class, DataType.DOUBLE);
        typeMap.put(float.class, DataType.FLOAT);
        typeMap.put(Float.class, DataType.FLOAT);
        typeMap.put(short.class, DataType.SMALLINT);
        typeMap.put(Short.class, DataType.SMALLINT);
    }

    public static DataType getTypeForClass(Class clazz) {
        return typeMap.get(clazz);
    }
}