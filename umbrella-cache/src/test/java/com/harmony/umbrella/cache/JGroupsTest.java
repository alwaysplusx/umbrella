package com.harmony.umbrella.cache;

import java.net.URL;
import java.util.List;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

/**
 * @author wuxii@foxmail.com
 */
public class JGroupsTest extends ReceiverAdapter {

    private final static String CONFIG_XML = "/network.xml";

    JChannel channel;

    public JGroupsTest(JChannel channel) {
        this.channel = channel;
    }

    public static void main(String[] args) throws Exception {
        URL xml = JGroupsTest.class.getResource(CONFIG_XML);
        if (xml == null)
            xml = JGroupsTest.class.getClassLoader().getParent().getResource(CONFIG_XML);
        JChannel channel = new JChannel(xml);
        JGroupsTest receiver = new JGroupsTest(channel);
        channel.setReceiver(receiver);
        channel.connect("jgroups");
        channel.send(new Message(null, "Text".getBytes()));
        Thread.sleep(1000 * 10);
        channel.close();
    }

    @Override
    public void receive(Message msg) {
        System.out.println(channel.getAddress() + " receive message from " + msg.getSrc() + " message is " + new String(msg.getBuffer()));
    }

    @Override
    public void viewAccepted(View view) {
        StringBuffer sb = new StringBuffer("Group Members Changed, LIST: ");
        List<Address> addrs = view.getMembers();
        for (int i = 0; i < addrs.size(); i++) {
            if (i > 0)
                sb.append(',');
            sb.append(addrs.get(i).toString());
        }
        System.out.println(sb.toString());
    }
}
