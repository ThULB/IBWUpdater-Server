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

import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
@Entity
@Cacheable
@Table(name = "IBWPackage")
@NamedQueries({ @NamedQuery(name = "Package.findAll", query = "SELECT p FROM Package p"),
		@NamedQuery(name = "Package.findByName", query = "SELECT p FROM Package p WHERE p.name = :name") })
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "package")
public class Package {
	/**
	 * The Package Type
	 * 
	 * @author Ren\u00E9 Adler (eagle)
	 *
	 */
	@XmlType(name = "package.type")
	@XmlEnum
	public enum Type {
		@XmlEnumValue("common") COMMON("common"),

		@XmlEnumValue("user") USER("user");

		private String value;

		/**
		 * Returns a package type for given value.
		 * 
		 * @param value
		 *            the package type value
		 * @return the package type {@link Package.Type}
		 */
		public static Type fromValue(final String value) {
			for (Type type : Type.values()) {
				if (type.value.equals(value)) {
					return type;
				}
			}
			throw new IllegalArgumentException(value);
		}

		Type(final String value) {
			this.value = value;
		}

		/**
		 * Returns the package type.
		 * 
		 * @return the set package type
		 */
		public String value() {
			return value;
		}
	}

	private String id;

	private Type type;

	private Integer version;

	private String name;

	private String description;

	private String url;

	private String startupScript;

	private Function function;

	/**
	 * 
	 */
	public Package() {
	}

	/**
	 * @param type
	 * @param name
	 * @param description
	 */
	public Package(Type type, String name, String description) {
		this.type = type;
		this.name = name;
		this.description = description;
	}

	/**
	 * @return the id
	 */
	@Id
	@Column(name = "id", length = 36, nullable = false)
	@XmlAttribute(name = "id")
	public String getId() {
		if (id == null) {
			id = UUID.randomUUID().toString();
		}
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.STRING)
	@XmlAttribute(name = "type", required = true)
	public Type getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * @return the version
	 */
	@Column(name = "version", nullable = false)
	@XmlAttribute(name = "version")
	public Integer getVersion() {
		if (version == null) {
			version = 1;
		}
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(Integer version) {
		this.version = version;
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
	 * @return the url
	 */
	@Column(name = "url", length = 4096)
	@XmlElement(name = "url")
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the startupScript
	 */
	@Column(name = "startupScript", length = 2048)
	@XmlElement(name = "startupScript")
	public String getStartupScript() {
		return startupScript;
	}

	/**
	 * @param startupScript
	 *            the startupScript to set
	 */
	public void setStartupScript(String startupScript) {
		this.startupScript = startupScript;
	}

	/**
	 * @return the function
	 */
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "package")
	@XmlElement(name = "function")
	public Function getFunction() {
		return function;
	}

	/**
	 * @param function
	 *            the function to set
	 */
	public void setFunction(Function function) {
		if (function != null && function.getPackage() == null) {
			function.setPackage(this);
		}
		this.function = function;
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
		result = prime * result + ((function == null) ? 0 : function.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((startupScript == null) ? 0 : startupScript.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		Package other = (Package) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (function == null) {
			if (other.function != null)
				return false;
		} else if (!function.equals(other.function))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (startupScript == null) {
			if (other.startupScript != null)
				return false;
		} else if (!startupScript.equals(other.startupScript))
			return false;
		if (type != other.type)
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
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
		return "Package [id=" + id + ", type=" + type + ", version=" + version + ", name=" + name + ", description="
				+ description + "]";
	}

}
