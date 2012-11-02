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
			LIB_DIR.'doctrine/',
			BASE_DIR.'class/',
			BASE_DIR.'class/cli/',
			BASE_DIR.'class/cli/table/',
			BASE_DIR.'class/commandline/',
			BASE_DIR.'class/datamodel/',
			BASE_DIR.'class/datamodel/generated/',
			BASE_DIR.'class/internal/',
			BASE_DIR.'class/persistency/',
	);

	//Add your file naming formats here
	$fileNameFormats = array(
			'%s.cls.php',
			'class.%s.inc.php'
	);

	foreach ( $directories as $directory ) {
		// this is to take care of the PEAR style of naming classes
		$path = $directory.str_ireplace('_', '/', $className);
		if ( @include_once $path.'.php' ){
			return;
		}

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
try {
	Doctrine_Core::loadModels(BASE_DIR."class/datamodel");

	$manager = Doctrine_Manager::getInstance();
	$conn = Doctrine_Manager::connection(BASE_DSN);
	$conn->setCharset('utf8');
} catch (Exception $e) {
	echo $e->getMessage()."\n";
	echo $e->getTraceAsString();
}
?>