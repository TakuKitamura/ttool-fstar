package ui.interactivesimulation;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

class TableRenderer extends DefaultTableCellRenderer
{
  private int row, col;

  public Component getTableCellRendererComponent (JTable table,
                          Object value,
                          boolean isSelected,
                          boolean hasFocus,
                          int row,
                          int column)
  {
   // Save row and column information for use in setValue().

   this.row = row;
   this.col = column;

   // Allow superclass to return rendering component.

   return super.getTableCellRendererComponent (table, value, 
                         isSelected, hasFocus,
                         row, column);
  }

  protected void setValue (Object v)
  {
   // Allow superclass to set the value.

   super.setValue (v);

   // If in names column, color cell with even row number white on
   // dark green, and cell with odd row number black on white.

   if (col == 0)
   {
     if (row % 2 == 0)
     {
       setForeground (Color.white);
       setBackground (new Color (0, 128, 0));
     }
     else
     {
       setForeground (UIManager.getColor ("Table.foreground"));
       setBackground (UIManager.getColor ("Table.background"));
     }

     return;
   }

   // Must be in balances column. Make sure v is valid.

   if (v == null)
     return;
    
   // Extract the cell's numeric value.

  // Double d = (Double) v;

   // If numeric value is less than zero, color cell yellow on red.
   // Otherwise, color cell black on white.

   if (v.equals("Request reqChannel_AppC_simplified_sec__SmartCard"))
   {
     setForeground (Color.yellow);
     setBackground (Color.red);
   }
   else
   {
     setForeground (UIManager.getColor ("Table.foreground"));
     setBackground (UIManager.getColor ("Table.background"));
   }
  }
}

