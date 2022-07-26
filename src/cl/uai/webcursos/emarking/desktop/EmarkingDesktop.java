// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop;

import java.util.Hashtable;
import javax.swing.ListSelectionModel;
import cl.uai.webcursos.emarking.desktop.data.Activity;
import cl.uai.webcursos.emarking.desktop.utils.ZipFile;
import org.ghost4j.document.PDFDocument;
import java.awt.Image;
import java.io.IOException;
import java.awt.image.ImageObserver;
import javax.imageio.ImageIO;
import java.util.Iterator;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import cl.uai.webcursos.emarking.desktop.data.MoodleWorkerEvent;
import cl.uai.webcursos.emarking.desktop.data.MoodleWorkerListener;
import cl.uai.webcursos.emarking.desktop.data.MoodleWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JOptionPane;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JScrollBar;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.Dimension;
import javax.swing.KeyStroke;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Component;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import cl.uai.webcursos.emarking.desktop.data.Student;
import cl.uai.webcursos.emarking.desktop.data.Course;
import cl.uai.webcursos.emarking.desktop.data.Page;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import java.io.File;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JButton;
import javax.swing.JToolBar;
import java.util.ResourceBundle;
import cl.uai.webcursos.emarking.desktop.data.Moodle;
import javax.swing.JPopupMenu;
import javax.swing.JFrame;
import org.apache.log4j.Logger;

public class EmarkingDesktop
{
    private static Logger logger;
    private JFrame frame;
    private PagesTable pagesTable;
    private JPopupMenu contextMenu;
    private Moodle moodle;
    private EmarkingProgress progress;
    private UploadProgress uploadProgress;
    private UploadWorker worker;
    public static ResourceBundle lang;
    private JToolBar toolBar;
    private JButton btnLoadPdf;
    private JButton btnSave;
    private JButton btnUpload;
    private JSplitPane splitPane;
    private JPanel imagePanel;
    private JScrollPane scrollPanePagesTable;
    private JButton btnNextProblem;
    private List<File> zipFiles;
    private JButton btnSelectAllProblems;
    public static boolean IS_MAC;
    private JMenuBar menuBar;
    private JMenuItem menuFileOpen;
    private JMenu menuFile;
    private JMenu menuEdit;
    private JMenuItem menuFix;
    private JMenuItem menuSave;
    private JMenuItem menuUpload;
    private JMenu menuNavigate;
    private JMenuItem menuNextProblem;
    private JMenuItem menuSelectAll;
    private JMenuItem menuFixPrevious;
    private JMenuItem menuFixFollowing;
    private JMenuItem menuRotate;
    private JMenuItem menuRotateAndFixPrevious;
    private JMenuItem menuSwap;
    private JSeparator separator;
    private JSeparator separator_1;
    private JSeparator separator_2;
    private JMenuItem menuRotateAndFixFollowing;
    private JLabel lblStatusBarRight;
    private JLabel lblStatusBar;
    private JTabbedPane tabbedPane;
    private JScrollPane scrollPaneStudentsTable;
    private StudentsTable studentsTable;
    private JScrollPane scrollAnonymousPagesTable;
    private AnonymousPagesTable anonymousPagesTable;
    
    static {
        EmarkingDesktop.logger = Logger.getLogger(EmarkingDesktop.class);
        EmarkingDesktop.IS_MAC = false;
    }
    
    public EmarkingDesktop() {
        this.initialize();
    }
    
    private void initialize() {
        this.moodle = new Moodle();
        final int[] courseId = {0};
        this.moodle.getQr().addPageProcessedListener(new PageProcessedListener() {
            @Override
            public void processed(final QRExtractorEvent e) {
                final QrDecodingResult qrResult = e.getQrresult();

                if(qrResult.getCourseid() == 0) {
                    // if the courseId is 0 we don't do anything
                } else if (courseId[0] == 0) {
                    // if the id is empty we fill it
                    courseId[0] = qrResult.getCourseid();
                } else if (courseId[0] != qrResult.getCourseid()) {
                    // if the ids are not the same we tell the user
                    // also write the situation to the log
                    logger.debug("Curso equivocado encontrado!!! - id esperada: "+ courseId[0] + "- id encontrada: " + qrResult.getCourseid());
                }

                int arg0 = EmarkingDesktop.this.progress.getProgressBar().getValue();
                ++arg0;
                EmarkingDesktop.this.progress.getProgressBar().setValue(arg0);
                EmarkingDesktop.logger.debug("IDENTIFIED - Student:" + qrResult.getUserid() + " Course:" + qrResult.getCourseid() + " Page:" + qrResult.getExampage());
                Label_0165: {
                    if (EmarkingDesktop.this.moodle.isFakeStudents()) {
                        if (EmarkingDesktop.this.moodle.getCourses().size() != 0) {
                            if (EmarkingDesktop.this.moodle.getStudents().size() != 0) {
                                break Label_0165;
                            }
                        }
                        try {
                            EmarkingDesktop.this.moodle.retrieveCourseFromId(0);
                            EmarkingDesktop.this.moodle.retrieveStudents(qrResult.getCourseid());
                        }
                        catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }
                if (!EmarkingDesktop.this.moodle.getCourses().containsKey(qrResult.getCourseid()) && qrResult.getCourseid() > 0) {
                    EmarkingDesktop.logger.debug("Course " + qrResult.getCourseid() + " not found!");
                    try {
                        EmarkingDesktop.this.moodle.retrieveCourseFromId(qrResult.getCourseid());
                        EmarkingDesktop.this.moodle.retrieveStudents(qrResult.getCourseid());
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                if (!EmarkingDesktop.this.moodle.getStudents().containsKey(qrResult.getUserid()) && qrResult.getUserid() > 0 && qrResult.getCourseid() > 0) {
                    EmarkingDesktop.logger.debug("Student " + qrResult.getUserid() + " not found");
                    try {
                        EmarkingDesktop.this.moodle.retrieveStudents(qrResult.getCourseid());
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                final Page p = new Page(EmarkingDesktop.this.moodle);
                p.setFilename(e.isBackPage() ? qrResult.getBackfilename() : qrResult.getFilename());
                p.setRow(EmarkingDesktop.this.pagesTable.getModel().getRowCount());
                p.setProblem(e.getQrresult().getOutput());
                p.setCourse(EmarkingDesktop.this.moodle.getCourses().getOrDefault(qrResult.getCourseid(), null));
                p.setStudent(EmarkingDesktop.this.moodle.getStudents().getOrDefault(qrResult.getUserid(), null));
                p.setPagenumber(qrResult.getExampage());
                p.setRotated(qrResult.isRotated());
                if (p.getStudent() != null) {
                    if (qrResult.isAnswersheet()) {
                        p.getStudent().setAnswers(qrResult.getAnswers());
                        p.getStudent().setAttemptid(qrResult.getAttemptId());
                        EmarkingDesktop.this.moodle.setAnswerSheets(true);
                    }
                    p.getStudent().addPage(p);
                    EmarkingDesktop.this.studentsTable.updateData(p.getStudent());
                }
                EmarkingDesktop.this.moodle.getPages().put(p.getRow(), p);
                EmarkingDesktop.this.pagesTable.getPagesTableModel().addRow((Object[])null);
                EmarkingDesktop.this.pagesTable.updateData(EmarkingDesktop.this.moodle.getPages().getRowData(p.getRow()), p.getRow(), false);
                if (EmarkingDesktop.this.pagesTable.getRowCount() == 1) {
                    EmarkingDesktop.this.pagesTable.selectAll();
                }
                EmarkingDesktop.this.anonymousPagesTable.getPagesTableModel().addRow((Object[])null);
                EmarkingDesktop.this.anonymousPagesTable.updateData(EmarkingDesktop.this.moodle.getPages().getRowData(p.getRow()), p.getRow(), false);
                EmarkingDesktop.this.progress.getLblProgress().setText(String.valueOf(EmarkingDesktop.lang.getString("processingpage")) + " " + p.getRow());
            }
            
            @Override
            public void finished(final QRExtractorEvent e) {
                EmarkingDesktop.this.progress.setVisible(false);
                if (EmarkingDesktop.this.pagesTable.getRowCount() > 0) {
                    EmarkingDesktop.this.btnSave.setEnabled(true);
                    EmarkingDesktop.this.btnUpload.setEnabled(true);
                    EmarkingDesktop.this.btnNextProblem.setEnabled(true);
                    EmarkingDesktop.this.btnSelectAllProblems.setEnabled(true);
                    EmarkingDesktop.this.menuNavigate.setEnabled(true);
                    EmarkingDesktop.this.menuUpload.setEnabled(true);
                    EmarkingDesktop.this.menuSave.setEnabled(true);
                    EmarkingDesktop.this.lblStatusBar.setText(EmarkingDesktop.this.moodle.getPages().getSummary());
                }
                EmarkingDesktop.logger.debug("QR extraction finished!");
            }
            
            @Override
            public void started(final QRExtractorEvent e) {
            }
        });
        (this.frame = new JFrame()).addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(final KeyEvent e) {
                super.keyTyped(e);
                EmarkingDesktop.logger.debug("Keychar:" + e.getKeyChar());
            }
            
            @Override
            public void keyPressed(final KeyEvent e) {
                super.keyPressed(e);
                EmarkingDesktop.logger.debug("Keychar:" + e.getKeyChar());
            }
            
            @Override
            public void keyReleased(final KeyEvent e) {
                super.keyReleased(e);
                EmarkingDesktop.logger.debug("Keychar:" + e.getKeyChar());
            }
        });
        this.frame.setIconImage(Toolkit.getDefaultToolkit().getImage(EmarkingDesktop.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/qrcode.png")));
        this.frame.setBounds(10, 10, 1024, 600);
        this.frame.setTitle("eMarking");
        this.frame.setDefaultCloseOperation(3);
        this.frame.getContentPane().setLayout(new BorderLayout(0, 0));
        this.contextMenu = new JPopupMenu("Right click!");
        final JMenuItem popupMenuItem3 = new JMenuItem(EmarkingDesktop.lang.getString("fix"));
        popupMenuItem3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionFix();
            }
        });
        this.contextMenu.add(popupMenuItem3);
        final JMenuItem popupMenuItem4 = new JMenuItem(EmarkingDesktop.lang.getString("fixfromprevious"));
        popupMenuItem4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionFixFromPrevious();
            }
        });
        this.contextMenu.add(popupMenuItem4);
        final JMenuItem popupMenuItem5 = new JMenuItem(EmarkingDesktop.lang.getString("fixfromfollowing"));
        popupMenuItem5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionFixFromFollowing();
            }
        });
        this.contextMenu.add(popupMenuItem5);
        final JMenuItem popupMenuItem6 = new JMenuItem(EmarkingDesktop.lang.getString("rotateimage180"));
        popupMenuItem6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionRotateImage180();
            }
        });
        this.contextMenu.add(popupMenuItem6);
        final JMenuItem popupMenuItem7 = new JMenuItem(EmarkingDesktop.lang.getString("swap"));
        popupMenuItem7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionSwap();
            }
        });
        if (this.moodle.isDoubleside()) {
            this.contextMenu.add(popupMenuItem7);
        }
        final JMenuItem popupMenuItem8 = new JMenuItem(EmarkingDesktop.lang.getString("rotateandfixfromprevious"));
        popupMenuItem8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionRotateAndFixFromPrevious();
            }
        });
        this.contextMenu.add(popupMenuItem8);
        final JMenuItem popupMenuItem9 = new JMenuItem(EmarkingDesktop.lang.getString("rotateandfixfromfollowing"));
        popupMenuItem9.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionRotateAndFixFromFollowing();
            }
        });
        this.contextMenu.add(popupMenuItem9);
        this.progress = new EmarkingProgress();
        (this.toolBar = new JToolBar()).setForeground(Color.LIGHT_GRAY);
        this.toolBar.setRollover(true);
        this.toolBar.setFloatable(false);
        this.frame.getContentPane().add(this.toolBar, "North");
        (this.btnLoadPdf = new JButton()).setToolTipText(EmarkingDesktop.lang.getString("loadpdf"));
        this.btnLoadPdf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionLoadPdf();
            }
        });
        this.btnLoadPdf.setIcon(new ImageIcon(EmarkingDesktop.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/glyphicons_036_file.png")));
        this.toolBar.add(this.btnLoadPdf);
        (this.btnSave = new JButton()).setToolTipText(EmarkingDesktop.lang.getString("save"));
        this.btnSave.setEnabled(false);
        this.btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionSave();
            }
        });
        this.btnSave.setIcon(new ImageIcon(EmarkingDesktop.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/glyphicons_443_floppy_disk.png")));
        this.toolBar.add(this.btnSave);
        (this.btnUpload = new JButton()).setToolTipText(EmarkingDesktop.lang.getString("upload"));
        this.btnUpload.setEnabled(false);
        this.btnUpload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionUpload();
            }
        });
        this.btnUpload.setIcon(new ImageIcon(EmarkingDesktop.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/glyphicons_201_upload.png")));
        this.toolBar.add(this.btnUpload);
        (this.btnNextProblem = new JButton()).setToolTipText(EmarkingDesktop.lang.getString("nextproblem"));
        this.btnNextProblem.setEnabled(false);
        this.btnNextProblem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionNextProblem();
            }
        });
        this.btnNextProblem.setIcon(new ImageIcon(EmarkingDesktop.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/glyphicons_178_step_forward.png")));
        this.toolBar.add(this.btnNextProblem);
        (this.btnSelectAllProblems = new JButton()).setToolTipText(EmarkingDesktop.lang.getString("selectallproblems"));
        this.btnSelectAllProblems.setEnabled(false);
        this.btnSelectAllProblems.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionSelectAllProblems();
            }
        });
        this.btnSelectAllProblems.setIcon(new ImageIcon(EmarkingDesktop.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/glyphicons_177_fast_forward.png")));
        this.toolBar.add(this.btnSelectAllProblems);
        (this.splitPane = new JSplitPane()).setOneTouchExpandable(true);
        this.splitPane.setContinuousLayout(true);
        this.splitPane.setDividerLocation(640);
        this.splitPane.setAutoscrolls(true);
        this.frame.getContentPane().add(this.splitPane, "Center");
        this.imagePanel = new JPanel();
        this.splitPane.setLeftComponent(this.imagePanel);
        this.tabbedPane = new JTabbedPane(1);
        this.splitPane.setRightComponent(this.tabbedPane);
        this.scrollPanePagesTable = new JScrollPane();
        this.tabbedPane.addTab(EmarkingDesktop.lang.getString("pages"), null, this.scrollPanePagesTable, null);
        this.scrollPanePagesTable.setVerticalScrollBarPolicy(22);
        this.scrollAnonymousPagesTable = new JScrollPane();
        this.tabbedPane.addTab(EmarkingDesktop.lang.getString("anonymouspages"), null, this.scrollAnonymousPagesTable, null);
        this.scrollAnonymousPagesTable.setVerticalScrollBarPolicy(22);
        (this.scrollPaneStudentsTable = new JScrollPane()).setVerticalScrollBarPolicy(22);
        this.tabbedPane.addTab(EmarkingDesktop.lang.getString("students"), null, this.scrollPaneStudentsTable, null);
        this.initializeTable();
        this.scrollPanePagesTable.add(this.pagesTable);
        this.scrollPanePagesTable.setViewportView(this.pagesTable);
        this.scrollPanePagesTable.add(this.anonymousPagesTable);
        this.scrollPanePagesTable.setViewportView(this.anonymousPagesTable);
        this.studentsTable = new StudentsTable(this.moodle);
        this.scrollPaneStudentsTable.setViewportView(this.studentsTable);
        this.menuBar = new JMenuBar();
        this.frame.setJMenuBar(this.menuBar);
        this.menuFile = new JMenu(EmarkingDesktop.lang.getString("file"));
        this.menuBar.add(this.menuFile);
        (this.menuFileOpen = new JMenuItem(EmarkingDesktop.lang.getString("loadpdf"))).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionLoadPdf();
            }
        });
        this.menuFileOpen.setAccelerator(KeyStroke.getKeyStroke(76, 2));
        this.menuFile.add(this.menuFileOpen);
        (this.menuSave = new JMenuItem(EmarkingDesktop.lang.getString("save"))).setEnabled(false);
        this.menuSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionSave();
            }
        });
        this.separator_2 = new JSeparator();
        this.menuFile.add(this.separator_2);
        this.menuSave.setAccelerator(KeyStroke.getKeyStroke(83, 2));
        this.menuFile.add(this.menuSave);
        (this.menuUpload = new JMenuItem(EmarkingDesktop.lang.getString("upload"))).setEnabled(false);
        this.menuUpload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionUpload();
            }
        });
        this.menuUpload.setAccelerator(KeyStroke.getKeyStroke(85, 2));
        this.menuFile.add(this.menuUpload);
        (this.menuEdit = new JMenu(EmarkingDesktop.lang.getString("edit"))).setEnabled(false);
        this.menuBar.add(this.menuEdit);
        (this.menuFix = new JMenuItem(EmarkingDesktop.lang.getString("fix"))).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionFix();
            }
        });
        this.menuFix.setAccelerator(KeyStroke.getKeyStroke(70, 2));
        this.menuEdit.add(this.menuFix);
        (this.menuFixPrevious = new JMenuItem(EmarkingDesktop.lang.getString("fixfromprevious"))).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionFixFromPrevious();
            }
        });
        this.menuFixPrevious.setAccelerator(KeyStroke.getKeyStroke(80, 2));
        this.menuEdit.add(this.menuFixPrevious);
        (this.menuFixFollowing = new JMenuItem(EmarkingDesktop.lang.getString("fixfromfollowing"))).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionFixFromFollowing();
            }
        });
        this.menuFixFollowing.setAccelerator(KeyStroke.getKeyStroke(74, 2));
        this.menuEdit.add(this.menuFixFollowing);
        (this.menuRotate = new JMenuItem(EmarkingDesktop.lang.getString("rotateimage180"))).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionRotateImage180();
            }
        });
        this.separator_1 = new JSeparator();
        this.menuEdit.add(this.separator_1);
        this.menuRotate.setAccelerator(KeyStroke.getKeyStroke(82, 2));
        this.menuEdit.add(this.menuRotate);
        (this.menuRotateAndFixPrevious = new JMenuItem(EmarkingDesktop.lang.getString("rotateandfixfromprevious"))).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionRotateAndFixFromPrevious();
            }
        });
        (this.menuSwap = new JMenuItem(EmarkingDesktop.lang.getString("swap"))).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionSwap();
            }
        });
        this.menuSwap.setAccelerator(KeyStroke.getKeyStroke(73, 2));
        this.menuEdit.add(this.menuSwap);
        this.separator = new JSeparator();
        this.menuEdit.add(this.separator);
        this.menuRotateAndFixPrevious.setAccelerator(KeyStroke.getKeyStroke(75, 2));
        this.menuEdit.add(this.menuRotateAndFixPrevious);
        (this.menuRotateAndFixFollowing = new JMenuItem(EmarkingDesktop.lang.getString("rotateandfixfromfollowing"))).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionRotateAndFixFromFollowing();
            }
        });
        this.menuRotateAndFixFollowing.setAccelerator(KeyStroke.getKeyStroke(77, 2));
        this.menuEdit.add(this.menuRotateAndFixFollowing);
        (this.menuNavigate = new JMenu(EmarkingDesktop.lang.getString("navigate"))).setEnabled(false);
        this.menuBar.add(this.menuNavigate);
        (this.menuNextProblem = new JMenuItem(EmarkingDesktop.lang.getString("nextproblem"))).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionNextProblem();
            }
        });
        this.menuNextProblem.setAccelerator(KeyStroke.getKeyStroke(78, 0));
        this.menuNavigate.add(this.menuNextProblem);
        (this.menuSelectAll = new JMenuItem(EmarkingDesktop.lang.getString("selectallproblems"))).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingDesktop.this.actionSelectAllProblems();
            }
        });
        this.menuSelectAll.setAccelerator(KeyStroke.getKeyStroke(65, 2));
        this.menuNavigate.add(this.menuSelectAll);
        final Dimension d = new Dimension(32, 32);
        this.btnLoadPdf.setMinimumSize(d);
        this.btnNextProblem.setMinimumSize(d);
        this.btnSave.setMinimumSize(d);
        this.btnSelectAllProblems.setMinimumSize(d);
        this.btnUpload.setMinimumSize(d);
        this.btnLoadPdf.setPreferredSize(d);
        this.btnNextProblem.setPreferredSize(d);
        this.btnSave.setPreferredSize(d);
        this.btnSelectAllProblems.setPreferredSize(d);
        this.btnUpload.setPreferredSize(d);
        final JPanel panelStatusBar = new JPanel();
        panelStatusBar.setBorder(new EtchedBorder(1, null, null));
        this.frame.getContentPane().add(panelStatusBar, "South");
        panelStatusBar.setLayout(new BorderLayout(0, 0));
        panelStatusBar.add(this.lblStatusBar = new JLabel(""), "West");
        panelStatusBar.add(this.lblStatusBarRight = new JLabel("Otro status"), "East");
    }
    
    private void scrollToRow(final int row) {
        final JScrollBar bar = this.scrollPanePagesTable.getVerticalScrollBar();
        if (this.pagesTable.getRowCount() > 0) {
            final int newIndex = Math.max(0, row - 2);
            final int newPosition = (int)(bar.getMaximum() / (float)this.pagesTable.getRowCount() * newIndex);
            bar.setValue(newPosition);
        }
    }
    
    private void initializeTable() {
        this.pagesTable = new PagesTable(this.moodle);
        this.scrollPanePagesTable.setViewportView(this.pagesTable);
        this.pagesTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseReleased(final MouseEvent e) {
            }
            
            @Override
            public void mousePressed(final MouseEvent e) {
            }
            
            @Override
            public void mouseExited(final MouseEvent e) {
            }
            
            @Override
            public void mouseEntered(final MouseEvent e) {
            }
            
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (EmarkingDesktop.this.moodle.getQr().isDoubleside() && EmarkingDesktop.this.pagesTable.getSelectedRow() % 2 != 0) {
                    JOptionPane.showMessageDialog(EmarkingDesktop.this.frame, EmarkingDesktop.lang.getString("onlyevenrowsdoubleside"));
                    return;
                }
                if (e.getButton() == 3) {
                    EmarkingDesktop.this.contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
                if (e.getClickCount() == 2 && e.getButton() == 1) {
                    try {
                        if (EmarkingDesktop.this.moodle.getPages().fixPageData(EmarkingDesktop.this.pagesTable.getSelectedRow(), EmarkingDesktop.this.frame)) {
                            EmarkingDesktop.this.updateTableData(EmarkingDesktop.this.pagesTable.getSelectedRow());
                        }
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                        JOptionPane.showMessageDialog(EmarkingDesktop.this.frame, e2.getMessage());
                    }
                }
            }
        });
        this.pagesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (EmarkingDesktop.this.pagesTable.getSelectedRow() >= 0) {
                    EmarkingDesktop.this.loadSelectedRowPreview(EmarkingDesktop.this.pagesTable.getSelectedRow(), false);
                    EmarkingDesktop.this.menuEdit.setEnabled(true);
                }
                else {
                    EmarkingDesktop.this.menuEdit.setEnabled(false);
                }
            }
        });
        this.anonymousPagesTable = new AnonymousPagesTable(this.moodle);
        this.scrollAnonymousPagesTable.setViewportView(this.anonymousPagesTable);
        this.anonymousPagesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (EmarkingDesktop.this.anonymousPagesTable.getSelectedRow() >= 0) {
                    EmarkingDesktop.this.loadSelectedRowPreview(EmarkingDesktop.this.anonymousPagesTable.getSelectedRow(), true);
                }
            }
        });
        this.studentsTable = new StudentsTable(this.moodle);
        this.scrollPaneStudentsTable.setViewportView(this.studentsTable);
    }
    
    public void updateTableData(final int row) {
        final Object[] data = this.moodle.getPages().getRowData(row);
        this.pagesTable.updateData(data, row, this.moodle.getQr().isDoubleside());
        this.anonymousPagesTable.updateData(data, row, this.moodle.getQr().isDoubleside());
        final Page p = moodle.getPages().get(row);
        if (p != null && p.getStudent() != null) {
            this.studentsTable.updateData(p.getStudent());
        }
        this.lblStatusBar.setText(this.moodle.getPages().getSummary());
    }
    
    private void executeCommand(final MoodleWorker.Action command) {
        final MoodleWorker worker = new MoodleWorker(this.moodle, this.pagesTable.getSelectedRows(), command);
        worker.addRowProcessedListener(new MoodleWorkerListener() {
            @Override
            public void stepPerformed(final MoodleWorkerEvent e) {
                EmarkingDesktop.this.progress.getProgressBar().setValue(e.getCurrent());
                EmarkingDesktop.this.progress.getLblProgress().setText(String.valueOf(EmarkingDesktop.lang.getString("processingpage")) + " " + e.getOutput());
                final int rowNumber = Integer.parseInt(e.getOutput().toString());
                EmarkingDesktop.this.scrollToRow(rowNumber);
                EmarkingDesktop.this.updateTableData(rowNumber);
                EmarkingDesktop.this.loadSelectedRowPreview(rowNumber, false);
            }
            
            @Override
            public void processFinished(final MoodleWorkerEvent e) {
                EmarkingDesktop.this.progress.setVisible(false);
            }
            
            @Override
            public void processStarted(final MoodleWorkerEvent e) {
                EmarkingDesktop.this.progress.getProgressBar().setMaximum(e.getTotal());
                EmarkingDesktop.this.progress.getProgressBar().setMinimum(0);
            }
        });
        this.progress.setWorker(worker);
        this.progress.setLocationRelativeTo(this.frame);
        this.progress.startProcessing();
    }
    
    private void saveZipFiles(final List<File> zipFiles) {
        final JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(EmarkingDesktop.lang.getString("savepagesfile"));
        chooser.setDialogType(1);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "*.zip";
            }
            
            @Override
            public boolean accept(final File arg0) {
                return arg0.getName().endsWith(".zip") || arg0.isDirectory();
            }
        });
        final int retvalsave = chooser.showSaveDialog(this.frame);
        if (retvalsave == 0) {
            String zipfilename = chooser.getSelectedFile().getAbsolutePath();
            if (zipfilename.endsWith(".zip")) {
                zipfilename = chooser.getSelectedFile().getAbsolutePath().substring(0, chooser.getSelectedFile().getAbsolutePath().length() - 4);
            }
            int num = 1;
            for (final File zip : zipFiles) {
                String filename = String.valueOf(zipfilename) + ".zip";
                if (num > 1) {
                    filename = String.valueOf(zipfilename) + "_" + num + ".zip";
                }
                final File dest = new File(filename);
                if (dest.exists()) {
                    final int result = JOptionPane.showConfirmDialog(this.frame, "File " + filename + " already exists. Overwrite?", "Alert", 0, 2);
                    if (result == 0) {
                        dest.delete();
                    }
                }
                zip.renameTo(dest);
                ++num;
            }
            JOptionPane.showMessageDialog(this.frame, EmarkingDesktop.lang.getString("done"));
        }
    }
    
    public JFrame getFrame() {
        return this.frame;
    }
    
    private void loadSelectedRowPreview(final int row, final boolean anonymous) {
        if (this.moodle.getPages().get(row) == null) {
            EmarkingDesktop.logger.error("Invalid row for preview:" + row);
        }
        final String pageFilename = moodle.getPages().get(row).getFilename();
        final String filename = anonymous ? (String.valueOf(this.moodle.getQr().getTempdirStringPath()) + "/" + pageFilename + "_a.png") : (String.valueOf(this.moodle.getQr().getTempdirStringPath()) + "/" + pageFilename + ".png");
        try {
            final File imagefile = new File(filename);
            if (!imagefile.exists()) {
                JOptionPane.showMessageDialog(null, String.valueOf(EmarkingDesktop.lang.getString("filenotfound")) + " " + filename);
                return;
            }
            final Image img = ImageIO.read(imagefile);
            int width = 640;
            if (this.imagePanel.getWidth() > 640) {
                width = this.imagePanel.getWidth();
            }
            final int height = (int)(width / (float)img.getWidth(null) * img.getHeight(null));
            this.imagePanel.getGraphics().drawImage(img, 0, 0, width, height, null);
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    
    private void actionLoadPdf() {
        final OptionsDialog dialog = new OptionsDialog(this.moodle);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(this.frame);
        dialog.setVisible(true);
        if (dialog.isCancelled()) {
            return;
        }
        this.moodle.getQr().setDoubleside(dialog.getDoubleSideSelected());
        this.moodle.setOMRTemplate(dialog.getOMRTemplate());
        this.moodle.clearPages();
        this.lblStatusBarRight.setText(this.moodle.getQr().getTempdirStringPath());
        final File pdfFile = new File(dialog.getFilename().getText());
        int pages = 0;
        Label_0300: {
            if (pdfFile.getPath().endsWith(".pdf")) {
                final PDFDocument pdfdoc = new PDFDocument();
                try {
                    pdfdoc.load(pdfFile);
                    pages = pdfdoc.getPageCount();
                    this.moodle.getQr().setFileType(QRextractor.FileType.PDF);
                    break Label_0300;
                }
                catch (Exception ex) {
                    JOptionPane.showMessageDialog(this.frame, String.valueOf(EmarkingDesktop.lang.getString("unabletoopenfile")) + " " + pdfFile.getName());
                    ex.printStackTrace();
                    return;
                }
            }
            if (pdfFile.getPath().endsWith(".zip")) {
                final ZipFile zpf = new ZipFile(this.moodle);
                pages = zpf.unZipIt(pdfFile.getAbsolutePath());
                this.moodle.getQr().setFileType(QRextractor.FileType.ZIP);
                if (pages == 0) {
                    JOptionPane.showMessageDialog(this.frame, String.valueOf(EmarkingDesktop.lang.getString("unabletoopenfile")) + " " + pdfFile.getName());
                    return;
                }
            }
        }
        this.toolBar.setEnabled(false);
        this.progress.getProgressBar().setMaximum(pages);
        this.progress.getProgressBar().setMinimum(0);
        this.progress.getProgressBar().setValue(0);
        this.moodle.getQr().setPdffile(dialog.getFilename().getText());
        this.moodle.getQr().setTotalpages(pages);
        this.progress.setLocationRelativeTo(this.frame);
        this.progress.setWorker(this.moodle.getQr());
        this.initializeTable();
        this.progress.startProcessing();
    }
    
    private void actionSave() {
        final ZipFile appZip = new ZipFile(this.moodle);
        this.progress.setLocationRelativeTo(this.frame);
        this.progress.setWorker(appZip);
        appZip.addProgressListener(new MoodleWorkerListener() {
            @Override
            public void processStarted(final MoodleWorkerEvent e) {
                EmarkingDesktop.this.progress.getProgressBar().setMinimum(0);
                EmarkingDesktop.this.progress.getProgressBar().setMaximum(e.getTotal());
                EmarkingDesktop.this.progress.getProgressBar().setValue(0);
            }
            
            @Override
            public void stepPerformed(final MoodleWorkerEvent e) {
                EmarkingDesktop.this.progress.getLblProgress().setText(e.getOutput().toString());
                EmarkingDesktop.this.progress.getProgressBar().setValue(e.getCurrent());
            }
            
            @Override
            public void processFinished(final MoodleWorkerEvent e) {
                EmarkingDesktop.this.progress.setVisible(false);
                final ZipFile appZip = (ZipFile)e.getSource();
                EmarkingDesktop.access$31(EmarkingDesktop.this, appZip.getZipFiles());
            }
        });
        this.progress.startProcessing();
        this.saveZipFiles(this.zipFiles);
    }
    
    private void actionUpload() {
        try {
            final UploadAnswersDialog dialog = new UploadAnswersDialog(this.moodle);
            dialog.setLocationRelativeTo(this.frame);
            dialog.setModal(true);
            dialog.setVisible(true);
            if (dialog.isCancelled()) {
                return;
            }
            final ZipFile appZip = new ZipFile(this.moodle);
            appZip.setDatalimit(2);
            appZip.addProgressListener(new MoodleWorkerListener() {
                @Override
                public void processStarted(final MoodleWorkerEvent e) {
                }
                
                @Override
                public void stepPerformed(final MoodleWorkerEvent e) {
                }
                
                @Override
                public void processFinished(final MoodleWorkerEvent e) {
                    final ZipFile appZip = (ZipFile)e.getSource();
                    EmarkingDesktop.access$31(EmarkingDesktop.this, appZip.getZipFiles());
                }
            });
            this.progress.setWorker(appZip);
            this.progress.setLocationRelativeTo(this.frame);
            this.progress.startProcessing();
            Activity activity = null;
            String newactivityname = EmarkingDesktop.lang.getString("defaultactivityname");
            boolean merge = true;
            if (!dialog.getChckbxNewActivity().isSelected() && dialog.getActivitiesComboBox().getSelectedItem() instanceof Activity) {
                activity = (Activity)dialog.getActivitiesComboBox().getSelectedItem();
                merge = !dialog.getChkMerge().isSelected();
            }
            if (dialog.getChckbxNewActivity().isSelected()) {
                newactivityname = dialog.getTxtActivityName().getText();
                merge = false;
            }
            final Course course = this.moodle.getCourses().get(this.moodle.getCourses().keySet().toArray()[0]);
            this.btnSave.setEnabled(false);
            (this.worker = new UploadWorker(this.moodle, activity, merge, newactivityname, this.zipFiles, course.getId())).addProcessingListener(new MoodleWorkerListener() {
                @Override
                public void processStarted(final MoodleWorkerEvent e) {
                }
                
                @Override
                public void stepPerformed(final MoodleWorkerEvent e) {
                    final int currentBytes = e.getCurrent() / 1024;
                    final int totalBytes = e.getTotal() / 1024;
                    final String message = "Uploading " + currentBytes + "K -" + totalBytes + "K";
                    EmarkingDesktop.this.uploadProgress.getProgressBar().setMaximum(e.getTotal());
                    EmarkingDesktop.this.uploadProgress.getProgressBar().setMinimum(0);
                    EmarkingDesktop.this.uploadProgress.getProgressBar().setValue(e.getCurrent());
                    EmarkingDesktop.this.uploadProgress.getLblProgress().setText(message);
                }
                
                @Override
                public void processFinished(final MoodleWorkerEvent e) {
                    EmarkingDesktop.logger.debug("Upload finished event");
                    EmarkingDesktop.this.btnUpload.setEnabled(false);
                    EmarkingDesktop.this.menuUpload.setEnabled(false);
                    EmarkingDesktop.this.btnSave.setEnabled(true);
                    EmarkingDesktop.this.menuSave.setEnabled(true);
                    EmarkingDesktop.this.uploadProgress.setVisible(false);
                }
            });
            this.progress.setWorker(this.worker);
            this.progress.setLocationRelativeTo(this.frame);
            this.progress.startProcessing();
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
    }
    
    private void actionNextProblem() {
        int start = 0;
        if (this.pagesTable.getSelectedRow() >= 0) {
            start = this.pagesTable.getSelectedRow();
        }
        boolean problemdetected = false;
        for (int i = start + 1; i < this.pagesTable.getRowCount(); ++i) {
            final Page page = moodle.getPages().get(i);
            if (page.isProblematic()) {
                this.pagesTable.setRowSelectionInterval(i, i);
                problemdetected = true;
                this.scrollToRow(i);
                break;
            }
        }
        if (!problemdetected) {
            JOptionPane.showMessageDialog(this.frame, EmarkingDesktop.lang.getString("nomoreproblems"));
        }
    }
    
    private void actionSelectAllProblems() {
        boolean problemdetected = false;
        final ListSelectionModel model = this.pagesTable.getSelectionModel();
        model.clearSelection();
        for (int i = 0; i < this.pagesTable.getRowCount(); ++i) {
            final Page page = moodle.getPages().get(i);
            if (page.isProblematic()) {
                model.addSelectionInterval(i, i);
                problemdetected = true;
                this.scrollToRow(i);
            }
        }
        if (!problemdetected) {
            JOptionPane.showMessageDialog(this.frame, EmarkingDesktop.lang.getString("nomoreproblems"));
        }
    }
    
    private void actionFix() {
        int[] selectedRows;
        for (int length = (selectedRows = this.pagesTable.getSelectedRows()).length, i = 0; i < length; ++i) {
            final int row = selectedRows[i];
            try {
                if (this.moodle.getPages().fixPageData(row, this.frame)) {
                    this.updateTableData(row);
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(this.frame, e1.getMessage());
            }
        }
    }
    
    private void actionFixFromPrevious() {
        try {
            this.executeCommand(MoodleWorker.Action.FIX_FROM_PREVIOUS);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this.frame, ex.getMessage());
        }
    }
    
    private void actionFixFromFollowing() {
        try {
            this.executeCommand(MoodleWorker.Action.FIX_FROM_FOLLOWING);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this.frame, ex.getMessage());
        }
    }
    
    private void actionRotateImage180() {
        try {
            this.executeCommand(MoodleWorker.Action.ROTATE180);
        }
        catch (Exception e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(this.frame, e1.getMessage());
        }
    }
    
    private void actionSwap() {
        try {
            this.executeCommand(MoodleWorker.Action.SWAPFRONTBACK);
        }
        catch (Exception e1) {
            JOptionPane.showMessageDialog(this.frame, e1.getMessage());
            e1.printStackTrace();
        }
    }
    
    private void actionRotateAndFixFromPrevious() {
        try {
            this.executeCommand(MoodleWorker.Action.ROTATE180ANDFIX);
        }
        catch (Exception e1) {
            JOptionPane.showMessageDialog(this.frame, e1.getMessage());
            e1.printStackTrace();
        }
    }
    
    private void actionRotateAndFixFromFollowing() {
        try {
            this.executeCommand(MoodleWorker.Action.ROTATE180ANDFIXFROMFOLLOWING);
        }
        catch (Exception e1) {
            JOptionPane.showMessageDialog(this.frame, e1.getMessage());
            e1.printStackTrace();
        }
    }
    
    static /* synthetic */ void access$31(final EmarkingDesktop emarkingDesktop, final List zipFiles) {
        emarkingDesktop.zipFiles = (List<File>)zipFiles;
    }
}
