package com.harmony.umbrella.message;

import java.io.Serializable;

import javax.jms.MessageListener;

/**
 * 此类只有在配合{@linkplain MessageTemplate#startMessageListener(javax.jms.MessageListener)}时候才有效
 * 
 * @author wuxii@foxmail.com
 */
public interface TypedMessageListener<TYPE extends Serializable> extends MessageListener {

}
