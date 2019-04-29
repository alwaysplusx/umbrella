package com.harmony.umbrella.log.interceptor;

import com.harmony.umbrella.log.Level;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author wuxii
 */
@Getter
@Setter
@Builder
public class LoggingOperation {

    private String module;
    private String action;
    private String message;
    private Level level;
    private ExpressionOperation keyExpression;
    private Map<String, ExpressionOperation> binds;

}
