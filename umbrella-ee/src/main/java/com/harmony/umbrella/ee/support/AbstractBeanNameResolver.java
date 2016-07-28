package com.harmony.umbrella.ee.support;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.harmony.umbrella.beans.BeansException;
import com.harmony.umbrella.ee.BeanDefinition;
import com.harmony.umbrella.ee.BeanNameResolver;
import com.harmony.umbrella.ee.SessionBean;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.AnnotationUtils;

/**
 * @author wuxii@foxmail.com
 */
public class AbstractBeanNameResolver implements BeanNameResolver {

    private static final Log log = Logs.getLog(AbstractBeanNameResolver.class);

    protected Properties contextProperties = new Properties();

    @Override
    public Context getContext() throws NamingException {
        return new InitialContext(contextProperties);
    }

    @Override
    public String[] guessNames(BeanDefinition bd) {
        return guessNames(bd, (Map<String, Object>) null);
    }

    @Override
    public String[] guessNames(BeanDefinition bd, Annotation... ann) {
        return guessNames(bd, annToMap(ann));
    }

    @Override
    public String[] guessNames(BeanDefinition bd, Map<String, Object> properties) {
        return guessNames(bd, properties, null);
    }

    protected String[] guessNames(BeanDefinition bd, Map<String, Object> properties, Context context) {
        if (context == null) {
            try {
                context = getContext();
            } catch (NamingException e) {
                log.warn("{}", e);
            }
        }
        JndiHolder holder = createJndiHolder(context);
        return holder.getJndis();
    }

    @Override
    public SessionBean[] guessBeans(BeanDefinition bd) {
        return guessBeans(bd, (Map<String, Object>) null);
    }

    @Override
    public SessionBean[] guessBeans(BeanDefinition bd, Annotation... ann) {
        return guessBeans(bd, annToMap(ann));
    }

    @Override
    public SessionBean[] guessBeans(BeanDefinition bd, Map<String, Object> properties) {
        try {
            Context context = getContext();
            String[] jndis = guessNames(bd, properties, context);
            for (String jndi : jndis) {
                tryLookup(jndi, context);
                // TODO check look up bean
            }
        } catch (NamingException e) {
            throw new BeansException(e);
        }
        return null;
    }

    protected final Object tryLookup(String jndi, Context context) {
        try {
            return context.lookup(jndi);
        } catch (NamingException e) {
            return null;
        }
    }

    private Map<String, Object> annToMap(Annotation[] ann) {
        Map<String, Object> properties = null;
        if (ann != null && ann.length > 0) {
            properties = new HashMap<String, Object>();
            for (Annotation a : ann) {
                properties.putAll(AnnotationUtils.toMap(a));
            }
        }
        return properties;
    }

    public Properties getContextProperties() {
        return contextProperties;
    }

    public void setContextProperties(Properties contextProperties) {
        this.contextProperties = contextProperties;
    }

    protected JndiHolder createJndiHolder(Context context) {
        return new JndiHolder(context);
    }

    protected class JndiHolder {

        protected final Context context;

        protected boolean forced;

        protected List<String> jndis = new ArrayList<String>();

        public JndiHolder(Context context) {
            this.context = context;
        }

        public void addIfAbsent(String jndi) {
            if (!jndis.contains(jndi) && (exists(jndi) || !forced)) {
                jndis.add(jndi);
            }
        }

        public boolean exists(String jndi) {
            return tryLookup(jndi, context) != null;
        }

        public final String[] getJndis() {
            String[] result = jndis.toArray(new String[jndis.size()]);
            Arrays.sort(result);
            return result;
        }

        public boolean isEmpty() {
            return jndis.isEmpty();
        }

    }

}
