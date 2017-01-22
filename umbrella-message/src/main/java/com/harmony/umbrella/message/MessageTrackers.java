package com.harmony.umbrella.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jms.Message;

/**
 * @author wuxii@foxmail.com
 */
public class MessageTrackers implements Iterable<MessageTracker>, MessageTracker {

    private List<MessageTracker> trackers = new ArrayList<>();

    public MessageTrackers() {
    }

    public MessageTrackers(Collection<MessageTracker> c) {
        this.trackers.addAll(c);
    }

    @Override
    public Iterator<MessageTracker> iterator() {
        return trackers.iterator();
    }

    public void addAll(MessageTracker[] trackers) {
        this.addAll(Arrays.asList(trackers));
    }

    public void addAll(Collection<MessageTracker> trackers) {
        this.trackers.addAll(trackers);
    }

    public void add(MessageTracker tracker) {
        trackers.add(tracker);
    }

    public void add(int index, MessageTracker tracker) {
        trackers.add(index, tracker);
    }

    public MessageTracker remove(int index) {
        return trackers.remove(index);
    }

    public MessageTracker get(int index) {
        return trackers.get(index);
    }

    public void remove(MessageTracker tracker) {
        trackers.remove(tracker);
    }

    @Override
    public void onBeforeSend(Message message) {
        Iterator<MessageTracker> it = iterator();
        while (it.hasNext()) {
            it.next().onBeforeSend(message);
        }
    }

    @Override
    public void onAfterSend(Message message) {
        Iterator<MessageTracker> it = iterator();
        while (it.hasNext()) {
            it.next().onAfterSend(message);
        }
    }

    @Override
    public void onBeforeConsume(Message message) {
        Iterator<MessageTracker> it = iterator();
        while (it.hasNext()) {
            it.next().onBeforeConsume(message);
        }
    }

    @Override
    public void onAfterConsume(Message message) {
        Iterator<MessageTracker> it = iterator();
        while (it.hasNext()) {
            it.next().onAfterConsume(message);
        }
    }

    @Override
    public void onSendException(Message message, Exception ex) {
        Iterator<MessageTracker> it = iterator();
        while (it.hasNext()) {
            it.next().onSendException(message, ex);
        }
    }

    @Override
    public void onConsumeException(Message message, Exception ex) {
        Iterator<MessageTracker> it = iterator();
        while (it.hasNext()) {
            it.next().onConsumeException(message, ex);
        }
    }

}
