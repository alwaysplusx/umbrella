package com.harmony.umbrella.log4j2.filter;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ObjectMessage;

import com.harmony.umbrella.log.LogInfo;

/**
 * @author wuxii@foxmail.com
 */
@Plugin(name = "TypeFilter", category = Node.CATEGORY, elementType = Filter.ELEMENT_TYPE, printObject = true)
public class TypeFilter extends AbstractFilter {

    @Override
    public Result filter(LogEvent event) {
        Message message = event.getMessage();
        if (message instanceof ObjectMessage //
                && message.getParameters().length > 0 //
                && message.getParameters()[0] instanceof LogInfo) {
            return Result.ACCEPT;
        }
        return super.filter(event);
    }

}
