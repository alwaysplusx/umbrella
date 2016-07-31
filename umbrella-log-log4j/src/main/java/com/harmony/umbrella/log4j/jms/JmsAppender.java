package com.harmony.umbrella.log4j.jms;

import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.xml.UnrecognizedElementHandler;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.support.StaticLogger;
import com.harmony.umbrella.log4j.AbstractAppender;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class JmsAppender extends AbstractAppender implements UnrecognizedElementHandler {

    private Destination destination;
    private ConnectionFactory connectionFactory;

    private String connectionFactoryName;
    private String destinationName;

    private String initialContextFactory;
    private String urlPkgPrefixes;
    private String providerURL;

    private Properties contextProperties = new Properties();

    @Override
    protected void init() {
        if (StringUtils.isNotBlank(initialContextFactory)) {
            contextProperties.setProperty(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
        }
        if (StringUtils.isNotBlank(providerURL)) {
            contextProperties.setProperty(Context.PROVIDER_URL, providerURL);
        }
        if (StringUtils.isNotBlank(urlPkgPrefixes)) {
            contextProperties.setProperty(Context.URL_PKG_PREFIXES, urlPkgPrefixes);
        }
    }

    @Override
    protected void append(LogInfo logInfo) {
        // TODO SEND JMS MESSAGE
    }

    @SuppressWarnings("unchecked")
    private <T> T lookup(String jndi) {
        Context ctx = null;
        try {
            ctx = new InitialContext();
            return (T) ctx.lookup(jndi);
        } catch (NamingException e) {
        }
        return null;
    }

    public void setInitialContextFactory(String initialContextFactory) {
        this.initialContextFactory = initialContextFactory;
    }

    public void setUrlPkgPrefixes(String urlPkgPrefixes) {
        this.urlPkgPrefixes = urlPkgPrefixes;
    }

    public void setProviderURL(String providerURL) {
        this.providerURL = providerURL;
    }

    @Override
    public boolean parseUnrecognizedElement(Element element, Properties props) throws Exception {
        String tagName = element.getTagName();
        if ("context".equalsIgnoreCase(tagName)) {
            NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node instanceof Element) {
                    String childTagName = ((Element) node).getTagName();
                    if ("property".equalsIgnoreCase(childTagName)) {
                        String name = ((Element) node).getAttribute("name");
                        String value = ((Element) node).getAttribute("value");
                        if (StringUtils.isEmpty(value)) {
                            value = node.getTextContent();
                        }
                        if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value)) {
                            contextProperties.setProperty(name, value);
                        }
                        continue;
                    }
                    StaticLogger.warn("unrecognized child element inner columns " + childTagName);
                }
            }
        }
        return false;
    }

}
