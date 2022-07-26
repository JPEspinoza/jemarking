// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.Dialog;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import org.apache.log4j.Logger;
import javax.swing.JDialog;

public class EmarkingProgress extends JDialog
{
    private static Logger logger;
    private static final long serialVersionUID = 5140842177243998750L;
    private JProgressBar progressBar;
    private JLabel lblProgress;
    private Runnable qr;
    private Thread th;
    private JButton btnCancel;
    
    static {
        EmarkingProgress.logger = Logger.getLogger(EmarkingProgress.class);
    }
    
    public EmarkingProgress() {
        this.setResizable(false);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setModal(true);
        this.setAlwaysOnTop(true);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(EmarkingProgress.class.getResource("/cl/uai/webcursos/emarking/desktop/resources/glyphicons_137_cogwheels.png")));
        this.setTitle(EmarkingDesktop.lang.getString("processingexam"));
        this.setBounds(100, 100, 450, 133);
        this.getContentPane().setLayout(null);
        this.setDefaultCloseOperation(0);
        (this.lblProgress = new JLabel(EmarkingDesktop.lang.getString("processingpages"))).setBounds(10, 11, 414, 14);
        this.getContentPane().add(this.lblProgress);
        (this.progressBar = new JProgressBar()).setStringPainted(true);
        this.progressBar.setBounds(10, 36, 414, 23);
        this.getContentPane().add(this.progressBar);
        (this.btnCancel = new JButton(EmarkingDesktop.lang.getString("cancel"))).setBounds(165, 70, 89, 27);
        this.btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EmarkingProgress.logger.debug("Cancelling operation");
                EmarkingProgress.this.lblProgress.setText(EmarkingDesktop.lang.getString("canceloperation"));
                EmarkingProgress.this.btnCancel.setEnabled(false);
                EmarkingProgress.this.th.interrupt();
            }
        });
        this.getContentPane().add(this.btnCancel);
    }
    
    public void setWorker(final Runnable qr) {
        this.qr = qr;
    }
    
    public void startProcessing() {
        this.btnCancel.setEnabled(true);
        (this.th = new Thread(this.qr)).start();
        this.setVisible(true);
    }
    
    public JProgressBar getProgressBar() {
        return this.progressBar;
    }
    
    public JLabel getLblProgress() {
        return this.lblProgress;
    }
}
