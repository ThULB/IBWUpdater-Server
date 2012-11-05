<?php
/**
 * Command line interface
 *
 * Allows easily writing OOP CLI scripts in PHP
 *
 * @package    IBWUpdater
 * @subpackage Commandline
 * @author     René Adler <rene.adler@tu-ilmenau.de>
 * @version    $Revision$
 */
class CLI {
	protected $VER	= "1.0";
	protected $REV	= '$Revision$';
	
	private $appname = 'CLI Framework';
	private $author = 'R. Adler';
	private $copyright = '(c) 2012 R. Adler - TU Ilmenau';
	
	function __construct($appname = null, $author = null, $copyright = null) {
		if($appname){
			$this->appname = $appname;
		}
		if($author){
			$this->author = $author;
		}
		if($copyright){
			$this->copyright = $copyright;
		}

		if(self::isCli()){
			try {
				$args = self::parseArgs();
				foreach($args['flags'] as $flag){
					$method_name = 'flag_'.$flag;
					if(method_exists($this, $method_name)){
						call_user_func(array($this, $method_name));
					}
				}
				foreach($args['arguments'] as $arg){
					$method_name = 'argument_'.$arg;
					if(method_exists($this, $method_name)){
						call_user_func(array($this, $method_name));
					}
				}

				foreach($args['options'] as $arg){
					if(is_string($arg) === false && isset($arg[0]) && isset($arg[1])){
						$method_name = 'option_'.$arg[0];
						if(method_exists($this, $method_name)){
							call_user_func(array($this, $method_name), $arg[1]);
						}
					}else{
						$method_name = 'option_'.$arg;
						if(method_exists($this, $method_name)){
							call_user_func(array($this, $method_name));
						}
					}
				}
				global $argv;
				if(count($argv) === 1){
					$this->help();
				}else{
					$this->main();
				}
				exit();
			} catch (Exception $e) {
				self::line("%r".$e->getMessage()."%n");
			}
		}
	}
	
	/**
	 * Simply test whether or not we are running in CLI mode.
	 */
	public static function isCli(){
		if(!defined('STDIN') && self::isCgi()) {

			if(getenv('TERM')) {
				return true;
			}
			return false;
		}
		return defined('STDIN');
	}


	public function getInput($question = "Are you sure you want to do this?  Type 'yes' to continue:"){
		echo $question." ";
		$handle = fopen ("php://stdin","r");
		$line = fgets($handle);
		$answer = trim($line);
		return $answer;
	}


	/**

	Example input: ./script.php -a arg1 --opt1 arg2 -bcde --opt2=val2 arg3 arg4 arg5 -fg --opt3
	Example output:
	Array
	(
	[exec] => ./script.php
	[options] => Array
	(
	[0] => opt1
	[1] => Array
	(
	[0] => opt2
	[1] => val2
	)
	[2] => opt3
	)
	[flags] => Array
	(
	[0] => a
	[1] => b
	[2] => c
	[3] => d
	[4] => e
	[5] => f
	[6] => g
	)
	[arguments] => Array
	(
	[0] => arg1
	[1] => arg2
	[2] => arg3
	[3] => arg4
	[4] => arg5
	)
	)
	*/
	public static function parseArgs($args = null) {
		if($args == null){
			global $argv,$argc;
			$args = $argv;
		}
		$ret = array(
				'exec'      => '',
				'options'   => array(),
				'flags'     => array(),
				'arguments' => array(),
		);
		if(count($args) == 0){
			return $ret;
		}

		$ret['exec'] = array_shift( $args );

		while (($arg = array_shift($args)) != NULL) {
			// Is it a option? (prefixed with --)
			if ( substr($arg, 0, 2) === '--' ) {
				$option = substr($arg, 2);

				// is it the syntax '--option=argument'?
				if (strpos($option,'=') !== FALSE)
					array_push( $ret['options'], explode('=', $option, 2) );
				else
					array_push( $ret['options'], $option );
					
				continue;
			}

			// Is it a flag or a serial of flags? (prefixed with -)
			if ( substr( $arg, 0, 1 ) === '-' ) {
				for ($i = 1; isset($arg[$i]) ; $i++)
					$ret['flags'][] = $arg[$i];

				continue;
			}

			// finally, it is not option, nor flag
			$ret['arguments'][] = $arg;
			continue;
		}
		return $ret;
	}

	/**
	 * ./script.php arg3
	 * input : arg3 ; return true
	 * input : arg4 ; return false
	 * @param unknown_type $argument
	 * @return bool
	 */
	function getArg($arg){
		$args = self::parseArgs();
		if(in_array($arg, $args)){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Checks if a certain option is set and returns the string.
	 * ./script --opt1
	 * @param unknown_type $option
	 */
	function getOption($option){
		//$args = $this->parseArgs();
		//return $args['options'][$option];
	}

	/**
	 * Handle the default help flag
	 */
	private function flag_h($opt = null){
		if($opt == 'help'){
			return 'Display help.';
		}
		$this->help();
	}

	/**
	 * Handle the default help argument
	 */
	private function argument_help($opt = null){
		if($opt == 'help'){
			return 'Display help.';
		}
		$this->help();
	}

	/**
	 * ./script.php --option1
	 *  --option1=var1 =>
	 *    array('options' =>
	 *      array( 0 => 'option1',
	 *        array('0' => 'option1', '1' => 'var1'))
	 *
	 * @param unknown_type $opt
	 */
	private function option_help($opt = null){

		if($opt == 'help'){
			return 'Display help for a specific command. ?=command';
		}


		if(substr($opt, 0, 2) == '--'){
			$opt = str_replace('--', '', $opt);
			//option
			$method = 'option_'.$opt;

		}elseif(substr($opt, 0, 1) == '-'){

			//flag
			$opt = str_replace('-', '', $opt);
			$method = 'flag_'.$opt;

		}else{
			//argument
			$method = 'argument_'.$opt;
		}


		if(method_exists($this, $method)){
			print "\n".$this->$method('help')."\n\n";
		}elseif($opt == null){
			$this->help();
		}else{
			print "\n".'Option, argument or flag not found.'."\n\n";
		}

	}

	protected function buildVersion() {
		preg_match('/(?P<rev>\d+)/', $this->REV, $match);
		return $this->VER.($match["rev"] != null ? ".".$match["rev"] : "");
	}
	
	/**
	 * Print out help for this program.
	 * The help is auto generated using various variables.
	 */
	public function help($args = array()){
		self::line("%r".$this->appname." (".$this->buildVersion().")%n");
		self::line("%r".$this->author.' - '.$this->copyright."%n");


		for($i=0; $i < strlen($this->appname.$this->author.$this->copyright); $i++){
			print '-';
		}
		print "\n";

		$longest = 0;
		$methods = get_class_methods(get_class($this));
		foreach($methods as $method){
			if(substr($method,0, 4) == 'flag'){
				$flag = str_replace('flag_', '', $method);
				$longest = strlen($flag) > $longest ? strlen($flag) : $longest;
				$flags_help[$flag] = $this->$method('help');
			}elseif(substr($method,0, 8) == 'argument'){
				$argument = str_replace('argument_', '', $method);
				$longest = strlen($argument) > $longest ? strlen($argument) : $longest;
				$arguments_help[$argument] = $this->$method('help');
			}elseif(substr($method,0, 6) == 'option'){
				$option = str_replace('option_', '', $method);
				$longest = strlen($option) > $longest ? strlen($option) : $longest;
				$options_help[$option] = $this->$method('help');
			}
		}

		self::line(" %bFlags:%n");
		foreach($flags_help as $flag => $desc){
			$spaces = ($longest*2+5) - strlen($flag);
			printf("  -%s%".$spaces."s%s\n", $flag,'', $desc);
		}
		self::line("\n %bArguments:%n");
		foreach($arguments_help as $arg => $desc){
			$spaces = ($longest*2+5) - strlen($arg) + 1;

			printf("  %s%".$spaces."s%s\n", $arg,'', $desc);
		}
		self::line("\n %bOptions:%n");
		foreach($options_help as $opt => $desc){
			$spaces = ($longest*2+5) - strlen($opt) - strlen($opt) - 4;
			printf("  --%s;%s=?%".$spaces."s%s\n",$opt, $opt,'', $desc);
		}
		print "\n";
		exit();
	}

	public static function line($string) {
		print Colors::colorize($string, !Shell::isPiped())."\n";
	}
}
?>