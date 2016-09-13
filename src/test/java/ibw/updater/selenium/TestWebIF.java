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

	@Test
	public void testCreateUser() throws InterruptedException {
		createUser();
		deleteUser();
	}

	@Test
	public void testEditUser() throws InterruptedException {
		createGroup();
		createUser();

		waitAndClick(By.xpath("//tbody/tr[1]/td[1]//button[starts-with(@ng-click, 'showUserDialog')]"));

		waitForElement(By.id("user-dialog"));

		WebElement description = waitForElement(By.id("description"));
		description.clear();
		description.sendKeys(TU_DESCRIPTION_UPDATE);

		waitAndClick(By.cssSelector("#groups option"));

		waitAndClick(By.cssSelector(".modal-footer button.btn-primary"));

		Thread.sleep(MAX_WAIT_TIME * 1000);

		description = waitForElement(By.xpath("//tbody/tr[1]/td[3]"));
		WebElement group = waitForElement(By.xpath("//tbody/tr[1]/td[4]"));

		assertEquals(TU_DESCRIPTION_UPDATE, description.getText());
		assertEquals(TG_NAME, group.getText());

		deleteUser();
		deleteGroup();
	}

	@Test
	public void testCreateGroup() throws InterruptedException {
		createGroup();
		deleteGroup();
	}

	@Test
	public void testEditGroup() throws InterruptedException {
		createUser();
		createGroup();

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
		deleteUser();
	}

	private void createUser() {
		waitAndClick(By.cssSelector("#navbar a[href='#/users']"));

		assertTrue(waitForElement(By.xpath("//tbody/tr[contains(@ng-if, '!users.user')]")) != null);

		waitAndClick(By.id("btnCreateUser"));
		waitForElement(By.id("user-dialog"));

		driver.findElement(By.id("name")).sendKeys(TU_NAME);
		driver.findElement(By.id("description")).sendKeys(TU_DESCRIPTION);
		waitAndClick(By.cssSelector(".modal-footer button.btn-primary"));

		WebElement name = waitForElement(By.xpath("//tbody/tr[1]/td[2]"));
		WebElement description = waitForElement(By.xpath("//tbody/tr[1]/td[3]"));

		assertEquals(TU_NAME, name.getText());
		assertEquals(TU_DESCRIPTION, description.getText());
	}

	private void deleteUser() {
		waitAndClick(By.cssSelector("#navbar a[href='#/users']"));

		waitAndClick(By.xpath("//tbody/tr[1]/td[1]//button[starts-with(@ng-click, 'showUserDeleteDialog')]"));

		waitForElement(By.id("delete-confirm-dialog"));
		waitAndClick(By.cssSelector(".modal-footer button.btn-danger"));

		assertTrue(waitForElement(By.xpath("//tbody/tr[contains(@ng-if, '!users.user')]")) != null);
	}

	private void createGroup() {
		waitAndClick(By.cssSelector("#navbar a[href='#/groups']"));

		assertTrue(waitForElement(By.xpath("//tbody/tr[contains(@ng-if, '!groups.group')]")) != null);

		waitAndClick(By.id("btnCreateGroup"));
		waitForElement(By.id("group-dialog"));

		driver.findElement(By.id("name")).sendKeys(TG_NAME);
		driver.findElement(By.id("description")).sendKeys(TG_DESCRIPTION);
		waitAndClick(By.cssSelector(".modal-footer button.btn-primary"));

		WebElement name = waitForElement(By.xpath("//tbody/tr[1]/td[2]"));
		WebElement description = waitForElement(By.xpath("//tbody/tr[1]/td[3]"));

		assertEquals(TG_NAME, name.getText());
		assertEquals(TG_DESCRIPTION, description.getText());
	}

	private void deleteGroup() {
		waitAndClick(By.cssSelector("#navbar a[href='#/groups']"));

		waitAndClick(By.xpath("//tbody/tr[1]/td[1]//button[starts-with(@ng-click, 'showGroupDeleteDialog')]"));

		waitForElement(By.id("delete-confirm-dialog"));
		waitAndClick(By.cssSelector(".modal-footer button.btn-danger"));

		assertTrue(waitForElement(By.xpath("//tbody/tr[contains(@ng-if, '!groups.group')]")) != null);
	}

}