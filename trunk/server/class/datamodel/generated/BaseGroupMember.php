<?php

/**
 * BaseGroupMember
 * 
 * This class has been auto-generated by the Doctrine ORM Framework
 * 
 * @property integer $gid
 * @property integer $uid
 * @property User $User
 * @property UserGroup $UserGroup
 * 
 * @package    IBWUpdater
 * @subpackage Datamodel
 * @author     René Adler <rene.adler@tu-ilmenau.de>
 * @version    $Revision$
 */
abstract class BaseGroupMember extends Doctrine_Record
{
    public function setTableDefinition()
    {
        $this->setTableName('GroupMember');
        $this->hasColumn('gid', 'integer', 4, array(
             'type' => 'integer',
             'length' => 4,
             'fixed' => false,
             'unsigned' => true,
             'primary' => true,
             'autoincrement' => false,
             ));
        $this->hasColumn('uid', 'integer', 4, array(
             'type' => 'integer',
             'length' => 4,
             'fixed' => false,
             'unsigned' => true,
             'primary' => true,
             'autoincrement' => false,
             ));
    }

    public function setUp()
    {
        parent::setUp();
        $this->hasOne('User', array(
             'local' => 'uid',
             'foreign' => 'id'));

        $this->hasOne('UserGroup', array(
             'local' => 'gid',
             'foreign' => 'id'));
    }
}