// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop;

import javax.swing.ImageIcon;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.JOptionPane;
import java.io.File;
import org.apache.commons.validator.routines.UrlValidator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import cl.uai.webcursos.emarking.desktop.data.Moodle;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import org.apache.log4j.Logger;
import javax.swing.JDialog;

public class OptionsDialog extends JDialog
{
    private static final long serialVersionUID = 3424578643623876331L;
    private static Logger logger;
    private JCheckBox chckbxDoubleSide;
    private boolean cancelled;
    private final JTextField username;
    private final JPasswordField password;
    private final JTextField moodleurl;
    private final JTextField filename;
    private final Moodle moodle;
    private final JButton btnTestConnection;
    private JButton okButton;
    private JComboBox<String> maxZipSize;
    private JComboBox<Integer> maxThreads;
    private JComboBox<Integer> resolution;
    private JPanel panel;
    private JButton btnOpenPdfFile;
    private final JTextField omrtemplate;
    private JButton btnOpenOMRTemplate;
    private JSpinner spinnerOMRshapeSize;
    private JSpinner spinnerOMRdensity;
    private JSpinner spinnerOMRthreshold;
    private JSpinner spinnerAnonymousPercentage;
    private JSpinner spinnerAnonymousPercentageCustomPage;
    private JSpinner spinnerCustomPage;
    private JLabel lblMarkersTraining;
    private JCheckBox chckbxMarkersTraining;
    
    static {
        OptionsDialog.logger = Logger.getLogger(OptionsDialog.class);
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public OptionsDialog(final Moodle _moodle) {
        this.cancelled = false;
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                OptionsDialog.access$0(OptionsDialog.this, true);
            }
        });
        this.setDefaultCloseOperation(2);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(OptionsDialog.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/glyphicons_439_wrench.png")));
        this.setTitle(EmarkingDesktop.lang.getString("emarkingoptions"));
        this.setModal(true);
        this.setBounds(100, 100, 707, 444);
        (this.moodle = _moodle).loadProperties();
        this.getContentPane().setLayout(new BorderLayout());
        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(2));
        this.getContentPane().add(buttonPane, "South");
        (this.okButton = new JButton(EmarkingDesktop.lang.getString("ok"))).setEnabled(false);
        this.okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    final UrlValidator validator = new UrlValidator(8L);
                    if (!validator.isValid(OptionsDialog.this.moodleurl.getText())) {
                        throw new Exception(String.valueOf(EmarkingDesktop.lang.getString("invalidmoodleurl")) + " " + OptionsDialog.this.moodleurl.getText());
                    }
                    final File f = new File(OptionsDialog.this.filename.getText());
                    if (!f.exists() || f.isDirectory() || (!f.getPath().endsWith(".pdf") && !f.getPath().endsWith(".zip"))) {
                        throw new Exception(String.valueOf(EmarkingDesktop.lang.getString("invalidpdffile")) + " " + OptionsDialog.this.filename.getText());
                    }
                    if (OptionsDialog.this.omrtemplate.getText().trim().length() > 0) {
                        final File omrf = new File(OptionsDialog.this.omrtemplate.getText());
                        if (!omrf.exists() || omrf.isDirectory() || !omrf.getPath().endsWith(".xtmpl")) {
                            throw new Exception(String.valueOf(EmarkingDesktop.lang.getString("invalidomrfile")) + " " + OptionsDialog.this.omrtemplate.getText());
                        }
                    }
                    OptionsDialog.this.moodle.setLastfile(OptionsDialog.this.filename.getText());
                    OptionsDialog.this.moodle.setDoubleside(OptionsDialog.this.chckbxDoubleSide.isSelected());
                    OptionsDialog.this.moodle.setMaxthreads(Integer.parseInt(OptionsDialog.this.getMaxThreads().getSelectedItem().toString()));
                    OptionsDialog.this.moodle.setResolution(Integer.parseInt(OptionsDialog.this.getResolution().getSelectedItem().toString()));
                    OptionsDialog.this.moodle.setMaxzipsize(OptionsDialog.this.getMaxZipSize().getSelectedItem().toString());
                    OptionsDialog.this.moodle.setOMRTemplate(OptionsDialog.this.omrtemplate.getText());
                    OptionsDialog.this.moodle.setThreshold(Integer.parseInt(OptionsDialog.this.spinnerOMRthreshold.getValue().toString()));
                    OptionsDialog.this.moodle.setDensity(Integer.parseInt(OptionsDialog.this.spinnerOMRdensity.getValue().toString()));
                    OptionsDialog.this.moodle.setShapeSize(Integer.parseInt(OptionsDialog.this.spinnerOMRshapeSize.getValue().toString()));
                    OptionsDialog.this.moodle.setAnonymousPercentage(Integer.parseInt(OptionsDialog.this.spinnerAnonymousPercentage.getValue().toString()));
                    OptionsDialog.this.moodle.setAnonymousPercentageCustomPage(Integer.parseInt(OptionsDialog.this.spinnerAnonymousPercentageCustomPage.getValue().toString()));
                    OptionsDialog.this.moodle.setFakeStudents(OptionsDialog.this.chckbxMarkersTraining.isSelected());
                    OptionsDialog.this.moodle.saveProperties();
                    OptionsDialog.access$0(OptionsDialog.this, false);
                    OptionsDialog.this.setVisible(false);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(OptionsDialog.this.panel, EmarkingDesktop.lang.getString("invaliddatainform"));
                }
            }
        });
        this.okButton.setActionCommand("OK");
        buttonPane.add(this.okButton);
        this.getRootPane().setDefaultButton(this.okButton);
        final JButton cancelButton = new JButton(EmarkingDesktop.lang.getString("cancel"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                OptionsDialog.access$0(OptionsDialog.this, true);
                OptionsDialog.this.setVisible(false);
            }
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
        final JTabbedPane tabbedPane = new JTabbedPane(1);
        this.getContentPane().add(tabbedPane, "Center");
        this.panel = new JPanel();
        tabbedPane.addTab(EmarkingDesktop.lang.getString("general"), null, this.panel, null);
        this.panel.setLayout(null);
        final JPanel panel_2 = new JPanel();
        panel_2.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
        panel_2.setBounds(10, 11, 665, 131);
        this.panel.add(panel_2);
        panel_2.setLayout(null);
        final JLabel lblPassword = new JLabel(EmarkingDesktop.lang.getString("password"));
        lblPassword.setBounds(10, 99, 109, 14);
        panel_2.add(lblPassword);
        lblPassword.setHorizontalAlignment(4);
        (this.password = new JPasswordField()).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                OptionsDialog.this.testConnection();
            }
        });
        this.password.setBounds(129, 96, 329, 20);
        panel_2.add(this.password);
        this.password.setText(this.moodle.getPassword());
        (this.btnTestConnection = new JButton(EmarkingDesktop.lang.getString("connect"))).setEnabled(false);
        this.btnTestConnection.setBounds(468, 93, 172, 27);
        panel_2.add(this.btnTestConnection);
        (this.username = new JTextField()).setBounds(129, 65, 329, 20);
        panel_2.add(this.username);
        this.username.setColumns(10);
        this.username.setText(this.moodle.getUsername());
        (this.moodleurl = new JTextField()).setBounds(129, 34, 329, 20);
        panel_2.add(this.moodleurl);
        this.moodleurl.setColumns(10);
        this.moodleurl.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(final DocumentEvent e) {
                this.warn();
            }
            
            @Override
            public void insertUpdate(final DocumentEvent e) {
                this.warn();
            }
            
            @Override
            public void changedUpdate(final DocumentEvent e) {
                this.warn();
            }
            
            private void warn() {
                final UrlValidator validator = new UrlValidator(8L);
                if (!validator.isValid(OptionsDialog.this.moodleurl.getText()) || !OptionsDialog.this.moodleurl.getText().endsWith("/")) {
                    OptionsDialog.this.moodleurl.setForeground(Color.RED);
                    OptionsDialog.this.btnTestConnection.setEnabled(false);
                }
                else {
                    OptionsDialog.this.moodleurl.setForeground(Color.BLACK);
                    OptionsDialog.this.btnTestConnection.setEnabled(true);
                }
            }
        });
        this.moodleurl.setText(this.moodle.getUrl());
        final JLabel lblMoodleUrl = new JLabel(EmarkingDesktop.lang.getString("moodleurl"));
        lblMoodleUrl.setBounds(10, 37, 109, 14);
        panel_2.add(lblMoodleUrl);
        lblMoodleUrl.setHorizontalAlignment(4);
        final JLabel lblUsername = new JLabel(EmarkingDesktop.lang.getString("username"));
        lblUsername.setBounds(10, 68, 109, 14);
        panel_2.add(lblUsername);
        lblUsername.setHorizontalAlignment(4);
        final JLabel lblMoodleSettings = new JLabel(EmarkingDesktop.lang.getString("moodlesettings"));
        lblMoodleSettings.setBounds(10, 11, 230, 14);
        panel_2.add(lblMoodleSettings);
        this.btnTestConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                OptionsDialog.this.testConnection();
            }
        });
        final JPanel panel_3 = new JPanel();
        panel_3.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
        panel_3.setBounds(10, 159, 666, 174);
        this.panel.add(panel_3);
        panel_3.setLayout(null);
        final JLabel lblPdfFile = new JLabel(EmarkingDesktop.lang.getString("pdffile"));
        lblPdfFile.setBounds(0, 39, 119, 14);
        panel_3.add(lblPdfFile);
        lblPdfFile.setHorizontalAlignment(4);
        final JLabel lblScanned = new JLabel(EmarkingDesktop.lang.getString("scanned"));
        lblScanned.setBounds(0, 64, 119, 14);
        panel_3.add(lblScanned);
        lblScanned.setHorizontalAlignment(4);
        (this.chckbxDoubleSide = new JCheckBox(EmarkingDesktop.lang.getString("doubleside"))).setEnabled(false);
        this.chckbxDoubleSide.setBounds(125, 60, 333, 23);
        panel_3.add(this.chckbxDoubleSide);
        this.chckbxDoubleSide.setToolTipText(EmarkingDesktop.lang.getString("doublesidetooltip"));
        this.chckbxDoubleSide.setSelected(this.moodle.isDoubleside());
        (this.filename = new JTextField()).setEnabled(false);
        this.filename.setBounds(129, 36, 329, 20);
        panel_3.add(this.filename);
        this.filename.setColumns(10);
        this.filename.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(final DocumentEvent e) {
                this.warn();
            }
            
            @Override
            public void insertUpdate(final DocumentEvent e) {
                this.warn();
            }
            
            @Override
            public void changedUpdate(final DocumentEvent e) {
                this.warn();
            }
            
            private void warn() {
                OptionsDialog.this.validateFileForProcessing(!OptionsDialog.this.btnTestConnection.isEnabled());
            }
        });
        this.filename.setText(this.moodle.getLastfile());
        (this.btnOpenPdfFile = new JButton(EmarkingDesktop.lang.getString("openfile"))).setEnabled(false);
        this.btnOpenPdfFile.setBounds(468, 33, 172, 27);
        panel_3.add(this.btnOpenPdfFile);
        final JLabel lblPdfFileSettings = new JLabel(EmarkingDesktop.lang.getString("filesettings"));
        lblPdfFileSettings.setBounds(10, 11, 230, 14);
        panel_3.add(lblPdfFileSettings);
        final JLabel lblOMRtemplate = new JLabel(EmarkingDesktop.lang.getString("omrfile"));
        lblOMRtemplate.setHorizontalAlignment(4);
        lblOMRtemplate.setBounds(0, 142, 119, 14);
        panel_3.add(lblOMRtemplate);
        (this.omrtemplate = new JTextField()).setEnabled(false);
        this.omrtemplate.setText(null);
        this.omrtemplate.setColumns(10);
        this.omrtemplate.setBounds(129, 139, 329, 20);
        panel_3.add(this.omrtemplate);
        this.omrtemplate.setText(this.moodle.getOMRTemplate());
        (this.btnOpenOMRTemplate = new JButton(EmarkingDesktop.lang.getString("openomrfile"))).setEnabled(false);
        this.btnOpenOMRTemplate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                final JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle(EmarkingDesktop.lang.getString("openfiletitle"));
                chooser.setDialogType(0);
                chooser.setFileFilter(new FileFilter() {
                    @Override
                    public String getDescription() {
                        return "*.xtmpl";
                    }
                    
                    @Override
                    public boolean accept(final File arg0) {
                        return arg0.getName().endsWith(".xtmpl") || arg0.isDirectory();
                    }
                });
                final int retval = chooser.showOpenDialog(OptionsDialog.this.panel);
                if (retval == 0) {
                    OptionsDialog.this.omrtemplate.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        this.btnOpenOMRTemplate.setBounds(468, 136, 172, 27);
        panel_3.add(this.btnOpenOMRTemplate);
        (this.lblMarkersTraining = new JLabel(EmarkingDesktop.lang.getString("markerstraining"))).setHorizontalAlignment(4);
        this.lblMarkersTraining.setBounds(0, 89, 119, 14);
        panel_3.add(this.lblMarkersTraining);
        (this.chckbxMarkersTraining = new JCheckBox(EmarkingDesktop.lang.getString("markerstrainingfakestudents"))).setBounds(125, 87, 333, 23);
        panel_3.add(this.chckbxMarkersTraining);
        this.btnOpenPdfFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                OptionsDialog.this.okButton.setEnabled(false);
                final JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle(EmarkingDesktop.lang.getString("openfiletitle"));
                chooser.setDialogType(0);
                chooser.setFileFilter(new FileFilter() {
                    @Override
                    public String getDescription() {
                        return "*.pdf, *.zip";
                    }
                    
                    @Override
                    public boolean accept(final File arg0) {
                        return arg0.getName().endsWith(".zip") || arg0.getName().endsWith(".pdf") || arg0.isDirectory();
                    }
                });
                final int retval = chooser.showOpenDialog(OptionsDialog.this.panel);
                if (retval == 0) {
                    OptionsDialog.this.filename.setText(chooser.getSelectedFile().getAbsolutePath());
                    OptionsDialog.this.okButton.setEnabled(true);
                }
            }
        });
        final JPanel panel_4 = new JPanel();
        tabbedPane.addTab(EmarkingDesktop.lang.getString("advanced"), null, panel_4, null);
        panel_4.setLayout(null);
        final JPanel panel_5 = new JPanel();
        panel_5.setLayout(null);
        panel_5.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
        panel_5.setBounds(10, 11, 665, 131);
        panel_4.add(panel_5);
        final JLabel lblAdvancedOptions = new JLabel(EmarkingDesktop.lang.getString("advancedoptions"));
        lblAdvancedOptions.setBounds(10, 11, 233, 14);
        panel_5.add(lblAdvancedOptions);
        final JLabel lblThreads = new JLabel(EmarkingDesktop.lang.getString("maxthreads"));
        lblThreads.setBounds(10, 38, 130, 14);
        panel_5.add(lblThreads);
        lblThreads.setHorizontalAlignment(4);
        final JLabel lblSomething = new JLabel(EmarkingDesktop.lang.getString("separatezipfiles"));
        lblSomething.setBounds(10, 73, 130, 14);
        panel_5.add(lblSomething);
        lblSomething.setHorizontalAlignment(4);
        final JLabel label = new JLabel(EmarkingDesktop.lang.getString("resolution"));
        label.setBounds(10, 105, 130, 14);
        panel_5.add(label);
        label.setHorizontalAlignment(4);
        (this.resolution = new JComboBox<Integer>()).setBounds(150, 99, 169, 27);
        panel_5.add(this.resolution);
        this.resolution.setModel(new DefaultComboBoxModel<Integer>(new Integer[] { 75, 100, 150, 300, 400, 500, 600 }));
        this.resolution.setSelectedIndex(2);
        this.resolution.setSelectedItem(this.moodle.getQr().getResolution());
        (this.maxZipSize = new JComboBox<String>()).setBounds(150, 67, 169, 27);
        panel_5.add(this.maxZipSize);
        this.maxZipSize.setModel(new DefaultComboBoxModel<String>(new String[] { "<dynamic>", "2Mb", "4Mb", "8Mb", "16Mb", "32Mb", "64Mb", "128Mb", "256Mb", "512Mb", "1024Mb" }));
        this.maxZipSize.setSelectedIndex(6);
        this.maxZipSize.setSelectedItem(this.moodle.getMaxZipSizeString());
        (this.maxThreads = new JComboBox<Integer>()).setBounds(150, 32, 169, 27);
        panel_5.add(this.maxThreads);
        this.maxThreads.setModel(new DefaultComboBoxModel<Integer>(new Integer[] { 2, 4, 8, 16 }));
        this.maxThreads.setSelectedIndex(1);
        this.maxThreads.setSelectedItem(this.moodle.getQr().getMaxThreads());
        final JPanel panel_6 = new JPanel();
        panel_6.setLayout(null);
        panel_6.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
        panel_6.setBounds(10, 153, 665, 131);
        panel_4.add(panel_6);
        final JLabel lblOMRoptions = new JLabel(EmarkingDesktop.lang.getString("omroptions"));
        lblOMRoptions.setBounds(10, 11, 233, 14);
        panel_6.add(lblOMRoptions);
        final JLabel lblOMRthreshold = new JLabel(EmarkingDesktop.lang.getString("omrthreshold"));
        lblOMRthreshold.setHorizontalAlignment(4);
        lblOMRthreshold.setBounds(10, 32, 130, 14);
        panel_6.add(lblOMRthreshold);
        final JLabel lblShapeSize = new JLabel(EmarkingDesktop.lang.getString("omrshapesize"));
        lblShapeSize.setHorizontalAlignment(4);
        lblShapeSize.setBounds(10, 99, 130, 14);
        panel_6.add(lblShapeSize);
        final JLabel lblDensity = new JLabel(EmarkingDesktop.lang.getString("omrdensity"));
        lblDensity.setHorizontalAlignment(4);
        lblDensity.setBounds(10, 70, 130, 14);
        panel_6.add(lblDensity);
        (this.spinnerOMRthreshold = new JSpinner()).setBounds(150, 32, 169, 20);
        panel_6.add(this.spinnerOMRthreshold);
        this.spinnerOMRthreshold.setValue(this.moodle.getOMRthreshold());
        (this.spinnerOMRdensity = new JSpinner()).setBounds(150, 67, 169, 20);
        panel_6.add(this.spinnerOMRdensity);
        this.spinnerOMRdensity.setValue(this.moodle.getOMRdensity());
        (this.spinnerOMRshapeSize = new JSpinner()).setBounds(150, 99, 169, 20);
        panel_6.add(this.spinnerOMRshapeSize);
        this.spinnerOMRshapeSize.setValue(this.moodle.getOMRshapeSize());
        final JLabel lblAnonymousPercentage = new JLabel("<html>" + EmarkingDesktop.lang.getString("anonymouspercentage") + "</html>");
        lblAnonymousPercentage.setHorizontalAlignment(4);
        lblAnonymousPercentage.setBounds(329, 32, 130, 27);
        panel_6.add(lblAnonymousPercentage);
        (this.spinnerAnonymousPercentage = new JSpinner()).setBounds(469, 32, 169, 20);
        panel_6.add(this.spinnerAnonymousPercentage);
        this.spinnerAnonymousPercentage.setValue(this.moodle.getAnonymousPercentage());
        final JLabel lblAnonymousPercentageCustomPage = new JLabel("<html>" + EmarkingDesktop.lang.getString("anonymouspercentagecustompage") + "</html>");
        lblAnonymousPercentageCustomPage.setHorizontalAlignment(4);
        lblAnonymousPercentageCustomPage.setBounds(329, 70, 130, 27);
        panel_6.add(lblAnonymousPercentageCustomPage);
        (this.spinnerAnonymousPercentageCustomPage = new JSpinner()).setBounds(469, 70, 169, 20);
        panel_6.add(this.spinnerAnonymousPercentageCustomPage);
        this.spinnerAnonymousPercentageCustomPage.setValue(this.moodle.getAnonymousPercentageCustomPage());
        final JLabel lblCustomPage = new JLabel("<html>" + EmarkingDesktop.lang.getString("anonymouscustompage") + "</html>");
        lblCustomPage.setHorizontalAlignment(4);
        lblCustomPage.setBounds(329, 99, 130, 27);
        panel_6.add(lblCustomPage);
        (this.spinnerCustomPage = new JSpinner()).setBounds(469, 99, 169, 20);
        panel_6.add(this.spinnerCustomPage);
        this.spinnerCustomPage.setValue(this.moodle.getAnonymousCustomPage());
    }
    
    public boolean getDoubleSideSelected() {
        return this.chckbxDoubleSide.isSelected();
    }
    
    public void setDoubleSideSelected(final boolean selected) {
        this.chckbxDoubleSide.setSelected(selected);
    }
    
    public String getUsername() {
        return this.username.getText();
    }
    
    public void setUsername(final String text) {
        this.username.setText(text);
    }
    
    public JPasswordField getPassword() {
        return this.password;
    }
    
    public JTextField getMoodleUrl() {
        return this.moodleurl;
    }
    
    public JTextField getFilename() {
        return this.filename;
    }
    
    public JComboBox<String> getMaxZipSize() {
        return this.maxZipSize;
    }
    
    public JComboBox<Integer> getMaxThreads() {
        return this.maxThreads;
    }
    
    public JComboBox<Integer> getResolution() {
        return this.resolution;
    }
    
    private void validateFileForProcessing(final boolean activateOkButton) {
        final File f = new File(this.filename.getText());
        if (!f.exists() || f.isDirectory() || (!f.getPath().endsWith(".pdf") && !f.getPath().endsWith(".zip"))) {
            this.filename.setForeground(Color.RED);
            this.okButton.setEnabled(false);
        }
        else {
            this.filename.setForeground(Color.BLACK);
            if (activateOkButton) {
                this.okButton.setEnabled(true);
            }
        }
    }
    
    public String getOMRTemplate() {
        return this.omrtemplate.getText();
    }
    
    private void testConnection() {
        this.btnTestConnection.setEnabled(false);
        OptionsDialog.logger.debug("Testing Moodle connection");
        this.moodle.setUrl(this.moodleurl.getText());
        this.moodle.setUsername(this.username.getText());
        final String _password = new String(this.password.getPassword());
        this.moodle.setPassword(_password);
        if (this.moodle.connect()) {
            this.btnTestConnection.setIcon(new ImageIcon(EmarkingDesktop.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/glyphicons_206_ok_2.png")));
            this.btnTestConnection.setText(EmarkingDesktop.lang.getString("connectionsuccessfull"));
            this.filename.setEnabled(true);
            this.chckbxDoubleSide.setEnabled(true);
            this.btnOpenPdfFile.setEnabled(true);
            this.btnTestConnection.setEnabled(false);
            this.username.setEnabled(false);
            this.moodleurl.setEnabled(false);
            this.password.setEnabled(false);
            this.btnOpenOMRTemplate.setEnabled(true);
            this.omrtemplate.setEnabled(true);
            this.validateFileForProcessing(true);
        }
        else {
            JOptionPane.showMessageDialog(this.panel, EmarkingDesktop.lang.getString("connectionfailed"));
            this.btnTestConnection.setEnabled(true);
        }
    }
    
    static /* synthetic */ void access$0(final OptionsDialog optionsDialog, final boolean cancelled) {
        optionsDialog.cancelled = cancelled;
    }
}
