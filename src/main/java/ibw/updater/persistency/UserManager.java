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

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ibw.updater.backend.jpa.EntityManagerProvider;
import ibw.updater.datamodel.User;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
public class UserManager {

	public static boolean exists(int id) {
		EntityManager em = EntityManagerProvider.getEntityManager();
		try {
			return em.find(User.class, id) != null;
		} finally {
			em.close();
		}
	}

	public static boolean exists(String name) {
		EntityManager em = EntityManagerProvider.getEntityManager();
		try {
			TypedQuery<User> query = em.createNamedQuery("User.findByName", User.class);
			query.setParameter("name", name);

			return !query.getResultList().isEmpty();
		} finally {
			em.close();
		}
	}

	public static User save(User user) {
		EntityManager em = EntityManagerProvider.getEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(user);
			em.getTransaction().commit();

			return user;
		} finally {
			em.close();
		}
	}

	public static User update(User user) {
		EntityManager em = EntityManagerProvider.getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(user);
			em.getTransaction().commit();

			return user;
		} finally {
			em.close();
		}
	}
}
