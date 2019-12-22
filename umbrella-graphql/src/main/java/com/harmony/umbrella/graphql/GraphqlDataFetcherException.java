package com.harmony.umbrella.graphql;

public class GraphqlDataFetcherException extends RuntimeException {

    public GraphqlDataFetcherException() {
        super();
    }

    public GraphqlDataFetcherException(String message) {
        super(message);
    }

    public GraphqlDataFetcherException(String message, Throwable cause) {
        super(message, cause);
    }

    public GraphqlDataFetcherException(Throwable cause) {
        super(cause);
    }

    protected GraphqlDataFetcherException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
