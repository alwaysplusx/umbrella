package com.harmony.umbrella.autoconfigure.lock;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author wuxii@foxmail.com
 */
@ConfigurationProperties(prefix = "harmony.lock")
public class LockProperties {

    private String type;

    private Zookeeper zookeeper = new Zookeeper();

    private Redis redis = new Redis();

    private Jdbc jdbc = new Jdbc();

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

        private String address = "localhost:9092";
        private Retry retry = new Retry();
        private Integer connectionTimeout;
        private Integer sessionTimeout;
        private Integer maxCloseWait;
        private String namespace;
        private String aclProvider;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Retry getRetry() {
            return retry;
        }

        public void setRetry(Retry retry) {
            this.retry = retry;
        }

        public Integer getConnectionTimeout() {
            return connectionTimeout;
        }

        public void setConnectionTimeout(Integer connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        public Integer getSessionTimeout() {
            return sessionTimeout;
        }

        public void setSessionTimeout(Integer sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
        }

        public Integer getMaxCloseWait() {
            return maxCloseWait;
        }

        public void setMaxCloseWait(Integer maxCloseWait) {
            this.maxCloseWait = maxCloseWait;
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getAclProvider() {
            return aclProvider;
        }

        public void setAclProvider(String aclProvider) {
            this.aclProvider = aclProvider;
        }

    }

    public static class Redis {

        private String registryKey = "spring-lock-registry";

        private Duration expireAfter = Duration.ofSeconds(60);

        public String getRegistryKey() {
            return registryKey;
        }

        public void setRegistryKey(String registryKey) {
            this.registryKey = registryKey;
        }

        public Duration getExpireAfter() {
            return expireAfter;
        }

        public void setExpireAfter(Duration expireAfter) {
            this.expireAfter = expireAfter;
        }
    }

    public static class Jdbc {
    }

    public static class Retry {

        /**
         * Forever, NTimes, UntilElapsed
         */
        private String type = RetryType.NTimes.name();
        private int interval = 100;
        private int times = 5;
        private int maxElapsedTime = 1000 * 60;

        public Retry() {
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getInterval() {
            return interval;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }

        public int getTimes() {
            return times;
        }

        public void setTimes(int times) {
            this.times = times;
        }

        public int getMaxElapsedTime() {
            return maxElapsedTime;
        }

        public void setMaxElapsedTime(int maxElapsedTime) {
            this.maxElapsedTime = maxElapsedTime;
        }

    }

    public enum RetryType {

        Forever, NTimes, UntilElapsed;

        static RetryType forName(String name) {
            RetryType[] types = RetryType.values();
            for (RetryType t : types) {
                if (t.name().equalsIgnoreCase(name)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknow retry type " + name);
        }
    }
}
