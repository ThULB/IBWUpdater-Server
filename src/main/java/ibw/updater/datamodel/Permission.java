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

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ibw.updater.datamodel.Permission.PermissionId;
import ibw.updater.persistency.PackageAdapter;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
@Entity
@Cacheable
@IdClass(value = PermissionId.class)
@Table(name = "IBWPermission")
@NamedQueries({ @NamedQuery(name = "Permission.findAll", query = "SELECT p FROM Permission p"),
		@NamedQuery(name = "Permission.findAllByPackage", query = "SELECT p FROM Permission p WHERE p.package = :package") })
@XmlRootElement(name = "permission")
public class Permission {
	/**
	 * The Source Type
	 * 
	 * @author Ren\u00E9 Adler (eagle)
	 *
	 */
	@XmlType(name = "permission.type")
	@XmlEnum
	public enum Type {

		@XmlEnumValue("g") GROUP("group"),

		@XmlEnumValue("u") USER("user");

		private String value;

		/**
		 * Returns a source type for given value.
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
		 * Returns the source type.
		 * 
		 * @return the set package type
		 */
		public String value() {
			return value;
		}
	}

	/**
	 * The Action Type
	 * 
	 * @author Ren\u00E9 Adler (eagle)
	 *
	 */
	@XmlType(name = "permission.action")
	@XmlEnum
	public enum Action {
		@XmlEnumValue("r") READ("read"),

		@XmlEnumValue("w") WRITE("write");

		private String value;

		/**
		 * Returns a action type for given value.
		 * 
		 * @param value
		 *            the action type value
		 * @return the action type {@link Permission.Action}
		 */
		public static Action fromValue(final String value) {
			for (Action action : Action.values()) {
				if (action.value.equals(value)) {
					return action;
				}
			}
			throw new IllegalArgumentException(value);
		}

		Action(final String value) {
			this.value = value;
		}

		/**
		 * Returns the permission action type.
		 * 
		 * @return the set permission action type
		 */
		public String value() {
			return value;
		}
	}

	private Type type;

	private int sourceId;

	private Action action;

	private Package _package;

	/**
	 * 
	 */
	public Permission() {
	}

	/**
	 * @param type
	 * @param sourceId
	 * @param action
	 * @param packageId
	 */
	public Permission(Type type, int sourceId, Action action, Package p) {
		this.type = type;
		this.sourceId = sourceId;
		this.action = action;
		this._package = p;
	}

	/**
	 * @return the type
	 */
	@Id
	@Column(name = "sourceType", nullable = false)
	@Enumerated(EnumType.STRING)
	@XmlAttribute(name = "sourceType", required = true)
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
	 * @return the sourceId
	 */
	@Id
	@Column(name = "sourceId", nullable = false)
	@XmlAttribute(name = "sourceId")
	public int getSourceId() {
		return sourceId;
	}

	/**
	 * @param sourceId
	 *            the sourceId to set
	 */
	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * @return the action
	 */
	@Id
	@Column(name = "action", nullable = false)
	@Enumerated(EnumType.STRING)
	@XmlAttribute(name = "action", required = true)
	public Action getAction() {
		return action;
	}

	/**
	 * @param action
	 *            the action to set
	 */
	public void setAction(Action action) {
		this.action = action;
	}

	/**
	 * @return the package
	 */
	@Id
	@OneToOne
	@JoinColumn(name = "packageId", nullable = false)
	@XmlAttribute(name = "packageId")
	@XmlJavaTypeAdapter(PackageAdapter.class)
	public Package getPackage() {
		return _package;
	}

	/**
	 * @param package
	 *            the package to set
	 */
	public void setPackage(Package p) {
		this._package = p;
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
		result = prime * result + ((_package == null) ? 0 : _package.hashCode());
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + sourceId;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Permission other = (Permission) obj;
		if (_package == null) {
			if (other._package != null)
				return false;
		} else if (!_package.equals(other._package))
			return false;
		if (action != other.action)
			return false;
		if (sourceId != other.sourceId)
			return false;
		if (type != other.type)
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
		return "Permission [type=" + type + ", sourceId=" + sourceId + ", action=" + action + ", package=" + _package
				+ "]";
	}

	public static class PermissionId implements Serializable {

		private static final long serialVersionUID = -3030317167265840962L;

		private Type type;

		private int sourceId;

		private Action action;

		private String packageId;

		/**
		 * 
		 */
		public PermissionId() {
		}

		public PermissionId(Permission permission) {
			this.type = permission.getType();
			this.sourceId = permission.getSourceId();
			this.packageId = permission.getPackage().getId();
			this.action = permission.getAction();
		}

		/**
		 * @return the type
		 */
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
		 * @return the sourceId
		 */
		public int getSourceId() {
			return sourceId;
		}

		/**
		 * @param sourceId
		 *            the sourceId to set
		 */
		public void setSourceId(int sourceId) {
			this.sourceId = sourceId;
		}

		/**
		 * @return the action
		 */
		public Action getAction() {
			return action;
		}

		/**
		 * @param action
		 *            the action to set
		 */
		public void setAction(Action action) {
			this.action = action;
		}

		/**
		 * @return the packageId
		 */
		public String getPackage() {
			return packageId;
		}

		/**
		 * @param packageId
		 *            the packageId to set
		 */
		public void setPackage(String packageId) {
			this.packageId = packageId;
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
			result = prime * result + ((packageId == null) ? 0 : packageId.hashCode());
			result = prime * result + ((action == null) ? 0 : action.hashCode());
			result = prime * result + sourceId;
			result = prime * result + ((type == null) ? 0 : type.hashCode());
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
			PermissionId other = (PermissionId) obj;
			if (packageId == null) {
				if (other.packageId != null)
					return false;
			} else if (!packageId.equals(other.packageId))
				return false;
			if (action != other.action)
				return false;
			if (sourceId != other.sourceId)
				return false;
			if (type != other.type)
				return false;
			return true;
		}
	}
}
