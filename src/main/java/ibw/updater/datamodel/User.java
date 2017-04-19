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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.lambdaworks.crypto.SCryptUtil;

import ibw.updater.datamodel.adapter.GroupAdapter;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */

@Entity
@Cacheable
@Table(name = "IBWUser")
@NamedQueries({ @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
		@NamedQuery(name = "User.findByName", query = "SELECT u FROM User u WHERE u.name = :name") })
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "user")
public class User {

	private int id;

	private String name;

	private String password;

	private String description;

	private List<Group> groups;

	/**
	 * 
	 */
	public User() {
	}

	/**
	 * @param name
	 * @param description
	 */
	public User(String name, String description) {
		this.name = name;
		this.description = description;
	}

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@XmlAttribute(name = "id")
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	@Column(name = "name", length = 64, nullable = false)
	@XmlAttribute(name = "name", required = true)
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the password
	 */
	@Column(name = "password", length = 255, nullable = true)
	@XmlElement(name = "password")
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		if (password != null && !password.startsWith("$s0$")) {
			this.password = SCryptUtil.scrypt(password, 16, 16, 16);
		} else {
			this.password = password;
		}
	}

	public boolean isValidPassword(String password) {
		try {
			return SCryptUtil.check(password, this.password);
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * @return the description
	 */
	@Column(name = "description", length = 4096)
	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the groups
	 */
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "IBWGroupMember", joinColumns = {
			@JoinColumn(name = "uid", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "gid", referencedColumnName = "id") })
	@XmlElement(name = "group")
	@XmlJavaTypeAdapter(GroupAdapter.class)
	public List<Group> getGroups() {
		return groups;
	}

	/**
	 * @param groups
	 *            the groups to set
	 */
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	/**
	 * @param group
	 *            the group to add
	 */
	@Transient
	public void addGroup(Group group) {
		if (groups == null) {
			groups = new ArrayList<>();
		}
		groups.add(group);
	}

	/**
	 * Removes group membership of user.
	 * 
	 * @param group
	 *            the group membership to remove
	 */
	@Transient
	public void removeGroup(Group group) {
		if (groups != null) {
			groups = groups.stream().filter(g -> group.getId() != 0 ? g.getId() != group.getId()
					: !g.getName().equalsIgnoreCase(group.getName())).collect(Collectors.toList());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equalsIgnoreCase(other.name))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", description=" + description + ", groups=" + groups + "]";
	}
}
