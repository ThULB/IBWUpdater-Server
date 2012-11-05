<?php
/**
 * The `Table` class is used to display data in a tabular format.
 */
class Table {
	protected $_renderer;
	protected $_headers = array();
	protected $_width = array();
	protected $_rows = array();

	/**
	 * Initializes the `Table` class.
	 *
	 * There are 3 ways to instantiate this class:
	 *
	 *  1. Pass an array of strings as the first paramater for the column headers
	 *     and a 2-dimensional array as the second parameter for the data rows.
	 *  2. Pass an array of hash tables (string indexes instead of numerical)
	 *     where each hash table is a row and the indexes of the *first* hash
	 *     table are used as the header values.
	 *  3. Pass nothing and use `setHeaders()` and `addRow()` or `setRows()`.
	 *
	 * @param array  $headers  Headers used in this table. Optional.
	 * @param array  $rows     The rows of data for this table. Optional.
	 */
	public function __construct(array $headers = null, array $rows = null) {
		if (!empty($headers)) {
			// If all the rows is given in $headers we use the keys from the
			// first row for the header values
			if (empty($rows)) {
				$rows = $headers;
				$keys = array_keys(array_shift($headers));
				$headers = array();

				foreach ($keys as $header) {
					$headers[$header] = $header;
				}
			}

			$this->setHeaders($headers);
			$this->setRows($rows);
		}

		if (Shell::isPiped()) {
			$this->setRenderer(new Tabular());
		} else {
			$this->setRenderer(new Ascii());
		}
	}

	/**
	 * Sets the renderer used by this table.
	 *
	 * @param table\Renderer  $renderer  The renderer to use for output.
	 * @see   table\Renderer
	 * @see   table\Standard
	 * @see   table\Tabular
	 */
	public function setRenderer($renderer) {
		$this->_renderer = $renderer;
	}

	/**
	 * Loops through the row and sets the maximum width for each column.
	 *
	 * @param array  $row  The table row.
	 */
	protected function checkRow(array $row) {
		foreach ($row as $column => $str) {
			$width = Colors::length($str);
			if (!isset($this->_width[$column]) || $width > $this->_width[$column]) {
				$this->_width[$column] = $width;
			}
		}

		return $row;
	}

	/**
	 * Output the table to `STDOUT` using `line()`.
	 *
	 * If STDOUT is a pipe or redirected to a file, should output simple
	 * tab-separated text. Otherwise, renders table with ASCII table borders
	 *
	 * @uses Shell::isPiped() Determine what format to output
	 *
	 * @see Table::renderRow()
	 */
	public function display() {
		$this->_renderer->setWidths($this->_width);
		$border = $this->_renderer->border();

		if (isset($border)) {
			CLI::line($border);
		}
		CLI::line($this->_renderer->row($this->_headers));
		if (isset($border)) {
			CLI::line($border);
		}

		foreach ($this->_rows as $row) {
			CLI::line($this->_renderer->row($row));
		}

		if (isset($border)) {
			CLI::line($border);
		}
	}
	
	/**
	 * Set the headers of the table.
	 *
	 * @param array  $headers  An array of strings containing column header names.
	 */
	public function setHeaders(array $headers) {
		$this->_headers = $this->checkRow($headers);
	}

	/**
	 * Add a row to the table.
	 *
	 * @param array  $row  The row data.
	 * @see Table::checkRow()
	 */
	public function addRow(array $row) {
		$this->_rows[] = $this->checkRow($row);
	}

	/**
	 * Clears all previous rows and adds the given rows.
	 *
	 * @param array  $rows  A 2-dimensional array of row data.
	 * @see Table::addRow()
	 */
	public function setRows(array $rows) {
		$this->_rows = array();
		foreach ($rows as $row) {
			$this->addRow($row);
		}
	}
}
