/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
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

    public String getName() {
        return name;
    }

    public int intLevel() {
        return standardLevel.intLevel;
    }

    public StandardLevel getStandardLevel() {
        return standardLevel;
    }

    public boolean isInRange(Level minLevel, Level maxLevel) {
        return this.standardLevel.isInRange(minLevel.standardLevel, maxLevel.standardLevel);
    }

    public boolean isLessSpecificThan(Level level) {
        return this.standardLevel.isLessSpecificThan(level.standardLevel);
    }

    public boolean isMoreSpecificThan(Level level) {
        return this.standardLevel.isMoreSpecificThan(level.standardLevel);
    }

    public static Level toLevel(String level) {
        return toLevel(level, DEBUG);
    }

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
