<?php
/**
 * Default values for internal use.
 */
@define('BASE_DIR', dirname(__FILE__) . '/../');
@define('CFG_ROOT', dirname(__FILE__) . '/');
@define('LIB_DIR', dirname(__FILE__) . '/../lib/');

@define('SAFEMODE', true);

if (SAFEMODE) {
	ini_set('include_path', ((LIB_DIR."doctrine/").PATH_SEPARATOR.ini_get('include_path')));
}

@include_once('Doctrine.php');

/**
 * DataSource Settings
 */
@define('BASE_DSN', 'mysql://ibwupd:ibwupd@127.0.0.1/IBWUpdater');

/**
 * Autoload and Init.
 */
@include_once BASE_DIR."class/internal/bootstrap.php";
?>