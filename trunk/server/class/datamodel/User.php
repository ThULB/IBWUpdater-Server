<?php

/**
 * User
 *
 * @package    IBWUpdater
 * @subpackage Datamodel
 * @author     René Adler <rene.adler@tu-ilmenau.de>
 * @version    $Revision$
 */
class User extends BaseUser {
	public function setId($aId) {
		$this->id = $aId;
	}

	public function getId() {
		return $this->id;
	}

	public function setName($aName) {
		$this->name = strtolower($aName);
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

	public function __toString() {
		return sprintf("User [id:%d, name:\"%s\", description:\"%s\"]", $this->id, $this->name, $this->description);
	}
}