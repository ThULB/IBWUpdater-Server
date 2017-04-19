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
package ibw.updater.access;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ibw.updater.common.config.Configuration;
import ibw.updater.common.events.annotation.AutoExecutable;
import ibw.updater.common.events.annotation.Startup;
import ibw.updater.datamodel.Group;
import ibw.updater.datamodel.User;
import ibw.updater.persistency.GroupManager;
import ibw.updater.persistency.UserManager;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
@AutoExecutable(name = "Init Superuser")
public class UserPermission {

	public static final String ADMINISTRATOR = "Administrator";

	private static final Logger LOGGER = LogManager.getLogger();

	private static final String ROLES_PREFIX = "Resource.Roles.";

	private static final Configuration CONFIG = Configuration.instance();

	@Startup
	public static void initSuperuser() {
		String suname = CONFIG.getString("APP.SuperUser");
		if (!UserManager.exists(suname)) {
			Group sugrp = new Group(CONFIG.getString("APP.SuperGroup"), "Superuser Group");
			GroupManager.save(sugrp);

			User su = new User(suname, "Superuser");
			String password = new BigInteger(130, new SecureRandom()).toString(32);
			su.setPassword(password);
			su.addGroup(sugrp);
			UserManager.save(su);

			outputSuperuser(suname, password);
		}
	}

	public static boolean isUserAllowed(final String username, final String password,
			final Collection<String> rolesSet) {
		boolean isAllowed = false;

		User user = UserManager.get(username);

		isAllowed = user.isValidPassword(password) && (rolesSet.contains("*") || rolesSet.stream()
				// read role from configuration
				.map(role -> CONFIG.getStrings(ROLES_PREFIX + role).stream()
						// match roles with defined groups and return count
						.filter(cr -> user != null && user.getGroups() != null
								&& user.getGroups().stream().filter(g -> g.getName().equalsIgnoreCase(cr)).count() != 0)
						.count())
				// if any found with count greater zero user is allowed
				.filter(c -> c != 0).findFirst().isPresent());

		return isAllowed;
	}

	private static void outputSuperuser(String username, String password) {
		final StringBuffer sb = new StringBuffer();

		sb.append("\n\n" + String.format(Locale.ROOT, String.format(Locale.ROOT, "%%0%dd", 80), 0).replace("0", "=")
				+ "\n");
		sb.append(" Init Superuser");
		sb.append("\n" + String.format(Locale.ROOT, String.format(Locale.ROOT, "%%0%dd", 80), 0).replace("0", "=")
				+ "\n\n");

		sb.append(" Username: " + username + "\n");

		if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win"))
			sb.append(" Password: " + password);
		else
			sb.append(" Password: \u001b[41m\u001b[1;37m" + password + "\u001b[m");

		sb.append("\n\n" + String.format(Locale.ROOT, String.format(Locale.ROOT, "%%0%dd", 80), 0).replace("0", "="));

		LOGGER.info(sb.toString());
	}
}
