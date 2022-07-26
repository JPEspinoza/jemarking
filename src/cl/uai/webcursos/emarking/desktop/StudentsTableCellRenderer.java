// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop;

import cl.uai.webcursos.emarking.desktop.data.Student;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import cl.uai.webcursos.emarking.desktop.data.Moodle;
import javax.swing.table.DefaultTableCellRenderer;

public class StudentsTableCellRenderer extends DefaultTableCellRenderer
{
    private static final long serialVersionUID = -3207417568718607073L;
    private Moodle moodle;
    
    public StudentsTableCellRenderer(final Moodle _moodle) {
        this.moodle = _moodle;
    }
    
    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (isSelected) {
            c.setBackground(new Color(Integer.parseInt("BE", 16), Integer.parseInt("7B", 16), Integer.parseInt("E8", 16)));
        }
        else {
            c.setBackground(Color.WHITE);
            final Student student = this.moodle.getStudentByRowNumber(row);
            if (student != null) {
                if (student.getPages() == 0) {
                    c.setBackground(new Color(Integer.parseInt("FF", 16), Integer.parseInt("38", 16), Integer.parseInt("10", 16)));
                }
                else if (this.moodle.getPages().isNumberOutlier(student.getPages())) {
                    c.setBackground(new Color(Integer.parseInt("E8", 16), Integer.parseInt("C0", 16), Integer.parseInt("43", 16)));
                }
            }
        }
        return c;
    }
}
