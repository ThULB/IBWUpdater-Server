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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.persistence.EntityExistsException;

import org.junit.Test;

import ibw.updater.backend.jpa.JPATestCase;
import ibw.updater.datamodel.Function;
import ibw.updater.datamodel.Group;
import ibw.updater.datamodel.Package;
import ibw.updater.datamodel.User;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
public class TestPersistency extends JPATestCase {

	@Test
	public void saveUser() {
		User u = new User("test", "Test User");
		UserManager.save(u);
		assertTrue("user id should not 0", u.getId() != 0);
	}

	@Test(expected = EntityExistsException.class)
	public void saveDuplicateUser() {
		User u = new User("test", "Test User");
		UserManager.save(u);
		assertTrue("user id should not 0", u.getId() != 0);

		User u2 = new User("test", "Test User");
		UserManager.save(u2);
	}

	@Test
	public void updateUser() {
		User u = new User("test", "Test User");
		UserManager.save(u);
		assertTrue("user id should not 0", u.getId() != 0);

		u.setName("updatedtest");
		UserManager.update(u);
		assertEquals("user should equal", "updatedtest", u.getName());
	}

	@Test
	public void existsUser() {
		saveUser();

		assertTrue("user should exists (by Id)", UserManager.exists(1));
		assertTrue("user should exists (by name)", UserManager.exists("test"));
	}

	@Test
	public void deleteUser() {
		User u = new User("test", "Test User");
		UserManager.save(u);
		UserManager.delete(u);

		assertFalse("user should not exists (by Id)", UserManager.exists(1));
		assertFalse("user should not exists (by name)", UserManager.exists("test"));
	}

	@Test
	public void addGroupToUser() {
		saveUser();
		saveGroup();

		User u = UserManager.get("test");
		Group g = GroupManager.get("test");

		u.addGroup(g);

		u = UserManager.update(u);

		assertEquals("user should have one group", 1, u.getGroups().size());
		assertEquals("user group should be test", "test", u.getGroups().get(0).getName());
	}

	@Test
	public void removeGroupFromUser() {
		addGroupToUser();

		User u = UserManager.get("test");
		Group g = GroupManager.get("test");

		u.removeGroup(g);

		u = UserManager.update(u);

		assertEquals("user should have no group", 0, u.getGroups().size());
	}

	@Test
	public void saveGroup() {
		Group g = new Group("test", "Test Group");
		GroupManager.save(g);
		assertTrue("group id should not 0", g.getId() != 0);
	}

	@Test(expected = EntityExistsException.class)
	public void saveDuplicateGroup() {
		Group g = new Group("test", "Test Group");
		GroupManager.save(g);
		assertTrue("group id should not 0", g.getId() != 0);

		Group g2 = new Group("test", "Test Group");
		GroupManager.save(g2);
	}

	@Test
	public void updateGroup() {
		Group g = new Group("test", "Test Group");
		GroupManager.save(g);
		assertTrue("group id should not 0", g.getId() != 0);

		g.setName("updatedtest");
		GroupManager.update(g);
		assertEquals("group should equal", "updatedtest", g.getName());
	}

	@Test
	public void existsGroup() {
		saveGroup();

		assertTrue("group should exists (by Id)", GroupManager.exists(1));
		assertTrue("group should exists (by name)", GroupManager.exists("test"));
	}

	@Test
	public void deleteGroup() {
		Group g = new Group("test", "Test Group");
		GroupManager.save(g);
		GroupManager.delete(g);

		assertFalse("group should not exists (by Id)", GroupManager.exists(1));
		assertFalse("group should not exists (by name)", GroupManager.exists("test"));
	}

	@Test
	public void addUserToGroup() {
		saveUser();
		saveGroup();

		User u = UserManager.get("test");
		Group g = GroupManager.get("test");

		g.addUser(u);

		g = GroupManager.update(g);

		assertEquals("group should have one user", 1, g.getUsers().size());
		assertEquals("group member should be test", "test", g.getUsers().get(0).getName());
	}

	@Test
	public void removeUserFromGroup() {
		addUserToGroup();

		User u = UserManager.get("test");
		Group g = GroupManager.get("test");

		g.removeUser(u);

		g = GroupManager.update(g);

		assertEquals("group should have no user", 0, g.getUsers().size());
	}

	@Test
	public void savePackageCommon() {
		Package p = new Package(Package.Type.COMMON, "test", "Test Package");
		p.setStartupScript("scripts/testStartup.js");

		p = PackageManager.save(p);

		assertNotNull("package id should not null", p.getId());
		assertEquals("version id should be 1", new Integer(1), p.getVersion());
	}

	@Test
	public void updatePackageCommon() throws IOException {
		Package p = new Package(Package.Type.COMMON, "test", "Test Package");
		p.setStartupScript("scripts/testStartup.js");

		p = PackageManager.save(p);

		assertNotNull("package id should not null", p.getId());
		assertEquals("version id should be 1", new Integer(1), p.getVersion());

		p.setDescription("Updated Description");

		p = PackageManager.update(p);

		assertEquals("Updated Description", p.getDescription());

		assertEquals("version id should be 1", new Integer(1), p.getVersion());

		p.setStartupScript("scripts/testStartup-new.js");

		p = PackageManager.update(p);

		assertEquals("version id should increased", new Integer(2), p.getVersion());
	}

	@Test
	public void savePackageUser() {
		Package p = new Package(Package.Type.USER, "test", "Test Package");
		Function f = new Function("test", null, "alert(\"Hello World!\")");
		p.setFunction(f);

		p = PackageManager.save(p);

		assertNotNull("package id should not null", p.getId());
		assertEquals("function id should package id", p.getId(), f.getPackage().getId());
		assertEquals("version id should be 1", new Integer(1), p.getVersion());
	}

	@Test
	public void updatePackageUser() throws IOException {
		Package p = new Package(Package.Type.USER, "test", "Test Package");
		Function f = new Function("test", null, "alert(\"Hello World!\")");
		p.setFunction(f);

		p = PackageManager.save(p);

		assertNotNull("package id should not null", p.getId());
		assertEquals("function id should package id", p.getId(), f.getPackage().getId());
		assertEquals("version id should be 1", new Integer(1), p.getVersion());

		f.setCode("alert(\"Hello World!\");");

		p = PackageManager.update(p);

		assertEquals("version id should increased", new Integer(2), p.getVersion());
	}

}
