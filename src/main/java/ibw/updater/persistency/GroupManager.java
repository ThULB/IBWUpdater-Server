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
package ibw.updater.persistency;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ibw.updater.backend.jpa.EntityManagerProvider;
import ibw.updater.datamodel.Group;
import ibw.updater.datamodel.Groups;

/**
 * The Class GroupManager.
 *
 * @author Ren\u00E9 Adler (eagle)
 */
public class GroupManager {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Returns all {@link Group}s.
	 * 
	 * @return a {@link List} of {@link Groups}
	 */
	public static Groups get() {
		EntityManager em = EntityManagerProvider.getEntityManager();
		LOGGER.debug("List all groups");
		return new Groups(em.createNamedQuery("Group.findAll", Group.class).getResultList());
	}

	/**
	 * Returns {@link Group} for given id or <code>null</code> if nothing was
	 * found.
	 * 
	 * @param id
	 *            the {@link Group#id}
	 * @return the {@link Group}
	 */
	public static Group get(int id) {
		EntityManager em = EntityManagerProvider.getEntityManager();
		LOGGER.debug("Get group by id: " + id);
		return em.find(Group.class, id);
	}

	/**
	 * Returns {@link Group} for given name or <code>null</code> if nothing was
	 * found.
	 * 
	 * @param name
	 *            the {@link Group#name}
	 * @return the {@link Group}
	 */
	public static Group get(String name) {
		EntityManager em = EntityManagerProvider.getEntityManager();
		try {
			LOGGER.debug("Get group by name: " + name);
			TypedQuery<Group> query = em.createNamedQuery("Group.findByName", Group.class);
			query.setParameter("name", name);

			return query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	/**
	 * Checks if {@link Group} is exists.
	 * 
	 * @param id
	 *            the {@link Group#id}
	 * @return <code>true</code> if {@link Group} exists or <code>false</code>
	 *         isn't.
	 */
	public static boolean exists(int id) {
		return get(id) != null;
	}

	/**
	 * Checks if {@link Group} is exists.
	 * 
	 * @param name
	 *            the {@link Group#name}
	 * @return <code>true</code> if {@link Group} exists or <code>false</code>
	 *         isn't.
	 */
	public static boolean exists(String name) {
		return get(name) != null;
	}

	/**
	 * Saves given {@link Group}.
	 * 
	 * @param group
	 *            the {@link Group} to save
	 * @return the saved {@link Group} object
	 */
	public static Group save(Group group) {
		if (group.getId() == 0 && exists(group.getName())) {
			throw new EntityExistsException("A group with name \"" + group.getName() + "\" already exists.");
		}

		EntityManager em = EntityManagerProvider.getEntityManager();
		LOGGER.info("Save group: " + group);

		EntityManagerProvider.beginTransaction();
		em.persist(group);
		EntityManagerProvider.commit();

		return group;
	}

	/**
	 * Updates given {@link Group}.
	 * 
	 * @param group
	 *            the {@link Group} to update
	 * @return the updated {@link Group} object
	 */
	public static Group update(Group group) {
		if (group.getId() == 0 && !exists(group.getName())) {
			return save(group);
		}

		EntityManager em = EntityManagerProvider.getEntityManager();
		Group inDB = get(group.getName());
		if (inDB != null) {
			group.setId(inDB.getId());
			em.detach(inDB);
		}

		LOGGER.info("Update group: " + group);
		EntityManagerProvider.beginTransaction();
		try {
			return em.merge(group);
		} finally {
			EntityManagerProvider.commit();
		}
	}

	/**
	 * Deletes given {@link Group#getId()}.
	 * 
	 * @param gid
	 *            the {@link Group#getId()} to delete
	 */
	public static void delete(int gid) {
		EntityManager em = EntityManagerProvider.getEntityManager();

		LOGGER.info("Delete group with id: " + gid);
		EntityManagerProvider.beginTransaction();
		em.remove(em.find(Group.class, gid));
		EntityManagerProvider.commit();
	}

	/**
	 * Deletes given {@link Group}.
	 * 
	 * @param group
	 *            the {@link Group} to delete
	 */
	public static void delete(Group group) {
		delete(group.getId());
	}
}
