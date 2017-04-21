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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class Users.
 *
 * @author Ren\u00E9 Adler (eagle)
 */
@XmlRootElement(name = "users")
public class Users {
	
	private List<User> users;

	/**
	 * Instantiates a new users.
	 */
	public Users() {

	}

	/**
	 * Instantiates a new users.
	 *
	 * @param users
	 *            the users
	 */
	public Users(List<User> users) {
		this.users = users;
	}

	/**
	 * Gets the users.
	 *
	 * @return the users
	 */
	@XmlElement(name = "user")
	public List<User> getUsers() {
		return users;
	}

	/**
	 * Sets the users.
	 *
	 * @param users
	 *            the users to set
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

}
