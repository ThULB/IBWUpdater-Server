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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
public class TestWebIF extends SeleniumTestCase {

	private static final String TU_NAME = "test";
	private static final String TU_DESCRIPTION = "Test User";
	private static final String TU_DESCRIPTION_UPDATE = "Selenium Test User";

	private static final String TG_NAME = "test";
	private static final String TG_DESCRIPTION = "Test Group";
	private static final String TG_DESCRIPTION_UPDATE = "Selenium Test Group";

	private static final String TP_NAME = "test";
	private static final String TP_DESCRIPTION = "Test Package";
	private static final String TP_DESCRIPTION_UPDATE = "Selenium Test Package";
	private static final String TP_PACKAGE_SRC = "https://github.com/adlerre/IBWUpdater-Client/releases/download/1.1.0/IBWUpdater-Client.zip";

	@Test
	public void testCreateUser() throws InterruptedException {
		createUser();
		driver.navigate().refresh();

		deleteUser();
	}

	@Test
	public void testEditUser() throws InterruptedException {
		createGroup();
		driver.navigate().refresh();

		createUser();
		driver.navigate().refresh();

		waitAndClick(By.xpath("//tbody/tr[1]/td[1]//button[starts-with(@ng-click, 'showUserDialog')]"));

		waitForElement(By.id("user-dialog"));

		WebElement description = waitForElement(By.id("description"));
		description.clear();
		description.sendKeys(TU_DESCRIPTION_UPDATE);
		waitAndClick(By.cssSelector("#groups option"));

		waitAndClick(By.cssSelector(".modal-footer button.btn-primary"));

		Thread.sleep(MAX_WAIT_TIME * 1000);
		driver.navigate().refresh();

		description = waitForElement(By.xpath("//tbody/tr[1]/td[3]"));
		WebElement group = waitForElement(By.xpath("//tbody/tr[1]/td[4]"));

		assertEquals(TU_DESCRIPTION_UPDATE, description.getText());
		assertEquals(TG_NAME, group.getText());

		deleteUser();
		driver.navigate().refresh();

		deleteGroup();
	}

	@Test
	public void testCreateGroup() throws InterruptedException {
		createGroup();
		driver.navigate().refresh();

		deleteGroup();
	}

	@Test
	public void testEditGroup() throws InterruptedException {
		createUser();
		driver.navigate().refresh();

		createGroup();
		driver.navigate().refresh();

		waitAndClick(By.xpath("//tbody/tr[1]/td[1]//button[starts-with(@ng-click, 'showGroupDialog')]"));

		waitForElement(By.id("group-dialog"));

		WebElement description = waitForElement(By.id("description"));

		description.clear();
		description.sendKeys(TG_DESCRIPTION_UPDATE);

		waitAndClick(By.cssSelector("#users option"));

		waitAndClick(By.cssSelector(".modal-footer button.btn-primary"));

		Thread.sleep(MAX_WAIT_TIME * 1000);

		description = waitForElement(By.xpath("//tbody/tr[1]/td[3]"));
		WebElement user = waitForElement(By.xpath("//tbody/tr[1]/td[4]"));

		assertEquals(TG_DESCRIPTION_UPDATE, description.getText());
		assertEquals(TU_NAME, user.getText());

		deleteGroup();
		driver.navigate().refresh();

		deleteUser();
	}

	@Test
	public void testCreateCommonPackage() {
		createCommonPackage();
		driver.navigate().refresh();

		deletePackage();
	}

	@Test
	public void testEditCommonPackage() throws InterruptedException {
		createCommonPackage();
		driver.navigate().refresh();

		waitAndClick(By.xpath("//tbody/tr[1]/td[1]//button[starts-with(@ng-click, 'showPackageDialog')]"));

		waitForElement(By.id("package-dialog"));

		WebElement description = waitForElement(By.id("description"));
		description.clear();
		description.sendKeys(TP_DESCRIPTION_UPDATE);

		waitAndClick(By.cssSelector(".modal-footer button.btn-primary"));

		Thread.sleep(MAX_WAIT_TIME * 1000);

		WebElement version = waitForElement(By.xpath("//tbody/tr[1]/td[3]"));
		description = waitForElement(By.xpath("//tbody/tr[1]/td[5]"));

		assertEquals(new Integer(1), new Integer(version.getText()));
		assertEquals(TP_DESCRIPTION_UPDATE, description.getText());

		driver.navigate().refresh();

		waitAndClick(By.xpath("//tbody/tr[1]/td[1]//button[starts-with(@ng-click, 'showPackageDialog')]"));

		waitForElement(By.id("package-dialog"));

		WebElement startupScript = waitForElement(By.id("startupScript"));
		startupScript.clear();
		startupScript.sendKeys("scripts/startupScript-new.js");

		waitAndClick(By.cssSelector(".modal-footer button.btn-primary"));

		Thread.sleep(MAX_WAIT_TIME * 1000);

		version = waitForElement(By.xpath("//tbody/tr[1]/td[3]"));
		assertEquals(new Integer(2), new Integer(version.getText()));

		deletePackage();
	}

	@Test
	public void testCreateUserPackage() {
		createUserPackage();
		driver.navigate().refresh();

		deletePackage();
	}

	@Test
	public void testEditUserPackage() throws InterruptedException {
		createUserPackage();
		driver.navigate().refresh();

		waitAndClick(By.xpath("//tbody/tr[1]/td[1]//button[starts-with(@ng-click, 'showPackageDialog')]"));

		waitForElement(By.id("package-dialog"));

		WebElement description = waitForElement(By.id("description"));
		description.clear();
		description.sendKeys(TP_DESCRIPTION_UPDATE);

		WebElement function = waitForElement(By.id("function"));
		function.clear();
		function.sendKeys("function HelloAgain() {\nalert(\"Hello Again World!\");\n}");

		waitAndClick(By.cssSelector(".modal-footer button.btn-primary"));

		Thread.sleep(MAX_WAIT_TIME * 1000);

		WebElement version = waitForElement(By.xpath("//tbody/tr[1]/td[3]"));
		description = waitForElement(By.xpath("//tbody/tr[1]/td[5]"));

		assertEquals(new Integer(2), new Integer(version.getText()));
		assertEquals(TP_DESCRIPTION_UPDATE, description.getText());

		deletePackage();
	}

	@Test
	public void testPackagePermission() throws InterruptedException {
		createGroup();
		driver.navigate().refresh();

		createUserPackage();
		driver.navigate().refresh();

		waitAndClick(By.xpath("//tbody/tr[1]/td[1]//button[starts-with(@ng-click, 'showPermissionDialog')]"));

		waitForElement(By.id("permission-dialog"));

		waitAndClick(By.xpath("//form//button[contains(@class, 'dropdown-toggle')][1]"));
		waitAndClick(By.xpath("//form//ul[contains(@class, 'dropdown-menu')][1]//li[1]/a"));
		waitAndClick(By.xpath("//form//select[@id = 'sourceId'][1]/option[2]"));

		waitAndClick(By.cssSelector(".modal-footer button.btn-primary"));

		Thread.sleep(MAX_WAIT_TIME * 1000);
		driver.navigate().refresh();

		waitAndClick(By.xpath("//tbody/tr[1]/td[1]//button[starts-with(@ng-click, 'showPermissionDialog')]"));

		waitForElement(By.id("permission-dialog"));

		waitAndClick(By.xpath("//form//button[starts-with(@ng-click, 'deletePermission')][1]"));

		waitAndClick(By.cssSelector(".modal-footer button.btn-default"));

		Thread.sleep(MAX_WAIT_TIME * 1000);

		deletePackage();
		driver.navigate().refresh();

		deleteGroup();
	}

	private void createUser() {
		switchNavLink("/users");

		assertTrue(waitForElement(By.xpath("//tbody/tr[contains(@ng-if, '!users.user')]")) != null);

		waitAndClick(By.id("btnCreateUser"));

		waitForElement(By.id("user-dialog"));
		waitForElement(By.id("name")).sendKeys(TU_NAME);
		waitForElement(By.id("description")).sendKeys(TU_DESCRIPTION);

		waitAndClick(By.cssSelector(".modal-footer button.btn-primary"));

		WebElement name = waitForElement(By.xpath("//tbody/tr[1]/td[2]"));
		WebElement description = waitForElement(By.xpath("//tbody/tr[1]/td[3]"));

		assertEquals(TU_NAME, name.getText());
		assertEquals(TU_DESCRIPTION, description.getText());
	}

	private void deleteUser() {
		switchNavLink("/users");

		waitAndClick(By.xpath("//tbody/tr[1]/td[1]//button[starts-with(@ng-click, 'showUserDeleteDialog')]"));

		waitForElement(By.id("delete-confirm-dialog"));
		waitAndClick(By.cssSelector(".modal-footer button.btn-danger"));

		assertTrue(waitForElement(By.xpath("//tbody/tr[contains(@ng-if, '!users.user')]")) != null);
	}

	private void createGroup() {
		switchNavLink("/groups");

		assertTrue(waitForElement(By.xpath("//tbody/tr[contains(@ng-if, '!groups.group')]")) != null);

		waitAndClick(By.id("btnCreateGroup"));

		waitForElement(By.id("group-dialog"));
		waitForElement(By.id("name")).sendKeys(TG_NAME);
		waitForElement(By.id("description")).sendKeys(TG_DESCRIPTION);

		waitAndClick(By.cssSelector(".modal-footer button.btn-primary"));

		WebElement name = waitForElement(By.xpath("//tbody/tr[1]/td[2]"));
		WebElement description = waitForElement(By.xpath("//tbody/tr[1]/td[3]"));

		assertEquals(TG_NAME, name.getText());
		assertEquals(TG_DESCRIPTION, description.getText());
	}

	private void deleteGroup() {
		switchNavLink("/groups");

		waitAndClick(By.xpath("//tbody/tr[1]/td[1]//button[starts-with(@ng-click, 'showGroupDeleteDialog')]"));

		waitForElement(By.id("delete-confirm-dialog"));
		waitAndClick(By.cssSelector(".modal-footer button.btn-danger"));

		assertTrue(waitForElement(By.xpath("//tbody/tr[contains(@ng-if, '!groups.group')]")) != null);
	}

	private void createCommonPackage() {
		switchNavLink("/packages");

		assertTrue(waitForElement(By.xpath("//tbody/tr[contains(@ng-if, '!packages.package')]")) != null);

		waitAndClick(By.id("btnCreatePackage"));

		waitForElement(By.id("package-dialog"));

		try {
			File pkg = downloadPackageSrc();

			waitAndClick(By.xpath("//select[@id='type']//option[@value='common']"));

			waitForElement(By.id("name")).sendKeys(TP_NAME);
			waitForElement(By.id("description")).sendKeys(TP_DESCRIPTION);
			waitForElement(By.id("startupScript")).sendKeys("scripts/startupScript.js");
			waitForElement(By.id("file")).sendKeys(pkg.getAbsolutePath());

			waitAndClick(By.cssSelector(".modal-footer button.btn-primary"));

			WebElement version = waitForElement(By.xpath("//tbody/tr[1]/td[3]"));
			WebElement name = waitForElement(By.xpath("//tbody/tr[1]/td[4]"));
			WebElement description = waitForElement(By.xpath("//tbody/tr[1]/td[5]"));

			assertEquals(new Integer(1), new Integer(version.getText()));
			assertEquals(TP_NAME, name.getText());
			assertEquals(TP_DESCRIPTION, description.getText());

			pkg.delete();
		} catch (IOException e) {
			assertTrue("couldn't download package src: " + TP_PACKAGE_SRC, e == null);
		}
	}

	private void createUserPackage() {
		switchNavLink("/packages");

		assertTrue(waitForElement(By.xpath("//tbody/tr[contains(@ng-if, '!packages.package')]")) != null);

		waitAndClick(By.id("btnCreatePackage"));

		waitForElement(By.id("package-dialog"));
		waitAndClick(By.xpath("//select[@id='type']//option[@value='user']"));

		waitForElement(By.id("name")).sendKeys(TP_NAME);
		waitForElement(By.id("description")).sendKeys(TP_DESCRIPTION);
		waitForElement(By.id("function")).sendKeys("function hello() {\nalert(\"Hello World!\");\n}");

		waitAndClick(By.cssSelector(".modal-footer button.btn-primary"));

		WebElement version = waitForElement(By.xpath("//tbody/tr[1]/td[3]"));
		WebElement name = waitForElement(By.xpath("//tbody/tr[1]/td[4]"));
		WebElement description = waitForElement(By.xpath("//tbody/tr[1]/td[5]"));

		assertEquals(new Integer(1), new Integer(version.getText()));
		assertEquals(TP_NAME, name.getText());
		assertEquals(TP_DESCRIPTION, description.getText());
	}

	private void deletePackage() {
		switchNavLink("/packages");

		waitAndClick(By.xpath("//tbody/tr[1]/td[1]//button[starts-with(@ng-click, 'showPackageDeleteDialog')]"));

		waitForElement(By.id("delete-confirm-dialog"));
		waitAndClick(By.cssSelector(".modal-footer button.btn-danger"));

		assertTrue(waitForElement(By.xpath("//tbody/tr[contains(@ng-if, '!packages.package')]")) != null);
	}

	private void switchNavLink(String link) {
		waitAndClick(By.xpath("//div[@id='navbar']//a[contains(@href, '" + link + "')]"));
	}

	private File downloadPackageSrc() throws IOException {
		File tmpFile = Files.createTempFile("pkg", ".zip").toFile();

		URL src = new URL(TP_PACKAGE_SRC);
		ReadableByteChannel rbc = Channels.newChannel(src.openStream());

		FileOutputStream fos = new FileOutputStream(tmpFile);
		try {
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		} finally {
			fos.close();
		}

		return tmpFile;
	}

}