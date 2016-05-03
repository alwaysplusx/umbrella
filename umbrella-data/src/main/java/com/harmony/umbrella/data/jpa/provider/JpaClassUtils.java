package com.harmony.umbrella.data.jpa.provider;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Metamodel;

import com.harmony.umbrella.util.Assert;

/**
 * Utility class to work with classes.
 * 
 * @author Oliver Gierke
 */
abstract class JpaClassUtils {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private JpaClassUtils() {

	}

	/**
	 * Returns whether the given {@link EntityManager} is of the given type.
	 * 
	 * @param em
	 *            must not be {@literal null}.
	 * @param type
	 *            the fully qualified expected {@link EntityManager} type, must
	 *            not be {@literal null} or empty.
	 * @return
	 */
	public static boolean isEntityManagerOfType(EntityManager em, String type) {
		return isOfType(em, type, em.getDelegate().getClass().getClassLoader());
	}

	public static boolean isMetamodelOfType(Metamodel metamodel, String type) {
		return isOfType(metamodel, type, metamodel.getClass().getClassLoader());
	}

	private static boolean isOfType(Object source, String typeName, ClassLoader classLoader) {
		Assert.notNull(source, "Source instance must not be null!");
		Assert.hasText(typeName, "Target type name must not be null or empty!");
		try {
			Class.forName(typeName, false, classLoader).cast(source);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}