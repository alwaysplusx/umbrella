package com.harmony.umbrella.log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 日志级别
 * 
 * @author wuxii@foxmail.com
 */
public class Level {

    private static final ConcurrentMap<String, Level> LEVELS = new ConcurrentHashMap<String, Level>();

    public static final Level TRACE;

    public static final Level DEBUG;

    public static final Level INFO;

    public static final Level WARN;

    public static final Level ERROR;

    static {
        TRACE = new Level(StandardLevel.TRACE.intLevel, "TRACE");
        DEBUG = new Level(StandardLevel.DEBUG.intLevel, "DEBUG");
        INFO = new Level(StandardLevel.INFO.intLevel, "INFO");
        WARN = new Level(StandardLevel.WARN.intLevel, "WARN");
        ERROR = new Level(StandardLevel.ERROR.intLevel, "ERROR");
    }

    protected String name;
    protected StandardLevel standardLevel;

    protected Level(int intLevel, String name) {
        this.name = name;
        this.standardLevel = StandardLevel.valueOf(intLevel);
        LEVELS.putIfAbsent(name, this);
    }

    /**
     * 级别的名称
     * 
     * @return 级别的名称
     */
    public String getName() {
        return name;
    }

    /**
     * 级别的权重
     * 
     * @return 级别的权重
     */
    public int intLevel() {
        return standardLevel.intLevel;
    }

    /**
     * 级别的标准类型
     * 
     * @return 级别的标准类型
     */
    public StandardLevel getStandardLevel() {
        return standardLevel;
    }

    /**
     * 判断是否在对应的级别内
     * 
     * @param minLevel
     *            最小级别点
     * @param maxLevel
     *            最大级别点
     * @return true在对应的级别内
     */
    public boolean isInRange(Level minLevel, Level maxLevel) {
        return this.standardLevel.isInRange(minLevel.standardLevel, maxLevel.standardLevel);
    }

    /**
     * 是否小于对应的级别
     * 
     * @param level
     *            级别
     * @return true or false
     */
    public boolean isLessSpecificThan(Level level) {
        return this.standardLevel.isLessSpecificThan(level.standardLevel);
    }

    /**
     * 是否大于对应的级别
     * 
     * @param level
     *            级别
     * @return true or false
     */
    public boolean isMoreSpecificThan(Level level) {
        return this.standardLevel.isMoreSpecificThan(level.standardLevel);
    }

    /**
     * 根据名称得到级别
     * 
     * @param level
     *            级别
     * @return Level
     */
    public static Level toLevel(String level) {
        return toLevel(level, DEBUG);
    }

    /**
     * 根据名称获取对应的级别,如果未找到返回默认值
     * 
     * @param name
     *            级别名称
     * @param defaultLevel
     *            默认级别
     * @return Level
     */
    public static Level toLevel(String name, Level defaultLevel) {
        if (name == null) {
            return defaultLevel;
        }
        name = name.toUpperCase();

        Level level = LEVELS.get(name);
        if (level != null) {
            return level;
        }

        return defaultLevel;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * 标准级别
     * 
     * @author wuxii@foxmail.com
     */
    public static enum StandardLevel {

        OFF(0), ERROR(100), WARN(200), INFO(300), DEBUG(400), TRACE(500), ALL(Integer.MAX_VALUE);

        private int intLevel;

        private StandardLevel(int intLevel) {
            this.intLevel = intLevel;
        }

        public int intLevel() {
            return intLevel;
        }

        public boolean isInRange(StandardLevel minLevel, StandardLevel maxLevel) {
            return this.intLevel >= minLevel.intLevel && this.intLevel <= maxLevel.intLevel;
        }

        public boolean isLessSpecificThan(StandardLevel level) {
            return this.intLevel >= level.intLevel;
        }

        public boolean isMoreSpecificThan(StandardLevel level) {
            return this.intLevel <= level.intLevel;
        }

        public static StandardLevel valueOf(int intLevel) {
            for (StandardLevel l : StandardLevel.values()) {
                if (l.intLevel == intLevel) {
                    return l;
                }
            }
            return StandardLevel.OFF;
        }
    }

}
