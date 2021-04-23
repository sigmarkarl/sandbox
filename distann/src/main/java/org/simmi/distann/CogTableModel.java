package org.simmi.distann;

import org.simmi.javafasta.shared.Cog;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.List;

public class CogTableModel implements TableModel {
    List<String> coglist;

    public CogTableModel(List<String> coglist) {
        this.coglist = coglist;
    }

    @Override
    public int getRowCount() {
        return coglist.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) return "Symbol";
        else return "Name";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String c = coglist.get(rowIndex);
        if (columnIndex == 0) return c;
        else return Cog.charcog.get(c);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
    }
}
