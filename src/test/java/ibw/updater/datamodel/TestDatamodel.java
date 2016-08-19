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
package ibw.updater.datamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ibw.updater.backend.jpa.JPATestCase;
import ibw.updater.datamodel.Group;
import ibw.updater.datamodel.Package;
import ibw.updater.datamodel.User;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
public class TestDatamodel extends JPATestCase {

	@Test
	public void testUser() {
		entitymanager.getTransaction().begin();

		User u = new User("test", "Test User");

		entitymanager.persist(u);
		entitymanager.getTransaction().commit();

		assertTrue("user id should not 0", u.getId() != 0);

		entitymanager.getTransaction().begin();

		Group g = new Group("test", "Test Group");

		entitymanager.persist(g);
		entitymanager.getTransaction().commit();

		assertTrue("group id should not 0", g.getId() != 0);

		entitymanager.getTransaction().begin();

		u.addGroup(g);

		entitymanager.getTransaction().commit();

		assertNotNull("groups should not null", u.getGroups());
		assertEquals("group count should be 1", 1, u.getGroups().size());
	}

	@Test
	public void testGroup() {
		entitymanager.getTransaction().begin();

		Group g = new Group("test", "Test Group");

		entitymanager.persist(g);
		entitymanager.getTransaction().commit();

		assertTrue("group id should not 0", g.getId() != 0);

		entitymanager.getTransaction().begin();

		User u = new User("test", "Test User");

		entitymanager.persist(u);
		entitymanager.getTransaction().commit();

		assertTrue("user id should not 0", u.getId() != 0);

		entitymanager.getTransaction().begin();

		g.addUser(u);

		entitymanager.getTransaction().commit();

		assertNotNull("users should not null", g.getUsers());
		assertEquals("user count should be 1", 1, g.getUsers().size());
	}

	@Test
	public void testPackage() {
		entitymanager.getTransaction().begin();

		Package p = new Package(Package.Type.USER, "test", "Test Package");
		p.setStartupScript("scripts/testStartup.js");

		assertNotNull("package id should not null", p.getId());

		entitymanager.persist(p);
		entitymanager.getTransaction().commit();
	}

	@Test
	public void testPermission() {
		entitymanager.getTransaction().begin();

		User u = new User("test", "Test User");

		entitymanager.persist(u);
		entitymanager.getTransaction().commit();

		entitymanager.getTransaction().begin();

		Package p = new Package(Package.Type.USER, "test", "Test Package");
		p.setStartupScript("scripts/testStartup.js");

		assertNotNull("package id should not null", p.getId());

		entitymanager.persist(p);
		entitymanager.getTransaction().commit();

		entitymanager.getTransaction().begin();

		Permission permission = new Permission(Permission.Type.USER, u.getId(), Permission.Action.READ, p);

		entitymanager.persist(permission);
		entitymanager.getTransaction().commit();
	}
}
