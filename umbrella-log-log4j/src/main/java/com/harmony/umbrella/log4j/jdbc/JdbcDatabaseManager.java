package com.harmony.umbrella.log4j.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.spi.LoggingEvent;

import com.harmony.umbrella.jdbc.AbstractDatabaseManager;
import com.harmony.umbrella.jdbc.ConnectionSource;
import com.harmony.umbrella.log.Level;
import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.LoggingException;
import com.harmony.umbrella.log.Message;

/**
 * @author wuxii@foxmail.com
 */
public class JdbcDatabaseManager extends AbstractDatabaseManager<LoggingEvent> {

    protected final List<Column> columns;
    protected final String sqlStatement;
    protected final ConnectionSource connectionSource;

    private boolean batchSupported;
    /*
     * scope in method {@linkplain #write(LogInfo, LoggingEvent)}
     */
    private Connection connection;
    private PreparedStatement statement;

    public JdbcDatabaseManager(int bufferSize, ConnectionSource connectionSource, String sqlStatement, List<Column> columns) {
        super(bufferSize);
        this.connectionSource = connectionSource;
        this.sqlStatement = sqlStatement;
        this.columns = columns;
    }

    @Override
    public void startup() {
        Connection connection = null;
        try {
            connection = connectionSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            this.batchSupported = metaData.supportsBatchUpdates();
        } catch (SQLException e) {
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }

    }

    @Override
    protected void connectAndStart() {
        try {
            this.connection = connectionSource.getConnection();
            this.connection.setAutoCommit(false);
            this.statement = connection.prepareStatement(sqlStatement);
        } catch (SQLException e) {
            throw new LoggingException(e);
        }
    }

    @Override
    protected void writeInternal(LogInfo logInfo, LoggingEvent event) {
        try {
            if (this.connection == null || this.connection.isClosed() //
                    || this.statement == null || this.statement.isClosed()) {
                throw new LoggingException("Cannot write logging event; JDBC manager not connected to the database.");
            }
            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                Object value = column.getColumnValue(logInfo);
                int index = i + 1;

                if (value == null) {
                    statement.setNull(index, Types.NULL);
                } else if (column.sqlType != null) {
                    statement.setObject(index, value, column.sqlType);
                } else if (value instanceof String) {
                    statement.setString(index, (String) value);
                } else if (value instanceof Message) {
                    statement.setString(index, ((Message) value).getFormattedMessage());
                } else if (value instanceof Throwable) {
                    statement.setString(index, value.toString());
                } else if (value instanceof Level) {
                    statement.setString(index, ((Level) value).getName());
                } else if (value instanceof Long) {
                    statement.setLong(index, (Long) value);
                } else if (value instanceof Boolean) {
                    statement.setBoolean(index, (Boolean) value);
                } else if (value instanceof Date) {
                    statement.setTimestamp(index, new Timestamp(((Date) value).getTime()));
                } else if (value instanceof Calendar) {
                    statement.setTimestamp(index, new Timestamp(((Calendar) value).getTimeInMillis()));
                } else if (value instanceof Enum<?>) {
                    statement.setString(index, ((Enum<?>) value).name());
                } else {
                    statement.setObject(index, value);
                }
            }

            if (this.isBatchSupported()) {
                this.statement.addBatch();
            } else if (this.statement.executeUpdate() == 0) {
                throw new LoggingException("No records inserted in database table for log event in JDBC manager.");
            }
        } catch (SQLException e) {
            throw new LoggingException("Failed to insert record for log event in JDBC manager: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new LoggingException("Failed find field in loginfo", e);
        }
    }

    @Override
    protected void commitAndClose() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                if (this.isBatchSupported()) {
                    this.statement.executeBatch();
                }
                this.connection.commit();
            }
        } catch (SQLException e) {
            throw new LoggingException("Failed to commit transaction logging event or flushing buffer.", e);
        } finally {
            try {
                if (this.statement != null) {
                    this.statement.close();
                }
            } catch (Exception e) {
                // logWarn("failed to close SQL statement logging event or flushing buffer", e);
            } finally {
                this.statement = null;
            }

            try {
                if (this.connection != null) {
                    this.connection.close();
                }
            } catch (Exception e) {
                // logWarn("failed to close database connection logging event or flushing buffer", e);
            } finally {
                this.connection = null;
            }
        }
    }

    public boolean isBatchSupported() {
        return batchSupported;
    }

    @Override
    public void shutdown() {
    }

}
