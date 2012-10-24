<?php
@include_once "./config/defaults.php";

try {
	Doctrine_Core::dropDatabases();
	Doctrine_Core::createDatabases();
	Doctrine_Core::createTablesFromModels(BASE_DIR."class/datamodel");
} catch (Exception $e) {
	echo $e->getMessage()."\n";
	echo $e->getTraceAsString();
}
?>