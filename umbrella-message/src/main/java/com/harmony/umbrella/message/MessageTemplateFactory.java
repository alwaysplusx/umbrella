package com.harmony.umbrella.message;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;

/**
 * @author wuxii@foxmail.com
 */
public interface MessageTemplateFactory {

    /**
     * 创建{@linkplain MessageTemplate}
     * 
     * @param templateCfg
     *            template config
     * @return message template
     */
    MessageTemplate createMessageTemplate(TemplateConfig templateCfg);

    /**
     * @author wuxii@foxmail.com
     */
    public interface TemplateConfig {

        /**
         * jms连接工厂
         * 
         * @return jms连接工厂
         */
        ConnectionFactory getConnectionFactory();

        /**
         * 消息的目的地
         * 
         * @return 消息的目的地
         */
        Destination getDestination();

        /**
         * 创建的messageTemplate是否具有监控效果
         * 
         * @return monitor flag
         */
        MessageMonitor getMessageMonitor();

        /**
         * 设置连接异常的监听器
         * 
         * @return exception listener
         * @see Connection#setExceptionListener(ExceptionListener)
         */
        ExceptionListener getExceptionListener();

        /**
         * 创建连接时候用的用户名密码
         * 
         * @return 用户名
         * @see ConnectionFactory#createConnection(String, String)
         */
        String getUsername();

        /**
         * 创建连接时候用的用户名密码
         * 
         * @return 用户密码
         * @see ConnectionFactory#createConnection(String, String)
         */
        String getPassword();

        /**
         * indicates whether the session will use a local transaction. If this method is called in the Java EE web or
         * EJB container then this argument is ignored.
         * 
         * @return session transacted
         * @see Connection#createSession(int)
         */
        boolean isTransacted();

        /**
         * indicates how messages received by the session will be acknowledged.
         * <ul>
         * <li>If this method is called in a Java SE environment or in the Java EE application client container, the
         * permitted values are {@code Session.CLIENT_ACKNOWLEDGE}, {@code Session.AUTO_ACKNOWLEDGE} and
         * {@code Session.DUPS_OK_ACKNOWLEDGE}.
         * <li>If this method is called in the Java EE web or EJB container when there is an active JTA transaction in
         * progress then this argument is ignored.
         * <li>If this method is called in the Java EE web or EJB container when there is no active JTA transaction in
         * progress, the permitted values are {@code Session.AUTO_ACKNOWLEDGE} and {@code Session.DUPS_OK_ACKNOWLEDGE}.
         * In this case {@code Session.CLIENT_ACKNOWLEDGE} is not permitted.
         * 
         * @return session mode
         * @see Connection#createSession(boolean, int)
         */
        int getSessionMode();

        /**
         * jms template中判断消息会话是否自动提交的标志位
         * 
         * @return
         * @see JmsTemplate#isSessionAutoCommit()
         */
        boolean isSessionAutoCommit();

        /**
         * 消费消息默认的messageSelector
         * 
         * @return messageSelector
         */
        String getMessageSelector();

    }

}
