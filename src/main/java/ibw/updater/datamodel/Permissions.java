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
 * The Class Permissions.
 *
 * @author Ren\u00E9 Adler (eagle)
 */
@XmlRootElement(name = "permissions")
public class Permissions {

	private List<Permission> permissions;

	/**
	 * Instantiates a new permissions.
	 */
	public Permissions() {
	}

	/**
	 * Instantiates a new permissions.
	 *
	 * @param permissions
	 *            the permissions
	 */
	public Permissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	/**
	 * Gets the permissions.
	 *
	 * @return the permissions
	 */
	@XmlElement(name = "permission")
	public List<Permission> getPermissions() {
		return permissions;
	}

	/**
	 * Sets the permissions.
	 *
	 * @param permissions
	 *            the permissions to set
	 */
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

}
