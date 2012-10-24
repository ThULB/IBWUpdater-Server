<?php
/**
 * PackageManager
 *
 * @package    IBWUpdater
 * @subpackage Persistency
 * @author     René Adler <rene.adler@tu-ilmenau.de>
 * @version    $Revision$
 */
class PackageManager {
	private static $instance;

	private $packages;
	
	static public function getInstance() {
		if (null === self::$instance) {
			self::$instance = new self;
		}
		return self::$instance;
	}

	public function __construct() {
		$this->packages = PackageTable::getInstance();
	}

	protected function getPackages() {
		$packages = array();
		foreach ($this->packages->findAll() as $package) {
			array_push($packages, $package);
		}
	
		return $packages;
	}
	
	public function getPackagesForUserName($aName) {
		$packages = array();
		foreach ($this->packages->findAll() as $package) {
			if ($package->hasPermission($aName)) {
				array_push($packages, $package);
			}
		}
		
		return $packages;
	}
	
	public function getPackage($aIdOrName) {
		$package = $this->packages->find($aIdOrName);
		
		if ($package == null)
			$package = $this->packages->findOneByName($aIdOrName);
		
		return $package;
	}

	public function createPackage($aName, $aDescription, $aType = Package::COMMON) {
		if ($this->getPackage($aName) != null)
			throw new Exception("Package with name \"$aName\" already exists!");

		$package = new Package();
		
		$package->setId(PackageManager::genUUID());
		$package->setName($aName);
		$package->setDescription($aDescription);
		$package->setType($aType);
		$package->setVersion(1);
		
		$package->save();
		
		return $package;
	}
	
	/**
	 * Build XML from object.
	 *
	 * @param boolean $formatOutput
	 */
	public function buildXML( $formatOutput = true, $aPackages = null ) {
		if ($aPackages == null && !is_array($aPackages)) {
			$aPackages = $this->getPackages();
		}
	
		$xml = new DOMDocument( "1.0", "UTF-8" );
	
		$root = $xml->createElement("packages");
	
		foreach( $aPackages as $package ) {
			$p_xml = new DOMDocument();
			$p_xml->loadXML( $package->buildXML( false ) );
	
			$p_node = $xml->importNode( $p_xml->firstChild, true );
			$root->appendChild( $p_node );
		}
	
		$xml->appendChild( $root );
	
		$xml->formatOutput = $formatOutput;
	
		return $xml->saveXML();
	}
	
	/**
	 * Generates version 4 UUID: random
	 */
	private static function genUUID() {
		return sprintf('%04x%04x-%04x-%04x-%04x-%04x%04x%04x',
				// 32 bits for "time_low"
				mt_rand(0, 0xffff), mt_rand(0, 0xffff),
	
				// 16 bits for "time_mid"
				mt_rand(0, 0xffff),
	
				// 16 bits for "time_hi_and_version",
				// four most significant bits holds version number 4
				mt_rand(0, 0x0fff) | 0x4000,
	
				// 16 bits, 8 bits for "clk_seq_hi_res",
				// 8 bits for "clk_seq_low",
				// two most significant bits holds zero and one for variant DCE1.1
				mt_rand(0, 0x3fff) | 0x8000,
	
				// 48 bits for "node"
				mt_rand(0, 0xffff), mt_rand(0, 0xffff), mt_rand(0, 0xffff)
		);
	}
}
?>