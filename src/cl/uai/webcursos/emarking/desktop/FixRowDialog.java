// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import java.awt.Component;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import cl.uai.webcursos.emarking.desktop.data.Course;
import cl.uai.webcursos.emarking.desktop.data.Student;
import javax.swing.JComboBox;
import cl.uai.webcursos.emarking.desktop.data.Moodle;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JDialog;

public class FixRowDialog extends JDialog
{
    private static final long serialVersionUID = -3193581699837376410L;
    private final JPanel contentPanel;
    private JLabel lblPageNumber;
    private Moodle moodle;
    private boolean cancelled;
    private JComboBox<Student> studentsCombo;
    private JComboBox<Course> coursesCombo;
    private JComboBox<Integer> comboPageNumber;
    
    public Moodle getMoodle() {
        return this.moodle;
    }
    
    public void setMoodle(final Moodle moodle) {
        this.moodle = moodle;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public FixRowDialog(final Moodle moodle) {
        this.contentPanel = new JPanel();
        this.moodle = null;
        this.cancelled = false;
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(FixRowDialog.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/glyphicons_152_check.png")));
        this.setTitle(EmarkingDesktop.lang.getString("fixrow"));
        this.setMoodle(moodle);
        this.setBounds(100, 100, 451, 194);
        this.getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.getContentPane().add(this.contentPanel, "Center");
        this.contentPanel.setLayout(null);
        final JLabel lblPageNumberTitle = new JLabel(EmarkingDesktop.lang.getString("pagenumber"));
        lblPageNumberTitle.setHorizontalAlignment(4);
        lblPageNumberTitle.setBounds(10, 16, 104, 14);
        this.contentPanel.add(lblPageNumberTitle);
        (this.lblPageNumber = new JLabel("N")).setBounds(126, 16, 291, 14);
        this.contentPanel.add(this.lblPageNumber);
        final JLabel lblNewLabel_1 = new JLabel(EmarkingDesktop.lang.getString("student"));
        lblNewLabel_1.setHorizontalAlignment(4);
        lblNewLabel_1.setBounds(10, 45, 104, 14);
        this.contentPanel.add(lblNewLabel_1);
        final JLabel lblCourseId = new JLabel(EmarkingDesktop.lang.getString("courseid"));
        lblCourseId.setHorizontalAlignment(4);
        lblCourseId.setBounds(10, 74, 104, 14);
        this.contentPanel.add(lblCourseId);
        final JLabel lblExamPage = new JLabel(EmarkingDesktop.lang.getString("exampage"));
        lblExamPage.setHorizontalAlignment(4);
        lblExamPage.setBounds(10, 103, 104, 14);
        this.contentPanel.add(lblExamPage);
        this.studentsCombo = new JComboBox<Student>();
        Student[] students = new Student[moodle.getStudents().size()];
        students = moodle.getStudents().values().toArray(students);
        final ComboBoxModel<Student> model = new DefaultComboBoxModel<Student>(students);
        this.studentsCombo.setModel(model);
        this.studentsCombo.setBounds(124, 42, 300, 27);
        this.contentPanel.add(this.studentsCombo);
        (this.coursesCombo = new JComboBox<Course>()).setBounds(124, 71, 300, 27);
        Course[] courses = new Course[moodle.getCourses().size()];
        courses = moodle.getCourses().values().toArray(courses);
        final ComboBoxModel<Course> coursesmodel = new DefaultComboBoxModel<Course>(courses);
        this.coursesCombo.setModel(coursesmodel);
        this.contentPanel.add(this.coursesCombo);
        final Integer[] items = new Integer[100];
        for (int i = 1; i < items.length; ++i) {
            items[i] = i;
        }
        (this.comboPageNumber = new JComboBox<Integer>()).setBounds(124, 100, 300, 27);
        this.comboPageNumber.setModel(new DefaultComboBoxModel<Integer>(items));
        this.contentPanel.add(this.comboPageNumber);
        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(2));
        this.getContentPane().add(buttonPane, "South");
        final JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    final int exampage = FixRowDialog.this.comboPageNumber.getSelectedIndex();
                    if (exampage <= 0) {
                        throw new Exception(EmarkingDesktop.lang.getString("invalidvalues"));
                    }
                }
                catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                    return;
                }
                FixRowDialog.access$1(FixRowDialog.this, false);
                FixRowDialog.this.setVisible(false);
            }
        });
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        this.getRootPane().setDefaultButton(okButton);
        final JButton cancelButton = new JButton(EmarkingDesktop.lang.getString("cancel"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                FixRowDialog.access$1(FixRowDialog.this, true);
                FixRowDialog.this.setVisible(false);
            }
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
    }
    
    public JLabel getLblPageNumber() {
        return this.lblPageNumber;
    }
    
    public JComboBox<Student> getStudentsCombo() {
        return this.studentsCombo;
    }
    
    public JComboBox<Course> getCoursesCombo() {
        return this.coursesCombo;
    }
    
    public JComboBox<Integer> getComboPageNumber() {
        return this.comboPageNumber;
    }
    
    static /* synthetic */ void access$1(final FixRowDialog fixRowDialog, final boolean cancelled) {
        fixRowDialog.cancelled = cancelled;
    }
}
