package com.harmony.umbrella.autoconfigure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration;

/**
 * @author wuxii@foxmail.com
 */
@SpringBootApplication(exclude = {
        JpaRepositoriesAutoConfiguration.class,
        JsonbAutoConfiguration.class
})
public class TestApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TestApplication.class, args);
    }

}
