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
package ibw.updater.frontend.resource;

import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ibw.updater.access.UserPermission;
import ibw.updater.datamodel.User;
import ibw.updater.frontend.entity.ExceptionWrapper;
import ibw.updater.persistency.UserManager;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
@Path("manage/users")
@Singleton
public class UserResource {

	private static final Logger LOGGER = LogManager.getLogger();

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@RolesAllowed(UserPermission.ADMINISTRATOR)
	public Response list() {
		return Response.ok().entity(UserManager.get()).build();
	}

	@POST
	@Path("add")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@RolesAllowed(UserPermission.ADMINISTRATOR)
	public Response add(final User user) {
		try {
			return Response.ok().entity(UserManager.save(user)).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ExceptionWrapper(e)).build();
		}
	}

	@POST
	@Path("update")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@RolesAllowed(UserPermission.ADMINISTRATOR)
	public Response update(final User user) {
		try {
			return Response.ok().entity(UserManager.update(user)).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ExceptionWrapper(e)).build();
		}
	}

	@POST
	@Path("delete")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@RolesAllowed(UserPermission.ADMINISTRATOR)
	public Response delete(final User user) {
		try {
			UserManager.delete(user);
			return Response.ok().build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ExceptionWrapper(e)).build();
		}
	}

	@DELETE
	@Path("delete/{uid}")
	@RolesAllowed(UserPermission.ADMINISTRATOR)
	public Response delete(@PathParam("uid") final int uid) {
		try {
			UserManager.delete(uid);
			return Response.ok().build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ExceptionWrapper(e)).build();
		}
	}

}
