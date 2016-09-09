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

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ibw.updater.datamodel.Permission;
import ibw.updater.datamodel.Permissions;
import ibw.updater.frontend.entity.ExceptionWrapper;
import ibw.updater.persistency.PermissionManager;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
@Path("manage/permissions")
@Singleton
public class PermissionResource {

	private static final Logger LOGGER = LogManager.getLogger();

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response list() {
		try {
			return Response.ok().entity(PermissionManager.get()).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ExceptionWrapper(e)).build();
		}
	}

	@GET
	@Path("{packageId}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response list(@PathParam("packageId") final String packageId) {
		try {
			return Response.ok().entity(PermissionManager.get(packageId)).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ExceptionWrapper(e)).build();
		}
	}

	@POST
	@Path("update")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response update(final Permissions permissions) {
		try {
			LOGGER.info("Save/Update permissions");
			return Response.ok().entity(PermissionManager.update(permissions)).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ExceptionWrapper(e)).build();
		}
	}

	@POST
	@Path("delete")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response delete(final Permission permission) {
		try {
			LOGGER.info("Remove " + permission);
			PermissionManager.delete(permission);
			return Response.ok().build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ExceptionWrapper(e)).build();
		}
	}
}
