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

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlAttribute;
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

	private String packageId;

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
	public Permission(Type type, int sourceId, Action action, String packageId) {
		this.type = type;
		this.sourceId = sourceId;
		this.action = action;
		this.packageId = packageId;
	}

	/**
	 * @return the type
	 */
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
	 * @return the packageId
	 */
	@Column(name = "packageId", length = 36, nullable = false)
	@XmlAttribute(name = "packageId")
	public String getPackageId() {
		return packageId;
	}

	/**
	 * @param packageId
	 *            the packageId to set
	 */
	public void setPackageId(String packageId) {
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
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((packageId == null) ? 0 : packageId.hashCode());
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
		if (action != other.action)
			return false;
		if (packageId == null) {
			if (other.packageId != null)
				return false;
		} else if (!packageId.equals(other.packageId))
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
		return "Permission [type=" + type + ", sourceId=" + sourceId + ", action=" + action + ", packageId=" + packageId
				+ "]";
	}

}
