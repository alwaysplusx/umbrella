package com.harmony.umbrella.ws.visitor;

import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.ContextVisitor;
import com.harmony.umbrella.ws.WebServiceAbortException;

/**
 * 周期访问空实现
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractContextVisitor implements ContextVisitor {

    @Override
    public boolean visitBefore(Context context) throws WebServiceAbortException {
        return true;
    }

    @Override
    public void visitAbort(WebServiceAbortException ex, Context context) {
    }

    @Override
    public void visitCompletion(Object result, Context context) {
    }

    @Override
    public void visitThrowing(Throwable throwable, Context context) {
    }

    @Override
    public void visitFinally(Object result, Throwable throwable, Context context) {
    }

}
