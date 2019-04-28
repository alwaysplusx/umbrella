package com.harmony.umbrella.log;

public enum Level {

    OFF(0),
    ERROR(100),
    WARN(200),
    INFO(300),
    DEBUG(400),
    TRACE(500),
    ALL(Integer.MAX_VALUE);

    private int intLevel;

    Level(int intLevel) {
        this.intLevel = intLevel;
    }

    public int intLevel() {
        return intLevel;
    }

    public boolean isInRange(Level minLevel, Level maxLevel) {
        return this.intLevel >= minLevel.intLevel && this.intLevel <= maxLevel.intLevel;
    }

    public boolean isLessSpecificThan(Level level) {
        return this.intLevel >= level.intLevel;
    }

    public boolean isMoreSpecificThan(Level level) {
        return this.intLevel <= level.intLevel;
    }

}