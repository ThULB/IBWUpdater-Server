<?php

/**
 * UserGroup
 *
 * @package    IBWUpdater
 * @subpackage Datamodel
 * @author     René Adler <rene.adler@tu-ilmenau.de>
 * @version    $Revision$
 */
class UserGroup extends BaseUserGroup {
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

	public function addMember($aObject) {
		$member = null;

		if ($aObject instanceof User) {
			$member = new GroupMember();
			$member->gid = $this->id;
			$member->uid = $aObject->getId();
		} else if (is_numeric($aObject)) {
			$member = new GroupMember();
			$member->gid = $this->id;
			$member->uid = $aObject;
		}

		// only add new one if function don't exists
		if ($this->getMember($member->User->getName()) == null) {
			$this->GroupMember[$this->GroupMember->count()] = $member;
		} else
			$this->setMember($member);

		$this->save();

		return $member;
	}

	public function setMember($aMember) {
		$member = $this->getMember($aMember->User->getName());
		if ($member != null) {
			$member->gid = $aMember->gid;
			$member->uid = $aMember->uid;
		} else
			$this->addMember($aMember);

		$this->save();
	}

	public function getMember($aObject) {
		if ($this->GroupMember->count() != 0) {
			foreach ($this->GroupMember as $key => $member) {
				if ((is_numeric($aObject) && $member->uid == $aObject) || ($member->User->name == $aObject))
					return $this->GroupMember[$key];
			}
		}

		return null;
	}

	public function isMember($aObject) {
		return $this->getMember($aObject) != null;
	}

	public function removeMember($aName) {
		if ($this->GroupMember->count() != 0) {
			foreach ($this->GroupMember as $key => $member) {
				if ($member->User->name == $aName) {
					unset($this->GroupMember[$key]);
					$this->GroupMember[$key]->delete();
					return;
				}
			}
		}
	}

	public function __toString() {
		return sprintf("UserGroup [id:%d, name:\"%s\", description::\"%s\"]", $this->id, $this->name, $this->description);
	}
}