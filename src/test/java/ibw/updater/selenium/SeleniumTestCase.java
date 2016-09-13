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

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.io.TemporaryFilesystem;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ibw.updater.Application;
import io.github.bonigarcia.wdm.MarionetteDriverManager;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
public class SeleniumTestCase {

	protected static final long MAX_WAIT_TIME = 5;

	private static File profileDir;

	protected WebDriver driver;

	@BeforeClass
	public static void setupClass() {
		File configDir = new File(
				System.getProperty("java.io.tmpdir") + File.separator + SeleniumTestCase.class.getSimpleName());
		if (!configDir.exists()) {
			configDir.mkdirs();
		}

		File tmpDir = new File(configDir, "tmp");
		if (!tmpDir.exists()) {
			tmpDir.mkdirs();
		}
		TemporaryFilesystem.setTemporaryDirectory(tmpDir);

		profileDir = new File(configDir, "profile");
		if (!profileDir.exists()) {
			profileDir.mkdirs();
		}

		Application.main(new String[] { "--configDir", configDir.getAbsolutePath() });

		MarionetteDriverManager.getInstance().setup();
	}

	@Before
	public void setupUp() throws Exception {
		driver = new FirefoxDriver(new FirefoxProfile(profileDir));
		driver.get("http://" + getHostName() + ":8085");
	}

	@After
	public void tearDown() throws Exception {
		if (driver != null) {
			driver.quit();
		}
	}

	public WebElement waitAndClick(By by) {
		WebDriverWait wait = new WebDriverWait(driver, MAX_WAIT_TIME);
		wait.until(ExpectedConditions.elementToBeClickable(by));
		WebElement elm = driver.findElement(by);
		elm.click();

		return elm;
	}

	public WebElement waitForElement(By by) {
		WebDriverWait wait = new WebDriverWait(driver, MAX_WAIT_TIME);
		wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		WebElement elm = driver.findElement(by);
		return elm;
	}

	private String getHostName() {
		String hostName = "localhost";
		try {
			hostName = InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return hostName;
	}
}
