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
import javax.persistence.Persistence;

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

	public static EntityManagerFactory getEntityManagerFactory() {
		return factory;
	}

	public static EntityManager getEntityManager() {
		return factory.createEntityManager();
	}

	static void init(EntityManagerFactory factory) {
		EntityManagerProvider.factory = factory;
	}

	@Startup
	public void startup() {
		EntityManagerProvider.init(Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME));
	}

	@Shutdown
	public void shutdown() {
		if (EntityManagerProvider.getEntityManagerFactory().isOpen()) {
			EntityManagerProvider.getEntityManagerFactory().close();
		}
	}
}
