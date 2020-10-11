package com.harmony.umbrella.query;

import java.io.Serializable;
import java.util.function.Function;

public interface PathFunction<DOMAIN, TYPE> extends Function<DOMAIN, TYPE>, Serializable {

}
