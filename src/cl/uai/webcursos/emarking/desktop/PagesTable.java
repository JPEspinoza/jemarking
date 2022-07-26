// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop;

import java.awt.Component;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import cl.uai.webcursos.emarking.desktop.data.Moodle;
import javax.swing.JTable;

public class PagesTable extends JTable
{
    private PagesTableModel model;
    private PagesTableCellRenderer renderer;
    private Moodle moodle;
    private static final Object[][] emptydata;
    private static final String[] columnNames;
    private static final long serialVersionUID = -2094514351707140215L;
    
    static {
        emptydata = new Object[0][];
        columnNames = new String[] { "#", EmarkingDesktop.lang.getString("student"), EmarkingDesktop.lang.getString("course"), EmarkingDesktop.lang.getString("page") };
    }
    
    public PagesTable(final Moodle _moodle) {
        super(new PagesTableModel(PagesTable.emptydata, PagesTable.columnNames));
        this.moodle = _moodle;
        this.setDefaultRenderer(Object.class, this.renderer = new PagesTableCellRenderer(this.moodle));
        this.setAutoResizeMode(2);
        this.setPagesTableModel((PagesTableModel)this.getModel());
    }
    
    public PagesTableModel getPagesTableModel() {
        return this.model;
    }
    
    public void setPagesTableModel(final PagesTableModel model) {
        this.model = model;
    }
    
    public void updateData(final Object[] data, final int row, final boolean doubleside) {
        for (int i = 0; i < data.length; ++i) {
            this.setValueAt(data[i], row, i);
            int width = 0;
            for (int mrow = 0; mrow < this.getRowCount(); ++mrow) {
                final TableCellRenderer renderer = this.getCellRenderer(mrow, i);
                final Component comp = this.prepareRenderer(renderer, mrow, i);
                width = Math.max(comp.getPreferredSize().width, width);
            }
            this.getColumnModel().getColumn(i).setPreferredWidth(width);
        }
        if (doubleside && row % 2 == 0) {
            this.updateData(data, row + 1, doubleside);
        }
    }
}
