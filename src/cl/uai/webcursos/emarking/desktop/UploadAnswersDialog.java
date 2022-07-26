// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop;

import javax.swing.ComboBoxModel;
import java.util.Hashtable;
import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.JSeparator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.LayoutManager;
import cl.uai.webcursos.emarking.desktop.data.Activity;
import cl.uai.webcursos.emarking.desktop.data.Course;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import cl.uai.webcursos.emarking.desktop.data.Moodle;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JDialog;

public class UploadAnswersDialog extends JDialog
{
    private static final long serialVersionUID = 2577755818098593404L;
    private final JPanel contentPanel;
    private boolean cancelled;
    private final JTextField txtActivityName;
    private Moodle moodle;
    private JCheckBox chckbxNewActivity;
    private JComboBox<Course> comboBoxCourses;
    private JComboBox<Activity> comboBox;
    private JCheckBox chkMerge;
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public UploadAnswersDialog(final Moodle _moodle) {
        this.contentPanel = new JPanel();
        this.cancelled = true;
        this.setTitle(EmarkingDesktop.lang.getString("uploadanswers"));
        this.setBounds(100, 100, 450, 300);
        this.getContentPane().setLayout(null);
        this.contentPanel.setBounds(0, 0, 450, 239);
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.getContentPane().add(this.contentPanel);
        this.contentPanel.setLayout(null);
        (this.txtActivityName = new JTextField()).setEnabled(false);
        this.txtActivityName.setBounds(208, 176, 236, 28);
        this.contentPanel.add(this.txtActivityName);
        this.txtActivityName.setColumns(10);
        final JLabel lblActivityName = new JLabel(EmarkingDesktop.lang.getString("activityname"));
        lblActivityName.setHorizontalAlignment(4);
        lblActivityName.setBounds(6, 182, 190, 16);
        this.contentPanel.add(lblActivityName);
        this.moodle = _moodle;
        this.comboBox = new JComboBox<Activity>();
        Hashtable<Integer, Activity> activitieshash = null;
        try {
            activitieshash = this.moodle.retrieveEmarkingActivities(this.moodle.getCourses());
        }
        catch (Exception e1) {
            e1.printStackTrace();
            this.comboBox.setEnabled(false);
        }
        Activity[] activities = new Activity[activitieshash.size()];
        activities = activitieshash.values().toArray(activities);
        final ComboBoxModel<Activity> model = new DefaultComboBoxModel<Activity>(activities);
        this.comboBox.setModel(model);
        this.comboBox.setBounds(208, 43, 236, 27);
        this.contentPanel.add(this.comboBox);
        this.comboBoxCourses = new JComboBox<Course>();
        Course[] courses = new Course[this.moodle.getCourses().size()];
        courses = this.moodle.getCourses().values().toArray(courses);
        final ComboBoxModel<Course> coursesModel = new DefaultComboBoxModel<Course>(courses);
        this.comboBoxCourses.setModel(coursesModel);
        this.comboBoxCourses.setBounds(208, 43, 236, 27);
        this.contentPanel.add(this.comboBoxCourses);
        final JLabel label = new JLabel(EmarkingDesktop.lang.getString("emarkingactivity"));
        label.setHorizontalAlignment(4);
        label.setBounds(6, 47, 190, 16);
        this.contentPanel.add(label);
        (this.chckbxNewActivity = new JCheckBox("")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (UploadAnswersDialog.this.chckbxNewActivity.isSelected()) {
                    UploadAnswersDialog.this.comboBox.setEnabled(false);
                    UploadAnswersDialog.this.txtActivityName.setEnabled(true);
                    UploadAnswersDialog.this.chkMerge.setEnabled(false);
                }
                else {
                    UploadAnswersDialog.this.comboBox.setEnabled(true);
                    UploadAnswersDialog.this.txtActivityName.setEnabled(false);
                    UploadAnswersDialog.this.chkMerge.setEnabled(true);
                }
            }
        });
        this.chckbxNewActivity.setBounds(208, 141, 128, 23);
        this.contentPanel.add(this.chckbxNewActivity);
        final JLabel lblNewActivity = new JLabel(EmarkingDesktop.lang.getString("createactivity"));
        lblNewActivity.setHorizontalAlignment(4);
        lblNewActivity.setBounds(6, 145, 190, 16);
        this.contentPanel.add(lblNewActivity);
        (this.chkMerge = new JCheckBox("")).setBounds(208, 82, 128, 23);
        this.contentPanel.add(this.chkMerge);
        final JLabel lblMerge = new JLabel(EmarkingDesktop.lang.getString("replaceanswers"));
        lblMerge.setHorizontalAlignment(4);
        lblMerge.setBounds(6, 86, 190, 16);
        this.contentPanel.add(lblMerge);
        final JSeparator separator = new JSeparator();
        separator.setBounds(6, 117, 438, 12);
        this.contentPanel.add(separator);
        final JPanel buttonPane = new JPanel();
        buttonPane.setBounds(0, 239, 450, 39);
        buttonPane.setLayout(new FlowLayout(2));
        this.getContentPane().add(buttonPane);
        final JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (UploadAnswersDialog.this.chckbxNewActivity.isSelected() && UploadAnswersDialog.this.txtActivityName.getText().trim().length() < 3) {
                    return;
                }
                UploadAnswersDialog.access$4(UploadAnswersDialog.this, false);
                UploadAnswersDialog.this.setVisible(false);
            }
        });
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        this.getRootPane().setDefaultButton(okButton);
        final JButton cancelButton = new JButton(EmarkingDesktop.lang.getString("cancel"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                UploadAnswersDialog.access$4(UploadAnswersDialog.this, true);
                UploadAnswersDialog.this.setVisible(false);
            }
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
        if (activitieshash.size() == 0) {
            this.chckbxNewActivity.setSelected(true);
            this.chckbxNewActivity.setEnabled(false);
            this.comboBox.setEnabled(false);
            this.chkMerge.setEnabled(false);
            this.txtActivityName.setEnabled(true);
        }
    }
    
    public JTextField getTxtActivityName() {
        return this.txtActivityName;
    }
    
    public JCheckBox getChckbxNewActivity() {
        return this.chckbxNewActivity;
    }
    
    public JComboBox<Activity> getActivitiesComboBox() {
        return this.comboBox;
    }
    
    public JCheckBox getChkMerge() {
        return this.chkMerge;
    }
    
    static /* synthetic */ void access$4(final UploadAnswersDialog uploadAnswersDialog, final boolean cancelled) {
        uploadAnswersDialog.cancelled = cancelled;
    }
}
