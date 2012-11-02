<?php
/**
 * Table renderers are used to change how a table is displayed.
 */
abstract class Renderer {
	protected $_widths = array();

	public function __construct(array $widths = array()) {
		$this->setWidths($widths);
	}

	/**
	 * Set the widths of each column in the table.
	 *
	 * @param array  $widths  The widths of the columns.
	 */
	public function setWidths(array $widths) {
		$this->_widths = $widths;
	}

	/**
	 * Render a border for the top and bottom and separating the headers from the
	 * table rows.
	 *
	 * @return string  The table border.
	 */
	public function border() {
		return null;
	}

	/**
	 * Renders a row for output.
	 *
	 * @param array  $row  The table row.
	 * @return string  The formatted table row.
	 */
	abstract public function row(array $row);
}
