<?php

/**
 * JSFunction
 *
 * @package    IBWUpdater
 * @subpackage Datamodel
 * @author     René Adler <rene.adler@tu-ilmenau.de>
 * @version    $Revision$
 */
class JSFunction extends BaseJSFunction {
	public function setId($aId) {
		$this->id = $aId;
	}
	
	public function getId() {
		return $this->id;
	}
	
	public function setPId($aPId) {
		$this->pid = $aPId;
	}
	
	public function getPId() {
		return $this->pid;
	}
	
	public function setName($aName) {
		$this->name = $aName;
	}

	public function getName() {
		return $this->name;
	}

	public function setParams($aParams) {
		$this->params = $aParams;
	}

	public function getParams() {
		return $this->params;
	}

	public function setCode($aCode) {
		$this->code = $aCode;
	}

	public function getCode() {
		return $this->code;
	}

	public function __toString() {
		return sprintf("JSFunction [name:\"%s\", params:\"%s\"]", $this->name, $this->params);
	}

	/**
	 * Build XML from object.
	 *
	 * @param boolean $formatOutput
	 */
	public function buildXML( $formatOutput = true ) {
		$xml = new DOMDocument( "1.0", "UTF-8" );

		$root = $xml->createElement("function");

		$root->setAttribute( "name", $this->name );
		$root->setAttribute( "params", $this->params );

		$code = $xml->createCDATASection($this->code);
		$root->appendChild($code);

		$xml->appendChild( $root );

		$xml->formatOutput = $formatOutput;

		return $xml->saveXML();
	}
}