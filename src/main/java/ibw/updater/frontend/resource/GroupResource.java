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

import java.io.OutputStream;
import java.io.PrintStream;

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
import javax.ws.rs.core.StreamingOutput;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ibw.updater.datamodel.Group;
import ibw.updater.persistency.GroupManager;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
@Path("manage/groups")
@Singleton
public class GroupResource {

	private static final Logger LOGGER = LogManager.getLogger();

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response list() {
		return Response.ok().status(Response.Status.OK).entity(GroupManager.get()).build();
	}

	@POST
	@Path("add")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response add(final Group group) {
		try {
			LOGGER.info("Add " + group);
			GroupManager.save(group);
			return Response.ok().status(Response.Status.OK).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			final StreamingOutput so = (OutputStream os) -> e.printStackTrace(new PrintStream(os));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(so).build();
		}
	}

	@POST
	@Path("edit")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response edit(final Group group) {
		try {
			LOGGER.info("Edit " + group);
			GroupManager.update(group);
			return Response.ok().status(Response.Status.OK).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			final StreamingOutput so = (OutputStream os) -> e.printStackTrace(new PrintStream(os));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(so).build();
		}
	}

	@POST
	@Path("delete")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response delete(final Group group) {
		try {
			LOGGER.info("Remove " + group);
			GroupManager.delete(group);
			return Response.ok().status(Response.Status.OK).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			final StreamingOutput so = (OutputStream os) -> e.printStackTrace(new PrintStream(os));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(so).build();
		}
	}

	@DELETE
	@Path("delete/{gid}")
	public Response delete(@PathParam("gid") final int gid) {
		try {
			LOGGER.info("Remove group with id " + gid);
			GroupManager.delete(gid);
			return Response.ok().status(Response.Status.OK).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			final StreamingOutput so = (OutputStream os) -> e.printStackTrace(new PrintStream(os));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(so).build();
		}
	}
}
