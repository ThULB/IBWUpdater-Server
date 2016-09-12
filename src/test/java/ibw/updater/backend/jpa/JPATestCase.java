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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;

import ibw.updater.common.config.ConfigurationDir;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
public class JPATestCase {

	protected EntityManager entitymanager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		String tmpDir = System.getProperty("java.io.tmpdir");
		Path tmp = Paths.get(tmpDir, "IBWUpdaterTests");
		if (Files.notExists(tmp)) {
			Files.createDirectories(tmp);
		}
		ConfigurationDir.setConfigurationDirectory(tmpDir);
		
		EntityManagerProvider.init(Persistence.createEntityManagerFactory(EntityManagerProvider.PERSISTENCE_UNIT_NAME));
		entitymanager = EntityManagerProvider.getEntityManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@After
	public void tearDown() throws Exception {
		Optional.ofNullable(EntityManagerProvider.getEntityManager()).ifPresent(em -> em.close());
		EntityManagerProvider.getEntityManagerFactory().close();
	}

}
