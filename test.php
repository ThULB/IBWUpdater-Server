<?php
@include_once "./config/defaults.php";

try {
	/*Doctrine_Core::generateModelsFromDb(
	 'models',
			array('doctrine'),
			array('generateTableClasses' => true)
	);*/

	
	/*Doctrine_Core::dropDatabases();
	Doctrine_Core::createDatabases();
	Doctrine_Core::createTablesFromModels(BASE_DIR."class/datamodel");*/

	/*$usrMgr = UserManager::getInstance();

	$user1 = $usrMgr->createUser("adlerre", "René Adler");
	print_r($user1->toArray(true));
	
	$user2 = $usrMgr->createUser("doerfer", "Klaus Dörfer");
	print_r($user2->toArray(true));
	
	$grpMgr = GroupManager::getInstance();
	$group = $grpMgr->createGroup("EDV", "EDV Abteilung", array($user1, $user2));
	
	print_r($group->toArray());*/

	$pkgMgr = PackageManager::getInstance();
	echo $pkgMgr->buildXML(true, $pkgMgr->getPackagesForUserName("adlerre"));
	
	/*$pkg = $pkgMgr->createPackage("IBWUpdater", "IBWUpdater Modul.", Package::COMMON);
	$pkg->setURL("packages/IBWUpdater.zip");
	$pkg->setStartupScript("resource:/scripts/IBWUpdater.js");
	$pkg->save();*/
	
	/*$pkg = $pkgMgr->createPackage("IBW3ESA", "Bearbeitungsmodul für die Semesterapparate in der DBT.", Package::COMMON);
	$pkg->setURL("packages/IBW3ESA.zip");
	$pkg->setStartupScript("resource:/scripts/ibw3esa.js");
	$pkg->addPermission($group);
	
	$pkg = $pkgMgr->createPackage("Geschäftsgang", "Macro für das setzen des Vorbestellung Links.", Package::USER);
	$pkg->addFunction("orderLink", "", "alert('Hello!');");
	$pkg->addPermission($user1);
	
	$pkg = $pkgMgr->createPackage("Inhaltsverzeichnis", "Macro für das setzen des Inhaltsverzeichnis Links.", Package::USER);
	$pkg->addFunction("ivLink", "", "alert('Hello!');");
	$pkg->addPermission($user1);
	*/
	
} catch (Exception $e) {
	echo $e->getMessage()."\n";
	echo $e->getTraceAsString();
}
?>