package com.harmony.umbrella.log4j.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.spi.LoggingEvent;

import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.LoggingException;
import com.harmony.umbrella.log.jdbc.AbstractDatabaseManager;
import com.harmony.umbrella.log.jdbc.ConnectionSource;

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

            for (Column column : columns) {
                column.setStatementValue(statement, logInfo);
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
