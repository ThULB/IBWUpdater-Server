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
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ibw.updater.backend.jpa.EntityManagerProvider;
import ibw.updater.datamodel.Package;
import ibw.updater.datamodel.Permission;
import ibw.updater.datamodel.Permission.PermissionId;
import ibw.updater.datamodel.Permissions;

/**
 * The Class PermissionManager.
 *
 * @author Ren\u00E9 Adler (eagle)
 */
public class PermissionManager {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Returns all {@link Permission}s.
	 * 
	 * @return a {@link List} of {@link Permissions}
	 */
	public static Permissions get() {
		EntityManager em = EntityManagerProvider.getEntityManager();
		LOGGER.debug("List all permissions");
		return new Permissions(em.createNamedQuery("Permission.findAll", Permission.class).getResultList());
	}

	/**
	 * Returns all {@link Permission}s for given packageId.
	 *
	 * @param packageId
	 *            the package id
	 * @return a {@link List} of {@link Permissions}
	 */
	public static Permissions get(String packageId) {
		EntityManager em = EntityManagerProvider.getEntityManager();
		LOGGER.debug("List permissions for packageId: " + packageId);
		Package p = PackageManager.get(packageId);
		return new Permissions(em.createNamedQuery("Permission.findAllByPackage", Permission.class)
				.setParameter("package", p).getResultList());
	}

	/**
	 * Return {@link Permission} for given {@link Permission.PermissionId}.
	 * 
	 * @param id
	 *            {@link Permission.PermissionId}
	 * @return the {@link Permission}
	 */
	public static Permission get(PermissionId id) {
		EntityManager em = EntityManagerProvider.getEntityManager();
		LOGGER.debug("Get permission for permissionId: " + id);
		return em.find(Permission.class, id);
	}

	/**
	 * Return {@link Permission} for given {@link Permission}.
	 * 
	 * @param permission
	 *            {@link Permission}
	 * @return the {@link Permission}
	 */
	public static Permission get(Permission permission) {
		return get(new Permission.PermissionId(permission));
	}

	/**
	 * Checks if {@link Permission} is exists.
	 * 
	 * @param permission
	 *            the {@link Permission}
	 * @return <code>true</code> if {@link Permission} exists or
	 *         <code>false</code> isn't.
	 */
	public static boolean exists(Permission permission) {
		return get(permission) != null;
	}

	/**
	 * Save given {@link Permission}.
	 * 
	 * @param permission
	 *            the {@link Permission} to save
	 * @return the persist {@link Permission}
	 */
	public static Permission save(Permission permission) {
		if (exists(permission)) {
			update(permission);
		}

		EntityManager em = EntityManagerProvider.getEntityManager();
		LOGGER.info("Save permission: " + permission);
		EntityManagerProvider.beginTransaction();
		em.persist(permission);
		EntityManagerProvider.commit();

		return permission;
	}

	/**
	 * Save given {@link Permissions}.
	 * 
	 * @param permissions
	 *            the {@link Permissions} to save
	 * @return the persist {@link Permissions}
	 */
	public static Permissions save(Permissions permissions) {
		return new Permissions(
				permissions.getPermissions().stream().map(PermissionManager::save).collect(Collectors.toList()));
	}

	/**
	 * Update given {@link Permission}.
	 * 
	 * @param permission
	 *            the {@link Permission} to save
	 * @return the updated {@link Permission}
	 */
	public static Permission update(Permission permission) {
		if (!exists(permission)) {
			save(permission);
		}

		EntityManager em = EntityManagerProvider.getEntityManager();
		LOGGER.info("Update permission: " + permission);
		EntityManagerProvider.beginTransaction();
		try {
			return em.merge(permission);
		} finally {
			EntityManagerProvider.commit();
		}
	}

	/**
	 * Update given {@link Permissions}.
	 * 
	 * @param permissions
	 *            the {@link Permissions} to save
	 * @return the updated {@link Permissions}
	 */
	public static Permissions update(Permissions permissions) {
		return new Permissions(
				permissions.getPermissions().stream().map(PermissionManager::update).collect(Collectors.toList()));
	}

	/**
	 * Deletes given {@link Permission}.
	 * 
	 * @param permissionId
	 *            the {@link Permission.PermissionId} to delete
	 */
	public static void delete(PermissionId permissionId) {
		EntityManager em = EntityManagerProvider.getEntityManager();
		LOGGER.info("Delete permission: " + permissionId);
		EntityManagerProvider.beginTransaction();
		em.remove(em.find(Permission.class, permissionId));
		EntityManagerProvider.commit();
	}

	/**
	 * Deletes given {@link Permission}.
	 * 
	 * @param permission
	 *            the {@link Permission} to delete
	 */
	public static void delete(Permission permission) {
		delete(new Permission.PermissionId(permission));
	}
}
