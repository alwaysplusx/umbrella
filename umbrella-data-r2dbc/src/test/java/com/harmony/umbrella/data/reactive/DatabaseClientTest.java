package com.harmony.umbrella.data.reactive;

import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.test.StepVerifier;

import static org.springframework.data.r2dbc.query.Criteria.where;

/**
 * @author wuxin
 */

@Slf4j
public class DatabaseClientTest {

    static DatabaseClient client;

    @BeforeClass
    public static void setup() {
        H2ConnectionConfiguration connConfig = H2ConnectionConfiguration
                .builder()
                .file("~/.h2/umbrella-data-r2dbc")
                .username("sa")
                .build();
        H2ConnectionFactory connectionFactory = new H2ConnectionFactory(connConfig);

        client = DatabaseClient
                .builder()
                .connectionFactory(connectionFactory)
                .build();
    }

    @Test
    public void test() {
        String initScript = "drop table if exists user;" +
                "create table user(id int primary key, username varchar(50));";

        client.execute(initScript)
                .then()
                .then(
                        client.insert()
                                .into(User.class)
                                .using(new User(1, "david"))
                                .then()
                )
                .then(
                        client.select()
                                .from(User.class)
                                .matching(where("id").is(1))
                                .fetch()
                                .first()
                )
                .doOnNext(e -> log.info("find user: {}", e))
                .then()
                .as(StepVerifier::create)
                .verifyComplete();

        client.execute("select * from user where id=:id")
                .bind("id", 1)
                .as(User.class)
                .fetch()
                .first()
                .doOnNext(e -> log.info("find user by sql query: {}", e))
                .then()
                .as(StepVerifier::create)
                .verifyComplete();

        // select * from user where id = 1 and (username like '%a' or username like 'a%')
    }

    @Test
    public void testQuery() {
        client.select()
                .from(User.class)
                .matching(where("id").is(1).and("username").like("david"))
                .fetch()
                .first()
                .doOnNext(e -> {
                    log.info("find result user: {}", e);
                })
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }

}

@AllArgsConstructor
@NoArgsConstructor
@ToString
@org.springframework.data.relational.core.mapping.Table("user")
class User {

    @Id
    int id;
    String username;

}
