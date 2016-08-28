package com.harmony.umbrella.log.message;

import com.harmony.umbrella.log.Message;
import com.harmony.umbrella.log.MessageFactory;


/**
 * Enables use of <code>{}</code> parameter markers in message strings.
 * <p>
 * Creates {@link ParameterizedMessage} instances for
 * {@link #newMessage(String, Object...)}.
 * </p>
 * <p>
 * This class is immutable.
 * </p>
 */
public final class ParameterizedMessageFactory extends AbstractMessageFactory {

    /**
     * Instance of StringFormatterMessageFactory.
     */
    public static final ParameterizedMessageFactory INSTANCE = new ParameterizedMessageFactory();

    private static final long serialVersionUID = 1L;

    /**
     * Creates {@link ParameterizedMessage} instances.
     * 
     * @param message
     *            The message pattern.
     * @param params
     *            The message parameters.
     * @return The Message.
     *
     * @see MessageFactory#newMessage(String, Object...)
     */
    @Override
    public Message newMessage(final String message, final Object... params) {
        return new ParameterizedMessage(message, params);
    }
}
