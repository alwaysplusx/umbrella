package com.harmony.umbrella.query;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Path<T> {

    @NotNull
    String getColumn();

    @Nullable
    Class<T> getDomainClass();

}
