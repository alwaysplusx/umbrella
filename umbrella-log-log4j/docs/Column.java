package com.harmony.umbrella.log;

import java.lang.reflect.Method;

class Column {

    final boolean isTimestamp;
    final boolean isClob;
    final String name;
    final Method method;

    public Column(String name, Method method, boolean isTimestamp, boolean isClob) {
        this.isTimestamp = isTimestamp;
        this.isClob = isClob;
        this.name = name;
        this.method = method;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Column other = (Column) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}