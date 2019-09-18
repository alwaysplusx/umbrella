package com.harmony.umbrella.autoconfigure.vertx;

import com.harmony.umbrella.autoconfigure.vertx.VertxSqlClientAutoConfiguration.MysqlClientConfiguration;
import com.harmony.umbrella.autoconfigure.vertx.VertxSqlClientAutoConfiguration.PostgresSqlClientConfiguration;
import com.harmony.umbrella.autoconfigure.vertx.VertxSqlClientProperties.ConnectOptions;
import com.harmony.umbrella.autoconfigure.vertx.VertxSqlClientProperties.PoolOptions;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnClass(SqlClient.class)
@EnableConfigurationProperties(VertxSqlClientProperties.class)
@Import({MysqlClientConfiguration.class, PostgresSqlClientConfiguration.class})
public class VertxSqlClientAutoConfiguration {

    @Configuration
    @ConditionalOnMissingBean(Pool.class)
    @ConditionalOnClass(MySQLPool.class)
    @ConditionalOnProperty(prefix = "harmony.vertx.sql-client", name = "type", havingValue = "mysql", matchIfMissing = true)
    static class MysqlClientConfiguration {

        private final VertxSqlClientProperties sqlClientProperties;

        MysqlClientConfiguration(VertxSqlClientProperties sqlClientProperties) {
            this.sqlClientProperties = sqlClientProperties;
        }

        @Bean({"pool", "mysqlPool"})
        public Pool pool() {
            ConnectOptions connectOptions = sqlClientProperties.getConnect();
            PoolOptions poolOptions = sqlClientProperties.getPool();
            return MySQLPool.pool(toMySQLConnectOptions(connectOptions), poolOptions);
        }

        MySQLConnectOptions toMySQLConnectOptions(ConnectOptions connectOptions) {
            return new MySQLConnectOptions(connectOptions.toJson());
        }

    }

    @Configuration
    @ConditionalOnMissingBean(Pool.class)
    @ConditionalOnClass(PgPool.class)
    @ConditionalOnProperty(prefix = "harmony.vertx.sql-client", name = "type", havingValue = "postgres", matchIfMissing = true)
    static class PostgresSqlClientConfiguration {

        private final VertxSqlClientProperties sqlClientProperties;

        PostgresSqlClientConfiguration(VertxSqlClientProperties sqlClientProperties) {
            this.sqlClientProperties = sqlClientProperties;
        }

        @Bean({"pool", "postgresPool"})
        public Pool pool() {
            ConnectOptions connectOptions = sqlClientProperties.getConnect();
            PoolOptions poolOptions = sqlClientProperties.getPool();
            return PgPool.pool(toPgConnectOptions(connectOptions), poolOptions);
        }

        PgConnectOptions toPgConnectOptions(ConnectOptions connectOptions) {
            return new PgConnectOptions(connectOptions.toJson());
        }

    }

}
