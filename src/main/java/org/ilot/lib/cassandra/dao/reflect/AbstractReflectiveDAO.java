package org.ilot.lib.cassandra.dao.reflect;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.ilot.lib.cassandra.connector.CassandraConnector;
import org.ilot.lib.cassandra.dao.AbstractDAO;
import org.ilot.lib.cassandra.dao.ResultSetNotFoundException;
import org.ilot.lib.cassandra.dao.reflect.annotation.Column;
import org.ilot.lib.cassandra.dao.reflect.annotation.CounterColumn;
import org.ilot.lib.cassandra.dao.reflect.annotation.Table;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.SortedMap;
import java.util.StringJoiner;
import java.util.TreeMap;

public abstract class AbstractReflectiveDAO<E> extends AbstractDAO<E> {
    public ReflectiveRowMapper<E> reflectiveRowMapper;

    public AbstractReflectiveDAO(CassandraConnector connector) {
        super(connector);
        reflectiveRowMapper = new ReflectiveRowMapper<>();
    }

    @Override
    protected String getTableName() {
        Class clazz = getClass();
        Annotation tableNameAnnotation = clazz.getAnnotation(Table.class);

        if (tableNameAnnotation instanceof Table) {
            Table tableName = (Table) tableNameAnnotation;
            return tableName.name();
        }
        throw new IllegalStateException("@Table annotation missing for class: " + clazz);
    }

    // fixme
    @Override
    protected String getCreateTableStatement() {
        Class clazz = getClass();
        Field[] fields = clazz.getDeclaredFields();

        SortedMap<Key, String> partitioningKeys = new TreeMap<>();
        SortedMap<Key, String> clusteringColumns = new TreeMap<>();
        SortedMap<Key, String> columns = new TreeMap<>();
        SortedMap<Key, String> counters = new TreeMap<>();

        for (Field field : fields) {
            field.setAccessible(true);
//            PartitioningKey partitioningKeyAnnotation = field.getAnnotation(PartitioningKey.class);
//            if (partitioningKeyAnnotation != null) {
//                partitioningKeys.put(new Key(partitioningKeyAnnotation.name(), partitioningKeyAnnotation.ordinal()), field.getType()
//                        .toString());
//                continue;
//            }
//
//            ClusteringColumn clusteringColumnAnnotation = field.getAnnotation(ClusteringColumn.class);
//            if (clusteringColumnAnnotation != null) {
//                clusteringColumns.put(new Key(clusteringColumnAnnotation.name(), clusteringColumnAnnotation.ordinal()), field.getType()
//                        .toString());
//                continue;
//            }
//
//            Column columnAnnotation = field.getAnnotation(Column.class);
//            if (columnAnnotation != null) {
//                columns.put(new Key(columnAnnotation.name(), columnAnnotation.ordinal()), field.getType()
//                        .toString());
//                continue;
//            }

            CounterColumn counterColumnAnnotation = field.getAnnotation(CounterColumn.class);
            if (counterColumnAnnotation != null) {
                counters.put(new Key(counterColumnAnnotation.name(), counterColumnAnnotation.ordinal()), field.getType()
                        .toString());
            }
        }

        if (partitioningKeys.isEmpty()) {
            throw new IllegalStateException("Missing @PartitioningKey annotation for class: " + clazz);
        }

        StringBuilder createTableStatement = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                .append(keyspaceName).append(".").append(wrap(tableName)).append(" (");

        partitioningKeys.forEach((k, v) -> createTableStatement.append(k.getName()).append(" ").append(v).append(", "));

        appendFields(counters, createTableStatement);

        // end
        appendFields(clusteringColumns, createTableStatement);
        appendFields(columns, createTableStatement);

        createTableStatement.append("PRIMARY KEY (");

        if (!clusteringColumns.isEmpty()) {
            createTableStatement.append("(");
        }

        StringJoiner stringJoiner = new StringJoiner(", ");
        partitioningKeys.keySet().forEach(k -> stringJoiner.add(k.getName()));

        createTableStatement.append(stringJoiner.toString()).append(")");

        if (!clusteringColumns.isEmpty()) {
            createTableStatement.append(", ");
            StringJoiner ckStringJoiner = new StringJoiner(", ");
            clusteringColumns.keySet().forEach(k -> ckStringJoiner.add(k.getName()));
            createTableStatement.append(stringJoiner).append(") WITH CLUSTERING ORDER BY (");
            StringJoiner ckStringJoiner2 = new StringJoiner(", ");
            clusteringColumns.keySet().forEach(k -> ckStringJoiner2.add(k.getName().concat(" ").concat(k.getOrdering().toString())));
            createTableStatement.append(ckStringJoiner2).append(")");
        }

        createTableStatement.append(";");

        return createTableStatement.toString();
    }

    private void appendFields(SortedMap<Key, String> fields, StringBuilder createTableStatement) {
        if (!fields.isEmpty()) {
            fields.forEach((k, v) -> createTableStatement.append(k.getName()).append(" ").append(v).append(", "));
        }
    }

    private String wrap(String tableName) {
        return "\"" + tableName + "\"";
    }


    public void write(E entity) throws IllegalAccessException {
        Insert statement = QueryBuilder.insertInto(keyspaceName, tableName);
        reflectiveRowMapper.mapInsertValues(statement, entity);
        write(statement);
    }

    // TODO sort exception handling
    public E read(Statement statement) throws ResultSetNotFoundException, InstantiationException, IllegalAccessException {
        ResultSet resultSet = cassandraConnector.execute(statement);

        if (resultSet == null) {
            String message = "ResultSet not found for statement: " + statement;
            logger.debug(message);
            throw new ResultSetNotFoundException(message);
        }
        return reflectiveRowMapper.mapResultSet(resultSet);
    }
}