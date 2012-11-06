<?php
/**
 * The IBWUpdater command line interface
 *
 * @package    IBWUpdater
 * @subpackage Commandline
 * @author     René Adler <rene.adler@tu-ilmenau.de>
 * @version    $Revision$
 */
class UpdaterCLI extends CLI{
	protected $VER		= "1.0";
	protected $REV		= '$Revision$';

	private static $pkgMgr;
	private static $grpMgr;
	private static $usrMgr;

	private $modifier	= "show";
	private $verbose	= false;
	private $object		= array();

	/**
	 * The Constructor of IBWUpdater CLI.
	 *
	 * @param string $appname
	 * @param string $author
	 * @param string $copyright
	 */
	function __construct($appname = null, $author = null, $copyright = null) {
		self::$grpMgr = GroupManager::getInstance();
		self::$usrMgr = UserManager::getInstance();
		self::$pkgMgr = PackageManager::getInstance();

		parent::__construct('IBWUpdater CLI', 'René Adler', '(c) 2012 R. Adler - TU Ilmenau');
	}

	/**
	 * The main() function gets called if at least one argument is present.
	 * If no arguments are present, the automatically generated help is displayed.
	 *
	 * The main functions job to do the main work of the script.
	 *
	 */
	public function main(){
		if ($this->object != null) {
			$method_name = $this->modifier.ucfirst($this->object["type"]);
			if(method_exists($this, $method_name)){
				call_user_func(array($this, $method_name));
			} else
				throw new Exception("Command not understood!");
		}
	}

	/**
	 * Modifier flag for adding a user, group or packages.
	 *
	 * @param string $opt
	 * @return string
	 */
	public function flag_a($opt = null){
		if($opt == 'help'){
			return 'Modifier to add a new user, group or package.';
		}
		$this->modifier = "add";
	}

	/**
	 * Modifier flag for editing a user, group or packages.
	 *
	 * @param string $opt
	 * @return string
	 */
	public function flag_e($opt = null){
		if($opt == 'help'){
			return 'Modifier to edit a user, group or package.';
		}
		$this->modifier = "edit";
	}

	/**
	 * Modifier flag for deleteting a user, group or packages.
	 *
	 * @param string $opt
	 * @return string
	 */
	public function flag_d($opt = null){
		if($opt == 'help'){
			return 'Modifier to delete a user, group or package.';
		}
		$this->modifier = "delete";
	}

	/**
	 * Flag used to set output more verbose.
	 *
	 * @param string $opt
	 */
	public function flag_v($opt = null){
		if($opt == 'help'){
			return 'More verbose output';
		}
		$this->verbose = true;
	}

	/**
	 * The user name or user list.<br />
	 * In combination with <code>--group</code> input is used as groupmember.<br />
	 * In combination with <code>--package</code> input is used as permission.
	 *
	 * @param string $opt
	 * @return string
	 * @throws Exception
	 */
	public function option_user($opt = null) {
		if($opt == 'help'){
			return 'The user to modify.';
		}

		if (!isset($this->object["type"])) {
			$this->object["type"] = "user";
			$this->object["name"] = $opt;
		} else if ($this->object["type"] == "group") {
			$this->object["members"] = $opt;
		} else if ($this->object["type"] == "package") {
			$this->object["permissions"]["u"] = $opt;
		} else
			throw new Exception("User can't be set on previously used Type.");
	}

	/**
	 * The group name or group list.<br />
	 * In combination with <code>--package</code> input is used as permission.
	 *
	 * @param string $opt
	 * @throws Exception
	 * @return string
	 */
	public function option_group($opt = null) {
		if($opt == 'help'){
			return 'The group to modify.';
		}

		if (!isset($this->object["type"])) {
			$this->object["type"] = "group";
			$this->object["name"] = $opt;
		} else if ($this->object["type"] == "package") {
			$this->object["permissions"]["g"] = $opt;
		} else
			throw new Exception("Group can't be set on previously used Type.");
	}

	/**
	 * The package name.
	 *
	 * @param string $opt
	 * @throws Exception
	 * @return string
	 */
	public function option_package($opt = null) {
		if($opt == 'help'){
			return 'The package to modify.';
		}
		if (!isset($this->object["type"])) {
			$this->object["type"] = "package";
			$this->object["name"] = $opt;
		} else
			throw new Exception("Group can't be set on previously used Type.");
	}

	/**
	 * The description for user, group or package.
	 *
	 * @param string $opt
	 * @return string
	 */
	public function option_description($opt = null) {
		if($opt == 'help'){
			return 'The description for an user, group or package.';
		}

		$this->object["description"] = $opt;
	}

	/**
	 * The startup script for package. Can only be used with package archive.
	 *
	 * @param string $opt
	 * @return string
	 */
	public function option_startscript($opt = null) {
		if($opt == 'help'){
			return 'The startup script file for common package.';
		}

		$this->object["startupScript"] = $opt;
	}

	/**
	 * The function name or list for package. Is used with JavaScript input file.
	 *
	 * @param string $opt
	 */
	public function option_function($opt = null) {
		if($opt == 'help'){
			return 'The function(s) to be set for user package.';
		}

		$this->object["functions"] = $opt;
	}

	/**
	 * Displays informations about the user.
	 */
	private function showUser() {
		$userName = $this->object["name"];

		$table = new Table();
		$table->setHeaders($this->verbose ? array("UID", "Name", "Description") : array("Name", "Description"));
		foreach (self::$usrMgr->getUsers($userName) as $user) {
			$table->addRow($this->verbose ? array($user->getId(), $user->getName(), $user->getDescription()) : array($user->getName(), $user->getDescription()));
		}
		$table->display();
	}

	/**
	 * Add a new user.
	 *
	 * @throws Exception
	 */
	private function addUser() {
		$userName = $this->object["name"];

		if ($userName != null) {
			CLI::line("Create user \"%r".$userName."%n\".");
			self::$usrMgr->createUser($userName, isset($this->object["description"]) ? $this->object["description"] : null);
			exit();
		} else
			throw new Exception("Missing user name!");
	}

	/**
	 * Edit a user.
	 *
	 * @throws Exception
	 */
	private function editUser() {
		$userName = $this->object["name"];

		if ($userName != null) {
			$user = self::$usrMgr->getUser($userName);
			if ($user != null) {
				if (isset($this->object["description"])) {
					CLI::line("Set description for User \"%r".$userName."%n\".");
					$user->setDescription($this->object["description"]);
					$user->save();
				}
				exit();
			} else
				throw new Exception("User \"".$userName."\" not found!");
		} else
			throw new Exception("Missing user name!");
	}

	/**
	 * Deletes a user.
	 *
	 * @throws Exception
	 */
	private function deleteUser() {
		$userName = $this->object["name"];

		if ($userName != null) {
			$user = self::$usrMgr->getUser($userName);
			if ($user != null) {
				CLI::line("Delete user \"%r".$userName."%n\".");
				$user->delete();
				exit();
			} else
				throw new Exception("User \"".$userName."\" not found!");
		} else
			throw new Exception("Missing user name!");
	}

	/**
	 * Helper function to parse groupmembers from arguments.
	 *
	 * @throws Exception
	 */
	private function parseMembers() {
		$members = array();
		if (isset($this->object["members"])) {
			foreach (explode(",", $this->object["members"]) as $userName) {
				$userName = trim($userName);
				$user = self::$usrMgr->getUser($userName);
				if ($user != null)
					$members[] = $user;
				else
					throw new Exception("User \"".$userName."\" doesn't exists!");
			}
		}

		return $members;
	}

	/**
	 * Displays information about the group.
	 */
	private function showGroup() {
		$groupName = $this->object["name"];

		$table = new Table();
		$table->setHeaders($this->verbose ? array("GID", "Name", "Description", "Member(s)") : array("Name", "Description", "Member(s)"));
		foreach (self::$grpMgr->getGroups($groupName) as $group) {
			$members = "";
			foreach ($group->getMembers() as $member) {
				$members .= strlen($members) != 0 ? ", ".$member->getName() : $member->getName();
			}
			$table->addRow($this->verbose ? array($group->getId(), $group->getName(), $group->getDescription(), $members) : array($group->getName(), $group->getDescription(), $members));
		}
		$table->display();
	}

	/**
	 * Add a new group and groupmeber is set.
	 *
	 * @throws Exception
	 */
	private function addGroup() {
		$groupName = $this->object["name"];
		$members = $this->parseMembers();

		if ($groupName != null) {
			CLI::line("Create group \"%r".$groupName."%n\"");
			self::$grpMgr->createGroup($groupName, isset($this->object["description"]) ? $this->object["description"] : null, $members);
			exit();
		} else
			throw new Exception("Missing group name!");
	}

	/**
	 * Edit group and add new groupmember.
	 *
	 * @throws Exception
	 */
	private function editGroup() {
		$groupName = $this->object["name"];
		$members = $this->parseMembers();

		if ($groupName != null) {
			$group = self::$grpMgr->getGroup($groupName);
			if ($group != null) {
				CLI::line("Edit group \"%r".$groupName."%n\"");

				if (isset($this->object["description"])) {
					CLI::line(" - set description");
					$group->setDescription($this->object["description"]);
					$group->save();
				}

				foreach ($members as $member) {
					if ($group->isMember($member) == false) {
						$group->addMember($member);
						CLI::line(" - add member \"%r".$member->getName()."%n\"");
					} else
						CLI::line(" - $yuser \"".$member->getName()."\" is already a member%n");
				}

				exit();
			} else
				throw new Exception("Group \"".$groupName."\" not found!");
		} else
			throw new Exception("Missing group name!");
	}

	/**
	 * Deletes group or a groupmeber.
	 *
	 * @throws Exception
	 */
	private function deleteGroup() {
		$groupName = $this->object["name"];
		$members = $this->parseMembers();

		if ($groupName != null) {
			$group = self::$grpMgr->getGroup($groupName);
			if ($group != null) {
				if (count($members) == 0) {
					$group->delete();
					CLI::line("Delete group \"%r".$groupName."%n\".");
				} else {
					foreach ($members as $member) {
						if ($group->isMember($member)) {
							$group->removeMember($member);
							CLI::line("Remove member \"%r".$member->getName()."%n\" from group \"%r".$groupName."%n\".");
						} else
							throw new Exception("User \"".$member->getName()."\" isn't a member of group \"".$groupName."\".");
					}
				}
				exit();
			} else
				throw new Exception("Group \"".$groupName."\" not found!");
		} else
			throw new Exception("Missing group name!");
	}

	/**
	 * Helper to check file is an ZIP.
	 *
	 * @param string $filename
	 */
	private function isZip($filename) {
		$zip = new ZipArchive();
		return $zip->open($filename) === true;
	}

	/**
	 * Helper to check if startup script in archive.
	 *
	 * @param string $filename
	 * @param string $startupScript
	 */
	private function isStartupScriptIncluded($filename, $startupScript) {
		$zip = new ZipArchive();
		$zip->open($filename);

		for ($i=0; $i<$zip->numFiles;$i++) {
			$fileInfo = $zip->statIndex($i);
			if ($fileInfo["name"] == $startupScript)
				return true;
		}

		return false;
	}

	/**
	 * Helper to parse permissions for user(s) and/or group(s) from arguments.
	 *
	 * @throws Exception
	 */
	private function parsePermissions() {
		$permissions = array();
		if ($this->object["permissions"] != null) {
			foreach ($this->object["permissions"] as $type => $perms) {
				foreach (explode(",", $perms) as $name) {
					$name = trim($name);
					$permObj = $type == "u" ? self::$usrMgr->getUser($name) : self::$grpMgr->getGroup($name);
					if ($permObj != null)
						$permissions[] = $permObj;
					else
						throw new Exception(($type == "u" ? "User" : "Group")." \"".$name."\" doesn't exists!");
				}
			}
		}

		return $permissions;
	}

	/**
	 * Helper function to parse function(s) from arguments.
	 * 
	 * @param array $args
	 * @throws Exception
	 */
	private function parseFunctions($args) {
		$functions = array();

		if ($this->object["functions"] != null) {
			$fCount = 0;
			$pfunc = null;

			foreach (explode(",", $this->object["functions"]) as $funcName) {
				$funcName = trim($funcName);

				if ($args[$fCount] != null) {
					$jsParser = new JSParser($args[$fCount]);
					if ($jsParser->getFunctions() != null) {
						$pfunc = $jsParser->getFunctions();
						if ($pfunc[$funcName] != null) {
							$functions[$funcName] = $pfunc[$funcName];
						}
					} else {
						$pfunc = $jsParser->getCodeString();
						$functions[$funcName] = array("params" => "", "code" => $pfunc);
						$pfunc = null;
					}
				} else if ($pfunc[$funcName] != null) {
					$functions[$funcName] = $pfunc[$funcName];
				} else
					throw new Exception("Function \"".$funcName."\" wasn't found!");

				$fCount++;
			}
		}

		return $functions;
	}

	/**
	 * Displays information about the package.
	 */
	private function showPackage() {
		$pkgName = $this->object["name"];

		$table = new Table();
		$table->setHeaders($this->verbose ? array("ID", "Name", "Description", "Type", "Ver.", "Permissions") : array("Name", "Description", "Type", "Ver.", "Permissions"));

		$packages = array();
		if (isset($this->object["permissions"])) {
			foreach ($this->parsePermissions() as $permObj) {
				if ($permObj instanceof User) {
					$packages = self::$pkgMgr->getPackagesForUserName($permObj->getName());
					break;
				}
			}
		} else {
			$packages = self::$pkgMgr->getPackages($pkgName);
		}

		foreach ($packages as $pkg) {
			$perms = array();
			foreach ($pkg->getPermissions() as $permObj) {
				array_push($perms, ($permObj instanceof User ? "[u] " : "[g] ").$permObj->getName());
			}
			$permissions = !empty($perms) ? implode(", ", $perms) : "all";
			$table->addRow($this->verbose ? array($pkg->getId(), $pkg->getName(), $pkg->getDescription(), $pkg->getType(), $pkg->getVersion(), $permissions) : array($pkg->getName(), $pkg->getDescription(), $pkg->getType(), $pkg->getVersion(), $permissions));
		}
		$table->display();
	}

	/**
	 * Add a new package.
	 * 
	 * @throws Exception
	 */
	private function addPackage() {
		$pkgName = $this->object["name"];
		$args = parent::parseArgs();

		if ($pkgName != null) {
			if ($args["arguments"] != null) {
				CLI::line("Create package \"%r".$pkgName."%n\"...");
				$inputFile = $args["arguments"][0];
				$pkgType = $this->isZip($inputFile) ? Package::COMMON : Package::USER;

				if ($pkgType == Package::COMMON && !isset($this->object["startupScript"]))
					throw new Exception("The startup script wasn't set!");
				if ($pkgType == Package::COMMON && !$this->isStartupScriptIncluded($inputFile, $this->object["startupScript"]))
					throw new Exception("The startup script \"".$this->object["startupScript"]."\" wasn't found within package archive!");

				$pkg = self::$pkgMgr->createPackage($pkgName, $this->object["description"], $pkgType);
				if ($pkgType == Package::COMMON) {
					CLI::line(" - copy archive");
					$pkgFile = PKG_DIR.$pkg->getId()."-".$pkg->getVersion().".zip";
					if (!@copy($inputFile, BASE_DIR.$pkgFile)) {
						throw new Exception("Couldn't copy \"".$inputFile."\" to package store directory!");
					}

					$pkg->setUrl($pkgFile);
					$pkg->setStartupScript($this->object["startupScript"]);
				} else {
					$functions = $this->parseFunctions($args["arguments"]);
					foreach ($functions as $funcName => $funcData) {
						CLI::line(" - add function \"".$funcName."\"");
						$pkg->addFunction($funcName, $funcData["params"], $funcData["code"]);
					}
				}

				if (isset($this->object["permissions"])) {
					foreach ($this->parsePermissions() as $permObj) {
						CLI::line(" - add permission for ".($permObj instanceof User ? "user" : "group"). " \"".$permObj->getName()."\"");
						$pkg->addPermission($permObj);
					}
				}

				$pkg->save();

				exit();
			} else
				throw new Exception("Missing file argument!");
		} else
			throw new Exception("Missing package name!");
	}

	/**
	 * Edit a package or set new content btw. permissions.
	 * 
	 * @throws Exception
	 */
	private function editPackage() {
		$pkgName = $this->object["name"];
		$args = parent::parseArgs();

		if ($pkgName != null) {
			$pkg = self::$pkgMgr->getPackage($pkgName);

			if ($pkg != null) {
				CLI::line("Edit package \"%r".$pkgName."%n\"...");

				if (isset($this->object["description"])) {
					CLI::line(" - set description for package");
					$pkg->setDescription($this->object["description"]);
				}

				if (isset($this->object["permissions"])) {
					foreach ($this->parsePermissions() as $permObj) {
						if ($pkg->getPermission($permObj) == null) {
							CLI::line(" - add permission for ".($permObj instanceof User ? "user" : "group"). " \"".$permObj->getName()."\"");
							$pkg->addPermission($permObj);
						} else
							CLI::line(" - %yPermission for ".($permObj instanceof User ? "user" : "group"). " \"".$permObj->getName()."\" was already set.%n");
					}
				}

				if ($args["arguments"] != null) {
					$inputFile = $args["arguments"][0];
					$pkgType = $pkg->getType();

					if ($pkgType == Package::COMMON && !isset($this->object["startupScript"]))
						$this->object["startupScript"] = $pkg->getStartupScript();
					if ($pkgType == Package::COMMON && !$this->isStartupScriptIncluded($inputFile, $this->object["startupScript"]))
						throw new Exception("The startup script \"".$this->object["startupScript"]."\" wasn't found within package archive!");

					if ($pkgType == Package::COMMON) {
						CLI::line(" - remove old archive");
						@unlink(BASE_DIR.$pkg->getUrl());

						CLI::line(" - copy archive");
						$pkgFile = PKG_DIR.$pkg->getId()."-".$pkg->getVersion().".zip";
						if (!@copy($inputFile, BASE_DIR.$pkgFile)) {
							throw new Exception("Couldn't copy \"".$inputFile."\" to package store directory!");
						}

						$pkg->setUrl($pkgFile);
						$pkg->setStartupScript($this->object["startupScript"]);
					} else {
						$functions = $this->parseFunctions($args["arguments"]);
						foreach ($functions as $funcName => $funcData) {
							CLI::line(" - set function \"".$funcName."\"");
							$pkg->addFunction($funcName, $funcData["params"], $funcData["code"]);
						}
					}

					$pkg->setVersion($pkg->getVersion() + 1);
				}
					
				$pkg->save();
				exit();
			} else
				throw new Exception("Package \"".$pkgName."\" not found.");
		} else
			throw new Exception("Missing package name!");
	}

	/**
	 * Delete a package, a function or a permission. 
	 */
	private function deletePackage() {
		$pkgName = $this->object["name"];

		if ($pkgName != null) {
			$pkg = self::$pkgMgr->getPackage($pkgName);

			if ($pkg != null) {
				$pkgType = $pkg->getType();

				if ($pkgType == Package::COMMON && $this->object["permissions"] == null) {
					CLI::line("Delete package \"%r".$pkgName."%n\".");
					CLI::line(" - remove old archive");
					@unlink(BASE_DIR.$pkg->getUrl());
					$pkg->delete();
					exit();
				} else if ($pkgType == Package::USER) {
					$functions = isset($this->object["functions"]) ? explode(",", $this->object["functions"]) : array();
					if (count($functions) == $pkg->countFunctions()) {
						CLI::line("Delete package \"%r".$pkgName."%n\".");
						CLI::line(" - remove old archive");
						@unlink(BASE_DIR.$pkg->getUrl());
						$pkg->delete();
						exit();
					} else if (!empty($functions)) {
						CLI::line("Edit package \"%r".$pkgName."%n\"...");
						foreach ($functions as $funcName) {
							CLI::line(" - remove function \"".$funcName."\"");
							$pkg->removeFunction($funcName);
						}
					}
				}

				if (isset($this->object["permissions"])) {
					CLI::line("Edit package \"%r".$pkgName."%n\"...");
					foreach ($this->parsePermissions() as $permObj) {
						if ($pkg->getPermission($permObj) != null) {
							CLI::line(" - remove permission for ".($permObj instanceof User ? "user" : "group"). " \"".$permObj->getName()."\"");
							$pkg->removePermission($permObj);
						} else
							CLI::line(" - %yPermission for ".($permObj instanceof User ? "user" : "group"). " \"".$permObj->getName()."\" wasn't set.%n");
					}
				}

				$pkg->save();
				exit();
			}
		}
	}
}
?>