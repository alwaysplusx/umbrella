package com.harmony.umbrella.log.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.spi.LoggingEvent;

import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.LoggingException;
import com.harmony.umbrella.log.jdbc.JdbcAppender.Column;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractLog4jDatabaseManager extends AbstractDatabaseManager<LoggingEvent> {

    protected final List<Column> columns;
    protected final String sqlStatement;

    /*
     * scope in method {@linkplain #write(LogInfo, LoggingEvent)}
     */
    private Connection connection;
    private PreparedStatement statement;

    public AbstractLog4jDatabaseManager(int bufferSize, List<Column> columns, String sqlStatement) {
        super(bufferSize);
        this.sqlStatement = sqlStatement;
        this.columns = columns;
    }

    protected abstract Connection getConnection() throws SQLException;

    public boolean isBatchSupported() {
        return false;
    }

    @Override
    protected void connectAndStart() {
        try {
            this.connection = getConnection();
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
                column.setValue(statement, column.getProperty(logInfo));
            }

            if (this.isBatchSupported()) {
                this.statement.addBatch();
            } else if (this.statement.executeUpdate() == 0) {
                throw new LoggingException("No records inserted in database table for log event in JDBC manager.");
            }
        } catch (SQLException e) {
            throw new LoggingException("Failed to insert record for log event in JDBC manager: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new LoggingException("Failed find field in loginfo");
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

}
