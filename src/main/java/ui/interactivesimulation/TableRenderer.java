package ui.interactivesimulation;

import java.awt.Color;
import java.awt.Component;
import java.util.Random;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

class TableRenderer extends DefaultTableCellRenderer {
	private int row, col;

	private Vector<Object> allCommands = new Vector();
	private Vector<Color> allColors = new Vector();

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		// Save row and column information for use in setValue().

		this.row = row;
		this.col = column;

		// Allow superclass to return rendering component.

		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

	protected void setValue(Object v) {
		// Allow superclass to set the value.

		super.setValue(v);

		// If in names column, color cell with even row number white on
		// dark green, and cell with odd row number black on white.

		/*
		 * if (col == 1) { if (row % 2 == 0) { setForeground(Color.white);
		 * setBackground(new Color(0, 128, 0)); } else {
		 * setForeground(UIManager.getColor("Table.foreground"));
		 * setBackground(UIManager.getColor("Table.background")); }
		 * 
		 * return; }
		 */

		// Must be in balances column. Make sure v is valid.

		if (v == null) {
			setForeground(UIManager.getColor("Table.foreground"));
			setBackground(UIManager.getColor("Table.background"));
			return;
		} else if (!allCommands.contains(v)) {
			allCommands.add(v);
			boolean x = true;
			while (x) {

				Random randomGenerator = new Random();
				int red = randomGenerator.nextInt(256);
				int green = randomGenerator.nextInt(256);
				int blue = randomGenerator.nextInt(256);

				Color randomColour = new Color(red, green, blue);

				if (!allColors.contains(randomColour)) {
					allColors.add(randomColour);
					x = false;

				}
			}

		}

		// Extract the cell's numeric value.

		// Double d = (Double) v;

		// If numeric value is less than zero, color cell yellow on red.
		// Otherwise, color cell black on white.

		if (allCommands.contains(v)) {
			setForeground(Color.yellow);
			setBackground(allColors.get(allCommands.indexOf(v)));
		} else {
			setForeground(UIManager.getColor("Table.foreground"));
			setBackground(UIManager.getColor("Table.background"));
		}
	}
}
