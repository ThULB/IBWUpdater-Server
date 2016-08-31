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

import javax.persistence.EntityManager;

import ibw.updater.backend.jpa.EntityManagerProvider;
import ibw.updater.datamodel.Package;
import ibw.updater.datamodel.Packages;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
public class PackageManager {

	/**
	 * Returns all {@link Package}s.
	 * 
	 * @return a {@link List} of {@link Package}s
	 */
	public static Packages get() {
		EntityManager em = EntityManagerProvider.getEntityManager();
		try {
			return new Packages(em.createNamedQuery("Package.findAll", Package.class).getResultList());
		} finally {
			em.close();
		}
	}

	/**
	 * Returns a {@link Package} for given id.
	 * 
	 * @param id
	 *            the {@link Package#id}
	 * @return the {@link Package}
	 */
	public static Package get(String id) {
		EntityManager em = EntityManagerProvider.getEntityManager();
		try {
			return em.find(Package.class, id);
		} finally {
			em.close();
		}
	}

	/**
	 * Checks if {@link Package} is exists.
	 * 
	 * @param id
	 *            the {@link Package#id}
	 * @return <code>true</code> if {@link Package} exists or <code>false</code>
	 *         isn't.
	 */
	public static boolean exists(String id) {
		return get(id) != null;
	}

	/**
	 * Saves given {@link Package}.
	 * 
	 * @param p
	 *            the {@link Package} to save
	 * @return the saved {@link Package} object
	 */
	public static Package save(Package p) {
		EntityManager em = EntityManagerProvider.getEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(p);
			em.getTransaction().commit();

			return p;
		} finally {
			em.close();
		}
	}

	/**
	 * Updates given {@link Package}.
	 * 
	 * @param p
	 *            the {@link Package} to update
	 * @return the updated {@link Package} object
	 */
	public static Package update(Package p) {
		if (!exists(p.getId())) {
			return save(p);
		}

		EntityManager em = EntityManagerProvider.getEntityManager();
		try {
			Package inDB = get(p.getName());
			if (inDB != null) {
				p.setId(inDB.getId());
				em.detach(inDB);
			}
			em.getTransaction().begin();
			em.merge(p);
			em.getTransaction().commit();

			return p;
		} finally {
			em.close();
		}
	}

	/**
	 * Deletes given {@link Package#getId()}.
	 * 
	 * @param id
	 *            the {@link Package#getId()} to delete
	 */
	public static void delete(String id) {
		EntityManager em = EntityManagerProvider.getEntityManager();
		try {
			em.getTransaction().begin();
			em.remove(em.find(Package.class, id));
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

}
