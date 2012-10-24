<?php

/**
 * Permission
 * 
 * @package    IBWUpdater
 * @subpackage Datamodel
 * @author     René Adler <rene.adler@tu-ilmenau.de>
 * @version    $Revision$
 */
class Permission extends BasePermission {
	const GROUP	= 'g';
	const USER	= 'u';
	
	const READ	= 'r';
	const WRITE	= 'w';
	
	private $sourceObject;
	
	public function setSourceType($aSourceType) {
		if ($aSourceType != self::GROUP && $aSourceType != self::USER)
			throw new Exception("The source type must either GROUP or USER!");
	
		$this->sourceType = $aSourceType;
	}
	
	public function getSourceType() {
		return $this->sourceType;
	}
	
	public function setSourceId($aSourceId) {
		$this->sourceId = $aSourceId;
	}
	
	public function getSourceId() {
		return $this->sourceId;
	}
	
	public function setSourceObject($aSourceObject) {
		if (!($aSourceObject instanceof UserGroup) && !($aSourceObject instanceof User))
			throw new Exception("The source object must either GROUP or USER!");
	
		$this->sourceObject = $aSourceObject;
	}
	
	public function getSourceObject() {
		return $this->sourceObject;
	}
	
	public function setAction($aAction) {
		if ($aAction != self::READ && $aAction != self::WRITE)
			throw new Exception("The action must either READ or WRITE!");
	
		$this->action = $aAction;
	}
	
	public function getAction() {
		return $this->action;
	}
	
	public function setTargetId($aTargetId) {
		$this->targetId = $aTargetId;
	}
	
	public function getTargetId() {
		return $this->targetId;
	}
}