<?php
/**
 * A simple JavaScript parser.
 *
 * @package    IBWUpdater
 * @subpackage Parser
 * @author     René Adler <rene.adler@tu-ilmenau.de>
 * @version    $Revision$
 */
class JSParser {
	const lineSeparator = "\n";
	
	private $functions;
	
	/**
	 * The Constructor of JSParser.
	 * 
	 * @param string $jsFile
	 */
	function __construct($jsFile) {
		$this->functions = $this->parseJSFile($jsFile);	
	}
	
	/**
	 * Returns the found code string if no function was found.
	 * 
	 * @return string
	 */
	public function getCodeString() {
		return !is_array($this->functions) ? $this->functions : null;
	}
	
	/**
	 * Returns the found functions or null if nothing was found.
	 * 
	 * @return array
	 */
	public function getFunctions() {
		return is_array($this->functions) ? $this->functions : null;
	}
	
	/**
	 * Helper for JavaScript parser to trim code from brackets.
	 *
	 * @param string $aCode
	 */
	private function trimCode($aCode) {
		$code = null;
	
		if (strpos($aCode, "{") !== false) {
			$code = substr($aCode, strpos($aCode, "{") + 1);
			$code = substr($code, 0, strrpos($code, "}"));
		}
	
		return $code;
	}
	
	/**
	 * Helper for JavaScript parser to check if a string starts with the given value.
	 *
	 * @param string $haystack
	 * @param string $needle
	 * @param boolean $withTrim
	 * @return boolean
	 */
	private function startsWith($haystack, $needle, $withTrim = true) {
		if ($withTrim)
			return substr(trim($haystack), 0, strlen($needle)) == $needle;
		else
			return substr($haystack, 0, strlen($needle)) == $needle;
	}
	
	private function parseJSFile($jsFile) {
		$functions = array();
	
		$jsLines = file($jsFile);
		$jsUnknown = null;
	
		$c = 0;
		while ($c < count($jsLines)) {
			$comment = null;
	
			// read comment(s)
			$line = trim($jsLines[$c]).self::lineSeparator;
	
			if ($this->startsWith($line, "/*")) {
				$comment = $line;
				$c++;
	
				$line = trim($jsLines[$c]).self::lineSeparator;
				while ($this->startsWith($line, "*/") != true) {
					$comment .= $line;
					$c++;
					$line = trim($jsLines[$c]).self::lineSeparator;
				}
				$comment .= $line;
				$c++;
				$line = trim($jsLines[$c]).self::lineSeparator;
			}
	
			if ($this->startsWith($line, "//")) {
				if ($comment == null)
					$comment = $line;
				else
					$comment .= $line;
			}
	
			// read function(s)
			if ($this->startsWith($line, "function")) {
				$found = preg_match("/function\s([^\(].*)\((.*)\)/", $line, $match);
				if ($found) {
					$brackets = 0;
					$code = null;
	
					while (strpos($line, "{") === false) {
						$c++;
						$line = trim($jsLines[$c]).self::lineSeparator;
					}
	
					do {
						if (strpos($line, "{") !== false)
							$brackets++;
						if (strpos($line, "}") !== false)
							$brackets--;
	
						if ($code == null)
							$code = $line;
						else
							$code .= $line;
	
						if (($brackets != 0)) {
							$c++;
							$line = trim($jsLines[$c]).self::lineSeparator;
						}
					} while ($brackets != 0);
	
					if (mb_detect_encoding($comment, "UTF-8", true) == false)
						$comment = utf8_encode($comment);
					if (mb_detect_encoding($code, "UTF-8", true) == false)
						$code = utf8_encode($code);
					
					$functions[$match[1]] = array("params" => $match[2], "comment" => $comment, "code" => $this->trimCode($code));
				}
			} else {
				$line = trim($line);
				if (strlen($line) != 0) {
					if ($jsUnknown == null)
						$jsUnknown = $line.self::lineSeparator;
					else
						$jsUnknown .= $line.self::lineSeparator;
				}
			}
	
			// next line
			$c++;
		}
	
		return $functions != null ? $functions : $jsUnknown;
	}
}