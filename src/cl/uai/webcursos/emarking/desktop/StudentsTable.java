// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop;

import java.awt.Component;
import cl.uai.webcursos.emarking.desktop.data.Student;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import cl.uai.webcursos.emarking.desktop.data.Moodle;
import org.apache.log4j.Logger;
import javax.swing.JTable;

public class StudentsTable extends JTable
{
    private static Logger logger;
    private StudentsTableModel model;
    private StudentsTableCellRenderer renderer;
    private Moodle moodle;
    private static final Object[][] emptydata;
    private static final String[] columnNames;
    private static final long serialVersionUID = -2094514351707140215L;
    
    static {
        StudentsTable.logger = Logger.getLogger(StudentsTable.class);
        emptydata = new Object[0][];
        columnNames = new String[] { "#", EmarkingDesktop.lang.getString("student"), EmarkingDesktop.lang.getString("pages"), "Answers" };
    }
    
    public StudentsTable(final Moodle _moodle) {
        super(new StudentsTableModel(StudentsTable.emptydata, StudentsTable.columnNames));
        this.moodle = _moodle;
        this.setDefaultRenderer(Object.class, this.renderer = new StudentsTableCellRenderer(this.moodle));
        this.setAutoResizeMode(2);
        this.setStudentsTableModel((StudentsTableModel)this.getModel());
    }
    
    public StudentsTableModel getPagesTableModel() {
        return this.model;
    }
    
    public void setStudentsTableModel(final StudentsTableModel model) {
        this.model = model;
    }
    
    public void updateData(final Student student) {
        if (this.model.getRowCount() == 0) {
            for (int i = 0; i < this.moodle.getStudents().size(); ++i) {
                final Student st = this.moodle.getStudentByRowNumber(i);
                this.model.addRow((Object[])null);
                if (st != null) {
                    this.setValueAt(st.getRownumber() + 1, i, 0);
                    this.setValueAt(st.getFullname(), i, 1);
                    this.setValueAt(st.getPages(), i, 2);
                    this.setValueAt(st.getAnswersValues(), i, 3);
                }
                else {
                    StudentsTable.logger.error("Invalid student");
                }
            }
        }
        final int row = student.getRownumber();
        this.setValueAt(student.getRownumber() + 1, row, 0);
        this.setValueAt(student.getFullname(), row, 1);
        this.setValueAt(student.getPages(), row, 2);
        this.setValueAt(student.getAnswersValues(), row, 3);
        for (int j = 0; j < this.model.getColumnCount(); ++j) {
            int width = 0;
            for (int mrow = 0; mrow < this.getRowCount(); ++mrow) {
                final TableCellRenderer renderer = this.getCellRenderer(mrow, j);
                final Component comp = this.prepareRenderer(renderer, mrow, j);
                width = Math.max(comp.getPreferredSize().width, width);
            }
            this.getColumnModel().getColumn(j).setPreferredWidth(width);
        }
    }
}
