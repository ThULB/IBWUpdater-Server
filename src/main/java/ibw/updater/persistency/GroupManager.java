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

import ibw.updater.backend.jpa.EntityManagerProvider;
import ibw.updater.datamodel.Group;
import ibw.updater.datamodel.Groups;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
public class GroupManager {

	/**
	 * Returns all {@link Group}s.
	 * 
	 * @return a {@link List} of {@link Groups}
	 */
	public static Groups get() {
		EntityManager em = EntityManagerProvider.getEntityManager();
		try {
			return new Groups(em.createNamedQuery("Group.findAll", Group.class).getResultList());
		} finally {
			em.close();
		}
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
		try {
			return em.find(Group.class, id);
		} finally {
			em.close();
		}
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
			TypedQuery<Group> query = em.createNamedQuery("Group.findByName", Group.class);
			query.setParameter("name", name);

			return query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		} finally {
			em.close();
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
		try {
			em.getTransaction().begin();
			em.persist(group);
			em.getTransaction().commit();

			return group;
		} finally {
			em.close();
		}
	}

	/**
	 * Updates given {@link Group}.
	 * 
	 * @param group
	 *            the {@link Group} to update
	 * @return the updated {@link Group} object
	 */
	public static Group update(Group group) {
		EntityManager em = EntityManagerProvider.getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(group);
			em.getTransaction().commit();

			return group;
		} finally {
			em.close();
		}
	}
}
