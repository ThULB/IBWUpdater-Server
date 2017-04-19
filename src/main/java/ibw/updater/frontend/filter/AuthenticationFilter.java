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
package ibw.updater.frontend.filter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.internal.util.Base64;

import ibw.updater.access.UserPermission;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
public class AuthenticationFilter implements ContainerRequestFilter {

	public static final Response ACCESS_DENIED = Response.status(Response.Status.UNAUTHORIZED)
			.header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"IBWUpdater Server Login\"")
			.header("Access-Control-Allow-Origin", "*")
			.header("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS")
			.header("Access-Control-Allow-Headers", "X-Requested-With, content-type")
			.header("Access-Control-Allow-Credentials", "true").build();
	public static final Response ACCESS_FORBIDDEN = Response.status(Response.Status.FORBIDDEN).build();

	public static final String PROPERTY_UID = "username";
	public static final String PROPERTY_PWD = "password";

	private static final Logger LOGGER = LogManager.getLogger();

	private static final String AUTHORIZATION_PROPERTY = "Authorization";
	private static final String AUTHENTICATION_SCHEME = "Basic";

	@Context
	private ResourceInfo resourceInfo;

	public static Map<String, String> getAuthenticatedUserInfo(ContainerRequestContext requestContext) {
		final List<String> authorization = requestContext.getHeaders().get(AuthenticationFilter.AUTHORIZATION_PROPERTY);

		if (authorization == null || authorization.isEmpty()) {
			return null;
		}

		final String encodedUserPassword = authorization.get(0)
				.replaceFirst(AuthenticationFilter.AUTHENTICATION_SCHEME + " ", "");

		String usernameAndPassword = new String(Base64.decode(encodedUserPassword.getBytes(StandardCharsets.UTF_8)),
				StandardCharsets.UTF_8);

		final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");

		if (!tokenizer.hasMoreTokens()) {
			return null;
		}

		Map<String, String> userInfo = new HashMap<>();
		userInfo.put(PROPERTY_UID, tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null);
		userInfo.put(PROPERTY_PWD, tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null);

		return userInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container
	 * .ContainerRequestContext)
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		Method method = resourceInfo.getResourceMethod();
		// Access allowed for all
		if (!method.isAnnotationPresent(PermitAll.class)) {
			// Access denied for all
			if (method.isAnnotationPresent(DenyAll.class)) {
				LOGGER.info("Access denied for all users.");
				requestContext.abortWith(ACCESS_FORBIDDEN);
				return;
			}

			Map<String, String> userInfo = getAuthenticatedUserInfo(requestContext);
			if (userInfo == null) {
				LOGGER.debug("User needs authentication.");
				requestContext.abortWith(ACCESS_DENIED);
				return;
			}

			// Verify user access
			if (method.isAnnotationPresent(RolesAllowed.class)) {
				RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
				Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));

				// Is user valid?
				if (!UserPermission.isUserAllowed(userInfo.get(AuthenticationFilter.PROPERTY_UID),
						userInfo.get(AuthenticationFilter.PROPERTY_PWD), rolesSet)) {
					LOGGER.info("Access denied for user \"" + userInfo.get(AuthenticationFilter.PROPERTY_UID)
							+ "\" and roles " + rolesSet + ".");
					requestContext.abortWith(ACCESS_DENIED);
					return;
				}
			}
		}
	}
}
