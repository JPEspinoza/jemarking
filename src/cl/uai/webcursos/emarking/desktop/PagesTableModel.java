// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop;

import javax.swing.table.DefaultTableModel;

public class PagesTableModel extends DefaultTableModel
{
    private static final long serialVersionUID = -7215867738204049050L;
    
    public PagesTableModel(final Object[][] data, final String[] headers) {
        super(data, headers);
    }
    
    @Override
    public boolean isCellEditable(final int row, final int column) {
        return false;
    }
}
