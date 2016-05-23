# 配置示例

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/"
  debug="false" reset="false" threshold="info">

  <appender name="console" class="org.apache.log4j.ConsoleAppender">
    <param name="target" value="System.out" />
    <param name="threshold" value="info" />
    <layout class="org.apache.log4j.PatternLayout">
      <param name="ConversionPattern" value="[%p] %c{1}.%M(%F:%L) - %m%n" />
    </layout>
  </appender>

  <appender name="jdbc" class="com.harmony.umbrella.log4j.jdbc.JdbcAppender">
    <param name="tableName" value="S_LOGINFO" />
    <param name="url" value="jdbc:h2:file:~/.h2/harmony/log" />
    <param name="user" value="sa" />
    <param name="password" value="" />
    <!-- columns字段配置信息 -->
    <!-- <param name="excludeFields" value="context" />
    <columns>
      <column source="module" target="module" />
      <column source="action" target="action" />
      <column source="message" target="message" />
      <column source="level" target="level" />
    </columns> -->
  </appender>

  <appender name="errorDailyFile" class="org.apache.log4j.DailyRollingFileAppender">
    <param name="threshold" value="error" />
    <param name="file" value="D:/temp/umbrella-error.log" />
    <param name="datePattern" value="'.'yyyy-MM-dd'.log'" />
    <layout class="org.apache.log4j.PatternLayout">
      <param name="ConversionPattern" value="[%p] %c{1}.%M(%F:%L) - %m%n" />
    </layout>
  </appender>

  <appender name="infoDailyFile" class="org.apache.log4j.DailyRollingFileAppender">
    <param name="threshold" value="info" />
    <param name="file" value="D:/temp/umbrella-info.log" />
    <param name="datePattern" value="'.'yyyy-MM-dd'.log'" />
    <layout class="org.apache.log4j.PatternLayout">
      <param name="ConversionPattern" value="[%p] %c{1}.%M(%F:%L) - %m%n" />
    </layout>
  </appender>

  <root>
    <level value="debug" />
    <appender-ref ref="console" />
    <!-- <appender-ref ref="jdbc" /> -->
    <!-- <appender-ref ref="infoDailyFile" /> -->
    <!-- <appender-ref ref="errorDailyFile" /> -->
  </root>

</log4j:configuration>
```