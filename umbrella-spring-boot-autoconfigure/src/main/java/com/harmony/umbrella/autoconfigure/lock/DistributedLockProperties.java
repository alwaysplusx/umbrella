package com.harmony.umbrella.autoconfigure.lock;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wuxii@foxmail.com
 */
@ConfigurationProperties(prefix = "harmony.lock")
public class DistributedLockProperties {

    private LockType type;

    private Zookeeper zookeeper;

    private Redis redis;

    private Jdbc jdbc;

    public LockType getType() {
        return type;
    }

    public void setType(LockType type) {
        this.type = type;
    }

    public Zookeeper getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(Zookeeper zookeeper) {
        this.zookeeper = zookeeper;
    }

    public Redis getRedis() {
        return redis;
    }

    public void setRedis(Redis redis) {
        this.redis = redis;
    }

    public Jdbc getJdbc() {
        return jdbc;
    }

    public void setJdbc(Jdbc jdbc) {
        this.jdbc = jdbc;
    }

    public static class Zookeeper {

    }

    public static class Redis {
    }

    public static class Jdbc {
    }

}
