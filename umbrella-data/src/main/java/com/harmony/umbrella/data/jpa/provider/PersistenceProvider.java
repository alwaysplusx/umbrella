package com.harmony.umbrella.data.jpa.provider;

import static com.harmony.umbrella.data.jpa.provider.JpaClassUtils.*;
import static com.harmony.umbrella.data.jpa.provider.PersistenceProvider.Constants.*;

import java.util.Arrays;
import java.util.Collections;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.metamodel.Metamodel;

import org.eclipse.persistence.jpa.JpaQuery;
import org.hibernate.jpa.HibernateQuery;
import org.hibernate.proxy.HibernateProxy;

import com.harmony.umbrella.util.Assert;

/**
 * Enumeration representing persistence providers to be used.
 * 
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
public enum PersistenceProvider {

	/**
	 * Hibernate persistence provider. <p> Since Hibernate 4.3 the location of the
	 * HibernateEntityManager moved to the org.hibernate.jpa package. In order to support
	 * both locations we interpret both classnames as a Hibernate
	 * {@code PersistenceProvider}.
	 * 
	 * @see DATAJPA-444
	 */
	HIBERNATE(//
			Arrays.asList(HIBERNATE43_ENTITY_MANAGER_INTERFACE, HIBERNATE_ENTITY_MANAGER_INTERFACE), //
			Arrays.asList(HIBERNATE43_JPA_METAMODEL_TYPE, HIBERNATE_JPA_METAMODEL_TYPE)) {

		public String extractQueryString(Query query) {
			return ((HibernateQuery) query).getHibernateQuery().getQueryString();
		}

		/**
		 * Return custom placeholder ({@code *}) as Hibernate does create invalid queries
		 * for count queries for objects with compound keys.
		 * 
		 * @see HHH-4044
		 * @see HHH-3096
		 */
		@Override
		public String getCountQueryPlaceholder() {
			return "*";
		}

		// @Override
		// public boolean shouldUseAccessorFor(Object entity) {
		// return entity instanceof HibernateProxy;
		// }

		@Override
		public Object getIdentifierFrom(Object entity) {
			return ((HibernateProxy) entity).getHibernateLazyInitializer().getIdentifier();
		}

	},

	/**
	 * EclipseLink persistence provider.
	 */
	ECLIPSELINK(//
			Collections.singleton(ECLIPSELINK_ENTITY_MANAGER_INTERFACE), //
			Collections.singleton(ECLIPSELINK_JPA_METAMODEL_TYPE)) {

		public String extractQueryString(Query query) {
			return ((JpaQuery<?>) query).getDatabaseQuery().getJPQLString();
		}

		// @Override
		// public boolean shouldUseAccessorFor(Object entity) {
		// return false;
		// }

		@Override
		public Object getIdentifierFrom(Object entity) {
			return null;
		}

	},

	/**
	 * Unknown special provider. Use standard JPA.
	 */
	GENERIC_JPA(//
			Collections.singleton(GENERIC_JPA_ENTITY_MANAGER_INTERFACE), //
			Collections.<String> emptySet()) {

		@Override
		public String extractQueryString(Query query) {
			return null;
		}

		@Override
		public boolean canExtractQuery() {
			return false;
		}

		// @Override
		// public boolean shouldUseAccessorFor(Object entity) {
		// return false;
		// }

		@Override
		public Object getIdentifierFrom(Object entity) {
			return null;
		}
	};

	/**
	 * Holds the PersistenceProvider specific interface names.
	 * 
	 * @author Thomas Darimont
	 */
	static interface Constants {

		String GENERIC_JPA_ENTITY_MANAGER_INTERFACE = "javax.persistence.EntityManager";
		String OPENJPA_ENTITY_MANAGER_INTERFACE = "org.apache.openjpa.persistence.OpenJPAEntityManager";
		String ECLIPSELINK_ENTITY_MANAGER_INTERFACE = "org.eclipse.persistence.jpa.JpaEntityManager";
		String HIBERNATE_ENTITY_MANAGER_INTERFACE = "org.hibernate.ejb.HibernateEntityManager";
		String HIBERNATE43_ENTITY_MANAGER_INTERFACE = "org.hibernate.jpa.HibernateEntityManager";

		String HIBERNATE_JPA_METAMODEL_TYPE = "org.hibernate.ejb.metamodel.MetamodelImpl";
		String HIBERNATE43_JPA_METAMODEL_TYPE = "org.hibernate.jpa.internal.metamodel.MetamodelImpl";
		String ECLIPSELINK_JPA_METAMODEL_TYPE = "org.eclipse.persistence.internal.jpa.metamodel.MetamodelImpl";
		String OPENJPA_JPA_METAMODEL_TYPE = "org.apache.openjpa.persistence.meta.MetamodelImpl";
	}

	private final Iterable<String> entityManagerClassNames;
	private final Iterable<String> metamodelClassNames;

	/**
	 * Creates a new {@link PersistenceProvider}.
	 * 
	 * @param entityManagerClassNames
	 *            the names of the provider specific {@link EntityManager}
	 *            implementations. Must not be {@literal null} or empty.
	 */
	private PersistenceProvider(Iterable<String> entityManagerClassNames, Iterable<String> metamodelClassNames) {
		this.entityManagerClassNames = entityManagerClassNames;
		this.metamodelClassNames = metamodelClassNames;
	}

	/**
	 * Determines the {@link PersistenceProvider} from the given {@link EntityManager}. If
	 * no special one can be determined {@link #GENERIC_JPA} will be returned.
	 * 
	 * @param em
	 *            must not be {@literal null}.
	 * @return will never be {@literal null}.
	 */
	public static PersistenceProvider fromEntityManager(EntityManager em) {
		Assert.notNull(em);
		for (PersistenceProvider provider : values()) {
			for (String entityManagerClassName : provider.entityManagerClassNames) {
				if (isEntityManagerOfType(em, entityManagerClassName)) {
					return provider;
				}
			}
		}
		return GENERIC_JPA;
	}

	public static PersistenceProvider fromMetamodel(Metamodel metamodel) {
		Assert.notNull(metamodel, "Metamodel must not be null!");
		for (PersistenceProvider provider : values()) {
			for (String metamodelClassName : provider.metamodelClassNames) {
				if (isMetamodelOfType(metamodel, metamodelClassName)) {
					return provider;
				}
			}
		}
		return GENERIC_JPA;
	}

	/**
	 * Returns whether the extractor is able to extract the original query string from a
	 * given {@link Query}.
	 * 
	 * @return
	 */
	public boolean canExtractQuery() {
		return true;
	}

	/**
	 * Reverse engineers the query string from the {@link Query} object. This requires
	 * provider specific API as JPA does not provide access to the underlying query string
	 * as soon as one has created a {@link Query} instance of it.
	 * 
	 * @param query
	 * @return the query string representing the query or {@literal null} if resolving is
	 *         not possible.
	 */
	public abstract String extractQueryString(Query query);

	/**
	 * Returns the placeholder to be used for simple count queries. Default implementation
	 * returns {@code *}.
	 * 
	 * @return
	 */
	public String getCountQueryPlaceholder() {
		return "x";
	}

	/**
	 * Returns whether the {@link ProxyIdAccessor} should be used for the given entity.
	 * Will inspect the entity to see whether it is a proxy so that lenient id lookup can
	 * be used.
	 * 
	 * @param entity
	 *            must not be {@literal null}.
	 * @return
	 */
	// public abstract boolean shouldUseAccessorFor(Object entity);

	/**
	 * Returns the identifier of the given entity by leniently inspecting it for the
	 * identifier value.
	 * 
	 * @param entity
	 *            must not be {@literal null}.
	 * @return
	 */
	public abstract Object getIdentifierFrom(Object entity);

}
