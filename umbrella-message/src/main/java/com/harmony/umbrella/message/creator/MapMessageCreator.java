package com.harmony.umbrella.message.creator;

import java.io.Serializable;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import com.harmony.umbrella.message.MessageType;

public class MapMessageCreator extends AbstractMessageCreator<MapMessage> {

    private static final long serialVersionUID = 2091868113420068661L;
    protected Map map;
    protected boolean skipNotSatisfiedEntry;

    public MapMessageCreator(Map map, boolean skipNotStatisfiedEntry) {
        super(MessageType.MapMessage);
        this.map = map;
        this.skipNotSatisfiedEntry = skipNotStatisfiedEntry;
    }

    @Override
    protected void doMapping(MapMessage message) throws JMSException {
        for (Object key : map.keySet()) {
            Object value = map.get(key);
            if (key instanceof String && value instanceof Serializable) {
                message.setObject((String) key, map.get(key));
            } else if (!skipNotSatisfiedEntry) {
                throw new IllegalArgumentException(key + "=" + value + " is invalid jms message entry");
            }
        }
    }

    public boolean isSkipNotSatisfiedEntry() throws JMSException {
        return skipNotSatisfiedEntry;
    }

    public void setSkipNotSatisfiedEntry(boolean skipNotSatisfiedEntry) {
        this.skipNotSatisfiedEntry = skipNotSatisfiedEntry;
    }

}