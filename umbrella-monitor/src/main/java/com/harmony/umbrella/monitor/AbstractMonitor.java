package com.harmony.umbrella.monitor;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.Assert;

/**
 * 监控基础抽象类
 *
 * @param <T>
 *            监控的资源类型，由子类指定
 * @author wuxii@foxmail.com
 */
public abstract class AbstractMonitor<T> implements com.harmony.umbrella.monitor.Monitor<T> {

    protected static final Log LOG = Logs.getLog(AbstractMonitor.class);

    public static final MonitorPolicy DEFAULT_POLICY = MonitorPolicy.All;

    protected final Set<String> whitePattern = new CopyOnWriteArraySet<String>();

    protected final Set<String> blackPattern = new CopyOnWriteArraySet<String>();

    protected final Set<T> whiteResource = new CopyOnWriteArraySet<T>();

    protected final Set<T> blackResource = new CopyOnWriteArraySet<T>();

    /**
     * 监控策略
     */
    protected MonitorPolicy policy = DEFAULT_POLICY;

    /**
     * 创建资源匹配器
     * <p/>
     * 通过模版路径创建资源匹配工具
     *
     * @return 模版资源匹配工具
     */
    protected abstract ResourceMatcher<T> getResourceMatcher();

    @Override
    public MonitorPolicy getPolicy() {
        return policy;
    }

    @Override
    public void setPolicy(MonitorPolicy policy) {
        Assert.notNull(policy, "cant' t set null to policy");
        this.policy = policy;
    }

    @Override
    public boolean isMonitored(T resource) {
        switch (policy) {
        case Skip:
            return false;
        case All:
            return true;
        case WhiteList:
            // whitelist 资源匹配不监控
            if (whiteResource.contains(resource)) {
                return false;
            }
            // 与模版匹配不监控
            for (String pattern : whitePattern) {
                if (getResourceMatcher().matches(pattern, resource)) {
                    return false;
                }
            }
            return true;
        case BlackList:
            // blocklist 资源匹配监控
            if (blackResource.contains(resource)) {
                return true;
            }
            for (String pattern : blackPattern) {
                if (getResourceMatcher().matches(pattern, resource)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public Set<T> getResources(MonitorPolicy policy) {
        if (policy == null || policy == MonitorPolicy.All || policy == MonitorPolicy.Skip) {
            return Collections.emptySet();
        }
        if (policy == MonitorPolicy.BlackList) {
            return blackResource;
        } else if (policy == MonitorPolicy.WhiteList) {
            return whiteResource;
        }
        return Collections.emptySet();
    }

    @Override
    public Set<String> getPattern(MonitorPolicy policy) {
        if (policy == null || policy == MonitorPolicy.All || policy == MonitorPolicy.Skip) {
            return Collections.emptySet();
        }
        if (policy == MonitorPolicy.BlackList) {
            return blackPattern;
        } else if (policy == MonitorPolicy.WhiteList) {
            return whitePattern;
        }
        return Collections.emptySet();
    }

    public void reset() {
        this.policy = DEFAULT_POLICY;
        clear();
    }

    public void clear() {
        whitePattern.clear();
        whiteResource.clear();
        blackPattern.clear();
        blackResource.clear();
    }

    @Override
    public void destroy() {
    }

}
