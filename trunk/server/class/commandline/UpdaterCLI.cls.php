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
	private $object		= array();

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
		} else
			$this->help();
	}

	public function flag_a($opt = null){
		if($opt == 'help'){
			return 'Modifier to add an new user, group or package.';
		}
		$this->modifier = "add";
	}

	public function flag_e($opt = null){
		if($opt == 'help'){
			return 'Modifier to edit an user, group or package.';
		}
		$this->modifier = "edit";
	}

	public function flag_d($opt = null){
		if($opt == 'help'){
			return 'Modifier to delete an user, group or package.';
		}
		$this->modifier = "delete";
	}

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

	public function option_desc($opt = null) {
		if($opt == 'help'){
			return 'The description for an user, group or package.';
		}

		$this->object["description"] = $opt;
	}

	public function option_startup($opt = null) {
		if($opt == 'help'){
			return 'The startup script file for common package.';
		}

		$this->object["startupScript"] = $opt;
	}

	public function option_function($opt = null) {
		if($opt == 'help'){
			return 'The function(s) to be set for user package.';
		}

		$this->object["functions"] = $opt;
	}

	private function addUser() {
		$userName = $this->object["name"];

		if ($userName != null) {
			print "Create user \"".$this->colorText($userName, "red")."\".\n";
			self::$usrMgr->createUser($userName, isset($this->object["description"]) ? $this->object["description"] : null);
			exit();
		} else
			throw new Exception("Missing user name!");
	}

	private function editUser() {
		$userName = $this->object["name"];

		if ($userName != null) {
			$user = self::$usrMgr->getUser($userName);
			if ($user != null) {
				if (isset($this->object["description"])) {
					print "Set description for User \"".$this->colorText($userName, "red")."\".\n";
					$user->setDescription($this->object["description"]);
					$user->save();
				}
				exit();
			} else
				throw new Exception("User \"".$userName."\" not found!");
		} else
			throw new Exception("Missing user name!");
	}

	private function deleteUser() {
		$userName = $this->object["name"];

		if ($userName != null) {
			$user = self::$usrMgr->getUser($userName);
			if ($user != null) {
				print "Delete user \"".$this->colorText($userName, "red")."\".\n";
				$user->delete();
				exit();
			} else
				throw new Exception("User \"".$userName."\" not found!");
		} else
			throw new Exception("Missing user name!");
	}

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

	private function addGroup() {
		$groupName = $this->object["name"];
		$members = $this->parseMembers();

		if ($groupName != null) {
			print "Create group \"".$this->colorText($groupName, "red")."\".\n";
			self::$grpMgr->createGroup($groupName, isset($this->object["description"]) ? $this->object["description"] : null, $members);
			exit();
		} else
			throw new Exception("Missing group name!");
	}

	private function editGroup() {
		$groupName = $this->object["name"];
		$members = $this->parseMembers();

		if ($groupName != null) {
			$group = self::$grpMgr->getGroup($groupName);
			if ($group != null) {
				print "Edit group \"".$this->colorText($groupName, "red")."\".\n";
				
				if (isset($this->object["description"])) {
					print " - set description\n";
					$group->setDescription($this->object["description"]);
					$group->save();
				}

				foreach ($members as $member) {
					if ($group->isMember($member) == false) {
						$group->addMember($member);
						print " - add member \"".$this->colorText($member->getName(), "red")."\"\n";
					} else
						$this->colorText(" - user \"".$member->getName()."\" is already a member\n", "yellow");
				}

				exit();
			} else
				throw new Exception("Group \"".$groupName."\" not found!");
		} else
			throw new Exception("Missing group name!");
	}

	private function deleteGroup() {
		$groupName = $this->object["name"];
		$members = $this->parseMembers();

		if ($groupName != null) {
			$group = self::$grpMgr->getGroup($groupName);
			if ($group != null) {
				if (count($members) == 0) {
					$group->delete();
					print "Delete group \"".$this->colorText($groupName, "red")."\".\n";
				} else {
					foreach ($members as $member) {
						if ($group->isMember($member)) {
							$group->removeMember($member);
							print "Remove member \"".$this->colorText($member->getName(), "red")."\" from group \"".$this->colorText($groupName, "red")."\".\n";
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

	private function isZip($filename) {
		$zip = new ZipArchive();
		return $zip->open($filename) === true;
	}

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

	private function trimCode($aCode) {
		$code = null;

		if (strpos($aCode, "{") !== false) {
			$code = substr($aCode, strpos($aCode, "{") + 1);
			$code = substr($code, 0, strrpos($code, "}"));
		}

		return $code;
	}

	private function startsWith($haystack, $needle, $withTrim = true) {
		if ($withTrim)
			return substr(trim($haystack), 0, strlen($needle)) == $needle;
		else
			return substr($haystack, 0, strlen($needle)) == $needle;
	}

	const lineSeparator = "\n";

	private function parseJSFile($jsFile) {
		$functions = array();

		$jsLines = file($jsFile);
		$jsUnknown = null;

		$c = 0;
		while ($c < count($jsLines)) {
			$comment = null;

			// read comment(s)
			$line = trim($jsLines[$c]).self::lineSeparator;

			if ($this->startsWith($line, "/*")) {
				$comment = $line;
				$c++;

				$line = trim($jsLines[$c]).self::lineSeparator;
				while ($this->startsWith($line, "*/") != true) {
					$comment .= $line;
					$c++;
					$line = trim($jsLines[$c]).self::lineSeparator;
				}
				$comment .= $line;
				$c++;
				$line = trim($jsLines[$c]).self::lineSeparator;
			}

			if ($this->startsWith($line, "//")) {
				if ($comment == null)
					$comment = $line;
				else
					$comment .= $line;
			}

			// read function(s)
			if ($this->startsWith($line, "function")) {
				$found = preg_match("/function\s([^\(].*)\((.*)\)/", $line, $match);
				if ($found) {
					$brackets = 0;
					$code = null;

					while (strpos($line, "{") === false) {
						$c++;
						$line = trim($jsLines[$c]).self::lineSeparator;
					}

					do {
						if (strpos($line, "{") !== false)
							$brackets++;
						if (strpos($line, "}") !== false)
							$brackets--;

						if ($code == null)
							$code = $line;
						else
							$code .= $line;

						if (($brackets != 0)) {
							$c++;
							$line = trim($jsLines[$c]).self::lineSeparator;
						}
					} while ($brackets != 0);

					$functions[$match[1]] = array("params" => $match[2], "comment" => utf8_encode($comment), "code" => utf8_encode($this->trimCode($code)));
				}
			} else {
				$line = trim($line);
				if (strlen($line) != 0) {
					if ($jsUnknown == null)
						$jsUnknown = $line.self::lineSeparator;
					else
						$jsUnknown .= $line.self::lineSeparator;
				}
			}

			// next line
			$c++;
		}

		return $functions != null ? $functions : $jsUnknown;
	}

	private function parseFunctions($args) {
		$functions = array();

		if ($this->object["functions"] != null) {
			$fCount = 0;
			$pfunc = null;

			foreach (explode(",", $this->object["functions"]) as $funcName) {
				$funcName = trim($funcName);

				if ($args[$fCount] != null) {
					$pfunc = $this->parseJSFile($args[$fCount]);
					if (is_array($pfunc)) {
						if ($pfunc[$funcName] != null) {
							$functions[$funcName] = $pfunc[$funcName];
						}
					} else {
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

	private function addPackage() {
		$pkgName = $this->object["name"];
		$args = parent::parseArgs();

		if ($pkgName != null) {
			if ($args["arguments"] != null) {
				print "Create package \"".$this->colorText($pkgName, "red")."\"...\n";
				$inputFile = $args["arguments"][0];
				$pkgType = $this->isZip($inputFile) ? Package::COMMON : Package::USER;

				if ($pkgType == Package::COMMON && !isset($this->object["startupScript"]))
					throw new Exception("The startup script wasn't set!");
				if ($pkgType == Package::COMMON && !$this->isStartupScriptIncluded($inputFile, $this->object["startupScript"]))
					throw new Exception("The startup script \"".$this->object["startupScript"]."\" wasn't found within package archive!");

				$pkg = self::$pkgMgr->createPackage($pkgName, $this->object["description"], $pkgType);
				if ($pkgType == Package::COMMON) {
					print " - copy archive\n";
					$pkgFile = PKG_DIR.$pkg->getId().".zip";
					if (!@copy($inputFile, BASE_DIR.$pkgFile)) {
						throw new Exception("Couldn't copy \"".$inputFile."\" to package store directory!");
					}

					$pkg->setUrl($pkgFile);
					$pkg->setStartupScript($this->object["startupScript"]);
				} else {
					$functions = $this->parseFunctions($args["arguments"]);
					foreach ($functions as $funcName => $funcData) {
						print " - add function \"".$funcName."\"\n";
						$pkg->addFunction($funcName, $funcData["params"], $funcData["code"]);
					}
				}

				if (isset($this->object["permissions"])) {
					foreach ($this->parsePermissions() as $permObj) {
						print " - add permission for ".($permObj instanceof User ? "user" : "group"). " \"".$permObj->getName()."\"\n";
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

	private function editPackage() {
		$pkgName = $this->object["name"];
		$args = parent::parseArgs();

		if ($pkgName != null) {
			$pkg = self::$pkgMgr->getPackage($pkgName);

			if ($pkg != null) {
				print "Edit package \"".$this->colorText($pkgName, "red")."\"...\n";

				if (isset($this->object["description"])) {
					print " - set description for package\n";
					$pkg->setDescription($this->object["description"]);
				}

				if (isset($this->object["permissions"])) {
					foreach ($this->parsePermissions() as $permObj) {
						if ($pkg->getPermission($permObj) == null) {
							print " - add permission for ".($permObj instanceof User ? "user" : "group"). " \"".$permObj->getName()."\"\n";
							$pkg->addPermission($permObj);
						} else
							$this->colorText(" - Permission for ".($permObj instanceof User ? "user" : "group"). " \"".$permObj->getName()."\" was already set.\n", "yellow", false);
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
						print " - copy archive\n";
						$pkgFile = PKG_DIR.$pkg->getId().".zip";
						if (!@copy($inputFile, BASE_DIR.$pkgFile)) {
							throw new Exception("Couldn't copy \"".$inputFile."\" to package store directory!");
						}

						$pkg->setUrl($pkgFile);
						$pkg->setStartupScript($this->object["startupScript"]);
					} else {
						$functions = $this->parseFunctions($args["arguments"]);
						foreach ($functions as $funcName => $funcData) {
							print " - set function \"".$funcName."\"\n";
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

	private function deletePackage() {
		$pkgName = $this->object["name"];

		if ($pkgName != null) {
			$pkg = self::$pkgMgr->getPackage($pkgName);

			if ($pkg != null) {
				$pkgType = $pkg->getType();

				if ($pkgType == Package::COMMON && $this->object["permissions"] == null) {
					print "Delete package \"".$this->colorText($pkgName, "red")."\".\n";
					$pkg->delete();
					exit();
				} else if ($pkgType == Package::USER) {
					$functions = explode(",", $this->object["functions"]);
					if (count($functions) == $pkg->countFunctions()) {
						print "Delete package \"".$this->colorText($pkgName, "red")."\".\n";
						$pkg->delete();
						exit();
					} else {
						print "Edit package \"".$this->colorText($pkgName, "red")."\"...\n";
						foreach ($functions as $funcName) {
							print " - remove function \"".$funcName."\"\n";
							$pkg->removeFunction($funcName);
						}
					}
				}

				if (isset($this->object["permissions"])) {
					print "Edit package \"".$this->colorText($pkgName, "red")."\"...\n";
					foreach ($this->parsePermissions() as $permObj) {
						if ($pkg->getPermission($permObj) != null) {
							print " - remove permission for ".($permObj instanceof User ? "user" : "group"). " \"".$permObj->getName()."\"\n";
							$pkg->removePermission($permObj);
						} else
							$this->colorText(" - Permission for ".($permObj instanceof User ? "user" : "group"). " \"".$permObj->getName()."\" wasn't set.\n", "yellow", false);
					}
				}

				$pkg->save();
				exit();
			}
		}
	}
}
?>