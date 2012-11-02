<?php
/**
 * UserManager
 *
 * @package    IBWUpdater
 * @subpackage Persistency
 * @author     René Adler <rene.adler@tu-ilmenau.de>
 * @version    $Revision$
 */
class UserManager {
	private static $instance;

	private $users;
	
	static public function getInstance() {
		if (null === self::$instance) {
			self::$instance = new self;
		}
		return self::$instance;
	}

	public function __construct() {
		$this->users = UserTable::getInstance();
	}

	/**
	 * Get an user object if exists.
	 * 
	 * @param Object $aSource
	 * @throws Exception if source not an ID or Name
	 */
	public function getUser($aSource) {
		if (is_numeric($aSource)) {
			return $this->users->find($aSource);
		} else if (is_string($aSource)) {
			return $this->users->findOneByName($aSource);
		} else
			throw new Exception("Source must either user ID or Name.");
	}
	
	public function getUsers() {
		return $this->users->findAll();
	}

	/**
	 * Create a new User object if not exists.
	 * 
	 * @param String $aName - the user name
	 * @param String $aDescription - the user description
	 * @throws Exception if user already exists
	 */
	public function &createUser($aName, $aDescription) {
		if ($this->getUser($aName) != null)
			throw new Exception("User with name \"$aName\" already exists!");
					
		$user = new User();
		
		$user->setName($aName);
		$user->setDescription($aDescription);
		
		$user->save();
		
		return $user;
	}
}
?>