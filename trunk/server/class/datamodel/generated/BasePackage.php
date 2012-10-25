<?php

/**
 * BasePackage
 *
 * This class has been auto-generated by the Doctrine ORM Framework
 *
 * @property string $id
 * @property string $type
 * @property integer $version
 * @property string $name
 * @property string $description
 * @property string $url
 * @property string $startupScript
 * @property Doctrine_Collection $JSFunction
 * @property Doctrine_Collection $Permission
 *
 * @package    IBWUpdater
 * @subpackage Datamodel
 * @author     René Adler <rene.adler@tu-ilmenau.de>
 * @version    $Revision$
 */
abstract class BasePackage extends Doctrine_Record
{
	public function setTableDefinition()
	{
		$this->setTableName('Package');
		$this->option('collate', 'utf8_unicode_ci');
		$this->option('charset', 'utf8');
		
		$this->hasColumn('id', 'string', 64, array(
				'type' => 'string',
				'length' => 64,
				'fixed' => false,
				'unsigned' => false,
				'primary' => true,
				'autoincrement' => false,
		));
		$this->hasColumn('type', 'enum', null, array(
				'type' => 'enum',
			 	'default' => 'common',
				'notnull' => true,
				'autoincrement' => false,
				'values' => array('common', 'user'),
		));
		$this->hasColumn('version', 'integer', 4, array(
				'type' => 'integer',
				'length' => 4,
				'fixed' => false,
				'unsigned' => false,
				'primary' => false,
				'notnull' => true,
				'autoincrement' => false,
		));
		$this->hasColumn('name', 'string', 64, array(
				'type' => 'string',
				'length' => 64,
				'fixed' => false,
				'unsigned' => false,
				'primary' => false,
				'default' => '',
				'notnull' => true,
				'autoincrement' => false,
		));
		$this->hasColumn('description', 'string', null, array(
				'type' => 'string',
				'fixed' => false,
				'unsigned' => false,
				'primary' => false,
				'notnull' => false,
				'autoincrement' => false,
		));
		$this->hasColumn('url', 'string', null, array(
				'type' => 'string',
				'fixed' => false,
				'unsigned' => false,
				'primary' => false,
				'notnull' => false,
				'autoincrement' => false,
		));
		$this->hasColumn('startupScript', 'string', 255, array(
				'type' => 'string',
				'length' => 255,
				'fixed' => false,
				'unsigned' => false,
				'primary' => false,
				'notnull' => false,
				'autoincrement' => false,
		));
	}

	public function setUp()
	{
		parent::setUp();
		$this->hasMany('JSFunction', array(
				'local' => 'id',
				'foreign' => 'pid'));

		$this->hasMany('Permission', array(
				'local' => 'id',
				'foreign' => 'targetId'));
	}
}