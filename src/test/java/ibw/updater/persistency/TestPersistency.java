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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ibw.updater.backend.jpa.JPATestCase;
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

}
