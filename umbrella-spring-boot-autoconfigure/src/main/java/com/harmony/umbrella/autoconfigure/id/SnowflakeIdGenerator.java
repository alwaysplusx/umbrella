package com.harmony.umbrella.autoconfigure.id;

import com.harmony.umbrella.core.IdGenerator;
import xyz.downgoon.snowflake.Snowflake;

public class SnowflakeIdGenerator implements IdGenerator<Long> {

    private final Snowflake snowflake;

    public SnowflakeIdGenerator(Snowflake snowflake) {
        this.snowflake = snowflake;
    }

    @Override
    public Long generateId() {
        return snowflake.nextId();
    }

}
