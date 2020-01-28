package org.ilot.lib.cassandra.dao;

public class ResultSetNotFoundException extends Exception {
    public ResultSetNotFoundException(String message) {
        super(message);
    }
}
