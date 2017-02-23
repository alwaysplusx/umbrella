package com.harmony.umbrella.cache.channel;

import java.util.Map;

import com.harmony.umbrella.cache.AbstractCacheChannel;
import com.harmony.umbrella.cache.CacheException;
import com.harmony.umbrella.cache.CacheManager;
import com.harmony.umbrella.cache.Command;
import com.harmony.umbrella.cache.redis.RedisCacheProvider;
import com.harmony.umbrella.cache.redis.RedisCacheProxy;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.util.SafeEncoder;

/**
 * 缓存Redis PUB/SUB监听通道
 *
 * @author flyfox 330627517@qq.com
 */
public class RedisCacheChannel extends AbstractCacheChannel {

    private static final Log log = Logs.getLog(RedisCacheChannel.class);

    private String channel;
    private CacheManager cacheManager;
    private RedisCacheProxy redisCacheProxy;

    private RedisMessageHandler messageHandler = new RedisMessageHandler();

    private Thread thread_subscribe;

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
        this.redisCacheProxy = new RedisCacheProvider().getResource();
        this.channel = (String) properties.get("redis.channel_name");
        thread_subscribe = new Thread(new Runnable() {
            @Override
            public void run() {
                long ct = System.currentTimeMillis();
                redisCacheProxy.subscribe(messageHandler, SafeEncoder.encode(channel));
                log.info("Connected to channel:" + channel + ", time " + (System.currentTimeMillis() - ct) + " ms.");
            }
        });
        try {
            thread_subscribe.start();
        } catch (Exception e) {
            throw new CacheException("unable connection redis", e);
        }
    }

    @Override
    public void close() {
        super.close();
        if (messageHandler.isSubscribed()) {
            messageHandler.unsubscribe();
        }
        thread_subscribe.interrupt();
    }

    @Override
    public void stop() {
        close();
    }

    private final class RedisMessageHandler extends BinaryJedisPubSub implements MessagePublish, MessageSubscriber {

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
            redisCacheProxy.publish(SafeEncoder.encode(channel), cmd.toBuffers());
        }

        @Override
        public void onMessage(byte[] channel, byte[] message) {
            // 无效消息
            if (message != null && message.length <= 0) {
                log.warn("Message is empty.");
                return;
            }
            Command cmd = Command.parse(message);
            if (cmd == null || cmd.isLocalCommand()) {
                return;
            }
            onMessage(cmd);
        }
    }

}
