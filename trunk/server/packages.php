<?php
/**
 * $Id$
 *
 * @package    IBWUpdater
 * @subpackage WebInterface
 * @author     René Adler <rene.adler@tu-ilmenau.de>
 * @version    $Revision$
 */

@include_once "./config/defaults.php";

try {
	$pkgMgr = PackageManager::getInstance();
	
	header('Content-Type: text/xml; charset=UTF-8');
	echo $pkgMgr->buildXML(true, $pkgMgr->getPackagesForUserName($_GET["uid"]));
} catch (Exception $e) {
	echo $e->getMessage()."\n";
	echo $e->getTraceAsString();
}
?>