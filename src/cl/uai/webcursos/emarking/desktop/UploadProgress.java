// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.Dialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JDialog;

public class UploadProgress extends JDialog
{
    private static final long serialVersionUID = 5140842177243998750L;
    private JProgressBar progressBar;
    private JLabel lblProgress;
    private UploadWorker uploadWorker;
    private Thread th;
    
    public UploadProgress(final UploadWorker uploadWorker) {
        this.uploadWorker = uploadWorker;
        this.setResizable(false);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setModal(true);
        this.setAlwaysOnTop(true);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(UploadProgress.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/glyphicons_137_cogwheels.png")));
        this.setTitle(EmarkingDesktop.lang.getString("uploadinganswers"));
        this.setBounds(100, 100, 450, 133);
        this.getContentPane().setLayout(null);
        this.setDefaultCloseOperation(0);
        (this.lblProgress = new JLabel(EmarkingDesktop.lang.getString("uploadinganswers"))).setBounds(10, 11, 414, 14);
        this.getContentPane().add(this.lblProgress);
        (this.progressBar = new JProgressBar()).setStringPainted(true);
        this.progressBar.setBounds(10, 36, 414, 23);
        this.getContentPane().add(this.progressBar);
        final JButton btnCancel = new JButton(EmarkingDesktop.lang.getString("cancel"));
        btnCancel.setBounds(165, 70, 89, 27);
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                UploadProgress.this.th.interrupt();
            }
        });
        this.getContentPane().add(btnCancel);
    }
    
    public void startProcessing() {
        (this.th = new Thread(this.uploadWorker)).start();
        this.setVisible(true);
    }
    
    public JProgressBar getProgressBar() {
        return this.progressBar;
    }
    
    public JLabel getLblProgress() {
        return this.lblProgress;
    }
}
