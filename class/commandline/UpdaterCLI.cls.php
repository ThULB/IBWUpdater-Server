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
	protected $VER	= "1.0";
	protected $REV	= '$Revision$';
	
	private $modifier;
	private $object;
	
	function __construct($appname = null, $author = null, $copyright = null) {
		parent::__construct('IBWUpdater CLI', $this->buildVersion(), 'René Adler', '(c) 2012 R. Adler - TU Ilmenau');
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
			$type = $this->object["type"];
			if ($type == "user") {
				$usrMgr = UserManager::getInstance();
				
				$userName = $this->object["name"];
				
				if ($this->modifier == "add") {
					if ($userName != null) {
						$usrMgr->createUser($userName, $this->object["description"]);
						print "Create user \"".$this->colorText($userName, "red")."\".\n";
						exit();
					} else
						throw new Exception("Missing user name!");
				} else if ($this->modifier == "edit") {
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
				} else if ($this->modifier == "delete") {
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
				
				if ($userName != null) {
					print $usrMgr->getUser($userName)."\n";
				}
			}
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
		
		if ($this->object == null)
			$this->object = array("type" => "user", "name" => $opt);
		else if ($this->object["type"] == null || $this->object["type"] == "user") {
			$this->object["type"] = "user";
			$this->object["name"] = $opt;
		} else
			throw new Exception("User can't be set on previously used Type.");
	}
	
	public function option_group($opt = null) {
		if($opt == 'help'){
			return 'The group to modify.';
		}
		if ($this->object == null)
			$this->object = array("type" => "group", "name" => $opt);
		else if ($this->object["type"] == null || $this->object["type"] == "group") {
			$this->object["type"] = "group";
			$this->object["name"] = $opt;
		} else
			throw new Exception("Group can't be set on previously used Type.");
	}
	
	public function option_desc($opt = null) {
		if($opt == 'help'){
			return 'The description for an user, group or package.';
		}
		if ($this->object == null)
			$this->object = array("description" => $opt);
		else
			$this->object["description"] = $opt;
	}
}
?>