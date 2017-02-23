package com.harmony.umbrella.cache.channel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import com.harmony.umbrella.cache.AbstractCacheChannel;
import com.harmony.umbrella.cache.CacheException;
import com.harmony.umbrella.cache.CacheManager;
import com.harmony.umbrella.cache.Command;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * 使用 JGroups 进行组播
 * 
 * @author winterlau
 */
public class JGroupsCacheChannel extends AbstractCacheChannel {

    private static final Log log = Logs.getLog(JGroupsCacheChannel.class);

    private static final String JGROUPS_CONFIG = "jgroups.config";
    private static final String JGROUPS_CLUSTER = "jgroups.channel_name";

    private CacheManager cacheManager;
    private JChannel channel;

    private JGroupMessageHandler messageHandler = new JGroupMessageHandler();

    @Override
    protected CacheManager getCacheManager() {
        return cacheManager;
    }

    @Override
    protected MessagePublish getMessagePublish() {
        return messageHandler;
    }

    @Override
    public void start(Map properties) {
        this.cacheManager = CacheManager.getInstance();
        this.cacheManager.init(properties);
        InputStream is = null;
        try {
            is = getJGroupConfig(properties);
            channel = new JChannel(is);
            channel.setReceiver(messageHandler);
            Object clusterName = properties.get(JGROUPS_CLUSTER);
            channel.connect(clusterName == null ? "defaults" : clusterName.toString());
        } catch (Exception e) {
            throw new CacheException("unable start jgroup channel", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
    }

    private InputStream getJGroupConfig(Map properties) throws IOException {
        Object config = properties.get(JGROUPS_CONFIG);
        if (config == null) {
            config = "/network.xml";
        }
        if (config instanceof String) {
            return new DefaultResourceLoader().getResource((String) config).getInputStream();
        } else if (config instanceof Resource) {
            return ((Resource) config).getInputStream();
        } else if (config instanceof InputStream) {
            return (InputStream) config;
        } else if (config instanceof File) {
            return new FileInputStream((File) config);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public void close() {
        super.close();
        channel.close();
    }

    @Override
    public void stop() {
        close();
    }

    private final class JGroupMessageHandler extends ReceiverAdapter implements MessagePublish, MessageSubscriber {

        @Override
        public void onMessage(Command cmd) {
            try {
                switch (cmd.getOperator()) {
                case Command.OPT_DELETE_KEY:
                    deleteCacheKey(cmd.getRegion(), cmd.getKey());
                    break;
                case Command.OPT_CLEAR_KEY:
                    clearCacheKey(cmd.getRegion());
                    break;
                default:
                    log.warn("Unknown message command " + cmd);
                }
            } catch (Exception e) {
                log.error("Unable to handle received msg", e);
            }
        }

        @Override
        public void publish(Command cmd) {
            try {
                channel.send(new Message(null, null, cmd.toBuffers()));
            } catch (Exception e) {
                log.warn("unable publish command, " + cmd);
            }
        }

        @Override
        public void receive(Message msg) {
            byte[] buffers = msg.getBuffer();
            if (buffers.length < 1) {
                log.warn("Message is empty.");
                return;
            }
            if (msg.getSrc().equals(channel.getAddress())) {
                return;
            }
            Command cmd = Command.parse(buffers);
            if (cmd == null) {
                return;
            }
            onMessage(cmd);
        }

        /**
         * 组中成员变化时
         * 
         * @param view
         *            group view
         */
        public void viewAccepted(View view) {
            StringBuffer sb = new StringBuffer("Group Members Changed, LIST: ");
            List<Address> addrs = view.getMembers();
            for (int i = 0; i < addrs.size(); i++) {
                if (i > 0)
                    sb.append(',');
                sb.append(addrs.get(i).toString());
            }
            log.info(sb.toString());
        }
    }

}
