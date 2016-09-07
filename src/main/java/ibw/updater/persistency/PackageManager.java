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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.ZipInputStream;

import javax.persistence.EntityManager;

import ibw.updater.backend.jpa.EntityManagerProvider;
import ibw.updater.common.config.ConfigurationDir;
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
	 * Saves given {@link Package} and file {@link InputStream}.
	 * 
	 * @param p
	 *            the {@link Package} to save
	 * @param is
	 *            the file {@link InputStream} to save
	 * @return the saved {@link Package} object
	 * @throws IOException
	 *             thrown on is close()
	 */
	public static Package save(Package p, InputStream is) throws IOException {
		try {
			if (p.getType() == Package.Type.COMMON) {
				BufferedInputStream bis = new BufferedInputStream(is);
				try {
					if (isValidPackage(bis)) {
						writePackage(p.getId(), bis);
					} else {
						throw new UnsupportedOperationException("Uploaded file isn't a ZIP file.");
					}
				} finally {
					bis.close();
				}
			}
			return save(p);
		} finally {
			if (is != null) {
				is.close();
			}
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
			Package inDB = get(p.getId());
			if (inDB != null) {
				p.setId(inDB.getId());
				em.detach(inDB);
			}
			em.getTransaction().begin();

			if (inDB.getFunction() != null && p.getFunction() != null && !inDB.getFunction().equals(p.getFunction())) {
				p.setVersion(p.getVersion() + 1);
			}

			em.merge(p);
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
	 * @throws IOException
	 */
	public static Package update(Package p, InputStream is) throws IOException {
		if (!exists(p.getId())) {
			return save(p, is);
		}

		EntityManager em = EntityManagerProvider.getEntityManager();
		try {
			Package inDB = get(p.getId());
			if (inDB != null) {
				p.setId(inDB.getId());
				em.detach(inDB);
			}
			em.getTransaction().begin();

			if (p.getType() == Package.Type.USER) {
				if (inDB.getFunction() != null && p.getFunction() != null
						&& !inDB.getFunction().equals(p.getFunction())) {
					p.setVersion(p.getVersion() + 1);
				}
			} else if (p.getType() == Package.Type.COMMON) {
				BufferedInputStream bis = new BufferedInputStream(is);
				try {
					if (isValidPackage(bis)) {
						writePackage(p.getId(), bis);
						p.setVersion(p.getVersion() + 1);
					} else {
						throw new UnsupportedOperationException("Uploaded file isn't a ZIP file.");
					}
				} finally {
					bis.close();
				}
			}

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

	private static boolean isValidPackage(InputStream is) throws IOException {
		return new ZipInputStream(is).getNextEntry() != null;
	}

	private static void writePackage(String id, InputStream is) throws IOException {
		Path packageDir = Paths.get(ConfigurationDir.getConfigFile("packages").toURI());
		Path file = packageDir.resolve(id + ".zip");

		if (Files.notExists(packageDir)) {
			Files.createDirectories(packageDir);
		}

		Files.copy(is, file, StandardCopyOption.REPLACE_EXISTING);
	}

}
