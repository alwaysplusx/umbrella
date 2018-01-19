package com.harmony.umbrella.autoconfigure.log;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.harmony.umbrella.log.LogProvider;
import com.harmony.umbrella.log.spi.Log4j2LogProvider;
import com.harmony.umbrella.log.spi.Log4jLogProvider;

/**
 * @author wuxii@foxmail.com
 */
@ConfigurationProperties(prefix = "harmony.log")
public class LogProperties {

    private LoggerType type;
    private String level;

    public LoggerType getType() {
        return type;
    }

    public void setType(LoggerType type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public static enum LoggerType {
        log4j {

            @Override
            public LogProvider provider() {
                return new Log4jLogProvider();
            }
        },
        log4j2 {

            @Override
            public LogProvider provider() {
                return new Log4j2LogProvider();
            }
        }/*,
        slf4j {

            @Override
            public LogProvider provider() {
                return new Slf4jLogProvider();
            }
        },
        commons_log {

            @Override
            public LogProvider provider() {
                return new CommonsLogProvider();
            }
        }*/;

        public abstract LogProvider provider();

    }

}
