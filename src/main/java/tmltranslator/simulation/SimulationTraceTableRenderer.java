package tmltranslator.simulation;

import java.awt.Color;
import java.awt.Component;
import java.util.Random;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

public class SimulationTraceTableRenderer extends DefaultTableCellRenderer {
    private int row, col;
    private Vector<Object> allCommands = new Vector<Object>();
    private Vector<Color> allColors = new Vector<Color>();

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        this.row = row;
        this.col = column;
        // Allow superclass to return rendering component.
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    protected void setValue(Object v) {
        super.setValue(v);
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
        if (allCommands.contains(v)) {
            setForeground(Color.yellow);
            setBackground(allColors.get(allCommands.indexOf(v)));
        } else {
            setForeground(UIManager.getColor("Table.foreground"));
            setBackground(UIManager.getColor("Table.background"));
        }
    }
}
