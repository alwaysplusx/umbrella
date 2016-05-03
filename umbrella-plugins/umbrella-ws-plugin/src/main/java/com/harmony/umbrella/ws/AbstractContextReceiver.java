package com.harmony.umbrella.ws;

import com.harmony.umbrella.message.TypedMessageResolver;
import com.harmony.umbrella.ws.jaxws.JaxWsExecutor;
import com.harmony.umbrella.ws.support.ContextReceiver;

/**
 * JaxWs Context接受与消息处理的抽象
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractContextReceiver extends TypedMessageResolver<ContextMessage> implements ContextReceiver {

    /**
     * 接收后是否重新加载Context内元数据的标识符Key
     */
    public static final String RELOAD_CONTEXT = ContextReceiver.class.getName() + ".RELOAD_CONTEXT";

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

    protected ContextVisitor[] getContextVisitor() {
        return new ContextVisitor[0];
    }

    @Override
    public void receive(Context context) {
        getJaxWsExecutor().execute(reloadContext(context), getContextVisitor());
    }

    @Override
    public void process(ContextMessage message) {
        this.receive(message.getContext());
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
                context = new ContextWrapper(context, metadata);
            }
        }
        return context;
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
