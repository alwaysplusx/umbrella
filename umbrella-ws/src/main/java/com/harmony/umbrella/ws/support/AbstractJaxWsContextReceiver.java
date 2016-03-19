/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.ws.support;

import java.util.ArrayList;
import java.util.List;

import com.harmony.umbrella.message.AbstractMessageResolver;
import com.harmony.umbrella.message.Message;
import com.harmony.umbrella.message.MessageResolver;
import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.ContextVisitor;
import com.harmony.umbrella.ws.Metadata;
import com.harmony.umbrella.ws.MetadataLoader;
import com.harmony.umbrella.ws.jaxws.JaxWsExecutor;

/**
 * JaxWs Context接受与消息处理的抽象
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractJaxWsContextReceiver extends AbstractMessageResolver<Context> implements MessageResolver, ContextReceiver {

    /**
     * 接收后是否重新加载Context内元数据的标识符Key
     */
    public static final String RELOAD_CONTEXT = ContextReceiver.class.getName() + ".RELOAD_CONTEXT";

    /**
     * 接收处理的周期访问者
     */
    protected final List<ContextVisitor> visitors = new ArrayList<ContextVisitor>();

    /**
     * 接收者是否重新reload{@linkplain Context}
     */
    protected boolean reload = true;

    /**
     * 真正的执行交互的执行工具
     * 
     * @return 交互执行者
     */
    protected abstract JaxWsExecutor getJaxWsExecutor();

    /**
     * 交互元数据的加载工具(非必须)
     * 
     * @return 交互元数据加载工具
     */
    protected MetadataLoader getMetadataLoader() {
        return null;
    }

    @Override
    public void receive(Context context) {
        getJaxWsExecutor().execute(reloadContext(context), visitors.toArray(new ContextVisitor[visitors.size()]));
    }

    @Override
    public boolean support(Message message) {
        return message instanceof ContextMessage;
    }

    @Override
    public void process(Context message) {
        this.receive(message);
    }
    
    @Override
    protected Context convert(Message message) {
        return ((ContextMessage) message).getContext();
    }

    /**
     * 重新加载{@linkplain Context}中的元数据,即重置元数据中的属性{@linkplain Metadata}
     * <p>
     * 只要满足一个条件则重新加载：
     * <ul>
     * <li>前提条件-配置的加载工具不为空{@linkplain #getMetadataLoader() MetadataLoader}
     * <li>设置{@linkplain #setReload(boolean) reload}为true
     * <li>在上下文中设置了重新加载标识{@linkplain #RELOAD_CONTEXT}
     * </ul>
     * 
     * @param context
     *            context
     * @return context after reload
     */
    protected Context reloadContext(Context context) {
        MetadataLoader loader = getMetadataLoader();
        if (loader != null && (reload || Boolean.valueOf(String.valueOf(context.get(RELOAD_CONTEXT))))) {
            Metadata metadata = loader.loadMetadata(context.getServiceInterface());
            if (metadata != null) {
                SimpleContext copyContext = new SimpleContext(context.getServiceInterface(), context.getMethodName());
                copyContext.setAddress(metadata.getAddress());
                copyContext.setUsername(metadata.getUsername());
                copyContext.setPassword(metadata.getPassword());
                copyContext.setConnectionTimeout(metadata.getConnectionTimeout());
                copyContext.setReceiveTimeout(metadata.getReceiveTimeout());

                copyContext.setParameters(context.getParameters());
                copyContext.putAll(context.getContextMap());
                return copyContext;
            }
        }
        return context;
    }

    /**
     * 设置执行周期访问者
     * 
     * @param visitors
     *            访问者
     */
    public void setContextVisitors(List<ContextVisitor> visitors) {
        this.visitors.clear();
        this.visitors.addAll(visitors);
    }

    /**
     * 增加执行周期访问者
     * 
     * @param visitor
     *            访问者
     * @return 增加成功标识
     */
    public boolean addPhaseVisitor(ContextVisitor visitor) {
        return this.visitors.add(visitor);
    }

    /**
     * 清楚周期访问者
     */
    public void clearVisitor() {
        this.visitors.clear();
    }

    /**
     * 是否重新加载context的标识
     * 
     * @param reload
     *            重加载标识
     */
    public void setReload(boolean reload) {
        this.reload = reload;
    }

}
