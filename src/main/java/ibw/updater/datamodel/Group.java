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

import ibw.updater.datamodel.adapter.UserAdapter;

/**
 * The Class Group.
 *
 * @author Ren\u00E9 Adler (eagle)
 */
@Entity
@Cacheable
@Table(name = "IBWGroup")
@NamedQueries({ @NamedQuery(name = "Group.findAll", query = "SELECT g FROM Group g"),
		@NamedQuery(name = "Group.findByName", query = "SELECT g FROM Group g WHERE g.name = :name") })
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "group")
public class Group {

	private int id;

	private String name;

	private String description;

	private List<User> users;

	/**
	 * Instantiates a new group.
	 */
	public Group() {
	}

	/**
	 * Instantiates a new group.
	 *
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	public Group(String name, String description) {
		this.name = name;
		this.description = description;
	}

	/**
	 * Gets the id.
	 *
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
	 * Sets the id.
	 *
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Column(name = "name", length = 64, nullable = false)
	@XmlAttribute(name = "name", required = true)
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	@Column(name = "description", length = 4096, nullable = false)
	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the users.
	 *
	 * @return the users
	 */
	@OneToMany
	@JoinTable(name = "IBWGroupMember", joinColumns = {
			@JoinColumn(name = "gid", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "uid", referencedColumnName = "id") })
	@XmlElement(name = "user")
	@XmlJavaTypeAdapter(UserAdapter.class)
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

	/**
	 * Adds the user.
	 *
	 * @param user
	 *            the user to add
	 */
	@Transient
	public void addUser(User user) {
		if (this.users == null) {
			this.users = new ArrayList<>();
		}
		this.users.add(user);
	}

	/**
	 * Removes user membership from group.
	 *
	 * @param user
	 *            the user to remove
	 */
	@Transient
	public void removeUser(User user) {
		if (users != null) {
			users = users.stream().filter(
					u -> user.getId() != 0 ? u.getId() != user.getId() : !u.getName().equalsIgnoreCase(user.getName()))
					.collect(Collectors.toList());
		}
	}

	/**
	 * Checks if given user is member of {@link Group}.
	 * 
	 * @param user
	 *            the {@link User}
	 * @return <code>true</code> if user is group member, <code>false</code>
	 *         isn't
	 */
	@Transient
	public boolean isMember(User user) {
		if (users != null) {
			return users.stream().filter(u -> u.getId() == user.getId() || u.getName().equalsIgnoreCase(user.getName()))
					.count() != 0;
		}

		return false;
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
		Group other = (Group) obj;
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
		return "Group [id=" + id + ", name=" + name + ", description=" + description + "]";
	}

}
