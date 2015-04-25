package com.harmony.umbrella.context.ee;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * @author wuxii@foxmail.com
 */
public class ContextReader {

	private final Context context;

	public ContextReader(Context context) {
		this.context = context;
	}

	public void accept(ContextVisitor visitor, String contextRoot) {
		if (visitor.isVisitEnd()) {
			return;
		}
		try {
			Object obj = context.lookup(contextRoot);
			if (obj instanceof Context) {
				Context ctx = (Context) obj;
				visitor.visitContext(ctx, contextRoot);
				NamingEnumeration<NameClassPair> ncps = ctx.list("");
				while (ncps.hasMoreElements()) {
					String jndi = contextRoot + ("".equals(contextRoot) ? "" : "/") + ncps.next().getName();
					accept(visitor, jndi);
				}
			} else {
				visitor.visitBean(obj, contextRoot);
			}
		} catch (NamingException e) {
		}
	}

}