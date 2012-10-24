<?php
/**
 * GroupManager
 *
 * @package    IBWUpdater
 * @subpackage Persistency
 * @author     René Adler <rene.adler@tu-ilmenau.de>
 * @version    $Revision$
 */
class GroupManager {
	private static $instance;

	private $groups;
	
	static public function getInstance() {
		if (null === self::$instance) {
			self::$instance = new self;
		}
		return self::$instance;
	}

	public function __construct() {
		$this->groups = UserGroupTable::getInstance();
	}

	public function getGroup($aSource) {
		if (is_numeric($aSource)) {
			return $this->groups->find($aSource);
		} else if (is_string($aSource)) {
			return $this->groups->findOneByName($aSource);
		} else
			throw new Exception("Source must either group ID or Name.");
	}

	public function &createGroup($aName, $aDescription, $users = array()) {
		if ($this->getGroup($aName) != null)
			throw new Exception("Group with name \"$aName\" already exists!");

		$group = new UserGroup();
		$group->setName($aName);
		$group->setDescription($aDescription);
		
		foreach ($users as $user) {
			$group->addMember($user);
		}

		$group->save();
		
		return $group;
	}
}
?>