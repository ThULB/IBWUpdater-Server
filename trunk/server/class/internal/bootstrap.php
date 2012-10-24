<?php
/**
 * Bootstrap
 *
 * @package    IBWUpdater
 * @subpackage Internal
 * @author     René Adler <rene.adler@tu-ilmenau.de>
 * @version    $Revision$
 */
function __autoload( $className ) {
	//Directories added here must be
	//relative to the script going to use this file.
	//New entries can be added to this list
	$directories = array(
			'class/',
			'class/datamodel',
			'class/internal/',
			'class/persistency/',
	);

	//Add your file naming formats here
	$fileNameFormats = array(
			'%s.cls.php',
			'class.%s.inc.php'
	);

	// this is to take care of the PEAR style of naming classes
	$path = str_ireplace('_', '/', $className);
	if ( @include_once $path.'.php' ){
		return;
	}

	foreach ( $directories as $directory ) {
		foreach ( $fileNameFormats as $fileNameFormat ) {
			$path = $directory.sprintf( $fileNameFormat, $className );
			if ( file_exists($path) ){
				include_once $path;
				return;
			} else {
				$path = $directory.sprintf( $fileNameFormat, strtolower($className) );
				if ( file_exists($path) ){
					include_once $path;
					return;
				}
			}
		}
	}
}

// init Doctrine
Doctrine_Core::loadModels(BASE_DIR."class/datamodel");

$manager = Doctrine_Manager::getInstance();
$conn = Doctrine_Manager::connection(BASE_DSN);
?>