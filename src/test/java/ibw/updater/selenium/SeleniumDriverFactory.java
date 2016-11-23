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
package ibw.updater.selenium;

import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.opera.OperaDriver;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.EdgeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import io.github.bonigarcia.wdm.InternetExplorerDriverManager;
import io.github.bonigarcia.wdm.OperaDriverManager;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
public class SeleniumDriverFactory {

	public final static String CHROME_DRIVER = "chrome";

	public final static String EDGE_DRIVER = "edge";

	public final static String FIREFOX_DRIVER = "firefox";

	public final static String IE_DRIVER = "ie";

	public final static String OPERA_DRIVER = "opera";

	private final static String SELENIUM_DRIVER_ENV = "SELENIUM_DRIVER";

	private final static String SELENIUM_DRIVER_PROP = "seleniumDriver";

	private final String driverName;

	public SeleniumDriverFactory() {
		this(Optional.ofNullable(System.getenv(SELENIUM_DRIVER_ENV)).orElse(System.getProperty(SELENIUM_DRIVER_PROP)));
	}

	public SeleniumDriverFactory(String driverName) {
		this.driverName = driverName;
		if (CHROME_DRIVER.equalsIgnoreCase(driverName)) {
			ChromeDriverManager.getInstance().setup();
		} else if (EDGE_DRIVER.equalsIgnoreCase(driverName)) {
			EdgeDriverManager.getInstance().setup();
		} else if (IE_DRIVER.equalsIgnoreCase(driverName)) {
			InternetExplorerDriverManager.getInstance().setup();
		} else if (OPERA_DRIVER.equalsIgnoreCase(driverName)) {
			OperaDriverManager.getInstance().setup();
		} else {
			FirefoxDriverManager.getInstance().setup();
		}
	}

	public WebDriver driver() {
		if (CHROME_DRIVER.equalsIgnoreCase(driverName)) {
			return new ChromeDriver();
		} else if (EDGE_DRIVER.equalsIgnoreCase(driverName)) {
			return new EdgeDriver();
		} else if (IE_DRIVER.equalsIgnoreCase(driverName)) {
			return new EdgeDriver();
		} else if (OPERA_DRIVER.equalsIgnoreCase(driverName)) {
			return new OperaDriver();
		}

		return new FirefoxDriver();
	}
}
