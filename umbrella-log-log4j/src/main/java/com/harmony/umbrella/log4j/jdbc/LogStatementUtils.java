package com.harmony.umbrella.log4j.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;

import com.harmony.umbrella.log.Level;
import com.harmony.umbrella.log.Message;

/**
 * @author wuxii@foxmail.com
 */
public class LogStatementUtils {

    public static void setValue(PreparedStatement pstmt, int index, Integer sqlType, Object value) throws SQLException {
        if (sqlType == null) {
            setValue(pstmt, index, value);
        } else {
            pstmt.setObject(index, value, sqlType);
        }
    }

    public static void setValue(PreparedStatement pstmt, int index, Object value) throws SQLException {
        if (value == null) {
            pstmt.setNull(index, Types.NULL);
        } else if (value instanceof String) {
            pstmt.setString(index, (String) value);
        } else if (value instanceof Message) {
            pstmt.setString(index, ((Message) value).getFormattedMessage());
        } else if (value instanceof Throwable) {
            pstmt.setString(index, value.toString());
        } else if (value instanceof Level) {
            pstmt.setString(index, ((Level) value).getName());
        } else if (value instanceof Long) {
            pstmt.setLong(index, (Long) value);
        } else if (value instanceof Boolean) {
            pstmt.setBoolean(index, (Boolean) value);
        } else if (value instanceof Date) {
            pstmt.setTimestamp(index, new Timestamp(((Date) value).getTime()));
        } else if (value instanceof Calendar) {
            pstmt.setTimestamp(index, new Timestamp(((Calendar) value).getTimeInMillis()));
        } else if (value instanceof Enum<?>) {
            pstmt.setString(index, ((Enum<?>) value).name());
        } else {
            pstmt.setObject(index, value);
        }
    }

}
