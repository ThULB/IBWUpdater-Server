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

	private $modifier	= "show";
	private $object		= array();

	function __construct($appname = null, $author = null, $copyright = null) {
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

		if ($this->object["type"] == null) {
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

		if ($this->object["type"] == null) {
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
		if ($this->object["type"] == null) {
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
		$usrMgr = UserManager::getInstance();

		$userName = $this->object["name"];

		if ($userName != null) {
			$usrMgr->createUser($userName, $this->object["description"]);
			print "Create user \"".$this->colorText($userName, "red")."\".\n";
			exit();
		} else
			throw new Exception("Missing user name!");
	}

	private function editUser() {
		$usrMgr = UserManager::getInstance();

		$userName = $this->object["name"];

		if ($userName != null) {
			$user = $usrMgr->getUser($userName);
			if ($user != null) {
				$user->setDescription($this->object["description"]);
				$user->save();
				print "Set description for User \"".$this->colorText($userName, "red")."\".\n";
				exit();
			} else
				throw new Exception("User \"".$userName."\" not found!");
		} else
			throw new Exception("Missing user name!");
	}

	private function deleteUser() {
		$usrMgr = UserManager::getInstance();

		$userName = $this->object["name"];

		if ($userName != null) {
			$user = $usrMgr->getUser($userName);
			if ($user != null) {
				$user->delete();
				print "Delete user \"".$this->colorText($userName, "red")."\".\n";
				exit();
			} else
				throw new Exception("User \"".$userName."\" not found!");
		} else
			throw new Exception("Missing user name!");
	}

	private function parseMembers() {
		$usrMgr = UserManager::getInstance();
		
		$members = array();
		if ($this->object["members"] != null) {
			foreach (explode(",", $this->object["members"]) as $userName) {
				$user = $usrMgr->getUser(trim($userName));
				if ($user != null)
					$members[] = $user;
				else
					throw new Exception("User \"".trim($userName)."\" doesn't exists!");
			}
		}

		return $members;
	}

	private function addGroup() {
		$grpMgr = GroupManager::getInstance();
		$usrMgr = UserManager::getInstance();

		$groupName = $this->object["name"];
		$members = $this->parseMembers();

		if ($groupName != null) {
			$grpMgr->createGroup($groupName, $this->object["description"], $members);
			print "Create group \"".$this->colorText($groupName, "red")."\".\n";
			exit();
		} else
			throw new Exception("Missing group name!");
	}

	private function editGroup() {
		$grpMgr = GroupManager::getInstance();
		$usrMgr = UserManager::getInstance();

		$groupName = $this->object["name"];
		$members = $this->parseMembers();

		if ($groupName != null) {
			$group = $grpMgr->getGroup($groupName);
			if ($group != null) {
				if ($this->object["description"] != null) {
					$group->setDescription($this->object["description"]);
					$group->save();
					print "Set description for Group \"".$this->colorText($groupName, "red")."\".\n";
				}

				foreach ($members as $member) {
					if ($group->isMember($member) == false) {
						$group->addMember($member);
						print "Add member \"".$this->colorText($member->getName(), "red")."\" to group \"".$this->colorText($groupName, "red")."\".\n";
					} else
						print "User \"".$this->colorText($member->getName(), "red")."\" is already a member of group \"".$this->colorText($groupName, "red")."\".\n";
				}

				exit();
			} else
				throw new Exception("Group \"".$groupName."\" not found!");
		} else
			throw new Exception("Missing group name!");
	}

	private function deleteGroup() {
		$grpMgr = GroupManager::getInstance();
		$usrMgr = UserManager::getInstance();

		$groupName = $this->object["name"];
		$members = $this->parseMembers();

		if ($groupName != null) {
			$group = $grpMgr->getGroup($groupName);
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
		$grpMgr = GroupManager::getInstance();
		$usrMgr = UserManager::getInstance();

		$permissions = array();
		if ($this->object["permissions"] != null) {
			foreach ($this->object["permissions"] as $type => $perms) {
				foreach (explode(",", $perms) as $name) {
					$permObj = $type == "u" ? $usrMgr->getUser(trim($name)) : $grpMgr->getGroup(trim($name));
					if ($permObj != null)
						$permissions[] = $permObj;
					else
						throw new Exception(($type == "u" ? "User" : "Group")." \"".trim($name)."\" doesn't exists!");
				}
			}
		}

		return $permissions;
	}

	private function addPackage() {
		$pkgMgr = PackageManager::getInstance();
		$grpMgr = GroupManager::getInstance();
		$usrMgr = UserManager::getInstance();

		$pkgName = $this->object["name"];
		$args = parent::parseArgs();

		if ($pkgName != null) {
			if ($args["arguments"] != null) {
				print "Create package \"".$this->colorText($pkgName, "red")."\"...\n";
				$inputFile = $args["arguments"][0];
				$pkgType = $this->isZip($inputFile) ? Package::COMMON : Package::USER;
				
				if ($pkgType == Package::COMMON && $this->object["startupScript"] == null)
					throw new Exception("The startup script wasn't set!");
				if ($pkgType == Package::COMMON && !$this->isStartupScriptIncluded($inputFile, $this->object["startupScript"]))
					throw new Exception("The startup script \"".$this->object["startupScript"]."\" wasn't found within package archive!");

				$pkg = $pkgMgr->createPackage($pkgName, $this->object["description"], $pkgType);
				if ($pkgType == Package::COMMON) {
					print " - copy archive...";
					$pkgFile = PKG_DIR.$pkg->getId().".zip";
					if (!@copy($inputFile, BASE_DIR.$pkgFile)) {
						throw new Exception("Couldn't copy \"".$inputFile."\" to package store directory!");
					}
					print "done.\n";
					
					$pkg->setUrl($pkgFile);
					$pkg->setStartupScript($this->object["startupScript"]);
						
					if ($this->object["permissions"] != null) {
						print " - set permissions...";
						foreach ($this->parsePermissions() as $permObj) {
							$pkg->addPermission($permObj);
						}
						print "done.\n";
					}
						
					$pkg->save();
				}

				print "...done.\n";
				exit();
			} else
				throw new Exception("Missing file argument!");
		} else
			throw new Exception("Missing package name!");
	}
}
?>