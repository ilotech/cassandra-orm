package org.ilot.lib.cassandra.dao.reflect;



import org.ilot.lib.cassandra.dao.reflect.annotation.Ordering;

import java.util.Objects;

public class Key implements Comparable<Key> {
    private final String name;
    private final int ordinal;
    private Ordering ordering;

    public Key(String name, int ordinal, Ordering ordering) {
        this.name = name;
        this.ordinal = ordinal;
        this.ordering = ordering;
    }

    public Key(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }

    public String getName() {
        return name;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public Ordering getOrdering() {
        return ordering;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key key = (Key) o;
        return ordinal == key.ordinal &&
               Objects.equals(name, key.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ordinal);
    }

    @Override
    public String toString() {
        return "Key{" +
               "name='" + name + '\'' +
               ", ordinal=" + ordinal +
               '}';
    }

    @Override
    public int compareTo(Key that) {
        return Integer.compare(this.ordinal, that.ordinal);
    }
}
