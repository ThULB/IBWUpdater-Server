<?php

/**
 * Package
 *
 * @package    IBWUpdater
 * @subpackage Datamodel
 * @author     René Adler <rene.adler@tu-ilmenau.de>
 * @version    $Revision$
 */
class Package extends BasePackage {
	const COMMON = "common";
	const USER = "user";

	public function setId($aId) {
		$this->id = $aId;
	}

	public function getId() {
		return $this->id;
	}

	public function setType($aType) {
		if ($aType != self::COMMON && $aType != self::USER)
			throw new Exception("Type \"".$aType."\" is incorrect!");

		$this->type = $aType;
	}

	public function getType() {
		return $this->type;
	}

	public function setVersion($aVersion) {
		$this->version = $aVersion;
	}

	public function getVersion() {
		return $this->version;
	}

	public function setName($aName) {
		$this->name = $aName;
	}

	public function getName() {
		return $this->name;
	}

	public function setDescription($aDescription) {
		$this->description = $aDescription;
	}

	public function getDescription() {
		return $this->description;
	}

	public function setURL($aURL) {
		$this->url = $aURL;
	}

	public function getURL() {
		return $this->url;
	}

	public function setStartupScript($aStartupScript) {
		$this->startupScript = $aStartupScript;
	}

	public function getStartupScript() {
		return $this->startupScript;
	}

	public function addFunction($aObject, $aParams = "", $aCode) {
		$func = null;

		if ($aObject instanceof JSFunction) {
			$func = $aObject;
		} else if (is_string($aObject)) {
			$func = new JSFunction();
			$func->pid = $this->id;
			$func->name = $aObject;
			$func->params = $aParams;
			$func->code = $aCode;
		}

		// only add new one if function don't exists
		if ($this->getFunction($func->getName()) == null) {
			$this->JSFunction[$this->JSFunction->count()] = $func;
		} else
			$this->setFunction($func);

		$this->save();

		return $func;
	}

	private function copyJSFunction($source, &$target) {
		$target->setName($source->getName());
		$target->setParams($source->getParams());
		$target->setCode($source->getCode());
	}

	public function setFunction($aFunc) {
		$func = $this->getFunction($aFunc->getName());

		if ($func != null) {
			$this->copyJSFunction($aFunc, $func);
		} else
			$this->addFunction($aFunc);

		$this->save();
	}

	public function getFunction($aObject) {
		if ($this->JSFunction->count() != 0) {
			foreach ($this->JSFunction as $key => $func) {
				if ((is_numeric($aObject) && $func->id == $aObject) || ($func->getName() == $aObject))
					return $this->JSFunction[$key];
			}
		}

		return null;
	}

	public function removeFunction($aObject) {
		if ($this->JSFunction->count() != 0) {
			foreach ($this->JSFunction as $key => $func) {
				if ((is_numeric($aObject) && $func->id == $aObject) || ($func->getName() == $aObject)) {
					$this->JSFunction[$key]->delete();
					unset($this->JSFunction[$key]);
					return;
				}
			}
		}

		return null;
	}

	private function createPermission($aSourceObject, $aAction = Permission::READ) {
		$permission = new Permission();

		if (!($aSourceObject instanceof UserGroup) && !($aSourceObject instanceof User))
			throw new Exception("The source object must either GROUP or USER!");
		if ($aAction != Permission::READ && $aAction != Permission::WRITE)
			throw new Exception("The action must either READ or WRITE!");
			
		if ($aSourceObject instanceof UserGroup) {
			$permission->setSourceType(Permission::GROUP);
			$permission->setSourceObject($aSourceObject);
			$permission->setSourceId($aSourceObject->getId());
		} else if ($aSourceObject instanceof User) {
			$permission->setSourceType(Permission::USER);
			$permission->setSourceObject($aSourceObject);
			$permission->setSourceId($aSourceObject->getId());
		}

		$permission->setAction($aAction);
		$permission->setTargetId($this->id);

		return $permission;
	}

	public function addPermission($aSourceObject, $aAction = Permission::READ) {
		$permission = $this->createPermission($aSourceObject, $aAction);
		$this->Permission[$this->Permission->count()] = $permission;

		$this->save();

		return $permission;
	}

	public function getPermission($aSourceObject, $aAction = Permission::READ, $aSourceType = null) {
		if ($this->Permission->count() != 0) {
			$aName = null;

			if ($aSourceObject instanceof UserGroup || $aSourceObject instanceof User) {
				$aName = $aSourceObject->getName();
				if ($aSourceObject instanceof UserGroup)
					$aSourceType = Permission::GROUP;
				else if ($aSourceObject instanceof User)
					$aSourceType = Permission::USER;
			} else
				$aName = $aSourceObject;

			foreach($this->Permission as $key => $permission) {
				if ($permission->getSourceObject() == null) {
					if ($permission->getSourceType() == Permission::USER)
						$source = GroupManager::getInstance()->getUser($permission->getSourceId());
					else if ($permission->getSourceType() == Permission::USER)
						$source = UserManager::getInstance()->getUser($permission->getSourceId());
					$this->Permission[$key]->setSourceObject($source);
				}
				if ($this->Permission[$key]->getSourceObject()->getName() == $aName && $this->Permission[$key]->getSourceType() == $aSourceType && $this->Permission[$key]->getAction() == $aAction)
					return $this->permissions[$key];
			}
		}

		return null;
	}

	public function removePermission($aSourceObject, $aAction = Permission::READ, $aSourceType = null) {
		if (count($this->permissions) != 0) {
			$aName = null;

			if ($aSourceObject instanceof Group || $aSourceObject instanceof User) {
				$aName = $aSourceObject->getName();
				if ($aSourceObject instanceof Group)
					$aSourceType = Permission::GROUP;
				else if ($aSourceObject instanceof User)
					$aSourceType = Permission::USER;
			} else
				$aName = $aSourceObject;

			foreach($this->permissions as $key => $permission); {
				if ($permission->getSourceObject()->getName() == $aName && $permission->getSourceType() == $aSourceType && $permission->getAction() == $aAction) {
					unset($this->permissions[$key]);
					return;
				}
			}
		}
	}

	public function hasPermission($aName, $aAction = Permission::READ) {
		if ($this->Permission->count() == 0)
			return true;

		foreach($this->Permission as $key => $permission); {
			if ($permission->getAction() == $aAction) {
				if ($permission->getSourceObject() == null) {
					if ($permission->getSourceType() == Permission::GROUP)
						$sourceObject = GroupManager::getInstance()->getGroup($permission->getSourceId());
					else if ($permission->getSourceType() == Permission::USER)
						$sourceObject = UserManager::getInstance()->getUser($permission->getSourceId());

					$this->Permission[$key]->setSourceObject($sourceObject);
				} else
					$sourceObject = $permission->getSourceObject();
				
				if ($permission->getSourceType() == Permission::USER && $sourceObject->getName() == $aName)
					return true;
				if ($permission->getSourceType() == Permission::GROUP && $sourceObject->isMember($aName))
					return true;
			}
		}

		return false;
	}

	/**
	 * Return <code>String</code> with package informations.
	 */
	public function __toString() {
		return sprintf("Package [id:%s, type:%s, name:\"%s\", version:%d]", $this->id, $this->type, $this->name, $this->version);
	}

	/**
	 * Build XML from object.
	 *
	 * @param boolean $formatOutput
	 */
	public function buildXML( $formatOutput = true ) {
		$xml = new DOMDocument( "1.0", "UTF-8" );

		$root = $xml->createElement("package");

		$root->setAttribute( "id", $this->id );
		$root->setAttribute( "type", $this->type );
		$root->setAttribute( "version", $this->version );

		$name = $xml->createElement("name", $this->name);
		$root->appendChild($name);

		if (!empty($this->description)) {
			$desc = $xml->createElement("description", $this->description);
			$root->appendChild($desc);
		}

		if ($this->type == self::COMMON) {
			if (!empty($this->url)) {
				$url = $xml->createElement("url", $this->url);
				$root->appendChild($url);
			}
			if (!empty($this->startupScript)) {
				$startupScript = $xml->createElement("startupScript", $this->startupScript);
				$root->appendChild($startupScript);
			}
		} else if ($this->type == self::USER) {
			foreach( $this->JSFunction as $func ) {
				$f_xml = new DOMDocument();
				$f_xml->loadXML( $func->buildXML( false ) );

				$f_node = $xml->importNode( $f_xml->firstChild, true );
				$root->appendChild( $f_node );
			}
		}

		$xml->appendChild( $root );

		$xml->formatOutput = $formatOutput;

		return $xml->saveXML();
	}
}