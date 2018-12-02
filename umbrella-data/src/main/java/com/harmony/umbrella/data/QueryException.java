package com.harmony.umbrella.data;

/**
 * @author wuxii
 */
public class QueryException extends RuntimeException {

    private static final long serialVersionUID = -8875037779884298448L;

    public QueryException() {
        super();
    }

    public QueryException(String message) {
        super(message);
    }

    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryException(Throwable cause) {
        super(cause);
    }

}
