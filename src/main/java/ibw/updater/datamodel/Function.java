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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
@Entity
@Cacheable
@Table(name = "IBWFunction")
@XmlRootElement(name = "function")
public class Function {

	private Package packAge;

	private String name;

	private String params;

	private String code;

	/**
	 * 
	 */
	public Function() {
	}

	/**
	 * @param name
	 * @param params
	 * @param code
	 */
	public Function(String name, String params, String code) {
		this.name = name;
		this.params = params;
		this.code = code;
	}

	/**
	 * @param packageId
	 * @param name
	 * @param description
	 * @param params
	 * @param code
	 */
	public Function(Package packAge, String name, String params, String code) {
		this.packAge = packAge;
		this.name = name;
		this.params = params;
		this.code = code;
	}

	/**
	 * @return the package
	 */
	@Id
	@OneToOne
	@JoinColumn(name = "packageId")
	@XmlTransient
	public Package getPackage() {
		return packAge;
	}

	/**
	 * @param packAge
	 *            the package to set
	 */
	public void setPackage(Package packAge) {
		this.packAge = packAge;
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
	 * @return the params
	 */
	@Column(name = "params", length = 4096)
	@XmlAttribute(name = "params")
	public String getParams() {
		return params;
	}

	/**
	 * @param params
	 *            the params to set
	 */
	public void setParams(String params) {
		this.params = params;
	}

	/**
	 * @return the code
	 */
	@Column(name = "code", length = Integer.MAX_VALUE, nullable = false)
	@XmlValue
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
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
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((params == null) ? 0 : params.hashCode());
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
		Function other = (Function) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (params == null) {
			if (other.params != null)
				return false;
		} else if (!params.equals(other.params))
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
		return "Function [name=" + name + ", params=" + params + ", code=" + code + "]";
	}

}
