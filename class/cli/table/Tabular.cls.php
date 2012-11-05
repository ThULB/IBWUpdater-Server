<?php
/**
 * The tabular renderer is used for displaying data in a tabular format.
 */
class Tabular extends Renderer {
	/**
	 * Renders a row for output.
	 *
	 * @param array  $row  The table row.
	 * @return string  The formatted table row.
	 */
	public function row($row) {
		return implode("\t", array_values($row));
	}
}
