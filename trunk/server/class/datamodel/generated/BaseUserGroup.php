<?php

/**
 * BaseUserGroup
 * 
 * This class has been auto-generated by the Doctrine ORM Framework
 * 
 * @property integer $id
 * @property string $name
 * @property string $description
 * @property Doctrine_Collection $GroupMember
 * 
 * @package    IBWUpdater
 * @subpackage Datamodel
 * @author     René Adler <rene.adler@tu-ilmenau.de>
 * @version    $Revision$
 */
abstract class BaseUserGroup extends Doctrine_Record
{
    public function setTableDefinition()
    {
        $this->setTableName('UserGroup');
        $this->hasColumn('id', 'integer', 4, array(
             'type' => 'integer',
             'length' => 4,
             'fixed' => false,
             'unsigned' => true,
             'primary' => true,
             'autoincrement' => true,
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
    }

    public function setUp()
    {
        parent::setUp();
        $this->hasMany('GroupMember', array(
             'local' => 'id',
             'foreign' => 'gid'));
    }
}