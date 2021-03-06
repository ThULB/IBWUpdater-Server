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
package ibw.updater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import ibw.updater.common.config.ConfigurationDir;
import ibw.updater.common.events.AutoExecutableHandler;
import ibw.updater.service.EmbeddedHttpServer;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
public class Application {
	private static final Logger LOGGER = LogManager.getRootLogger();

	@Parameter(names = { "-h", "--help" }, description = "Print help (this message) and exit", help = true)
	private boolean help;

	@Parameter(names = { "--port", "-p" }, description = "Set port listen on")
	private Integer port = 8085;

	@Parameter(names = "--host", description = "Set host listen on")
	private String host;

	@Parameter(names = { "--configDir", "-cd" }, description = "Set configuration dir")
	private String configDir;

	public static void main(String[] args) {
		Application app = new Application();
		JCommander jcmd = new JCommander(app, null, args);

		if (app.help) {
			jcmd.usage();
		} else {
			try {
				app.run();
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	private void run() throws Exception {
		if (configDir != null) {
			ConfigurationDir.setConfigurationDirectory(configDir);
		}

		AutoExecutableHandler.setHaltOnError(false);
		AutoExecutableHandler.startup();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				AutoExecutableHandler.shutdown();
			}
		});

		EmbeddedHttpServer embeddedHttpServer = new EmbeddedHttpServer(host, port);
		embeddedHttpServer.start();
	}
}
