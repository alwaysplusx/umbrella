package com.harmony.umbrella.i18n;

/**
 * @author wuxii@foxmail.com
 */
public class UnsupportedLanguageException extends RuntimeException {

    private static final long serialVersionUID = -6780424587805439294L;

    public UnsupportedLanguageException() {
        super();
    }

    public UnsupportedLanguageException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedLanguageException(String message) {
        super(message);
    }

    public UnsupportedLanguageException(Throwable cause) {
        super(cause);
    }

}
