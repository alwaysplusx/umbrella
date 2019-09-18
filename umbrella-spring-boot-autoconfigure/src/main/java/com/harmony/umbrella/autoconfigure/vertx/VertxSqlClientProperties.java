package com.harmony.umbrella.autoconfigure.vertx;

import io.vertx.sqlclient.SqlConnectOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "harmony.vertx.sql-client")
public class VertxSqlClientProperties {

    private PoolOptions pool = new PoolOptions();
    private ConnectOptions connect = new ConnectOptions();
    private String type;

    public PoolOptions getPool() {
        return pool;
    }

    public void setPool(PoolOptions pool) {
        this.pool = pool;
    }

    public ConnectOptions getConnect() {
        return connect;
    }

    public void setConnect(ConnectOptions connect) {
        this.connect = connect;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class PoolOptions extends io.vertx.sqlclient.PoolOptions {

    }

    public static class ConnectOptions extends SqlConnectOptions {

        @Override
        protected void init() {

        }

    }
}
