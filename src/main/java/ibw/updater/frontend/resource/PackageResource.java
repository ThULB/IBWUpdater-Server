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

import java.io.InputStream;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import ibw.updater.access.UserPermission;
import ibw.updater.datamodel.Package;
import ibw.updater.frontend.entity.ExceptionWrapper;
import ibw.updater.frontend.entity.ResourceWrapper;
import ibw.updater.persistency.PackageManager;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
@Path("")
@Singleton
public class PackageResource {

	private static final Logger LOGGER = LogManager.getLogger();

	@GET
	@Path("packages")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@PermitAll
	public Response listExtended(@QueryParam("uid") String uid) {
		return Response.ok().entity(PackageManager.getExtended(uid)).build();
	}

	@GET
	@Path("packages/{fileName:.+}")
	@Produces("*/*")
	@PermitAll
	public Response getPackageFile(@PathParam("fileName") String fileName) {
		try {
			ResourceWrapper r = new ResourceWrapper(fileName, PackageManager.getContentByFileName(fileName));

			CacheControl cc = new CacheControl();
			cc.setNoCache(true);
			cc.setNoStore(true);

			return Response.ok().tag(r.getETag()).type(r.getMimeType()).entity(r.getContent()).cacheControl(cc).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ExceptionWrapper(e)).build();
		}
	}

	@GET
	@Path("manage/packages")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@RolesAllowed(UserPermission.ADMINISTRATOR)
	public Response list() {
		return Response.ok().entity(PackageManager.get()).build();
	}

	@POST
	@Path("manage/packages/add")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@RolesAllowed(UserPermission.ADMINISTRATOR)
	public Response add(final Package p) {
		try {
			return Response.ok().entity(PackageManager.save(p)).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ExceptionWrapper(e)).build();
		}
	}

	@POST
	@Path("manage/packages/add")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@RolesAllowed(UserPermission.ADMINISTRATOR)
	public Response add(@FormDataParam("package") FormDataBodyPart obj, @FormDataParam("file") InputStream is) {
		try {
			Package p = obj.getValueAs(Package.class);
			return Response.ok().entity(PackageManager.save(p, is)).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ExceptionWrapper(e)).build();
		}
	}

	@POST
	@Path("manage/packages/update")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@RolesAllowed(UserPermission.ADMINISTRATOR)
	public Response update(final Package p) {
		try {
			return Response.ok().entity(PackageManager.update(p)).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ExceptionWrapper(e)).build();
		}
	}

	@POST
	@Path("manage/packages/update")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@RolesAllowed(UserPermission.ADMINISTRATOR)
	public Response update(@FormDataParam("package") FormDataBodyPart obj, @FormDataParam("file") InputStream is) {
		try {
			Package p = obj.getValueAs(Package.class);
			return Response.ok().entity(PackageManager.update(p, is)).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ExceptionWrapper(e)).build();
		}
	}

	@POST
	@Path("manage/packages/delete")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@RolesAllowed(UserPermission.ADMINISTRATOR)
	public Response delete(final Package p) {
		try {
			PackageManager.delete(p.getId());
			return Response.ok().build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ExceptionWrapper(e)).build();
		}
	}

	@DELETE
	@Path("manage/packages/delete/{pid}")
	@RolesAllowed(UserPermission.ADMINISTRATOR)
	public Response delete(@PathParam("pid") final String pid) {
		try {
			PackageManager.delete(pid);
			return Response.ok().build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ExceptionWrapper(e)).build();
		}
	}
}
