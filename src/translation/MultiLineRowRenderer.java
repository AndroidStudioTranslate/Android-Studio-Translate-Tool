/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package translation;

import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.*;
import javax.swing.JTable;
import java.awt.Component;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * <p>
 * Title: </p>
 * <p>
 * Description: </p>
 * <p>
 * Copyright: Copyright (c) 2008</p>
 * <p>
 * Company: </p>
 *
 * @Author SamZheng 2008-03-31
 * @version 1.0
 */
public class MultiLineRowRenderer
        extends JTextArea
        implements TableCellRenderer {

    private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();

    /**
     * map from table to map of rows to map of column heights
     */
    private final Map cellSizes = new HashMap();

    public MultiLineRowRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
    }

    public Component getTableCellRendererComponent( //
            JTable table, Object obj, boolean isSelected, boolean hasFocus, int row,
            int column) {
        // set the colours, etc. using the standard for that platform
        adaptee.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row,
                column);
        setForeground(adaptee.getForeground());
        setBackground(adaptee.getBackground());
        setBorder(adaptee.getBorder());
        setFont(adaptee.getFont());
        setText(adaptee.getText());

        // This line was very important to get it working with JDK1.4
        TableColumnModel columnModel = table.getColumnModel();
        setSize(columnModel.getColumn(column).getWidth(), 100000);
        int height_wanted = (int) getPreferredSize().getHeight();
        addSize(table, row, column, height_wanted);
        height_wanted = findTotalMaximumRowSize(table, row);
        if (height_wanted != table.getRowHeight(row)) {
            table.setRowHeight(row, height_wanted);
        }
        return this;
    }

    private void addSize(JTable table, int row, int column, int height) {
        Map rows = (Map) cellSizes.get(table);
        if (rows == null) {
            cellSizes.put(table, rows = new HashMap());
        }
        Map rowheights = (Map) rows.get(new Integer(row));
        if (rowheights == null) {
            rows.put(new Integer(row), rowheights = new HashMap());
        }
        rowheights.put(new Integer(column), new Integer(height));
    }

    /**
     * Look through all columns and get the renderer. If it is also a
     * MultiLineRowRenderer, we look at the maximum height in its hash table for
     * this row.
     */
    private int findTotalMaximumRowSize(JTable table, int row) {
        int maximum_height = 0;
        Enumeration columns = table.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn tc = (TableColumn) columns.nextElement();
            TableCellRenderer cellRenderer = tc.getCellRenderer();
            if (cellRenderer instanceof MultiLineRowRenderer) {
                MultiLineRowRenderer tar = (MultiLineRowRenderer) cellRenderer;
                maximum_height = Math.max(maximum_height,
                        tar.findMaximumRowSize(table, row));
            }
        }
        return maximum_height;
    }

    private int findMaximumRowSize(JTable table, int row) {
        Map rows = (Map) cellSizes.get(table);
        if (rows == null) {
            return 0;
        }
        Map rowheights = (Map) rows.get(new Integer(row));
        if (rowheights == null) {
            return 0;
        }
        int maximum_height = 0;
        for (Iterator it = rowheights.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            int cellHeight = ((Integer) entry.getValue()).intValue();
            maximum_height = Math.max(maximum_height, cellHeight);
        }
        return maximum_height;
    }
}
