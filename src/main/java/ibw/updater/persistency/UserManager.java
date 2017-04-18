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
import ibw.updater.datamodel.User;
import ibw.updater.datamodel.Users;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
public class UserManager {

	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Returns all {@link User}s.
	 * 
	 * @return a {@link List} of {@link Users}
	 */
	public static Users get() {
		EntityManager em = EntityManagerProvider.getEntityManager();
		LOGGER.debug("List all users");
		return new Users(em.createNamedQuery("User.findAll", User.class).getResultList());
	}

	/**
	 * Returns {@link User} for given id or <code>null</code> if nothing was
	 * found.
	 * 
	 * @param id
	 *            the {@link User#id}
	 * @return the {@link User}
	 */
	public static User get(int id) {
		EntityManager em = EntityManagerProvider.getEntityManager();
		LOGGER.debug("Get user by id: " + id);
		return em.find(User.class, id);
	}

	/**
	 * Returns {@link User} for given name or <code>null</code> if nothing was
	 * found.
	 * 
	 * @param name
	 *            the {@link User#name}
	 * @return the {@link User}
	 */
	public static User get(String name) {
		EntityManager em = EntityManagerProvider.getEntityManager();
		try {
			LOGGER.debug("Get user by name: " + name);
			TypedQuery<User> query = em.createNamedQuery("User.findByName", User.class);
			query.setParameter("name", name);

			return query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	/**
	 * Checks if {@link User} is exists.
	 * 
	 * @param id
	 *            the {@link User#id}
	 * @return <code>true</code> if {@link User} exists or <code>false</code>
	 *         isn't.
	 */
	public static boolean exists(int id) {
		return get(id) != null;
	}

	/**
	 * Checks if {@link User} is exists.
	 * 
	 * @param name
	 *            the {@link User#name}
	 * @return <code>true</code> if {@link User} exists or <code>false</code>
	 *         isn't.
	 */
	public static boolean exists(String name) {
		return get(name) != null;
	}

	/**
	 * Saves given {@link User}.
	 * 
	 * @param user
	 *            the {@link User} to save
	 * @return the saved {@link User} object
	 */
	public static User save(User user) {
		if (user.getId() == 0 && exists(user.getName())) {
			throw new EntityExistsException("A user with name \"" + user.getName() + "\" already exists.");
		}

		EntityManager em = EntityManagerProvider.getEntityManager();
		LOGGER.info("Save user: " + user);
		EntityManagerProvider.beginTransaction();
		em.persist(user);
		EntityManagerProvider.commit();

		return user;
	}

	/**
	 * Updates given {@link User}.
	 * 
	 * @param user
	 *            the {@link User} to update
	 * @return the updated {@link User} object
	 */
	public static User update(User user) {
		if (user.getId() == 0 && !exists(user.getName())) {
			return save(user);
		}

		EntityManager em = EntityManagerProvider.getEntityManager();
		User inDB = get(user.getName());
		if (inDB != null) {
			user.setId(inDB.getId());
			em.detach(inDB);
		}

		LOGGER.info("Update user: " + user);
		EntityManagerProvider.beginTransaction();
		user = em.merge(user);
		EntityManagerProvider.commit();

		return user;
	}

	/**
	 * Deletes given {@link User#getId()}.
	 * 
	 * @param uid
	 *            the {@link User#getId()} to delete
	 */
	public static void delete(int uid) {
		EntityManager em = EntityManagerProvider.getEntityManager();
		LOGGER.info("Delete user with id: " + uid);
		EntityManagerProvider.beginTransaction();
		em.remove(em.find(User.class, uid));
		EntityManagerProvider.commit();
	}

	/**
	 * Deletes given {@link User}.
	 * 
	 * @param user
	 *            the {@link User} to delete
	 */
	public static void delete(User user) {
		delete(user.getId());
	}
}
