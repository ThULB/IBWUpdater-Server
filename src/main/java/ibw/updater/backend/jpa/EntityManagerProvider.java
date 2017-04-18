/*
 * This program is free software; you can use it, redistribute it
 * and / or modify it under the terms of the GNU General Public License
 * (GPL) as published by the Free Software Foundation; either version 3
 * of the License or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, in a file called gpl.txt or license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 */
package ibw.updater.backend.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;

import ibw.updater.common.events.annotation.AutoExecutable;
import ibw.updater.common.events.annotation.Shutdown;
import ibw.updater.common.events.annotation.Startup;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
@AutoExecutable(name = "EntityManagerProvider", priority = 1000)
public class EntityManagerProvider {

	public static final String PERSISTENCE_UNIT_NAME = "IBWUpdater";

	private static EntityManagerFactory factory;

	private static ThreadLocal<EntityManager> threadLocal;

	public static EntityManagerFactory getEntityManagerFactory() {
		return factory;
	}

	public static EntityManager getEntityManager() {
		EntityManager em = threadLocal.get();
		if (em == null || !em.isOpen()) {
			em = factory.createEntityManager();
			em.setFlushMode(FlushModeType.COMMIT);
			threadLocal.set(em);
		}
		return em;
	}

	public static void closeEntityManager() {
		EntityManager em = threadLocal.get();
		if (em != null && em.isOpen()) {
			if (em.getTransaction().isActive()) {
				try {
					commit();
				} catch (RollbackException e) {
					rollback();
				}
			}
			em.close();
			threadLocal.set(null);
		}
	}

	public static void beginTransaction() {
		EntityTransaction tx = getEntityManager().getTransaction();
		if (!tx.isActive()) {
			tx.begin();
		}
	}

	public static void rollback() {
		EntityTransaction tx = getEntityManager().getTransaction();
		if (tx.isActive()) {
			tx.rollback();
		}
	}

	public static void commit() {
		EntityTransaction tx = getEntityManager().getTransaction();
		if (tx.isActive()) {
			tx.commit();
		}
	}

	protected static void init(EntityManagerFactory factory) {
		EntityManagerProvider.factory = factory;
		threadLocal = new ThreadLocal<EntityManager>();
	}

	@Startup
	public static void startup() {
		init(Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME));
	}

	@Shutdown
	public static void shutdown() {
		if (factory != null && factory.isOpen()) {
			closeEntityManager();
			factory.close();
		}
	}
}
