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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ibw.updater.backend.jpa.EntityManagerProvider;
import ibw.updater.common.config.ConfigurationDir;
import ibw.updater.datamodel.Package;
import ibw.updater.datamodel.Packages;
import ibw.updater.datamodel.Permission;
import ibw.updater.datamodel.Permissions;
import ibw.updater.datamodel.User;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
public class PackageManager {

	private static final Logger LOGGER = LogManager.getLogger();

	private static Path PACKAGE_DIR;

	static {
		try {
			Path cfgDir = Paths.get(ConfigurationDir.getConfigurationDirectory().getAbsolutePath());
			if (Files.notExists(cfgDir)) {
				Files.createDirectories(cfgDir);
			}

			PACKAGE_DIR = Paths.get(ConfigurationDir.getConfigFile("packages").toURI());
			if (Files.notExists(PACKAGE_DIR)) {
				Files.createDirectories(PACKAGE_DIR);
			}
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Returns all {@link Package}s.
	 * 
	 * @return a {@link List} of {@link Package}s
	 */
	public static Packages get() {
		EntityManager em = EntityManagerProvider.getEntityManager();
		LOGGER.debug("List all packages");
		return new Packages(em.createNamedQuery("Package.findAll", Package.class).getResultList());
	}

	/**
	 * Returns all {@link Package}s with extended informations.
	 * 
	 * @return a {@link List} of {@link Package}s
	 */
	public static Packages getExtended() {
		EntityManager em = EntityManagerProvider.getEntityManager();
		LOGGER.debug("List all packages with extended informations");
		List<Package> packages = em.createNamedQuery("Package.findAll", Package.class).getResultList();
		return new Packages(packages.stream().map(p -> {
			if (p.getType() == Package.Type.COMMON) {
				p.setUrl("packages/" + p.getId() + ".zip");
			}
			return p;
		}).collect(Collectors.toList()));
	}

	/**
	 * Returns all {@link Package}s with extended informations for given uid.
	 * 
	 * @param uid
	 *            the user name
	 * @return a {@link List} of {@link Package}s
	 */
	public static Packages getExtended(String uid) {
		return getExtended(uid != null ? UserManager.get(uid) : null);
	}

	/**
	 * Returns all {@link Package}s with extended informations for given uid.
	 * 
	 * @param uid
	 *            the user name
	 * @return a {@link List} of {@link Package}s
	 */
	public static Packages getExtended(User user) {
		Packages packages = getExtended();
		LOGGER.debug("Filter packages by user: " + user);
		return new Packages(packages.getPackages().stream().filter(p -> {
			Permissions permissions = PermissionManager.get(p.getId());
			return permissions.getPermissions().isEmpty()
					|| user != null && permissions.getPermissions().stream().filter(permission -> {
						return permission.getType() == Permission.Type.USER && permission.getSourceId() == user.getId()
								|| permission.getType() == Permission.Type.GROUP
										&& GroupManager.get(permission.getSourceId()).isMember(user);
					}).count() != 0;
		}).collect(Collectors.toList()));
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
		LOGGER.debug("Get package by id: " + id);
		return em.find(Package.class, id);
	}

	/**
	 * Returns the {@link Package} file/content for given id.
	 * 
	 * @param id
	 *            the {@link Package#id}
	 * @return a {@link InputStream} with content
	 * @throws IOException
	 */
	public static InputStream getContent(String id) throws IOException {
		Package p = get(id);
		if (p.getType() != Package.Type.COMMON) {
			throw new UnsupportedOperationException("Get file is unsupported for package type " + p.getType() + ".");
		}
		return Channels.newInputStream(FileChannel.open(PACKAGE_DIR.resolve(p.getId() + ".zip")));
	}

	/**
	 * Returns the {@link Package} file/content for given filename.
	 * 
	 * @param fileName
	 *            the fileName
	 * @return a {@link InputStream} with content
	 * @throws IOException
	 */
	public static InputStream getContentByFileName(String fileName) throws IOException {
		return Channels.newInputStream(FileChannel.open(PACKAGE_DIR.resolve(fileName)));
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

		LOGGER.info("Save package: " + p);
		EntityManagerProvider.beginTransaction();
		em.persist(p);
		EntityManagerProvider.commit();

		return p;
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
			if (p.getType() == Package.Type.COMMON && is != null) {
				Path tmpFile = createTempFile(p.getId(), is);
				Path pFile = PACKAGE_DIR.resolve(p.getId() + ".zip");

				try {
					if (isValidPackage(tmpFile)) {
						if (p.getStartupScript() != null && !isValidStartupScript(tmpFile, p.getStartupScript())) {
							throw new UnsupportedOperationException("Startup-Script wasn't found in ZIP file.");
						}

						Files.copy(tmpFile, pFile, StandardCopyOption.REPLACE_EXISTING);
					} else {
						throw new UnsupportedOperationException("Uploaded file isn't a ZIP file.");
					}
				} finally {
					Files.delete(tmpFile);
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
	 * @throws IOException
	 */
	public static Package update(Package p) throws IOException {
		if (!exists(p.getId())) {
			return save(p);
		}

		EntityManager em = EntityManagerProvider.getEntityManager();
		Package inDB = get(p.getId());
		if (inDB != null) {
			p.setId(inDB.getId());
			em.detach(inDB);
		}
		EntityManagerProvider.beginTransaction();

		if (Package.Type.USER.equals(p.getType()) && inDB.getFunction() != null && p.getFunction() != null
				&& !inDB.getFunction().equals(p.getFunction())) {
			p.setVersion(p.getVersion() + 1);
		} else if (p.getType() == Package.Type.COMMON
				&& (inDB.getStartupScript() == null && p.getStartupScript() != null || inDB.getStartupScript() != null
						&& p.getStartupScript() != null && !inDB.getStartupScript().equals(p.getStartupScript()))) {
			if (!isValidStartupScript(PACKAGE_DIR.resolve(p.getId() + ".zip"), p.getStartupScript())) {
				throw new UnsupportedOperationException("Startup-Script wasn't found in ZIP file.");
			}
			p.setVersion(p.getVersion() + 1);
		}

		LOGGER.info("Update package: " + p);

		p = em.merge(p);
		EntityManagerProvider.commit();

		return p;
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
		Package inDB = get(p.getId());
		if (inDB != null) {
			p.setId(inDB.getId());
			em.detach(inDB);
		}
		EntityManagerProvider.beginTransaction();

		if (p.getType() == Package.Type.USER) {
			if (inDB.getFunction() != null && p.getFunction() != null && !inDB.getFunction().equals(p.getFunction())) {
				p.setVersion(p.getVersion() + 1);
			}
		} else if (p.getType() == Package.Type.COMMON) {
			boolean incVer = false;

			if (inDB.getStartupScript() == null && p.getStartupScript() != null || inDB.getStartupScript() != null
					&& p.getStartupScript() != null && !inDB.getStartupScript().equals(p.getStartupScript())) {
				incVer = true;
			}

			if (is != null) {
				Path tmpFile = createTempFile(p.getId(), is);
				Path pFile = PACKAGE_DIR.resolve(p.getId() + ".zip");

				try {
					if (isValidPackage(tmpFile)) {
						if (!isEqualPackage(pFile, tmpFile)) {
							if (p.getStartupScript() != null && !isValidStartupScript(tmpFile, p.getStartupScript())) {
								throw new UnsupportedOperationException("Startup-Script wasn't found in ZIP file.");
							}
							Files.copy(tmpFile, pFile, StandardCopyOption.REPLACE_EXISTING);
							incVer = true;
						}
					} else {
						throw new UnsupportedOperationException("Uploaded file isn't a ZIP file.");
					}
				} finally {
					Files.delete(tmpFile);
				}
			}

			if (incVer) {
				p.setVersion(p.getVersion() + 1);
			}
		}

		LOGGER.info("Update package: " + p);

		p = em.merge(p);
		EntityManagerProvider.commit();

		return p;
	}

	/**
	 * Deletes given {@link Package#getId()}.
	 * 
	 * @param id
	 *            the {@link Package#getId()} to delete
	 */
	public static void delete(String id) {
		EntityManager em = EntityManagerProvider.getEntityManager();
		LOGGER.info("Delete package with id: " + id);
		EntityManagerProvider.beginTransaction();
		em.remove(em.find(Package.class, id));
		EntityManagerProvider.commit();
	}

	private static Path createTempFile(String id, InputStream is) throws IOException {
		Path tmpFile = Files.createTempFile(id, null);
		Files.copy(is, tmpFile, StandardCopyOption.REPLACE_EXISTING);
		return tmpFile;
	}

	private static boolean isValidPackage(Path pFile) throws IOException {
		return new ZipInputStream(Channels.newInputStream(FileChannel.open(pFile))).getNextEntry() != null;
	}

	private static boolean isValidStartupScript(Path pFile, String startupScript) throws IOException {
		ZipFile zf = new ZipFile(pFile.toFile());
		try {
			return zf.getEntry(startupScript) != null;
		} finally {
			zf.close();
		}
	}

	private static boolean isEqualPackage(Path p1, Path p2) throws IOException {
		ReadableByteChannel ch1 = FileChannel.open(p1);
		ReadableByteChannel ch2 = FileChannel.open(p2);

		ByteBuffer buf1 = ByteBuffer.allocateDirect(1024);
		ByteBuffer buf2 = ByteBuffer.allocateDirect(1024);

		while (true) {

			int n1 = ch1.read(buf1);
			int n2 = ch2.read(buf2);

			if (n1 == -1 || n2 == -1)
				return n1 == n2;

			buf1.flip();
			buf2.flip();

			for (int i = 0; i < Math.min(n1, n2); i++)
				if (buf1.get() != buf2.get())
					return false;

			buf1.compact();
			buf2.compact();
		}
	}
}
