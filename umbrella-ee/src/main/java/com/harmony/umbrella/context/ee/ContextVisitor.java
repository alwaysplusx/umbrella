package com.harmony.umbrella.context.ee;

import javax.naming.Context;

public class ContextVisitor {

	private boolean isVisitEnd = false;

	public void visitContext(Context context, String jndi) {
	}

	public void visitBean(Object bean, String jndi) {
	}

	public void visitEnd() {
		isVisitEnd = true;
	}

	public boolean isVisitEnd() {
		return isVisitEnd;
	}

}